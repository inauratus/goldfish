/*
 * jsrsClientServerHTTPLib.js - javascript library to make make asynchronous
 * remote calls to server without client page refresh (based off Brent
 * Ashley's [jsrs@megahuge.com] jsrsClient.js, v.2.4).
 * See http://www.ashleyit.com/rs/jsrs/ for details.
 *
 * see jsrsLicense.txt for copyright and license information
 *
 * changes:
 * csc_033005_1 - Upgraded to jsrs 2.4. Note that this is still problematic w/ FF,
 *      because FF _always_ adds an entry to the browser history when a form submits,
 *      even if its submitted from an IFRAME. What this means is that back button
 *      disabling WILL NOT WORK in FF (and I doubt that this will change in the future).
 *      So, even though I've updated this class, I'm not sure that its going to do
 *      any good...
 *
 * csc_082001_1 - revamped to interact with the Barracuda ParamGateway servlet; the
 *      script submits a form to the servlet, which saves all the form values in
 *      session state, and then returns a url to the client (which can then do
 *      a location.replace on that url).
 */

// callback pool needs global scope
var jsrsContextPoolSize = 0;
var jsrsContextMaxPool = 10;
var jsrsContextPool = new Array();
var jsrsBrowser = jsrsBrowserSniff();
var jsrsPOST = true;
var containerName;

//--------------------- Public functions ----------------------------
/**
 * Used to submit a form without causing a screen refresh
 */
function bmvc_SubmitAndReplace(targetForm) {
    jsrsSubmit(targetForm, myCallback, false);
}

//--------------------- Utility functions ---------------------------
/**
 * callback function that receives the resulting redirect URL and replaces the
 * URL accordingly
 */
function myCallback(result) {
    location.replace(result+'?$csjs=true'); //&$u='+generateUniqueString());  //adding script stuff prevents an extra round trip to the server
    //location.replace(result);
}

// constructor for context object
function jsrsContextObj( contextID ){
  // properties
  this.id = contextID;
  this.busy = true;
  this.callback = null;
  this.container = contextCreateContainer( contextID );

  // methods
  this.GET = contextGET;
  this.POST = contextPOST;
  this.getPayload = contextGetPayload;
  this.setVisibility = contextSetVisibility;
}

//  method functions are not privately scoped
//  because Netscape's debugger chokes on private functions
function contextCreateContainer( containerName ){
  // creates hidden container to receive server data
  var container;
  switch( jsrsBrowser ) {
    case 'NS':
      container = new Layer(100);
      container.name = containerName;
      container.visibility = 'hidden';
      container.clip.width = 100;
      container.clip.height = 100;
      break;

    case 'IE':
      document.body.insertAdjacentHTML( "afterBegin", '<span id="SPAN' + containerName + '"></span>' );
      var span = document.all( "SPAN" + containerName );
      var html = '<iframe name="' + containerName + '" src=""></iframe>';
      span.innerHTML = html;
      span.style.display = 'none';
      container = window.frames[ containerName ];
      break;

    case 'MOZ':
      var span = document.createElement('SPAN');
      span.id = "SPAN" + containerName;
      document.body.appendChild( span );
      var iframe = document.createElement('IFRAME');
      iframe.name = containerName;
      iframe.id = containerName;
      span.appendChild( iframe );
      container = iframe;
      break;

    case 'OPR':
      var span = document.createElement('SPAN');
      span.id = "SPAN" + containerName;
      document.body.appendChild( span );
      var iframe = document.createElement('IFRAME');
      iframe.name = containerName;
      iframe.id = containerName;
      span.appendChild( iframe );
      container = iframe;
      break;

    case 'KONQ':
      var span = document.createElement('SPAN');
      span.id = "SPAN" + containerName;
      document.body.appendChild( span );
      var iframe = document.createElement('IFRAME');
      iframe.name = containerName;
      iframe.id = containerName;
      span.appendChild( iframe );
      container = iframe;

      // Needs to be hidden for Konqueror, otherwise it'll appear on the page
      span.style.display = none;
      iframe.style.display = none;
      iframe.style.visibility = hidden;
      iframe.height = 0;
      iframe.width = 0;

      break;
  }
  return container;
}

