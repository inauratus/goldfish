/*
 * Copyright (C) 2003  Christian Cryder [christianc@granitepeaks.com]
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * $Id: AbstractBComponent.java 259 2013-09-19 18:00:41Z charleslowery $
 */
package org.barracudamvc.core.comp;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.*;
import org.apache.log4j.Logger;
import org.barracudamvc.core.comp.renderer.Renderer;
import org.barracudamvc.core.comp.renderer.RendererFactory;
import org.barracudamvc.core.util.dom.DefaultDOMWriter;
import org.barracudamvc.core.view.FormatType;
import org.barracudamvc.plankton.Classes;
import org.barracudamvc.plankton.HashSequentialList;
import org.barracudamvc.plankton.data.DefaultStateMap;
import org.barracudamvc.plankton.data.StateMap;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * This class provides the abstract implementation for BComponent.
 *
 * NOTE: You should never extend from this class. This class is primarily implemented
 * as an abstract class for code-hiding purposes. ALL BARRACUDA COMPONENTS MUST EXTEND
 * FROM BComponent. If that causes you problems, ask Christian why this must be the case.
 */
public abstract class AbstractBComponent implements BContainer, StateMap, Attrs {

    //public constants
    protected static final Logger logger = Logger.getLogger(AbstractBComponent.class.getName());
    //private constants    
    private static byte[] sep = System.getProperty("line.separator").getBytes();
    private final static Map<Class, Map<Class, RendererFactory>> rfCompMap = new HashMap<Class, Map<Class, RendererFactory>>();
    protected BContainer parent = null;
    protected HashSequentialList<BContainer> children = new HashSequentialList<BContainer>();
    protected HashSet<BContainer> stepChildren = null;
    protected boolean isStepChild = false;
    protected List<View> views = new ArrayList<View>();
    protected List<View> tempViews = null;
    protected Object dvc = null;                        //default view context (opt)
    protected boolean validated = false;
    protected StateMap statemap = new DefaultStateMap();
    protected Map<Object, Object> attrs = null;

    //--------------- AbstractBComponent -------------------------
    @Override
    public abstract <T extends BContainer> T setName(String iname);

    @Override
    public abstract String getName();

    public abstract BComponent setVisible(boolean val);

    public abstract BComponent setVisible(boolean val, boolean recurse);

    public abstract boolean isVisible();

    public abstract BComponent setEnabled(boolean val);

    public abstract BComponent setEnabled(boolean val, boolean recurse);

    public abstract boolean isEnabled();

    public abstract BComponent setView(View view);

    public abstract BComponent addView(View view);

    public abstract boolean removeView(View view);

    public abstract BComponent removeAllViews();

    public abstract List<View> getViews();
//    public abstract void setAttr(Object attr, Object val);
//    public abstract Object getAttr(Object attr);
//    public abstract Map getAttrMap();

    public abstract BComponent render(ViewContext vc) throws RenderException;

    public abstract boolean supports(ViewContext vc);

    /**
     * Set the default ViewContext. 
     *
     * @param idvc the default ViewContext
     */
    public BComponent setDefaultViewContext(ViewContext idvc) {
        dvc = idvc;
        return (BComponent) this;   //yes, this assumes BComponent is the only thing that ever extends AbstractBComponent
    }

    /**
     * Get the default ViewContext. 
     *
     * @return the default ViewContext
     */
    public ViewContext getDefaultViewContext() {
        return (ViewContext) dvc;
    }

    public AbstractBComponent getRootComponent() {
        if (parent != null && parent instanceof AbstractBComponent) {
            return ((AbstractBComponent) parent).getRootComponent();
        } else {
            return this;
        }
    }

