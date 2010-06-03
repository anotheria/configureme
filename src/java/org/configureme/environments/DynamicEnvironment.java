package org.configureme.environments;

import java.util.ArrayList;
import java.util.List;

import net.anotheria.util.StringUtils;

import org.configureme.Environment;
import org.configureme.GlobalEnvironment;

/**
 * A dynamic environment class. This class can be used for any type of environments, for any environment deepth. However, we recommend the usage of more strict Application/LocaleBased Environment if 
 * applicable, since they allow less and can prevent you from absolute environmental chaos. The DynamicEnvironment is the ultima ratio, a class that is capable of everything, but 
 * requires not testable discipline of the developers.
 * @author lrosenberg
 */
public class DynamicEnvironment implements Environment, Cloneable{

	/**
	 * Stores parts of the environment
	 */
	private ArrayList<String> elements;
	
	/**
	 * Creates a new empty dynamic environment.
	 * Warning, since the environment produced by this call is another form of the GlobalEnvironment, it should better not be used, except you are planing to add elements immediately after the creation.
	 */
	public DynamicEnvironment(){
		elements = new ArrayList<String>();
	}
	
	/**
	 * Creates a new DynamicEnvironment with at least one element. Favor this constructor for modifyable DynamicEnvironment.
	 * @param start
	 * @param additional
	 */
	public DynamicEnvironment(String start, String ... additional){
		this();
		add(start);
		if (additional!=null){
			for (String s : additional)
				add(s);
		}
	}

	/**
	 * Creates a new environment with the given list of elements Used internally to create a reduced environment version.
	 * @param someElements starting elements of the environment.
	 */
	private DynamicEnvironment(List<String> someElements){
		elements = new ArrayList<String>(someElements.size());
		//ensure immutability
		elements.addAll(someElements);
	}
	
	
	@Override
	public String toString(){
		StringBuilder ret = new StringBuilder();
		for (String s : elements){
			if (ret.length()>0)
				ret.append("_");
			ret.append(s);
		}
		return ret.toString();
	}
	
	@Override
	public String expandedStringForm(){
		return toString();
	}
	
	/**
	 * Creates a new DynamicEnvironment by adding an element to the current environment. Creates a new object, hence threadsafe.
	 * @param anElement element to add to the environment
	 * @return
	 */
	public DynamicEnvironment add(String anElement){
		elements.add(anElement);
		return this;
	}
	
	/**
	 * Extends 'this' environment by adding an additional element to it. Modifies current object, hence NOT THREADSAFE.
	 * @param anElement
	 */
	public void extendThis(String anElement){
		elements.add(anElement);
	}
	
	/**
	 * Reduces current environment. Modifies current object, hence NOT THREADSAFE.
	 */
	public void reduceThis(){
		if (elements==null || elements.size()==0)
			throw new AssertionError("Can't reduce this environment");
		elements.remove(elements.size()-1);
	}
	
	@SuppressWarnings("unchecked")
	@Override public Object clone(){
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
	public Environment reduce() {
		if (!isReduceable())
			throw new AssertionError("Can't reduce unreduceable environment.");
		if (elements.size()==1)
			return GlobalEnvironment.INSTANCE;
		return new DynamicEnvironment(elements.subList(0, elements.size()-1));
	}
	
	/**
	 * Parses a string and creates a new Environment which corresponds the string. 
	 * @param s
	 * @return
	 */
	public static Environment parse(String s){
		if (s==null || s.length()==0 || s.trim().length()==0)
			return GlobalEnvironment.INSTANCE;
		String[] tokens = StringUtils.tokenize(s, '_');
		DynamicEnvironment env = new DynamicEnvironment();
		for (String t : tokens)
			env.add(t);
		return env;
	}
	
	@Override public boolean equals(Object o){
		return o == this || ((o instanceof DynamicEnvironment) && ((DynamicEnvironment)o).elements.equals(elements));
	}
	
	@Override public int hashCode(){
		return elements == null ? 42 : elements.hashCode();
	}
}
