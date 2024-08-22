package a13;

import a13.A13.Foo;
import a13.A13.InvokeHandler;

import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;


/**
 * 代理类
 * <p>
 * jdk的代理类、目标类要实现共同的接口
 */
public class $Proxy0 implements Foo {
    private InvokeHandler handler;

    public $Proxy0(InvokeHandler handler) {
        this.handler = handler;
    }

    @Override
    public void foo() {
        try {
            handler.invoke(this, foo, new Object[0]);
        } catch (RuntimeException | Error e) {  // 运行时异常，直接抛出
            throw e;
        } catch (Throwable e) { // 检查异常，得转换成运行时异常后再抛出（不能直接抛）
            throw new UndeclaredThrowableException(e);
        }
    }

    @Override
    public int bar() {
        try {
            Object result = handler.invoke(this, bar, new Object[0]);
            return (int) result;
        } catch (RuntimeException | Error e) {  // 运行时异常，直接抛出
            throw e;
        } catch (Throwable e) { // 检查异常，得转换成运行时异常后再抛出（不能直接抛）
            throw new UndeclaredThrowableException(e);
        }
    }

    // 让方法对象只被获取一次
    static Method foo;
    static Method bar;

    static {
        try {
            foo = Foo.class.getMethod("foo");
            bar = Foo.class.getMethod("bar");
        } catch (NoSuchMethodException e) {
            throw new NoSuchMethodError(e.getMessage());
        }
    }
}