    //--------------- Renderer -----------------------------------
    /**
     * This allows developers to install custom renderers for specific
     * classes of components. This should generally be done at startup.
     *
     * @param rf the RendererFactory to be registered
     * @param compCl the target document class 
     * @param domCl the target dom class 
     */
    public static void installRendererFactory(RendererFactory rf, Class compCl, Class domCl) {
        synchronized (rfCompMap) {
            Map<Class, RendererFactory> rfDomMap = rfCompMap.get(compCl);
            if (rfDomMap == null) {
                rfDomMap = new HashMap<Class, RendererFactory>();
                rfCompMap.put(compCl, rfDomMap);
            }
            synchronized (rfDomMap) {
                rfDomMap.put(domCl, rf);
            }
        }
    }

    /**
     * This method allows a component to get a reference to the appropriate 
     * renderer. If an exact match does not exist, it will automatically 
     * look for renderers registered on parent classes.
     */
    public Renderer getRenderer(View view) throws NoSuitableRendererException {
        Node node = view.getNode();
        Class domCl = node.getClass();
        return getRenderer(domCl);
    }

    // fro_031207_1 Handle heritage on components and fix FindRenderer bug
    public Renderer getRenderer(Class domCl) throws NoSuitableRendererException {
        Class cl = this.getClass();
        RendererFactory rf = getRendererFactory(cl, domCl);
        if (rf == null) {
            throw new NoSuitableRendererException("No renderer available for this component:" + cl);
        }
        return rf.getInstance();
    }
    //  fro_031207_1_end Handle heritage on components and fix FindRenderer bug

    protected RendererFactory getRendererFactory(Class cl, Class domCl) throws NoSuitableRendererException {
        boolean registered = true;

        //first try and find the dom map class
        Map<Class, RendererFactory> rfDomMap = rfCompMap.get(cl);
        //..if it can't be located, look for the interfaces it implements
        if (rfDomMap == null) {
            List<Class> list = Classes.getAllInterfaces(domCl);
            for (Class clint : list) {
                rfDomMap = rfCompMap.get(clint);
                if (rfDomMap != null) {
                    break;
                }
            }
            registered = false;
        }
        //..if we still can't find it, look for parent classes
        if (rfDomMap == null) {
            rfDomMap = findComponentMap(cl.getSuperclass());
        }
        if (rfDomMap == null) {
            throw new NoSuitableRendererException("No renderer available for this component:" + cl);
        }
        //now look for the actual factory
        RendererFactory rf = rfDomMap.get(domCl);
        //..if it can't be located, look for the interfaces it implements
        if (rf == null) {
            List list = Classes.getAllInterfaces(domCl);
            Iterator it = list.iterator();
            while (it.hasNext()) {
                Class clint = (Class) it.next();
                rf = rfDomMap.get(clint);
                if (rf != null) {
                    break;
                }
            }
            registered = false;
        }
        //..if we still can't find it, look for parent classes
        if (rf == null) {
            // fro_031207_1 - avoid NPE if superClass is null
            if (domCl.getSuperclass() != null) {
                rf = findRendererFactory(domCl.getSuperclass(), rfDomMap);
            }
            // fro_031207_1_end - avoid NPE if superClass is null
        }
        // fro_031207_1 Handle heritage on components and fix FindRenderer bug
        // If still no rf, try again on comp's superClass if there is any
        if (cl.getSuperclass() != null && rf == null) {
            rf = getRendererFactory(cl.getSuperclass(), domCl);
        }
        // fro_031207_1_end Handle heritage on components and fix FindRenderer bug
        // ok, give up now
        if (rf == null) {
            throw new NoSuitableRendererException("No renderer available for this markup:" + domCl);
        }

        //if the particular combination was not registered, register it now
        //so that the next time we look it up it'll be faster
        if (!registered) {
            installRendererFactory(rf, cl, domCl);
        }

        //return the renderer
        return rf;
    }

    /**
     * Look for a factory that can render either this class or its parent
     * class.
     */
    protected Map<Class, RendererFactory> findComponentMap(Class cl) {
        Map<Class, RendererFactory> rfDomMap = rfCompMap.get(cl);
        if (rfDomMap == null && cl.getSuperclass() != null) {
            rfDomMap = findComponentMap(cl.getSuperclass());
        }
        return rfDomMap;
    }

