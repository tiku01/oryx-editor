/**
 * Copyright (c) 2008 Tobias Pfeiffer
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
if (!ORYX.Plugins) {
	ORYX.Plugins = new Object();
}

ORYX.Plugins.UMLState = Clazz
		.extend({

			facade : undefined,

			construct : function(facade) {

				this.facade = facade;

				// We raise an Event, that registers our templating functions in
				// renameShape.js
				// the edit template function takes the value of a property and
				// changes it so the
				// user sees something different to edit
				// the render template function takes the input the user
				// submitted and reformats
				// it in the way it should be saved as a property
				// So for more information on what this does exactly refer to
				// renameShape.js

				this.facade.raiseEvent({
					type : ORYX.CONFIG.EVENT_REGISTER_LABEL_TEMPLATE,
					edit_template : this.templatizeValue.bind(this),
					render_template : this.untemplatizeValue.bind(this)
				});
			},
			/**
			 * Templatize value is the funcation that is called, when a user
			 * starts editing a field. For UML State this function decides which
			 * template to use based on the element which is being edited. If it
			 * is neither the controlflow edge nor the state with actions,
			 * nothing is done.
			 * 
			 * templatize value expects the oldValue to be in format of the
			 * template... if not hell may break loose. I'm serious.
			 * 
			 * We need the propId parameter in order to differentiate between
			 * the 2 textfiels of the state with actions
			 */
			templatizeValue : function templatizeValue(oldValue, propId, shape) {

				var stencilID = shape._stencil.id();
				// It is the edge (controlflow)
				if (stencilID === "http://b3mn.org/stencilset/umlstate#controlFlow") {
					return this.templatizeEdgeValue(oldValue);
				}
				// Make sure it's the state with actions and it is the textfield
				// actions, not the the textfiel name (name has no template)
				else if ((stencilID === "http://b3mn.org/stencilset/umlstate#stateWithActions")
						&& (propId.indexOf("actions") !== -1)) {
					return this.templatizeStateWithActionsValue(oldValue);
				}
				// Otherweise nothing special to do, so the old value gets
				// returned
				else {
					return oldValue;
				}
			},

			/**
			 * templatizes the old value an edge has. The template looks as
			 * follows:
			 * 
			 * Event [Guard] /Action
			 * 
			 * so if the old value is: "Rain" it considers this as an event and
			 * returns:
			 * 
			 * Rain [Guard] /Action
			 */
			templatizeEdgeValue : function templatizeEdgeValue(oldValue) {
				// optimization: just query the strings once for the relevant
				// indexes
				var indexOfBracket = oldValue.indexOf("[");
				var indexOfSlash = oldValue.indexOf("/");
				// following are all the different possible cases,
				// the comment indicates which case the if construct should
				// catch

				// Case of: blank field ""
				if (oldValue === "") {
					return "Event [Guard] /Action";
				}
				// Case of: Event
				if ((indexOfBracket === -1) && (indexOfSlash === -1)) {
					return oldValue + " [Guard] /Action";
				}
				// Case of: [Guard]
				if ((indexOfBracket === 0) && (indexOfSlash === -1)) {
					return "Event " + oldValue + " /Action";
				}
				// Case of: /Action
				if (indexOfSlash === 0) {
					return "Event [Guard] " + oldValue;
				}
				// Case of: Event [Guard]
				if ((indexOfBracket !== -1) && (indexOfSlash === -1)) {
					return oldValue + " /Action";
				}
				// Case of: Event /Action
				if ((indexOfBracket === -1) && (indexOfSlash > 0)) {
					var splitter = oldValue.split("/");
					return splitter[0] + "[Guard] /" + splitter[1];
				}
				// Case of:[Guard] /Action
				if ((indexOfBracket === 0) && (indexOfSlash !== -1)) {
					return "Event " + oldValue;
				}

				// Case of: Event [Guard] /Action
				// TODO: it may start with whitespaces? (may count for other
				// too)
				if ((indexOfBracket > 0) && (indexOfSlash > 0)) {
					return oldValue;
				}

				// We didn't return so far: WTF happened?
				// TODO: make this a better output, oldvalue or Event [Guard]
				// /Action are most suitable I guess. More pro oldValue
				return "Roflcoptaaaaaa";
			},
			/**
			 * This function templatizes the actions of the state with actions
			 * in the scheme of: entry / action do / action exit / action
			 */
			templatizeStateWithActionsValue : function templatizeStateWithActionsValue(
					oldValue) {
				// performance optimization, save the values of the indexes so
				// we don't need to query them every time
				var indexOfEntry = oldValue.indexOf("entry /");
				var indexOfDo = oldValue.indexOf("do /");
				var indexOfExit = oldValue.indexOf("exit /");

				// \n is used as a line end character because \r\n was causing
				// trouble in Firefox although I was using windows.

				// standardize the ending of the oldValue, with \n that is. (The
				// rest is simpler that way)
				if (oldValue.charAt(oldValue.length - 1) !== "\n") {
					oldValue = oldValue + "\n";
				}

				// you see, here we match against \n because we add it above.
				// (but it really is the case of an empty value)
				if (oldValue === "\n") {
					return "entry / action\ndo / action\nexit / action";
				}
				// entry / action
				if ((indexOfEntry === 0) && (indexOfDo === -1)
						&& (indexOfExit === -1)) {
					return oldValue + "do / action\nexit / action";
				}
				// do / action
				if ((indexOfEntry === -1) && (indexOfDo === 0)
						&& (indexOfExit === -1)) {
					return "entry / action\n" + oldValue + "exit / action";
				}
				// exit / action
				if ((indexOfEntry === -1) && (indexOfDo === -1)
						&& (indexOfExit === 0)) {
					return "entry / action\ndo / action\n" + oldValue;
				}
				// entry / action
				// do / action
				if ((indexOfEntry === 0) && (indexOfDo !== -1)
						&& (indexOfExit === -1)) {
					return oldValue + "exit / action";
				}
				// entry / action
				// exit / action
				if ((indexOfEntry === 0) && (indexOfDo === -1)
						&& (indexOfExit !== -1)) {
					return oldValue.slice(0, indexOfExit) + "do / action\n"
							+ oldValue.slice(indexOfExit);
				}
				// do / action
				// exit / action
				if ((indexOfEntry === -1) && (indexOfDo === 0)
						&& (indexOfExit !== -1)) {
					return "entry / action\n" + oldValue;
				}
				// the whole bunch
				// entry / action
				// do / action
				// exit / action
				if ((indexOfEntry === 0) && (indexOfDo !== -1)
						&& (indexOfExit !== -1)) {
					return oldValue;
				}

				// If we got this far something went wrong
				// TODO: replace with old value or the whole template
				return "aye what is this?\nYou seem a little bit off";
			},

			/**
			 * untemplatize value is called when the user is done editing a
			 * property now we want to remove the template and only keep the
			 * part the user changed. This particular function just decides
			 * which more specific method to call based on the shape which is
			 * currently edited.
			 */
			untemplatizeValue : function untemplatizeValue(newValue, propId,
					shape) {

				var stencilID = shape._stencil.id();
				// It is the edge (controlflow)
				if (stencilID === "http://b3mn.org/stencilset/umlstate#controlFlow") {
					return this.untemplatizeEdgeValue(newValue);
				}
				// Make sure it's the state with actions and it is the textfield
				// actions, not the the textfiel name (there is no template for
				// name)
				else if ((stencilID === "http://b3mn.org/stencilset/umlstate#stateWithActions")
						&& (propId.indexOf("actions") !== -1)) {
					return this.untemplatizeStateWithActionsValue(newValue);
				}
				// Otherweise nothing special to do (as we don't want to affect
				// every stencil)
				else {
					return newValue;
				}
			},

			/**
			 * The intent of this function is that if the user does something
			 * like this one a controlFlow edge:
			 * 
			 * Event [x >= 5] /Action
			 * 
			 * what gets saved and displayed is
			 * 
			 * [x >= 5]
			 * 
			 * Therefore we'll try to delete any occurences of Event, [Guard]
			 * and /Action
			 */
			untemplatizeEdgeValue : function untemplatizeEdgeValue(value) {
				// Quiet a chain, I love message chaining.
				// we actually don't replace but replace the occurance with an
				// empty string
				var newValue = value.replace("Event ", "").replace("[Guard]",
						"").replace(" /Action", "");
				return newValue;
			},

			/**
			 * The intent of this function is that if the user does something
			 * like this one a state with actions edge:
			 * 
			 * entry / action do / write JavaScript exit / action
			 * 
			 * what gets saved and displayed is
			 * 
			 * do / write JavaScript
			 * 
			 * Therefore we'll try to delete any occurences of entry / action,
			 * do / action\n and exit / action
			 */
			untemplatizeStateWithActionsValue : function untemplatizeStateWithActionsValue(
					value) {
				// the \n at the end is wanted because otherwise it would remain
				// and things would look ugly
				// moreover a user couldn't do something like entry / action go
				// to (or something like this)
				var newValue = value.replace("entry / action\n", "").replace(
						"do / action\n", "").replace("exit / action", "");
				return newValue;
			}
		});