function contextPOST(sourceForm, vis) {
    //preliminary stuff
    var unique = generateUniqueString();
    var doc = window.frames[this.container.name].document; //formerly (jsrsBrowser == "IE" ) ? this.container.document : this.container.contentDocument;, but window.frames works for both
    var sep = "?";
    if (sourceForm.action.indexOf(sep)>-1) sep = "&amp;"; //formerly '&', but it is illegal in html...must use special character (jrk_120201.10:51)

    //start building the form
    doc.open();
    doc.writeln('<html><head><title></title></head><body>');
    doc.writeln('<form name="jsrsForm" method="post" action="'+sourceForm.action+sep+'U='+unique+'"><div>');
    doc.writeln('<input type="hidden" name="pm_cid" value="' + this.id + '">');

    // add in form's input elements
    for (var i=0; i<sourceForm.elements.length; i++) {
        var element = sourceForm.elements[i];

        //note that we skip buttons...this is because these are not really
        //needed for the post information
        if ((element.type=="submit") ||
            (element.type=="reset") ||
            (element.type=="button")) continue;

        //select controls
        if ((element.type=="select-one") || (element.type=="select-multiple")) {
            if (element.type=="select-one") doc.write('<select ');
            else doc.write('<select multiple ');
            doc.writeln('name="'+element.name+'">');

            var options = element.options;
            for (var j=0; j<options.length; j++) {
                var opt = options[j];
                doc.write('<option value="'+opt.value+'" ');
                if (opt.selected) doc.write('selected="selected" ');
                doc.writeln('>'+opt.text+'</option>');
            }
            doc.writeln('</select>');

        //everything else
        } else {
            doc.write('<input ');
            doc.write('type="'+element.type+'" ');
            doc.write('name="'+element.name+'" ');
            doc.write('value="'+element.value+'" ');
            if ((element.type=="checkbox") || (element.type=="radio")) {
                if (element.checked) doc.write('checked="checked" ');
            }
            doc.writeln('>');
        }
    }

    //finish up
    doc.writeln('<input type="submit" value="Finish Submit!" name="jsrsSubmit">');
    doc.writeln('</div></form>');
    doc.writeln('</body></html>');
    doc.close();
    if (!vis) doc.forms['jsrsForm'].submit();
}

function contextGET(sourceForm) {
    //preliminary stuff
    var unique = generateUniqueString();
    var sep = "?";
    if (sourceForm.action.indexOf(sep)>-1) sep = "&amp;"; //formerly '&', but it is illegal in html...must use special character (jrk_120201.10:51)
    var URL = sourceForm.action+sep+'U='+unique;

    //add in the context id
    URL += '&pm_cid='+this.id;

    // add in form's input elements
    for (var i=0; i<sourceForm.elements.length; i++) {
        var element = sourceForm.elements[i];

        //note that we skip buttons...this is because these are not really
        //needed for the post information
        if ((element.type=="submit") ||
            (element.type=="reset") ||
            (element.type=="button")) continue;

        //select controls
        if ((element.type=="select-one") || (element.type=="select-multiple")) {
            if (element.selectedIndex==-1) continue;
            var options = element.options;
            for (var j=0; j<options.length; j++) {
                var opt = options[j];
                if (opt.selected) URL += '&'+element.name+'='+opt.value;
            }

        //everything else
        } else if ((element.type=="checkbox") || (element.type=="radio")) {
            if (element.checked) URL += '&'+element.name+'='+element.value;
        } else {
            URL += '&'+element.name+'='+element.value;
        }
    }

  // make the call
  switch( jsrsBrowser ) {
    case 'NS':
      this.container.src = URL;
      break;
    case 'IE':
      this.container.document.location.replace(URL);
      break;
    case 'MOZ':
      this.container.src = '';
      this.container.src = URL;
      break;
    case 'OPR':
      this.container.src = '';
      this.container.src = URL;
      break;
    case 'KONQ':
      this.container.src = '';
      this.container.src = URL;
      break;
  }
}

function contextGetPayload(){
  switch( jsrsBrowser ) {
    case 'NS':
      return this.container.document.forms['jsrs_Form'].elements['jsrs_Payload'].value;
    case 'IE':
      return this.container.document.forms['jsrs_Form']['jsrs_Payload'].value;
    case 'MOZ':
      return window.frames[this.container.name].document.forms['jsrs_Form']['jsrs_Payload'].value;
    case 'OPR':
      var textElement = window.frames[this.container.name].document.getElementById("jsrs_Payload");
    case 'KONQ':
      var textElement = window.frames[this.container.name].document.getElementById("jsrs_Payload");
      return textElement.value;
  }
}

function contextSetVisibility( vis ){
  switch( jsrsBrowser ) {
    case 'NS':
      this.container.visibility = (vis)? 'show' : 'hidden';
      break;
    case 'IE':
      document.all("SPAN" + this.id ).style.display = (vis)? '' : 'none';
      break;
    case 'MOZ':
      document.getElementById("SPAN" + this.id).style.visibility = (vis)? '' : 'hidden';
    case 'OPR':
      document.getElementById("SPAN" + this.id).style.visibility = (vis)? '' : 'hidden';
      this.container.width = (vis)? 250 : 0;
      this.container.height = (vis)? 100 : 0;
      break;
  }
}
// end of context constructor

