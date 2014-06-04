package org.jgll.disambiguation.conditions;

import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.condition.RegularExpressionCondition;
import org.jgll.grammar.symbol.Keyword;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Plus;
import org.jgll.grammar.symbol.Range;
import org.jgll.grammar.symbol.Rule;
import org.jgll.grammar.transformation.EBNFToBNF;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * S ::= Label !>> "8" [0-9]
 *
 * Label ::= [a-z]+ !>> [a-z]
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class FollowRestrictionTest2 {
	
	private GLLParser parser;
	private Grammar grammar;
	
	@Before
	public void init() {
		
		Grammar.Builder builder = new Grammar.Builder();
		
		Nonterminal S = Nonterminal.withName("S");
		Nonterminal Label = Nonterminal.withName("Label");
		Range az = Range.in('a', 'z');
		Range zero_nine = Range.in('0', '9');
		
		Rule r1 = new Rule(S, Label.builder().addCondition(RegularExpressionCondition.notFollow(Keyword.from("8"))).build(), zero_nine);
		
		Rule r2 = new Rule(Label, Plus.from(az).builder().addCondition(RegularExpressionCondition.notFollow(az)).build());
		builder.addRule(r1);
		builder.addRule(r2);

		EBNFToBNF ebnfToBNF = new EBNFToBNF();
		grammar = ebnfToBNF.transform(builder.build());
	}
	
	@Test
	public void testParser1() {
		Input input = Input.fromString("abc8");
		parser =  ParserFactory.newParser(grammar, input);
		ParseResult result = parser.parse(input, grammar.toGrammarGraph(), "S");
		assertTrue(result.isParseError());
	}
	
	@Test
	public void testParser2() throws Exception {
		Input input = Input.fromString("abc3");
		parser =  ParserFactory.newParser(grammar, input);
		parser.parse(input, grammar.toGrammarGraph(), "S");
	}


}
