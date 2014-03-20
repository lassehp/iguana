package org.jgll.grammar.symbol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.jgll.grammar.condition.Condition;

public abstract class AbstractSymbol implements Symbol {

	private static final long serialVersionUID = 1L;
	
	protected final Set<Condition> conditions;
	
	protected final String name;
	
	public AbstractSymbol(String name) {
		this(name, new ArrayList<Condition>());
	}
	
	public AbstractSymbol(String name, Iterable<Condition> conditions) {
		this.name = name;
		this.conditions = new HashSet<>();
		for(Condition condition : conditions) {
			this.conditions.add(condition);
		}
	}
	
	@Override
	public Set<Condition> getConditions() {
		return conditions;
	}
	
	@Override
	public Symbol addCondition(Condition condition) {
		return addConditions(Arrays.asList(condition));
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public Symbol addConditions(Collection<Condition> conditions) {
		AbstractSymbol s = (AbstractSymbol) this.copy();
		s.conditions.addAll(this.conditions);
		s.conditions.addAll(conditions);
		return s;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
