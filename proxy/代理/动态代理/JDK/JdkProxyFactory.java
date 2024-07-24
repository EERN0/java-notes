package 动态代理.JDK;

import common.SmsService;
import common.SmsServiceImpl;

import java.lang.reflect.Proxy;

/**
 * 4.获取代理对象的工厂类
 */
public class JdkProxyFactory {

    // 通过Proxy类的newProxyInstance()创建代理对象，代理对象在调用方法时，
    // 实际会调用到自己实现的InvocationHandler接口的类中的invoke()方法，
    // 在invoke()中调用目标对象的方法（被代理类的方法）并自定义一些处理逻辑
    public static Object getProxy(Object target) {
        return Proxy.newProxyInstance(
                target.getClass().getClassLoader(),     // 被代理的类的类加载器
                target.getClass().getInterfaces(),      // 被代理的类中实现的接口，可指定多个
                new JDKProxyInvocationHandler(target)   // 代理对象对应的自定义 InvocationHandler
        );
    }

    // 调用代理对象
    public static void main(String[] args) {
        SmsService smsService = (SmsService) JdkProxyFactory.getProxy(new SmsServiceImpl());
        smsService.send("java");
    }
}