    /**
     * Look for a factory that can render either this class or its parent
     * class.
     */
    protected RendererFactory findRendererFactory(Class cl, Map domMap) {
        RendererFactory rf = (RendererFactory) domMap.get(cl);
        if (rf == null && cl.getSuperclass() != null) {
            rf = findRendererFactory(cl.getSuperclass(), domMap);
        }
        return rf;
    }

    //--------------- Lifecycle ----------------------------------
    /**
     * Initialize cycle. The component should use this 
     * to perform any initialization. Note that the component
     * should be added to the overall component hierarchy before 
     * you invoke this method.
     */
    public void initCycle() {
        //initialize self

        //initialize all children
        if (children == null) {
            children = new HashSequentialList<BContainer>();
            return;
        }
        Iterator it = children.iterator();
        while (it.hasNext()) {
            Object child = it.next();
            if (child instanceof AbstractBComponent) {
                ((AbstractBComponent) child).initCycle();
            }
        }
    }

    /**
     * Destroy cycle. The component should use this cycle to
     * perform any special cleanup.
     */
    public void destroyCycle() {
        //cleanup all children
        if (children != null) {
            for (BContainer child : children) {
                if (child instanceof AbstractBComponent) {
                    ((AbstractBComponent) child).destroyCycle();
                }
            }
        }

        removeAllStepChildren();
        removeAllViews();
        clearState();
    }

    //--------------- BContainer ---------------------------------
    /**
     * Set the parent container. Null indicates its the root.
     *
     * @param iparent the parent container
     */
    @Override
    @SuppressWarnings({"unchecked"})
    public <DesiredType extends BContainer> DesiredType setParent(BContainer iparent) {
        parent = iparent;
        return (DesiredType) this;
    }

    /**
     * Get the parent container. Returns null if it's the root.
     *
     * @return the parent container
     */
    @Override
    public BContainer getParent() {
        return parent;
    }

    /**
     * Add a child container to this one
     *
     * @param child the child container to be added
     */
    @Override
    @SuppressWarnings({"unchecked"})
    public <DesiredType extends BContainer> DesiredType addChild(BContainer child) {
        if ((child == null) || (children != null && children.contains(child))) {
            return (DesiredType) this;
        }
        if (children == null) {
            children = new HashSequentialList<BContainer>();
        }
        //the purpose of this logic here is to add the child into the list. 
        //...if the child IS a BScript, or the list is empty, just add it on
        int childrenSize = children.size();
        if ((child instanceof BScript) || childrenSize == 0) {
            children.add(child);

            //...otherwise, add the child at the end of the list, but BEFORE any BScripts
            //...(BScripts ALWAYS need to render after other components)
        } else {
            int idx = childrenSize - 1;
            for (; idx >= 0; idx--) {
                Object o = children.get(idx);
                if (!(o instanceof BScript)) {
                    break;
                }
            }
            children.add(idx + 1, child);
        }
        child.setParent(this);
        invalidate();
        return (DesiredType) this;
    }

    /**
     * Get a child container at a given index
     *
     * @param index the index of the child container
     */
    @Override
    public BContainer getChild(int index) {
        return (BContainer) children.get(index);
    }

    /**
     * Remove a child container from this one
     *
     * @param child the a child container to be removed
     * @return the child container that was removed
     */
    @Override
    public <DesiredType extends BContainer> DesiredType removeChild(BContainer child) {
        if (child == null)
            return null;
        return removeChild(children.indexOf(child));
    }

    /**
     * Remove a child container for a given index
     *
     * @param index of the child container to be removed
     * @return the child container that was removed
     */
    @Override
    @SuppressWarnings("unchecked")
    public <DesiredType extends BContainer> DesiredType removeChild(int index) {
        if (index < 0) {
            return null;
        }
        BContainer child = children.remove(index);
        child.setParent(null);
        invalidate();
        return (DesiredType) child;
    }

