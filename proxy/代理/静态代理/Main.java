package 静态代理;

import common.SmsService;
import common.SmsServiceImpl;


// 4.把目标对象注入代理类中，调用执行
public class Main {
    public static void main(String[] args) {
        // 多态，父类指针指向子类对象
        SmsService smsService = new SmsServiceImpl();
        // 把目标对象注入代理对象
        SmsProxy smsProxy = new SmsProxy(smsService);
        smsProxy.send("java");
    }
}
