package ck;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 反射
 */
public class MyReflect {
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchFieldException, NoSuchMethodException, InvocationTargetException {

        /**
         * 获取 Person 类的Class对象，并创建Person类对象
         */

        // 1.类名.class获取该类的Class对象
        Class<?> p0Class = Person.class;

        // 2.通过 Class.forName()传入类的包名+路径名 获取Class对象
        Class<?> p1Class = Class.forName("ck.Person");
        Person p1 = (Person) p1Class.newInstance();

        // 3.创建Person类对象，对象调用getClass()创建Class类对象
        Person p2 = new Person();
        Class<?> p2Class = p2.getClass();

        // 4.类加载器ClassLoader，传入类路径获取Class对象
        Class<?> aClass = ClassLoader.getSystemClassLoader().loadClass("ck.Person");

        /**
         * 获取Person类中定义的所有方法
         */
        Method[] methods = p1Class.getDeclaredMethods();
        for (Method method : methods) {
            System.out.println(method.getName());
        }

        /**
         * 调用private方法
         */
        Method aPrivateMethod = p1Class.getDeclaredMethod("aPrivateMethod");
        // 为了调用private方法，取消安全检查
        aPrivateMethod.setAccessible(true);
        aPrivateMethod.invoke(p1);


        /**
         * 获取指定属性字段
         */
        Field field = p1Class.getDeclaredField("name");
        // 为了对类中的属性进行修改，取消安全检查
        field.setAccessible(true);
        // 修改对象的属性
        field.set(p1, "aaaaa");

        System.out.println(p1.getName());   // aaa

    }
}
