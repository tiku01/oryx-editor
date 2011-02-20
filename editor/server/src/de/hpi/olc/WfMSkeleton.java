package de.hpi.olc;

import org.oryxeditor.server.diagram.Shape;

import de.hpi.olc.CPNGenerator.ColorSet;

public class WfMSkeleton {
	private CPNFactory factory;
	
	private Shape sync = null;
	private Shape syncStates = null;
	private Shape init = null;
	private Shape or = null;
	private Shape gate = null;
	
	public WfMSkeleton(CPNFactory factory) {
		this.factory = factory;
	}
	
	/**
	 * Generates the basic skeleton of the workflow net 
	 */
	public void generate(String guardExit, String guardResume) {
		init = factory.getAPlace("init", ColorSet.State);
		init.setBounds(Layout.getBoundsForPlace(620, 60));
		or = factory.getATransition("Or");
		or.setBounds(Layout.getBoundsForTransition(740, 60));
		gate = factory.getAPlace("gate", ColorSet.State);
		gate.setBounds(Layout.getBoundsForPlace(1200, 60));
		Shape initToOr = factory.getAnArc(Constants.arcState);
		factory.connect(init, initToOr, or);
		
		generateProcessTermination(gate, guardExit);
		generateTokenMatcher(init, gate, guardResume);
	}
	
	/**
	 * This process part is the connection between Gate and INIT Generates the
	 * static structure of the token matcher
	 */
	private void generateTokenMatcher(Shape init, Shape gate, String guardResume) {
		Shape gateToResume = factory.getAnArc(Constants.arcState);
		Shape resume = factory.getATransition("resume", guardResume);
		resume.setBounds(Layout.getBoundsForTransition(1200, 500));
		Shape resumeToEntry = factory.getAnArc(Constants.arcState);
		Shape entry = factory.getAPlace("entry", ColorSet.State);
		entry.setBounds(Layout.getBoundsForPlace(500, 175));
		Shape entryToCont = factory.getAnArc(Constants.arcState);
		Shape cont = factory.getATransition("continue", Constants.guardContinue);
		cont.setBounds(Layout.getBoundsForTransition(500, 60));
		Shape contToInit = factory.getAnArc(Constants.arcState);
		// connect
		factory.connect(gate, gateToResume, resume);
		factory.connect(resume, resumeToEntry, entry, false);
		factory.connect(entry, entryToCont, cont);
		factory.connect(cont, contToInit, init);

		// generate shapes for entry
		Shape enter = factory.getATransition("enter", Constants.guardEnter);
		enter.setBounds(Layout.getBoundsForTransition(340, 175));
		syncStates = factory.getAPlace("syncStates", ColorSet.StateList);
		syncStates.setBounds(Layout.getBoundsForPlace(340, 60));
		Shape entryToEnter = factory.getAnArc(Constants.arcState);
		Shape enterToSyncStates = factory.getAnArc(Constants.arcStateList);
		Shape syncStatesToEnter = factory.getAnArc(Constants.arcStateList);
		Shape contToSyncStates = factory.getAnArc(Constants.arcStateList);
		Shape syncStatesToCont = factory.getAnArc(Constants.arcStateList);
		// connect
		factory.connect(entry, entryToEnter, enter);
		factory.connect(enter, enterToSyncStates, syncStates);
		factory.connect(syncStates, syncStatesToEnter, enter);
		factory.connect(syncStates, syncStatesToCont, cont);
		factory.connect(cont, contToSyncStates, syncStates);

		// generate shapes for matching
		Shape first = factory.getATransition("first", Constants.guardFirst);
		first.setBounds(Layout.getBoundsForTransition(235, 100));
		Shape second = factory.getATransition("second", Constants.guardSecond);
		second.setBounds(Layout.getBoundsForTransition(235, 240));
		Shape matcher = factory.getAPlace("matcher", ColorSet.State);
		matcher.setBounds(Layout.getBoundsForPlace(235, 175));
		Shape wait = factory.getAPlace("wait", ColorSet.SyncState);
		wait.setBounds(Layout.getBoundsForPlace(130, 175));
		sync = factory.getAPlace("sync", ColorSet.SyncState);
		sync.setBounds(Layout.getBoundsForPlace(50, 175));
		Shape enterToMatcher = factory.getAnArc(Constants.arcState);
		Shape matcherToFirst = factory.getAnArc(Constants.arcState);
		Shape matcherToSecond = factory.getAnArc(Constants.arcState);
		Shape firstToWait = factory.getAnArc(Constants.arcSync);
		Shape waitToSecond = factory.getAnArc(Constants.arcSync);
		Shape syncToFirst = factory.getAnArc(Constants.arcSync);
		Shape secondToSync = factory.getAnArc(Constants.arcSync);
		Shape secondToEntry = factory.getAnArc(Constants.arcNextState);
		// connect
		factory.connect(enter, enterToMatcher, matcher);
		factory.connect(matcher, matcherToFirst, first);
		factory.connect(matcher, matcherToSecond, second);
		factory.connect(first, firstToWait, wait, false);
		factory.connect(wait, waitToSecond, second);
		factory.connect(sync, syncToFirst, first);
		factory.connect(second, secondToSync, sync, false);
		factory.connect(second, secondToEntry, entry, false);
	}
	
	/**
	 * This process part is connected to GATE Generates Exit-Transition and
	 * Final Place.
	 */
	private void generateProcessTermination(Shape gate, String guardExit) {
		// generate arc, exit-transition, arc and final place
		Shape gateToTransition = factory.getAnArc(Constants.arcState);
		Shape transition = factory.getATransition("exit", guardExit);
		transition.setBounds(Layout.getBoundsForTransition(1340, 60));
		Shape transitionToEnd = factory.getAnArc(Constants.arcState);
		Shape finalPlace = factory.getAPlace("end", ColorSet.State);
		finalPlace.setBounds(Layout.getBoundsForPlace(1440, 60));
		
		// connect shapes
		factory.connect(gate, gateToTransition, transition);
		factory.connect(transition, transitionToEnd, finalPlace);
	}
	public Shape getInit() {
		return this.init;
	}
	
	public Shape getSync() {
		return this.sync;
	}
	
	public Shape getSyncStates() {
		return this.syncStates;
	}
	
	public Shape getOr() {
		return this.or;
	}
	
	public Shape getGate() {
		return this.gate;
	}
}