    /**
     * Remove all child containers from this one
     */
    @Override
    @SuppressWarnings("unchecked")
    public <DesiredType extends BContainer> DesiredType removeAll() {
        if (children != null) {
            children.clear();
        }
        invalidate();
        return (DesiredType) this;
    }

    /**
     * Determine whether this container has any children
     *
     * @return true if the container has child containers
     */
    @Override
    public boolean hasChildren() {
        return (children != null && children.size() > 0);
    }

    /**
     * Gets a list of all child BContainers. Note, this method 
     * returns a copy of the underlying child list
     *
     * @return a list of all child components.
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BContainer> List<T> getChildren() {
        if (children == null) {
            return new ArrayList<T>();
        } else {
            return (List<T>) new ArrayList(children);
        }
    }

    /**
     * Determine whether this component is a step child
     *
     * @return true if this component is a step child
     */
    public boolean isStepChild() {
        return isStepChild;
    }

    /**
     * Add a step child; step children are automatically removed after
     * each render. 

     * @param <SubType>     The type that should be returned to the user. This
     * should always be a parent type of this class or this classes type.
     * @param child         The element to be added.
     * @return 
     */
    @SuppressWarnings({"unchecked"})
    public <SubType extends AbstractBComponent> SubType addStepChild(BContainer child) {
        addStepChild(child, false);
        return (SubType) this;
    }

    /**
     * Add a step child; step children are automatically removed after
     * each render. 
     *
     * @param child the step child container to be added
     * @param inheritParentAttrs true if we want the step child to
     *      inherit the parent's settings for visibility/enabled
     */
    @SuppressWarnings({"unchecked"})
    public <UnknownSubType extends BContainer> UnknownSubType addStepChild(BContainer child, boolean inheritParentAttrs) {
        if (child == null) {
            return (UnknownSubType) this;
        }
        if (stepChildren != null && stepChildren.contains(child)) {
            return (UnknownSubType) this;
        }
        if (children != null && children.contains(child)) {
            return (UnknownSubType) this;
        }
        this.addChild(child);
        if (stepChildren == null) {
            stepChildren = new HashSet<BContainer>();
        }
        stepChildren.add(child);

        //csc_090501_start - the purpose of this fix is simple. When you add
        //a step child, its almost always because you're delegating behaviour
        //to a sub component. In these cases, that child's visibility/enabled
        //characteristics should generally match the parent (so if the parent
        //is disabled the child will be too).
        if (child instanceof BComponent) {
            BComponent chComp = (BComponent) child;
            chComp.isStepChild = true;
            if (inheritParentAttrs) {
                chComp.setVisible(this.isVisible());
                chComp.setEnabled(this.isEnabled());
            }
        }
        //csc_090501_finish

        //csc_041403.2 - added
        //if the child being added has no views, add in any views/temp views 
        //associated with the parent
        if (inheritParentAttrs && child instanceof BComponent) {
            BComponent bchild = (BComponent) child;
            if (!bchild.hasViews()) {
                for (View v : getViews()) {
                    bchild.addView(v);
                }
                if (tempViews != null) {
                    for (View v : tempViews) {
                        bchild.addTempView(v);
                    }
                }
            }
        }
        return (UnknownSubType) this;
    }

    /**
     * Remove all step children
     */
    public BComponent removeAllStepChildren() {
        if (stepChildren != null) {
            Iterator it = stepChildren.iterator();
            while (it.hasNext()) {
                BContainer tempChild = (BContainer) it.next();
                it.remove();
                //csc_030802.1 - if we are removing a temp child make sure we destroy it
                //so that memory can get freed up. This bug reared its ugly head when I
                //was using a BTemplateViewHandler that was declared as an inner class, and which
                //used a model which itself was declared as an inner class of the view handler.
                //After the BTemplate renders, when it goes to remove its step children, it
                //must invoke destroyCycle on those or else they retain a reference to the 
                //BTemplate which prevents it from getting gc'd which in turn prevents the 
                //handler from getting gc'd which in turn runs you out of memory real fast on a
                //big result set. Nasty nasty nasty!
                if (tempChild instanceof BComponent) {
                    ((BComponent) tempChild).destroyCycle();
                }
            }
        }
        stepChildren = null;
        return (BComponent) this;   //yes, this assumes BComponent is the only thing that ever extends AbstractBComponent
    }

