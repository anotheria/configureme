package org.configureme.embeddedobjects;

import org.configureme.annotations.Configure;

/**
 * TODO comment this class
 *
 * @author lrosenberg
 * @since 22.10.12 11:28
 */
public class InnerListItem {
	@Configure
	private String name;

	@Configure
	private int amount;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	@Override
	public String toString() {
		return "InnerListItem{" +
				"name='" + name + '\'' +
				", amount=" + amount +
				'}';
	}
}
