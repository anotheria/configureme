package org.configureme.embeddedobjects;

import org.configureme.annotations.Configure;

/**
 * TODO comment this class
 *
 * @author lrosenberg
 * @since 22.10.12 11:27
 */
public class ListItem {
	@Configure
	private String name;

	@Configure
	private InnerListItem item;

	@Override
	public String toString() {
		return "ListItem{" +
				"name='" + name + '\'' +
				", item=" + item +
				'}';
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public InnerListItem getItem() {
		return item;
	}

	public void setItem(InnerListItem item) {
		this.item = item;
	}
}