    /**
     * Add a temporary view; temp views are automatically removed after
     * each render
     *
     * @param tview the temp view to be added
     */
    public BComponent addTempView(View tview) {
        //jrk_20021018.5 - added null check for views
        if ((tview == null)
                || (tempViews != null && tempViews.contains(tview))
                || (views != null && views.contains(tview))) {
            return (BComponent) this;
        }
        if (tempViews == null) {
            tempViews = new ArrayList<View>();
        }
        tempViews.add(tview);

        //csc_041403.2 - added
        //in addition, when we add a temp view, add it to any step children 
        //that do not have a view. This makes it MUCH easier for a template model
        //to return nested components none of which may have a view yet...when
        //TemplateHelper assigns a temp view, it will stick to step children as well
        if (stepChildren != null) {
            Iterator it = stepChildren.iterator();
            while (it.hasNext()) {
                BComponent tempChild = (BComponent) it.next();
                if (!tempChild.hasViews()) {
                    tempChild.addTempView(tview);
                }
            }
        }
        return (BComponent) this;
    }

    /**
     * Determine whether the component has any views (either regular or temp)
     *
     * @return true if the component has any views
     */
    public boolean hasViews() {
        return ((views != null && views.size() > 0)
                || (tempViews != null && tempViews.size() > 0));
    }

    /**
     * Invalidates the container and all parent containers above it.
     * This essentially indicates the components need to be laid out 
     * (in the server side world, this is equivalent to rendering)
     * again. After a component has been rendered, it will be marked 
     * valid again.
     */
    @Override
    @SuppressWarnings("unchecked")
    public <DesiredType extends BContainer> DesiredType invalidate() {
        if (validated) {
            validated = false;
            BContainer parentNode = getParent();
            while (parentNode != null) {
                parentNode.invalidate();
                parentNode = parentNode.getParent();
            }
        }
        return (DesiredType) this;
    }

    /**
     * Invalidates the container and all child containers below it.
     * Calling validate on the root of a tree effectively invalidates
     * the entire tree, forcing the whole tree to be revalidated the
     * next time it is rendered. This call is more expensive than an 
     * invalidate because it has to hit every node in the hierarchy.
     */
    @Override
    @SuppressWarnings("unchecked")
    public <DesiredType extends BContainer> DesiredType validate() {
        validated = false;
        for (BContainer child : children) {
            child.validate();
        }
        return (DesiredType) this;
    }

    //-------------------- StateMap ------------------------------
    /**
     * set a property in this StateMap
     *
     * @param key the state key object
     * @param val the state value object
     */
    @Override
    public void putState(Object key, Object val) {
        statemap.putState(key, val);
    }

    /**
     * get a property in this StateMap
     *
     * @param key the state key object
     * @return the value for the given key
     */
    @Override
    public <DesiredType extends Object> DesiredType getState(Object key) {
        return statemap.getState(key);
    }

    /**
     * remove a property in this StateMap
     *
     * @param key the key object
     * @return the object which was removed
     */
    @Override
    public Object removeState(Object key) {
        return statemap.removeState(key);
    }

    /**
     * get a keyset for this StateMap (whether or 
     * not the set is backed by the data store depends on 
     * the implementation)
     *
     * @return a Set of keys for this StateMap
     */
    @Override
    public Set getStateKeys() {
        return statemap.getStateKeys();
    }

    /**
     * get a copy of the underlying Map
     *
     * @return a copy of the underlying state Map
     */
    @Override
    public Map getStateStore() {
        return statemap.getStateStore();
    }

    //csc_052803_2 - added
    /**
     * clear all state information
     */
    @Override
    public void clearState() {
        statemap.clearState();
    }

