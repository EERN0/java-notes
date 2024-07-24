package 静态代理;

import common.SmsService;

/**
 * 3.创建代理类，也实现发送短信的接口
 */
public class SmsProxy implements SmsService {
    private final SmsService smsService;

    // 注入父类对象
    public SmsProxy(SmsService smsService) {
        this.smsService = smsService;
    }

    @Override
    public String send(String message) {
        //调用方法之前，可以添加自己的操作
        System.out.println("before method send()");

        smsService.send(message);

        //调用方法之后，我们同样可以添加自己的操作
        System.out.println("after method send()");
        return null;
    }
}
