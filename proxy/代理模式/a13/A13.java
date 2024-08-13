package a13;

import java.lang.reflect.Method;

public class A13 {

    interface Foo {
        void foo();

        int bar();
    }

    interface InvokeHandler {

        /**
         * @param proxy  代理对象
         * @param method 调用的方法
         * @param args   方法参数
         * @return 返回值
         */
        Object invoke(Object proxy, Method method, Object[] args) throws Throwable;
    }

    /**
     * 目标类
     * jdk的代理类、目标类要实现共同的接口
     */
    static class Target implements Foo {

        @Override
        public void foo() {
            System.out.println("目标 foo()执行");
        }

        @Override
        public int bar() {
            System.out.println("目标 bar()执行");
            return 100;
        }
    }

    // 通过代理，调用目标方法
    public static void main(String[] args) {
        Foo proxy = new $Proxy0(new InvokeHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                // 1.功能增强
                System.out.println("前置功能增强...");

                // 2.调用目标类指定的method方法
                return method.invoke(new Target(), args);
            }
        });
        proxy.foo();
        proxy.bar();
    }
}