    @Override
    public <DesiredType> DesiredType getState(Class<DesiredType> type, String key) {
        return getState(key);
    }

    //--------------- Attrs --------------------------------------
    //csc_072604_1 - moved here from BComponent
    /**
     * set an attribute for this particular component. When the component
     * is rendered, component attributes will be shown as element attributes
     * in the elements that back each of the views associated with this component.
     * This means that if you set an attribute for the component, it will 
     * affect all views associated with the component.If you wish to set an 
     * attribute for a specific view alone, then you should get the view, find
     * the node that backs it, and then set the attribute manually that way.
     *
     * @param attr the attribute name
     * @param val the attribute value
     */
    @Override
    public Attrs setAttr(Object attr, Object val) {
        if (attrs == null) {
            attrs = new TreeMap<Object, Object>();
        }
        attrs.put(attr, val);
        invalidate();
        return this;
    }

    //csc_072604_1 - moved here from BComponent
    /**
     * get an attribute associated with this particular component. Note that
     * the attribute map that backs this method only keeps tracks of specific
     * attributes you have added to the component. It does not look at attributes
     * that are physically associated with the underlying elements that back each
     * of the views associated with this component. What this means is that if
     * the template that backs a view has some attribute "foo" and you try to
     * see the value of that attribute using this method, you will not be able 
     * to find it unless you have actually associated an attribute named "foo" 
     * with the specific component.
     *
     * @param attr the attribute name
     * @return the value for the given attribute (may be null)
     */
    @Override
    public Object getAttr(Object attr) {
        if (attrs == null) {
            return null;
        }
        return attrs.get(attr);
    }

    //csc_072604_1 - moved here from BComponent
    /**
     * return a reference to the underlying component attribute Map
     *
     * @return a copy of the underlying component attribute Map
     */
    @Override
    public Map getAttrMap() {
        return attrs;
    }

    /**
     * Convinience method to append a class to the "class" attribute
     * @param cls the class to append
     * @author franck routier <alci@mecadu.org>
     */
    public void addClass(String cls) {
        Object att = getAttr("class");
        String classes;
        if (att != null) {
            // only add the class if it does not exist yet
            if (att.toString().contains(cls)) {
                classes = att.toString();
            } else {
                classes = att.toString() + " " + cls;
            }
        } // if no class was set yet, set the new class
        else {
            classes = cls;
        }
        setAttr("class", classes);
    }

    //--------------- Utility Methods ----------------------------
    /**
     * Get the component reference
     *
     * @return a String representation of the component reference
     */
    public String toRef() {
//don't need the fully qualified class name since logger provides this    
//        return this.getClass().getName()+"@"+Integer.toHexString(this.hashCode());
        return "[@" + Integer.toHexString(this.hashCode()) + "]";
    }

    /**
     * Get a String representation of the component
     *
     * @return a String representation of the component
     */
    @Override
    public String toString() {
        return toString(getDefaultViewContext());
    }

