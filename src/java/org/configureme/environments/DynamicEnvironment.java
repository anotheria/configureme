package org.configureme.environments;

import java.util.ArrayList;

import net.anotheria.util.StringUtils;

import org.configureme.Environment;
import org.configureme.GlobalEnvironment;

public class DynamicEnvironment implements Environment, Cloneable{

	private ArrayList<String> elements;
	
	public DynamicEnvironment(){
		elements = new ArrayList<String>();
	}
	
	private DynamicEnvironment(DynamicEnvironment toReduce){
		elements = new ArrayList<String>();
		elements.addAll(toReduce.elements.subList(0, toReduce.elements.size()-1));
	}
	
	public DynamicEnvironment(String start, String ... additional){
		this();
		add(start);
		if (additional!=null){
			for (String s : additional)
				add(s);
		}
	}
	
	public String toString(){
		StringBuilder ret = new StringBuilder();
		for (String s : elements){
			if (ret.length()>0)
				ret.append("_");
			ret.append(s);
		}
		return ret.toString();
	}
	
	public String expandedStringForm(){
		return toString();
	}
	
	public DynamicEnvironment add(String anElement){
		elements.add(anElement);
		return this;
	}
	
	public void extendThis(String anElement){
		elements.add(anElement);
	}
	
	public void reduceThis(){
		if (elements==null || elements.size()==0)
			throw new AssertionError("Can't reduce this environment");
		elements.remove(elements.size()-1);
	}
	
	@SuppressWarnings("unchecked")
	public Object clone(){
		try{
			DynamicEnvironment ret = (DynamicEnvironment)super.clone();
			ret.elements = (ArrayList<String>)elements.clone();
			return ret;
		}catch(CloneNotSupportedException e){
			throw new AssertionError("cloneable we are!");
		}
	}
	
	@Override
	public boolean isReduceable() {
		return elements!=null && elements.size()>0;
	}

	@Override
	public DynamicEnvironment reduce() {
		return new DynamicEnvironment(this);
	}
	
	public static Environment parse(String s){
		if (s==null || s.length()==0 || s.trim().length()==0)
			return GlobalEnvironment.INSTANCE;
		String[] tokens = StringUtils.tokenize(s, '_');
		DynamicEnvironment env = new DynamicEnvironment();
		for (String t : tokens)
			env.add(t);
		return env;
	}
	
	public boolean equals(Object o){
		return o == this || ((o instanceof DynamicEnvironment) && ((DynamicEnvironment)o).elements.equals(elements));
	}
}
