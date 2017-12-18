package nl.naturalis.nba.utils.reflect;

public class Person {

  private String name;
  private int age;

  public Person() {}

  @SuppressWarnings("unused")
  private Person(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    this.age = age;
  }

  public double calculateMinimumWage(int multiplier, boolean withBonus) {
    return (age * multiplier) + (withBonus ? 5000 : 0);
  }

}
