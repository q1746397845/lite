package com.baidu.shop.component;

import com.baidu.shop.constant.MqMessageConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

/**
 * @ClassName MrRabbitMQ
 * @Description: TODO
 * @Author lite
 * @Date 2020/10/23
 * @Version V1.0
 **/
@Component
@Slf4j
public class MrRabbitMQ implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback{

    private RabbitTemplate rabbitTemplate;


    //构造方法注入
    @Autowired
    public MrRabbitMQ(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        //这是是设置回调能收到发送到响应
        rabbitTemplate.setConfirmCallback(this);
        //如果设置备份队列则不起作用
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setReturnCallback(this);
    }

    public void send(Map<String,String> sendMsg, int delayTimes) {
        CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend(MqMessageConstant.ORDER_DELAY_Exchange_Name, MqMessageConstant.ORDER_ROUT_KEY_UPDATE,sendMsg,message ->{
            message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            message.getMessageProperties().setDelay(delayTimes * 1000);// 毫秒为单位，指定此消息的延时时长
            return message;
        },correlationId);
    }



    @Override
    public void confirm(CorrelationData correlationData, boolean b, String s) {
        if(b){
            log.info("消息发送成功:correlationData({}),ack({}),cause({})",correlationData,b,s);
        }else{
            log.error("消息发送失败:correlationData({}),ack({}),cause({})",correlationData,b,s);
        }
    }

    @Override
    public void returnedMessage(Message message, int i, String s, String s1, String s2) {
        //请注意!如果你使用了延迟队列插件，那么一定会调用该callback方法，因为数据并没有提交上去，而是提交在交换器中，过期时间到了才提交上去，并非是bug！你可以用if进行判断交换机名称来捕捉该报错

        //如果是延迟队列则忽略，使用插件延迟队列不管成功都会调用
        if(MqMessageConstant.ORDER_DELAY_Exchange_Name.equals(s1)) {
            return;
        }
        log.warn("消息丢失:exchange({}),route({}),replyCode({}),replyText({}),message:{}",s1,s2,i,s,message);

    }
}
