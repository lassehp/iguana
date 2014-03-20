package org.jgll.grammar.slot.specialized;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.test.ConditionsTest;
import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;
import org.jgll.regex.RegularExpression;

public class OnlyOneTokenSlotWithPostCondition extends OnlyOneTokenSlot {
	
	private static final long serialVersionUID = 1L;

	private ConditionsTest postConditions;

	public OnlyOneTokenSlotWithPostCondition(int id, int nodeId, String label,
			BodyGrammarSlot previous, RegularExpression regularExpression,
			int tokenID, ConditionsTest conditions) {
		super(id, nodeId, label, previous, regularExpression, tokenID);
		this.postConditions = conditions;
	}


	@Override
	public GrammarSlot parse(GLLParser parser, GLLLexer lexer) {
		
		if(postConditions.execute(parser, lexer)) {
			return null;
		}
		
		return super.parse(parser, lexer);
	}

}
