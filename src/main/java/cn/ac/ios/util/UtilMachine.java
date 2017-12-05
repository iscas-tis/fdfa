/* Copyright (c) 2016, 2017                                               */
/*       Institute of Software, Chinese Academy of Sciences               */
/* This file is part of ROLL, a Regular Omega Language Learning library.  */
/* ROLL is free software: you can redistribute it and/or modify           */
/* it under the terms of the GNU General Public License as published by   */
/* the Free Software Foundation, either version 3 of the License, or      */
/* (at your option) any later version.                                    */

/* This program is distributed in the hope that it will be useful,        */
/* but WITHOUT ANY WARRANTY; without even the implied warranty of         */
/* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the          */
/* GNU General Public License for more details.                           */

/* You should have received a copy of the GNU General Public License      */
/* along with this program.  If not, see <http://www.gnu.org/licenses/>.  */

package cn.ac.ios.util;


import cn.ac.ios.machine.Acceptance;
import cn.ac.ios.machine.Machine;
import cn.ac.ios.machine.buchi.DBA;
import cn.ac.ios.machine.dfa.DFA;
import cn.ac.ios.machine.dfa.DFAAcc;
import cn.ac.ios.machine.fdfa.FDFA;
import cn.ac.ios.words.APList;
import dk.brics.automaton.Automaton;
import dk.brics.automaton.State;
import dk.brics.automaton.Transition;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntPredicate;

public class UtilMachine {
	
	private UtilMachine() {
		
	}
	
	private static State getState(TIntObjectMap<State> map, int stateNr) {
		State state = map.get(stateNr);
		if(state == null) {
			state = new State();
			map.put(stateNr, state);
		}
		return state;
	}

	/**
	 *  DKFactory
 	 */

	public static Automaton dfaToDkAutomaton(Machine machine) {
		int initNr = machine.getInitialState();
		Acceptance f = machine.getAcceptance();
		TIntObjectMap<State> map = new TIntObjectHashMap<>();
		return dfaToDkAutomaton(
					map,
					machine,
					(n) -> { return n == initNr; },
					f::isFinal
				);
	}
	public static Automaton dfaToDkAutomaton(Machine machine, int init, int accept) {
		TIntObjectMap<State> map = new TIntObjectHashMap<>();
		return dfaToDkAutomaton(
				map,
				machine,
				(n) -> { return n == init; },
				(n) -> { return n == accept; }
		);
	}
	public static Automaton dfaToDkAutomaton(TIntObjectMap<State> map, Machine machine, IntPredicate isInitial, IntPredicate isFinal) {
		dk.brics.automaton.Automaton dkAut = new dk.brics.automaton.Automaton();
		APList aps = machine.getInAPs();
		
		for(int stateNr = 0; stateNr < machine.getStateSize(); stateNr ++) {
			State state = getState(map, stateNr);
			// initial states
			if(isInitial.test(stateNr)) {
				dkAut.setInitialState(state);
			}
			// final states
			if(isFinal.test(stateNr)) {
				state.setAccept(true);
			}

			for (int letter = 0; letter < aps.size(); letter ++) {
				int succ = machine.getSuccessor(stateNr, letter);
				State stateSucc = getState(map, succ);
				state.addTransition(
						new Transition(
								aps.get(letter).toString().charAt(0),
								stateSucc
						)
				);
			}
		}
		
		dkAut.setDeterministic(true);
		return dkAut;
	}
}
