package org.jgll.regex;

import java.util.Arrays;
import java.util.List;

public class Sequence implements RegularExpression {

	private static final long serialVersionUID = 1L;

	private final List<RegularExpression> regularExpressions;
	
	private final NFA nfa;
	
	public Sequence(List<RegularExpression> regularExpressions) {
		this.nfa = createNFA();
		this.regularExpressions = regularExpressions;
	}
	
	public Sequence(RegularExpression...regularExpressions) {
		this(Arrays.asList(regularExpressions));
	}
	
	public List<RegularExpression> getRegularExpressions() {
		return regularExpressions;
	}

	@Override
	public NFA toNFA() {
		return nfa;
	}
	
	private NFA createNFA() {
		State startState = new State();
		State finalState = new State(true);
		
		State currentState = startState;
		
		for(RegularExpression regexp : regularExpressions) {
			currentState.addTransition(Transition.emptyTransition(regexp.toNFA().getStartState()));
			currentState = regexp.toNFA().getEndState();
		}
		
		currentState.addTransition(Transition.emptyTransition(finalState));
		
		return new NFA(startState, finalState);
	}
	
	@Override
	public boolean isNullable() {
		for(RegularExpression regex : regularExpressions) {
			if(!regex.isNullable()) {
				return false;
			}
		}
		return true;
	}


}