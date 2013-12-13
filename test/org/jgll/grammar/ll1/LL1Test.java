package org.jgll.grammar.ll1;

import static org.jgll.util.CollectionsUtil.list;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarBuilder;
import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.grammar.symbol.Terminal;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseError;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.TokenSymbolNode;
import org.jgll.util.BitSetUtil;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * S ::= A a
 * 
 * A ::= B D
 * 
 * B ::= b | epsilon
 * 
 * D ::= d | epsilon
 * 
 * @author Ali Afroozeh
 * 
 */
public class LL1Test {
	
	private static final int EOF = 1;

	private Grammar grammar;
	private GLLParser rdParser;
	
	Nonterminal S = new Nonterminal("S");
	Nonterminal A = new Nonterminal("A");
	Nonterminal B = new Nonterminal("B");
	Nonterminal D = new Nonterminal("D");

	Terminal a = new Character('a');
	Terminal b = new Character('b');
	Terminal d = new Character('d');

	@Before
	public void init() {

		GrammarBuilder builder = new GrammarBuilder("DanglingElse");

		Rule rule1 = new Rule(S, list(A, a));
		builder.addRule(rule1);

		Rule rule2 = new Rule(A, list(B, D));
		builder.addRule(rule2);

		Rule rule3 = new Rule(B, list(b));
		builder.addRule(rule3);

		Rule rule4 = new Rule(B);
		builder.addRule(rule4);

		Rule rule5 = new Rule(D, list(d));
		builder.addRule(rule5);

		Rule rule6 = new Rule(D);
		builder.addRule(rule6);

		grammar = builder.build();
		rdParser = ParserFactory.createRecursiveDescentParser(grammar);
	}
	
	@Test
	public void testNullable() {
		assertFalse(grammar.getNonterminalByName("S").isNullable());
		assertTrue(grammar.getNonterminalByName("A").isNullable());
		assertTrue(grammar.getNonterminalByName("B").isNullable());
		assertTrue(grammar.getNonterminalByName("D").isNullable());
	}
	
	@Test
	public void ll1Test() {
		assertTrue(grammar.getNonterminalByName("A").isLL1());
		assertTrue(grammar.getNonterminalByName("B").isLL1());
		assertTrue(grammar.getNonterminalByName("D").isLL1());
	}

	@Test
	public void testPredictSets() {
		BodyGrammarSlot slot1 = grammar.getGrammarSlotByName("S ::= . A [a]");
		assertEquals(BitSetUtil.from(grammar.getTokenID(d), grammar.getTokenID(b), grammar.getTokenID(a)), slot1.getPredictionSet());
		
		BodyGrammarSlot slot2 = grammar.getGrammarSlotByName("A ::= . B D");
		assertEquals(BitSetUtil.from(grammar.getTokenID(d), grammar.getTokenID(b), grammar.getTokenID(a), EOF), slot2.getPredictionSet());
		
		BodyGrammarSlot slot3 = grammar.getGrammarSlotByName("B ::= . [b]");
		assertEquals(BitSetUtil.from(grammar.getTokenID(b)), slot3.getPredictionSet());

		BodyGrammarSlot slot4 = grammar.getGrammarSlotByName("B ::= .");
		assertEquals(BitSetUtil.from(grammar.getTokenID(d), grammar.getTokenID(a), EOF), slot4.getPredictionSet());
	}

	@Test
	public void test1() throws ParseError {
		NonterminalSymbolNode sppf = rdParser.parse(Input.fromString("bda"), grammar, "S");

		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 0, 3);
		NonterminalSymbolNode node2 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 0, 2);
		NonterminalSymbolNode node3 = new NonterminalSymbolNode(grammar.getNonterminalByName("B"), 0, 1);
		TokenSymbolNode node4 = new TokenSymbolNode(3, 0, 1);
		node3.addChild(node4);
		NonterminalSymbolNode node5 = new NonterminalSymbolNode(grammar.getNonterminalByName("D"), 1, 2);
		TokenSymbolNode node6 = new TokenSymbolNode(4, 1, 1);
		node5.addChild(node6);
		node2.addChild(node3);
		node2.addChild(node5);
		TokenSymbolNode node7 = new TokenSymbolNode(2, 2, 1);
		node1.addChild(node2);
		node1.addChild(node7);
		
		assertTrue(sppf.deepEquals(node1));
	}
	
	@Test
	public void test2() throws ParseError {
		NonterminalSymbolNode sppf = rdParser.parse(Input.fromString("a"), grammar, "S");

		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 0, 1);
		NonterminalSymbolNode node2 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 0, 0);
		NonterminalSymbolNode node3 = new NonterminalSymbolNode(grammar.getNonterminalByName("B"), 0, 0);
		NonterminalSymbolNode node4 = new NonterminalSymbolNode(grammar.getNonterminalByName("D"), 0, 0);
		node2.addChild(node3);
		node2.addChild(node4);
		TokenSymbolNode node5 = new TokenSymbolNode(2, 0, 1);
		node1.addChild(node2);
		node1.addChild(node5);
		
		assertTrue(sppf.deepEquals(node1));
	}
	
	@Test
	public void test3() throws ParseError {
		NonterminalSymbolNode sppf = rdParser.parse(Input.fromString("ba"), grammar, "S");

		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 0, 2);
		NonterminalSymbolNode node2 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 0, 1);
		NonterminalSymbolNode node3 = new NonterminalSymbolNode(grammar.getNonterminalByName("B"), 0, 1);
		TokenSymbolNode node4 = new TokenSymbolNode(3, 0, 1);
		node3.addChild(node4);
		NonterminalSymbolNode node5 = new NonterminalSymbolNode(grammar.getNonterminalByName("D"), 1, 1);
		node2.addChild(node3);
		node2.addChild(node5);
		TokenSymbolNode node6 = new TokenSymbolNode(2, 1, 1);
		node1.addChild(node2);
		node1.addChild(node6);
		
		assertTrue(sppf.deepEquals(node1));
	}

	@Test
	public void test4() throws ParseError {
		NonterminalSymbolNode sppf = rdParser.parse(Input.fromString("da"), grammar, "S");
		
		NonterminalSymbolNode node1 = new NonterminalSymbolNode(grammar.getNonterminalByName("S"), 0, 2);
		NonterminalSymbolNode node2 = new NonterminalSymbolNode(grammar.getNonterminalByName("A"), 0, 1);
		NonterminalSymbolNode node3 = new NonterminalSymbolNode(grammar.getNonterminalByName("B"), 0, 0);
		NonterminalSymbolNode node4 = new NonterminalSymbolNode(grammar.getNonterminalByName("D"), 0, 1);
		TokenSymbolNode node5 = new TokenSymbolNode(4, 0, 1);
		node4.addChild(node5);
		node2.addChild(node3);
		node2.addChild(node4);
		TokenSymbolNode node6 = new TokenSymbolNode(2, 1, 1);
		node1.addChild(node2);
		node1.addChild(node6);
		
		assertTrue(sppf.deepEquals(node1));
	}

	
}