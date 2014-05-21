package org.jgll.parser;


import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.LastGrammarSlot;
import org.jgll.lexer.GLLLexerImpl;
import org.jgll.parser.descriptor.Descriptor;
import org.jgll.parser.descriptor.DescriptorFactory;
import org.jgll.parser.gss.GSSEdge;
import org.jgll.parser.gss.GSSNode;
import org.jgll.parser.lookup.factory.DescriptorLookupFactory;
import org.jgll.parser.lookup.factory.GSSLookupFactory;
import org.jgll.parser.lookup.factory.SPPFLookupFactory;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.util.Input;

/**

 * @author Ali Afroozeh
 * 
 */
public class NewGLLParserImpl extends AbstractGLLParserImpl {
		
	public NewGLLParserImpl(GSSLookupFactory gssLookupFactory, 
						 SPPFLookupFactory sppfLookupFactory, 
						 DescriptorFactory descriptorFactory,
						 DescriptorLookupFactory descriptorLookupFactory) {
		super(gssLookupFactory, sppfLookupFactory, descriptorFactory, descriptorLookupFactory);
	}
	
	@Override
	public NonterminalSymbolNode parse(Input input, GrammarGraph grammar, String startSymbolName) throws ParseError {
		return parse(new GLLLexerImpl(input, grammar), grammar, startSymbolName);
	}
	
	public final void pop(GSSNode gssNode, int inputIndex, NonPackedNode node) {
		
		if (gssNode != u0) {

			log.trace("Pop %s, %d, %s", gssNode, inputIndex, node);
			
			gssLookup.addToPoppedElements(gssNode, node);
			
			label:
			for(GSSEdge edge : gssLookup.getEdges(gssNode)) {
				BodyGrammarSlot returnSlot = edge.getReturnSlot();
				
				if(returnSlot.getPopConditions().execute(this, lexer, gssNode, inputIndex)) {
					continue label;
				}
				
				SPPFNode y = returnSlot.getNodeCreatorFromPop().create(this, returnSlot, edge.getNode(), node);
				
				createContinuation(returnSlot, inputIndex, edge.getDestination(), y);
			}
		}
	}
	
	private void createContinuation(BodyGrammarSlot slot, int inputIndex, GSSNode gssNode, SPPFNode sppfNode) {
		Descriptor descriptor = descriptorFactory.createDescriptor(slot, gssNode, inputIndex, sppfNode);

		// Perform a direct pop for continuations of the form A ::= alpha ., instead of 
		// creating descriptors
		if (slot instanceof LastGrammarSlot) {
			if (descriptorLookup.addDescriptor(descriptor)) {
				if (!slot.getPopConditions().execute(this, lexer, gssNode, inputIndex)) {
					HeadGrammarSlot head = (HeadGrammarSlot) gssNode.getGrammarSlot();
					if (head.testFollowSet(lexer.getInput().charAt(inputIndex))) {
						pop(gssNode, inputIndex, (NonPackedNode) sppfNode);
					}
				}
			}
		} else {
			scheduleDescriptor(descriptor);					
		}		
	}
	
	/**
	 * 
	 * create(L, u, w) {
     *	 let w be the value of cn
	 *	 if there is not already a GSS node labelled (L,A ::= alpha . beta, ci) create one
	 * 	 let v be the GSS node labelled (L,A ::= alpha . beta, ci)
	 *   if there is not an edge from v to cu labelled w {
	 * 		create an edge from v to cu labelled w
	 * 		for all ((v, z) in P) {
	 * 			let x be the node returned by getNodeP(A ::= alpha . beta, w, z)
	 * 			add(L, cu, h, x)) where h is the right extent of z
	 * 		}
	 * 	 }
	 * 	 return v
	 * }
	 * 
	 * @param returnSlot the grammar label
	 * 
	 * @param nonterminalIndex the index of the nonterminal appearing as the head of the rule
	 *                         where this position refers to. 
	 * 
	 * @param alternateIndex the index of the alternate of the rule where this position refers to.
	 * 
	 * @param position the position in the body of the rule where this position refers to
	 *
	 * @return 
     *
	 */
	@Override
	public final void createGSSNode(BodyGrammarSlot returnSlot, HeadGrammarSlot head) {
		GSSNode v = gssLookup.getGSSNode(head, ci);
		log.trace("GSSNode created: (%s, %d)",  head, ci);
		createGSSEdge(returnSlot, cu, cn, v);
		cu = v;
	}
	
	@Override
	public final boolean hasGSSNode(BodyGrammarSlot slot, HeadGrammarSlot head) {
		GSSNode v = gssLookup.hasGSSNode(head, ci);
		if(v == null) return false;
		
		log.trace("GSSNode found: (%s, %d)",  head, ci);
		createGSSEdge(slot, cu, cn, v);
		return true;
	}
	
	protected void createGSSEdge(BodyGrammarSlot returnSlot, GSSNode destination, SPPFNode w, GSSNode source) {
		if(gssLookup.getGSSEdge(source, destination, w, returnSlot)) {
			
			log.trace("GSS Edge created: %s from %s to %s", returnSlot, source, destination);
			
			label:
			for (SPPFNode z : source.getPoppedElements()) {
				
				// Execute pop actions for continuations, when the GSS node already
				// exits. The input index will be the right extend of the node
				// stored in the popped elements.
				if(returnSlot.getPopConditions().execute(this, lexer, destination, z.getRightExtent())) {
					continue label;
				}
				
				SPPFNode x = returnSlot.getNodeCreatorFromPop().create(this, returnSlot, w, z); 
				
				createContinuation(returnSlot, z.getRightExtent(), destination, x);
			}
		}
	}	
}