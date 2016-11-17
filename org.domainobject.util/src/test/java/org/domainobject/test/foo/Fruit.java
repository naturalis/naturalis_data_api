package org.domainobject.test.foo;

public class Fruit extends Organism {
	
	public String name;
	public Color color;

	public Fruit(String name,Color color)
	{
		this.isOrganic = true;
		this.type = "__FRUIT__";
		this.name=name;
		this.color=color;
	}

}