    /**
     * Provide a String representation of the component. This will attempt to 
     * render the link as markup, which allows you to inline components along 
     * with text (a very useful feature!). Here's a summary of how it works:
     *
     * -no vc: render as object signature
     * -everything else: render using DOM renderers
     *
     * @return a String representation of the component
     */
    public String toString(ViewContext vc) {
        if (vc == null) {
            String sname = getName();
            if (sname == null) {
                sname = this.getClass().getName();
            }
            int lpos = sname.lastIndexOf(".");
            if (lpos > -1) {
                sname = sname.substring(lpos);
            }
            return sname + " (@" + Integer.toHexString(this.hashCode()) + ")";
        }

        //get the format type and the doc from the view context
        FormatType ft = vc.getViewCapabilities().getFormatType();
//csc_072604_2        Document doc = vc.getElementFactory().getDocument();
        Document doc = vc.getDocument();    //csc_072604_2

        //get any views associated with this link and then remove 
        //all views from the component
        List linkViews = this.getViews();
        this.removeAllViews();

        try {
            //get the appropriate renderer by looking up the
            //DOM class associated with the given format type
            Renderer r = this.getRenderer(ft.getDOMClass());

//csc_110501.1_start -             
/*
             //ask the renderer to create the default Node
             Node n = r.createDefaultNode(doc, vc);
            
             //create a view on the resulting node and add it in
             this.addTempView(new DefaultView(n));
             */
            //ask the renderer to create the default Node (Note that technically
            //this is a dangerous cast, since it assumes that we are actually an 
            //instance of BComponent...in reality, its probably ok, since we are
            //really only using AbstractBComponent for code hiding...if anyone
            //extends Barracuda components, they should always do so by extending
            //from BComponent, NOT by extending from AbstractBComponent. Unfortunately,
            //there's no way to enforce this, but in practice it probably won't happen 
            //very often, if at all, and if it does, at least its well documented... ;-)
            Node n = r.createDefaultNode(doc, (BComponent) this, vc); //csc_110501.1

            //if the component still needs a default view create one for it...
            if (!this.hasViews()) {
                this.addTempView(new DefaultView(n));
            }
//csc_110501.1_end

            //invalidate the component to ensure redraw
            this.invalidate();

            //now render the component
            this.render(vc);

            //now render the node
            StringWriter sw = new StringWriter(200);
            DefaultDOMWriter ddw = new DefaultDOMWriter();
            ddw.write(n, sw);
            String linkStr = sw.toString();
            String endln = System.getProperty("line.separator");
            if (linkStr.endsWith(endln)) {
                linkStr = linkStr.substring(0, linkStr.length() - endln.length());
            }
            return linkStr;

        } catch (Exception e) {
            e.printStackTrace();
            return ("Err building component markup string: " + e);
        } finally {
            //add the views back in if need be (resetting component
            //to original state)
            if (linkViews != null) {
                Iterator it = linkViews.iterator();
                while (it.hasNext()) {
                    this.addView((View) it.next());
                    if (validated) {
                        this.invalidate();
                    }
                }
            }
        }
    }

    public void printStackTrace(int depth, Logger logger) {
        printStackTrace(depth, logger, null);
    }

    public void printStackTrace(int depth, OutputStream out) {
        printStackTrace(depth, null, out);
    }

    /**
     * For debugging purposes. Print the basic structure of the
     * gateway.
     */
    protected void printStackTrace(int depth, Logger logger, OutputStream out) {
        if (depth < 0) {
            depth = 0;
        }
        if (depth > 25) {
            depth = 25;
        }
        String spaces = "                                                                              ";
        String inset = spaces.substring(0, depth * 3);

        boolean stepchild = (parent != null
                && ((AbstractBComponent) parent).stepChildren != null
                && ((AbstractBComponent) parent).stepChildren.contains(this));
        print(logger, out, inset + this.getClass().getName() + "@" + Integer.toHexString(this.hashCode()) + (stepchild ? " (stepchild)" : ""));

        //children
        print(logger, out, inset + "   children: " + (children == null ? "null" : ""));
        int cntr = -1;
        if (children != null) {
            Iterator it = children.iterator();
            while (it.hasNext()) {
                BContainer cont = (BContainer) it.next();
                if (cont instanceof BComponent) {
                    print(logger, out, inset + "      [" + (++cntr) + "]:");
                    ((BComponent) cont).printStackTrace(depth + 2, logger, out);
                } else {
                    print(logger, out, inset + "      [" + (++cntr) + "]" + cont.getClass().getName() + " (details unknown)");
                }
            }
        }
        print(logger, out, inset + "   /end children");

        print(logger, out, inset + "/end @" + Integer.toHexString(this.hashCode()));
    }

    private static void print(Logger logger, OutputStream out, String s) {
        if (logger != null) {
            logger.debug(s);
        } else if (out != null) {
            try {
                out.write(s.getBytes());
                out.write(sep);
            } catch (IOException ioe) {
            }
        }
    }
}
