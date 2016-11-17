package org.domainobject.test.foo;

import java.util.HashMap;

public class Company extends Thing {
	
	private String name;
	private Person boss;
	private String[] tags = new String[] {"Service", "Customer", "Coffee"};
	private HashMap myPersonalThings = new HashMap();

	public Company(String name)
	{
		this.name = name;
		this.isOrganic = false;
//		myPersonalThings.put("ayco", "Holleman");
//		myPersonalThings.put("dennis", "seijts");
//		myPersonalThings.put("ruud", "alternburg");
//		myPersonalThings.put("wouter", "addink");
		myPersonalThings.put(new Person("Marianne",40,true), new Fruit("Banana",Color.YELLOW));
		myPersonalThings.put(new Person("Frits",45,true), new Fruit("Pear",Color.GREEN));
		myPersonalThings.put(new Person("Tineke",70,true), new Fruit("Grape",Color.BLUE));
		myPersonalThings.put(new Person("Eric",73,false), new Fruit[] {new Fruit("Grape",Color.GREEN), new Fruit("Pine Apple",Color.YELLOW)});
	}
	
	public void setBoss(Person boss) {
		this.boss = boss;
	}

}
