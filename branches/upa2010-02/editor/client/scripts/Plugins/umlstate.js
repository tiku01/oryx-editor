// most badass copy&paste action evveeeeerrrr! I am really sorry for it but right now I see no other way...
/**
 * Copyright (c) 2008
 * Tobias Pfeiffer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 **/
if (!ORYX.Plugins) 
    ORYX.Plugins = new Object();

ORYX.Plugins.UMLState = Clazz.extend({

 facade: undefined,
 
 construct: function(facade){
 
     this.facade = facade;
   	
	// dokumentieren
	this.facade.raiseEvent({
		type: ORYX.CONFIG.EVENT_REGISTER_LABEL_TEMPLATE,
		edit_template: this.templatizeValue.bind(this),
		render_template: this.untemplatizeValue.bind(this)
	});
 },
	   
    /**
     * starting here come my very own functions for the templating
     * 
     */
    /**
     * templatize value expects the oldValue to be in format of the template... if not hell may break loose.
     * I'm serious. 
     * 
     * We need the propId parameter in order to differentiate between the 2 textfiels of the state with actions
     */
    templatizeValue : function templatizeValue(oldValue, propId, shape){
    	
    	
    	ORYX.Log.info("templatizeValue", arguments);
    	
    	var stencilID = shape._stencil.id();
    	//var oldValue = shape.properties[propId];
    	// It is the edge (controlflow)
    	if (stencilID == "http://b3mn.org/stencilset/umlstate#controlFlow") {
    		return this.templatizeEdgeValue(oldValue);
    	}
    	// Make sure it's the state with actions and it is the textfield actions, not the the textfiel name
    	else if ((stencilID == "http://b3mn.org/stencilset/umlstate#stateWithActions") && (propId.indexOf("actions") != -1)) {
    		return this.templatizeStateWithActionsValue(oldValue);	
    	}
    	// Otherweise nothing special to do
    	else{	
    		return oldValue;
    	}
    },
    
    templatizeEdgeValue : function templatizeEdgeValue(oldValue){
    	
    	
    	
    	
    	// the matching is done by a couple of ifs rather than a regex since I don't know
    	// how to match on just "Event" and not "Event [Guard]"
    	// optimization: just query the strings once
    	var indexOfBracket = oldValue.indexOf("[");
    	var indexOfSlash = oldValue.indexOf("/");
    	// Case of: blank field ""
    	if (oldValue == "") {
    		return "Event [Guard] /Action";
    	}
    	// Case of: Event
    	if ((indexOfBracket == -1) && (indexOfSlash == -1)) {
    		return oldValue+" [Guard] /Action";
    	}
    	// Case of: [Guard]
    	if ((indexOfBracket == 0) && (indexOfSlash == -1)) {
    		return "Event "+oldValue+" /Action";
    	}
    	// Case of: /Action
    	if (indexOfSlash == 0) {
    		return "Event [Guard] "+oldValue;
    	}
    	// Case of: Event [Guard] 
    	if ((indexOfBracket != -1) && (indexOfSlash == -1)) {
    		return oldValue + " /Action";
    	}
    	// Case of: Event /Action
    	if ((indexOfBracket == -1) && (indexOfSlash > 0)) {
    		var splitter = oldValue.split("/");
    		return splitter[0] + "[Guard] /"+splitter[1];
    	}
    	// Case of:[Guard] /Action
    	if ((indexOfBracket == 0) && (indexOfSlash  != -1)) {
    		return "Event " + oldValue;
    	}
    	
    	// Case of: Event [Guard] /Action
    	// TODO: it may start with whitespaces? (may coutn for other too)
    	if ((indexOfBracket > 0) && (indexOfSlash  > 0)) {
    		return oldValue;
    	}
    	
    	// We didn't return so far: WTF happened?
    	return "Roflcoptaaaaaa";
    },
    /**
     * This thing templatizes the actions of the state with actions in the scheme of:
     * entry / action
     * do / action
     * exit / action
     */
    templatizeStateWithActionsValue : function templatizeStateWithActionsValue(oldValue){
    	// performance optimization, save the values of the indexes
    	var indexOfEntry = oldValue.indexOf("entry /");
    	var indexOfDo = oldValue.indexOf("do /");
    	var indexOfExit = oldValue.indexOf("exit /");
    	
    	// standardize the ending of the oldValue, with \n that is.
    	if (oldValue.charAt(oldValue.length -1) != "\n"){
    		oldValue = oldValue + "\n"
    	}
    	
    	// \n is used throughout cause this seems to be what my firefox is using.. though I'm working on Windows
    	// you see, here we match against \n because we add it above.
    	if (oldValue == "\n"){
    		return "entry / action\ndo / action\nexit / action";
    	}
    	// entry / action
    	if ((indexOfEntry == 0) && (indexOfDo == -1) && (indexOfExit == -1)){
    		return oldValue + "do / action\nexit / action";
    	}
    	// do / action
    	if ((indexOfEntry == -1) && (indexOfDo == 0) && (indexOfExit == -1)){
    		return "entry / action\n"+ oldValue + "exit / action";
    	}
    	// exit / action
    	if ((indexOfEntry == -1) && (indexOfDo == -1) && (indexOfExit == 0)){
    		return "entry / action\ndo / action\n"+ oldValue;
    	}
    	// entry / action
    	// do / action
    	if ((indexOfEntry == 0) && (indexOfDo != -1) && (indexOfExit == -1)){
    		return oldValue +"exit / action";
    	}
    	// entry / action
    	// exit / action
    	if ((indexOfEntry == 0) && (indexOfDo == -1) && (indexOfExit != -1)){
    		return oldValue.slice(0, indexOfExit) + "do / action\n" + oldValue.slice(indexOfExit);
    	}
    	// do / action
    	// exit / action
    	if ((indexOfEntry == -1) && (indexOfDo == 0) && (indexOfExit != -1)){
    		return "entry / action\n" + oldValue;
    	}
    	// the whole bunch
    	// entry / action
    	// do / action
    	// exit / action
    	if ((indexOfEntry == 0) && (indexOfDo != -1) && (indexOfExit != -1)){
    		return oldValue;
    	}
    	
    	// If we got this far something went wrong
    	return "aye what is this?\nYou seem a little bit off";
    },
    
    untemplatizeValue : function untemplatizeValue(newValue, propId, shape){
    	
    	ORYX.Log.info("untemplatizeValue", arguments);
    	
    	var stencilID = shape._stencil.id();
    	// It is the edge (controlflow)
    	if (stencilID == "http://b3mn.org/stencilset/umlstate#controlFlow") {
    		return this.untemplatizeEdgeValue(newValue);
    	}
    	// Make sure it's the state with actions and it is the textfield actions, not the the textfiel name
    	else if ((stencilID == "http://b3mn.org/stencilset/umlstate#stateWithActions") && (propId.indexOf("actions") != -1)) {
    		return this.untemplatizeStateWithActionsValue(newValue);	
    	}
    	// Otherweise nothing special to do (as we don't want to affect every stencil)
    	else{	
    		return newValue;
    	}
    },
    
    /**
     * The intent of this function is that if the user does something like this:
     * 
     * Event [x >= 5] /Action
     * 
     * what gets saved and displayed is
     * 
     * [x >= 5]
     * 
     * Therefore we'll try to delete any occurences of Event, [Guard] and /Action
     */
    untemplatizeEdgeValue: function untemplatizeEdgeValue(value){
    	// Quiet a chain, I love message chaining.
    	var newValue = value.replace("Event ", "").replace("[Guard]", "").replace(" /Action", "");
    	return newValue;
    },
    
    untemplatizeStateWithActionsValue: function untemplatizeStateWithActionsValue(value){
    	var newValue = value.replace("entry / action\n", "").replace("do / action\n", "").replace("exit / action", "");
    	return newValue;
    }
});