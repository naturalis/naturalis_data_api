package nl.naturalis.nba.utils.reflect;

public class Person {

  public static int getAverageAge() {
    return 40;
  }

  private String name;
  private int age;

  public Person() {}

  @SuppressWarnings("unused")
  private Person(String name) {
    this.name = name;
  }

  @SuppressWarnings("unused")
  private Person(String name, int age) {
    this.name = name;
    this.age = age;
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

  @SuppressWarnings("unused")
  public double calculateMinimumWage(int multiplier, boolean withBonus) {
    return (age * multiplier) + (withBonus ? 1000 : 0);
  }

}