function jsrsGetContextID(){
  var contextObj;
  for (var i = 1; i <= jsrsContextPoolSize; i++){
    contextObj = jsrsContextPool[ 'jsrs' + i ];
    if ( !contextObj.busy ){
      contextObj.busy = true;
      return contextObj.id;
    }
  }
  // if we got here, there are no existing free contexts
  if ( jsrsContextPoolSize <= jsrsContextMaxPool ){
    // create new context
    var contextID = "jsrs" + (jsrsContextPoolSize + 1);
    jsrsContextPool[ contextID ] = new jsrsContextObj( contextID );
    jsrsContextPoolSize++;
    return contextID;
  } else {
    alert( "jsrs Error:  context pool full" );
    return null;
  }
}

function jsrsGetContextID(){
  var contextObj;
  for (var i = 1; i <= jsrsContextPoolSize; i++){
    contextObj = jsrsContextPool[ 'jsrs' + i ];
    if ( !contextObj.busy ){
      contextObj.busy = true;
      return contextObj.id;
    }
  }
  // if we got here, there are no existing free contexts
  if ( jsrsContextPoolSize <= jsrsContextMaxPool ){
    // create new context
    var contextID = "jsrs" + (jsrsContextPoolSize + 1);
    jsrsContextPool[ contextID ] = new jsrsContextObj( contextID );
    jsrsContextPoolSize++;
    return contextID;
  } else {
    alert( "jsrs Error:  context pool full" );
    return null;
  }
}

function jsrsSubmit(sourceForm, callback, visibility) {
 // submit a form to the server from client code without causing a new page
 // to be loaded
 //
 // sourceForm  - the sourceForm
 // callback    - function to call on return
 //               or null if no return needed
 //               (passes returned string to callback)
 // visibility  - optional boolean to make container visible for debugging

  // get context
  var contextObj = jsrsContextPool[ jsrsGetContextID() ];
  contextObj.callback = callback;

  var vis = (visibility == null)? false : visibility;
  contextObj.setVisibility( vis );


  if ( jsrsPOST && ((jsrsBrowser == 'IE') || (jsrsBrowser == 'MOZ'))){
    contextObj.POST(sourceForm, vis);
  } else {
    contextObj.GET(sourceForm);
  }

  return contextObj.id;
}

function jsrsLoaded( contextID ){
  // get context object and invoke callback
  var contextObj = jsrsContextPool[ contextID ];
  if( contextObj.callback != null){
    contextObj.callback( jsrsUnescape( contextObj.getPayload() ), contextID );
  }
  // clean up and return context to pool
  contextObj.callback = null;
  contextObj.busy = false;
}

function jsrsError( contextID, str ){
  alert( unescape(str) );
  jsrsContextPool[ contextID ].busy = false
}

function jsrsEscapeQQ( thing ){
  return thing.replace(/'"'/g, '\\"');
}

function jsrsUnescape( str ){
  // payload has slashes escaped with whacks
  return str.replace( /\\\//g, "/" );
}

function jsrsBrowserSniff(){
  if (document.layers) return "NS";
  if (document.all) {
        // But is it really IE?
        // convert all characters to lowercase to simplify testing
        var agt=navigator.userAgent.toLowerCase();
        var is_opera = (agt.indexOf("opera") != -1);
        var is_konq = (agt.indexOf("konqueror") != -1);
        if(is_opera) {
            return "OPR";
        } else {
            if(is_konq) {
                return "KONQ";
            } else {
                // Really is IE
                return "IE";
            }
        }
  }
  if (document.getElementById) return "MOZ";
  return "OTHER";
}

/////////////////////////////////////////////////
//
// user functions

function jsrsArrayFromString( s, delim ){
  // rebuild an array returned from server as string
  // optional delimiter defaults to ~
  var d = (delim == null)? '~' : delim;
  return s.split(d);
}

function jsrsDebugInfo(){
  // use for debugging by attaching to f1 (works with IE)
  // with onHelp = "return jsrsDebugInfo();" in the body tag
  var doc = window.open().document;
  doc.open;
  doc.write('<div id="DebugInfo">Pool Size: ' + jsrsContextPoolSize + '<div style="font:bold .8em Arial,Helvetica,sans-serif;">');
  for( var i in jsrsContextPool ){
    var contextObj = jsrsContextPool[i];
    doc.write('<hr><div>' + contextObj.id + ' : ' + (contextObj.busy ? 'busy' : 'available') + '</div>');
    doc.write('<div>' + contextObj.container.document.location.pathname + '</div>');
    doc.write('<div>' + contextObj.container.document.location.search + '</div>');
    doc.write('<div style="border:1px solid black;">' + contextObj.container.document.body.innerHTML + '</div>');
  }
  doc.write('</div></div>');
  doc.close();
  return false;
}

function generateUniqueString() {
    var d = new Date();
    return d.getTime() + '' + Math.floor(1000 * Math.random());
}
