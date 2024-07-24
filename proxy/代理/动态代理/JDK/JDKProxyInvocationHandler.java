package 动态代理.JDK;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 3.定义一个 JDK 动态代理类
 * JDK动态代理只能代理实现了接口的类
 */
public class JDKProxyInvocationHandler implements InvocationHandler {
    /**
     * 代理类中的目标对象
     */
    private final Object target;

    // 注入目标对象
    public JDKProxyInvocationHandler(Object target) {
        this.target = target;
    }

    /**
     * 在invoke()中会调用目标对象的方法（被代理类的方法）并自定义一些处理逻辑；
     * @param proxy       被代理的对象（需要增强的对象）
     * @param method      被代理的类中的方法（需要增强的方法）
     * @param args        方法入参
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException {
        // 调用方法之前，我们可以添加自己的操作
        System.out.println("before method " + method.getName());

        // 通过反射，调用目标对象的方法
        Object result = method.invoke(target, args);

        // 调用方法之后，我们同样可以添加自己的操作
        System.out.println("after method " + method.getName());

        return result;
    }
}
