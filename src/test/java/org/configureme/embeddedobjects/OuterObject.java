package org.configureme.embeddedobjects;

import org.configureme.annotations.Configure;
import org.configureme.annotations.ConfigureMe;

import java.util.Arrays;

/**
 * TODO comment this class
 *
 * @author lrosenberg
 * @since 22.10.12 11:27
 */
@ConfigureMe(name="embeddedobjects")
public class OuterObject {

	@Configure
	private ListItem[] list;

	@Configure
	private String name;

	@Configure
	private InnerObject inner;

	@Configure
	private String[] listofstrings;

	@Override public String toString(){
		return "Name: "+name+", inner: "+inner+", list: "+Arrays.toString(list)+" listofstrings:"+ Arrays.toString(listofstrings);
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public InnerObject getInner() {
		return inner;
	}

	public void setInner(InnerObject inner) {
		this.inner = inner;
	}

	public String[] getListofstrings() {
		return listofstrings;
	}

	public void setListofstrings(String[] listofstrings) {
		this.listofstrings = listofstrings;
	}

	public ListItem[] getList() {
		return list;
	}

	public void setList(ListItem[] list) {
		this.list = list;
	}
}
