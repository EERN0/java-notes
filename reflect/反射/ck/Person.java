package ck;

public class Person {
    public String name;

    public Person() {
        name = "kk";
    }

    public void aMethod(String s) {
        System.out.println("I am " + s);
    }

    private void aPrivateMethod() {
        System.out.println("name is " + name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
