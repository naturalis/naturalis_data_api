package org.domainobject.test.foo;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class Person extends Organism {
	
	public String name;
	public int age;
	public boolean married;
	public short[] luckyNumbers = new short[] {(short) 7};
	public List<Fruit> favoriteFruits;
	public Person boss;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Person(String name, int age, boolean married, Fruit... fruits)
	{
		this.type = "__PERSON__";
		this.isOrganic = true;
		this.name = name;
		this.age = age;
		this.married = married;
		this.favoriteFruits = new ArrayList(Arrays.asList(fruits));
	}
	
	
	
	

}
