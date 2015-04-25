/*
 * Copyright (c) 2015, CWI
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this 
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this 
 *    list of conditions and the following disclaimer in the documentation and/or 
 *    other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT 
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY 
 * OF SUCH DAMAGE.
 *
 */

package org.iguana.datadependent.descriptor;

import org.iguana.datadependent.env.Environment;
import org.iguana.grammar.slot.BodyGrammarSlot;
import org.iguana.parser.GLLParser;
import org.iguana.parser.gss.GSSNode;
import org.iguana.sppf.NonPackedNode;

public class Descriptor extends org.iguana.parser.descriptor.Descriptor {
	
	private final Environment env;
	
	public Descriptor(BodyGrammarSlot slot, GSSNode gssNode, int inputIndex, NonPackedNode sppfNode, Environment env) {
		super(slot, gssNode, inputIndex, sppfNode);
		
		assert env != null;
		this.env = env;
	}

	@Override
	public void execute(GLLParser parser) {
		getGrammarSlot().execute(parser, getGSSNode(), getInputIndex(), getSPPFNode(), env);
	}
}