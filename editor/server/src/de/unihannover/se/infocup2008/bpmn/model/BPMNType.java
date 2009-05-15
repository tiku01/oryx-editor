/**
 * Copyright (c) 2009
 * Ingo Kitzmann, Christoph Koenig
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
package de.unihannover.se.infocup2008.bpmn.model;

/**
 * This class holds all constants from the oryx erdf and provides some methods
 * to check types.
 * 
 * @author Team Royal Fawn
 * 
 */
public class BPMNType {
	// Canvas
	public final static String BPMNDiagram = "http://b3mn.org/stencilset/bpmn1.1#BPMNDiagram";

	public static boolean isADiagram(String type) {
		return type.equals(BPMNDiagram);
	}

	// Activities
	public final static String Task = "http://b3mn.org/stencilset/bpmn1.1#Task";
	public final static String CollapsedSubprocess = "http://b3mn.org/stencilset/bpmn1.1#CollapsedSubprocess";
	public final static String Subprocess = "http://b3mn.org/stencilset/bpmn1.1#Subprocess";

	public static boolean isAActivity(String type) {
		return type.equals(Task) || type.equals(CollapsedSubprocess)
				|| type.equals(Subprocess);
	}

	// Artifacts
	public final static String Group = "http://b3mn.org/stencilset/bpmn1.1#Group";
	public final static String TextAnnotation = "http://b3mn.org/stencilset/bpmn1.1#TextAnnotation";
	public final static String DataObject = "http://b3mn.org/stencilset/bpmn1.1#DataObject";

	public static boolean isAArtifact(String type) {
		return type.equals(Group) || type.equals(TextAnnotation)
				|| type.equals(DataObject);
	}

	// Oryx Throwing Intermediate Events
	public final static String IntermediateMessageEventThrowing = "http://b3mn.org/stencilset/bpmn1.1#IntermediateMessageEventThrowing";
	public final static String IntermediateSignalEventThrowing = "http://b3mn.org/stencilset/bpmn1.1#IntermediateSignalEventThrowing";
	public final static String IntermediateLinkEventThrowing = "http://b3mn.org/stencilset/bpmn1.1#IntermediateLinkEventThrowing";
	public final static String IntermediateMultipleEventThrowing = "http://b3mn.org/stencilset/bpmn1.1#IntermediateMultipleEventThrowing";
	public final static String IntermediateCompensationEventThrowing = "http://b3mn.org/stencilset/bpmn1.1#IntermediateCompensationEventThrowing";

	public static boolean isAThrowingIntermediateEvent(String type) {
		return type.equals(IntermediateMessageEventThrowing)
				|| type.equals(IntermediateSignalEventThrowing)
				|| type.equals(IntermediateLinkEventThrowing)
				|| type.equals(IntermediateMultipleEventThrowing)
				|| type.equals(IntermediateCompensationEventThrowing);
	}

	// Catching Intermediate Events
	public final static String IntermediateEvent = "http://b3mn.org/stencilset/bpmn1.1#IntermediateEvent";
	public final static String IntermediateMessageEventCatching = "http://b3mn.org/stencilset/bpmn1.1#IntermediateMessageEventCatching";
	public final static String IntermediateTimerEvent = "http://b3mn.org/stencilset/bpmn1.1#IntermediateTimerEvent";
	public final static String IntermediateErrorEvent = "http://b3mn.org/stencilset/bpmn1.1#IntermediateErrorEvent";
	public final static String IntermediateCancelEvent = "http://b3mn.org/stencilset/bpmn1.1#IntermediateCancelEvent";
	public final static String IntermediateCompensationEventCatching = "http://b3mn.org/stencilset/bpmn1.1#IntermediateCompensationEventCatching";
	public final static String IntermediateConditionalEvent = "http://b3mn.org/stencilset/bpmn1.1#IntermediateConditionalEvent";
	public final static String IntermediateSignalEventCatching = "http://b3mn.org/stencilset/bpmn1.1#IntermediateSignalEventCatching";
	public final static String IntermediateMultipleEventCatching = "http://b3mn.org/stencilset/bpmn1.1#IntermediateMultipleEventCatching";
	public final static String IntermediateLinkEventCatching = "http://b3mn.org/stencilset/bpmn1.1#IntermediateLinkEventCatching";

	public static boolean isACatchingIntermediateEvent(String type) {
		return type.equals(IntermediateEvent)
				|| type.equals(IntermediateMessageEventCatching)
				|| type.equals(IntermediateTimerEvent)
				|| type.equals(IntermediateErrorEvent)
				|| type.equals(IntermediateCancelEvent)
				|| type.equals(IntermediateCompensationEventCatching)
				|| type.equals(IntermediateConditionalEvent)
				|| type.equals(IntermediateSignalEventCatching)
				|| type.equals(IntermediateMultipleEventCatching)
				|| type.equals(IntermediateLinkEventCatching);
	}

