/**
 * Copyright (c) 2009 Luciano Garcia-Banuelos
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
package de.hpi.PTnet.serialization;

import java.io.PrintStream;

import de.hpi.petrinet.LabeledTransition;
import de.hpi.petrinet.Node;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.Place;
import de.hpi.petrinet.Transition;

public class PetriNet2Dot {
	public static void print(PrintStream out, PetriNet net) {
		out.println("digraph G {");
		for (Transition t : net.getTransitions()) {
			if (t instanceof LabeledTransition)
				out.printf("\tn%s[shape=box,label=%s];\n", Math.abs(t.getId().hashCode()), ((LabeledTransition)t).getLabel());
			else
				out.printf("\tn%s[shape=box];\n", Math.abs(t.getId().hashCode()));
		}
		for (Place n : net.getPlaces())
			out.printf("\tn%s[shape=ellipse,label=%s];\n", Math.abs(n.getId().hashCode()), "");
		
		for (Transition t : net.getTransitions()) {
			for (Node n : t.getPrecedingNodes())
				out.printf("\tn%s->n%s;\n", Math.abs(n.getId().hashCode()), Math.abs(t.getId().hashCode()));
			for (Node n : t.getSucceedingNodes())
				out.printf("\tn%s->n%s;\n", Math.abs(t.getId().hashCode()), Math.abs(n.getId().hashCode()));
		}
		out.println("}");
	}
}
