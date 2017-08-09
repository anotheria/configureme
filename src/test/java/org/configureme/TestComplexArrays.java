package org.configureme;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.configureme.annotations.Configure;
import org.configureme.annotations.ConfigureMe;

@ConfigureMe(name="complexarrays")
public class TestComplexArrays{
	@Configure private ArrayList<String> listString;
	@Configure private LinkedList<Integer> listInteger;
	@Configure private List<Boolean> listBoolean = new LinkedList<>();
	@Configure private List<List<String>> listListString;

	@Configure private Set<String> setString;
	@Configure private Set<Integer> setInteger;
	@Configure private HashSet<Boolean> setBoolean;
	@Configure private Set<Set<String>> setsetString;
	@Configure private List<Set<String>> listSetString;

	public ArrayList<String> getListString() {
		return listString;
	}

	public void setListString(ArrayList<String> listString) {
		this.listString = listString;
	}

	public LinkedList<Integer> getListInteger() {
		return listInteger;
	}

	public void setListInteger(LinkedList<Integer> listInteger) {
		this.listInteger = listInteger;
	}

	public List<Boolean> getListBoolean() {
		return listBoolean;
	}

	public void setListBoolean(List<Boolean> listBoolean) {
		this.listBoolean = listBoolean;
	}

	public List<List<String>> getListListString() {
		return listListString;
	}

	public void setListListString(List<List<String>> listListString) {
		this.listListString = listListString;
	}

	public Set<String> getSetString() {
		return setString;
	}

	public void setSetString(Set<String> setString) {
		this.setString = setString;
	}

	public Set<Integer> getSetInteger() {
		return setInteger;
	}

	public void setSetInteger(Set<Integer> setInteger) {
		this.setInteger = setInteger;
	}

	public HashSet<Boolean> getSetBoolean() {
		return setBoolean;
	}

	public void setSetBoolean(HashSet<Boolean> setBoolean) {
		this.setBoolean = setBoolean;
	}

	public Set<Set<String>> getSetsetString() {
		return setsetString;
	}

	public void setSetsetString(Set<Set<String>> setsetString) {
		this.setsetString = setsetString;
	}

	public List<Set<String>> getListSetString() {
		return listSetString;
	}

	public void setListSetString(List<Set<String>> listSetString) {
		this.listSetString = listSetString;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder(this.getClass().getSimpleName());
		sb.append("[listString=").append(listString);
		sb.append(", listInteger=").append(listInteger);
		sb.append(", listBoolean=").append(listBoolean);
		sb.append(", listListString=").append(listListString);
		sb.append(", setString=").append(setString);
		sb.append(", setInteger=").append(setInteger);
		sb.append(", setBoolean=").append(setBoolean);
		sb.append(", setsetString=").append(setsetString);
		sb.append(", listSetString=").append(listSetString);
		sb.append(']');
		return sb.toString();
	}



}