	// Connecting Elements
	public final static String SequenceFlow = "http://b3mn.org/stencilset/bpmn1.1#SequenceFlow";
	public final static String MessageFlow = "http://b3mn.org/stencilset/bpmn1.1#MessageFlow";
	public final static String Association_Undirected = "http://b3mn.org/stencilset/bpmn1.1#Association_Undirected";
	public final static String Association_Unidirectional = "http://b3mn.org/stencilset/bpmn1.1#Association_Unidirectional";
	public final static String Association_Bidirectional = "http://b3mn.org/stencilset/bpmn1.1#Association_Bidirectional";

	public static boolean isAConnectingElement(String type) {
		return type.equals(SequenceFlow) || type.equals(MessageFlow)
				|| type.equals(Association_Undirected)
				|| type.equals(Association_Unidirectional)
				|| type.equals(Association_Bidirectional);
	}

	// End Events
	public final static String EndEvent = "http://b3mn.org/stencilset/bpmn1.1#EndEvent";
	public final static String EndMessageEvent = "http://b3mn.org/stencilset/bpmn1.1#EndMessageEvent";
	public final static String EndErrorEvent = "http://b3mn.org/stencilset/bpmn1.1#EndErrorEvent";
	public final static String EndCancelEvent = "http://b3mn.org/stencilset/bpmn1.1#EndCancelEvent";
	public final static String EndCompensationEvent = "http://b3mn.org/stencilset/bpmn1.1#EndCompensationEvent";
	public final static String EndSignalEvent = "http://b3mn.org/stencilset/bpmn1.1#EndSignalEvent";
	public final static String EndMultipleEvent = "http://b3mn.org/stencilset/bpmn1.1#EndMultipleEvent";
	public final static String EndTerminateEvent = "http://b3mn.org/stencilset/bpmn1.1#EndTerminateEvent";

	public static boolean isAEndEvent(String type) {
		return type.equals(EndEvent) || type.equals(EndMessageEvent)
				|| type.equals(EndErrorEvent) || type.equals(EndCancelEvent)
				|| type.equals(EndCompensationEvent)
				|| type.equals(EndSignalEvent) || type.equals(EndMultipleEvent)
				|| type.equals(EndTerminateEvent);
	}

	// GateWays
	public final static String Exclusive_Databased_Gateway = "http://b3mn.org/stencilset/bpmn1.1#Exclusive_Databased_Gateway";
	public final static String Exclusive_Eventbased_Gateway = "http://b3mn.org/stencilset/bpmn1.1#Exclusive_Eventbased_Gateway";
	public final static String AND_Gateway = "http://b3mn.org/stencilset/bpmn1.1#AND_Gateway";
	public final static String OR_Gateway = "http://b3mn.org/stencilset/bpmn1.1#OR_Gateway";
	public final static String Complex_Gateway = "http://b3mn.org/stencilset/bpmn1.1#Complex_Gateway";

	public static boolean isAGateWay(String type) {
		return type.equals(Exclusive_Databased_Gateway)
				|| type.equals(Exclusive_Eventbased_Gateway)
				|| type.equals(AND_Gateway) || type.equals(OR_Gateway)
				|| type.equals(Complex_Gateway);
	}

	// Start Events
	public final static String StartEvent = "http://b3mn.org/stencilset/bpmn1.1#StartEvent";
	public final static String StartMessageEvent = "http://b3mn.org/stencilset/bpmn1.1#StartMessageEvent";
	public final static String StartTimerEvent = "http://b3mn.org/stencilset/bpmn1.1#StartTimerEvent";
	public final static String StartConditionalEvent = "http://b3mn.org/stencilset/bpmn1.1#StartConditionalEvent";
	public final static String StartSignalEvent = "http://b3mn.org/stencilset/bpmn1.1#StartSignalEvent";
	public final static String StartMultipleEvent = "http://b3mn.org/stencilset/bpmn1.1#StartMultipleEvent";

	public static boolean isAStartEvent(String type) {
		return type.equals(StartEvent) || type.equals(StartMessageEvent)
				|| type.equals(StartTimerEvent)
				|| type.equals(StartConditionalEvent)
				|| type.equals(StartSignalEvent)
				|| type.equals(StartMultipleEvent);
	}

	// Swimlanes
	public final static String Pool = "http://b3mn.org/stencilset/bpmn1.1#Pool";
	public final static String Lane = "http://b3mn.org/stencilset/bpmn1.1#Lane";
	public final static String CollapsedPool = "http://b3mn.org/stencilset/bpmn1.1#CollapsedPool";

	public static boolean isASwimlane(String type) {
		return type.equals(Pool) || type.equals(Lane)
				|| type.equals(CollapsedPool);
	}

}
