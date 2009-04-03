package org.configureme.environments;

import java.util.ArrayList;
import java.util.List;

import org.configureme.Environment;

public class DynamicEnvironment implements Environment, Cloneable{

	private ArrayList<String> elements;
	
	public DynamicEnvironment(){
		elements = new ArrayList<String>();
	}
	
	private DynamicEnvironment(DynamicEnvironment toReduce){
		elements = new ArrayList<String>();
		elements.addAll(toReduce.elements.subList(0, toReduce.elements.size()-1));
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
	
	public Object clone(){
		try{
			DynamicEnvironment ret = (DynamicEnvironment)super.clone();
			ret.elements = (ArrayList<String>)elements.clone();
			return ret;
		}catch(CloneNotSupportedException e){
			throw new AssertionError("cloneable we are!");
		}
	}
	
	public static void main(String a[]){
		test(new DynamicEnvironment());
		test(new DynamicEnvironment().add("a"));
		test(new DynamicEnvironment().add("a").add("b"));
		test(new DynamicEnvironment().add("a").add("b").add("c"));
	}
	
	private static void test(DynamicEnvironment de){
		System.out.println("environment: "+de+", reduceable: "+de.isReduceable());
		if (de.isReduceable())
			System.out.println("\t-->"+de.reduce());
	}

	@Override
	public boolean isReduceable() {
		return elements!=null && elements.size()>0;
	}

	@Override
	public DynamicEnvironment reduce() {
		return new DynamicEnvironment(this);
	}
}
