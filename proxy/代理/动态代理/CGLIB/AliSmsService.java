package 动态代理.CGLIB;

/**
 * 1.实现一个使用阿里云发送短信的类，这是目标类（要被代理的类）
 *
 * 不能用final修饰类、方法，因为CGLIB动态代理是通过Enhancer生成目标类的子类
 */
public /*final*/ class AliSmsService {
    public String send(String message) {
        System.out.println("send message:" + message);
        return message;
    }
}
