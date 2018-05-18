package com.newbiest.guava;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import lombok.Data;
import org.junit.Test;

/**
 * Created by guoxunbo on 2018/5/8.
 */
public class EventBusTest {


    @Test
    public void eventBusTest() {
        // 注册事件订阅者 如果有多个订阅者都订阅了相同事件，
        // 则每个订阅者都会收到消息，收到消息的顺序按照register的顺序来决定
        EventBus eventBus = new EventBus();
        eventBus.register(new EsbListener());

        EsbMessage esbMessage = new EsbMessage();
        esbMessage.setMessage("This is a EsbMessage");

        eventBus.post(esbMessage);
        eventBus.post("This is a StringMessage");

    }

}

/**
 * 传递消息的对象
 */
@Data
class EsbMessage {

    private String message;

}

/**
 * 事件订阅者
 * 一个事件监听者可以订阅多个事件，Guava会通过事件类型来和订阅方法的形参来决定到底调用subscriber的哪个订阅方法
 */
class EsbListener {


    @Subscribe
    private void onMessage(EsbMessage esbMessage) {
        System.out.println("Receive esbMessage " + esbMessage.getMessage());
    }

    @Subscribe
    private void onMessage(String str) {
        System.out.println("Receive stringMessage " + str);
    }

}