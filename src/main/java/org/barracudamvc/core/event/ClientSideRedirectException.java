package org.barracudamvc.core.event;


/**
 * This class defines a ClientSideRedirectException...throwing this will
 * cause the ApplicationGateway to redirect the browser to the
 * new Event.
 */
public class ClientSideRedirectException extends EventException {

    private String url = null;
    private boolean preventPersistContext = false;

    /**
     * The public contructor for ClientSideRedirectException
     *
     * @param newEvent - the newEvent the client browser
     *                 should be redirected to
     */
    public ClientSideRedirectException(BaseEvent newEvent) {
        setRedirectURL(newEvent.getEventURL());
    }

    /**
     * The public contructor for ClientSideRedirectException
     *
     * @param iurl - the URL the client browser should be
     *             redirected to
     */
    public ClientSideRedirectException(String iurl) {
        setRedirectURL(iurl);
    }

    /**
     * Get the new event that triggered this interrupt
     */
    public String getRedirectURL() {
        return url;
    }

    /**
     * Set the Redirect URL (normally you don't need to
     * do this since you simply pass the target URL into the
     * constructor. If, howver, you find the need to modify a
     * redirect after its already been created, you can do it
     * through this method)
     */
    public void setRedirectURL(String iurl) {
        url = iurl;
    }

    public boolean isPreventPersistContext() {
        return preventPersistContext;
    }

    public ClientSideRedirectException preventPersistContext() {
        this.preventPersistContext = true;
        return this;
    }

    public String toString() {
        return this.getClass().getName() + "(url=" + url + ")";
    }
}
