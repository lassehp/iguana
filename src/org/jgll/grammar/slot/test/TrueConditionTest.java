package org.jgll.grammar.slot.test;

import java.util.Collections;

import org.jgll.grammar.slotaction.SlotAction;
import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;

public class TrueConditionTest implements ConditionTest {

	@Override
	public boolean execute(GLLParser parser, GLLLexer lexer, int inputIndex) {
		return true;
	}

	@Override
	public Iterable<SlotAction<Boolean>> getConditions() {
		return Collections.emptyList();
	}

}
