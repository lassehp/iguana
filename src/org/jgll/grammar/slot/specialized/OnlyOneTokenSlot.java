package org.jgll.grammar.slot.specialized;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.LastGrammarSlot;
import org.jgll.grammar.slot.TokenGrammarSlot;
import org.jgll.grammar.slot.test.ConditionTest;
import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;
import org.jgll.regex.RegularExpression;
import org.jgll.sppf.TokenSymbolNode;

/**
 * Corresponds to the case A ::= .x, where x is the only terminal
 * in the right hand side of the rule.
 * 
 * @author Ali Afroozeh
 *
 */
public class OnlyOneTokenSlot extends TokenGrammarSlot {
	
	private static final long serialVersionUID = 1L;
	
	public OnlyOneTokenSlot(int id, int nodeId, String label, BodyGrammarSlot previous, RegularExpression regularExpression, int tokenID) {
		super(id, nodeId, label, previous, regularExpression, tokenID);
	}
	
	@Override
	public GrammarSlot parse(GLLParser parser, GLLLexer lexer) {

		int ci = parser.getCurrentInputIndex();
		
		if(preConditions.execute(parser, lexer, ci)) {
			return null;
		}

		int length = lexer.tokenLengthAt(ci, tokenID);
		
		if(length < 0) {
			parser.recordParseError(this);
			return null;
		}
		
		if(postConditions.execute(parser, lexer, ci)) {
			return null;
		}
		
		TokenSymbolNode cn = parser.getTokenNode(tokenID, ci, length);
		
		parser.getNonterminalNode((LastGrammarSlot) next, cn);
		parser.pop();
		return null;
	}

	@Override
	public ConditionTest getPreConditions() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ConditionTest getPostConditions() {
		throw new UnsupportedOperationException();
	}	

}
