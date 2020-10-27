//package com.baidu.shop.config;
//
//import com.baidu.shop.constant.MqMessageConstant;
//import com.rabbitmq.client.impl.AMQImpl;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.core.*;
//import org.springframework.amqp.rabbit.connection.ConnectionFactory;
//import org.springframework.amqp.rabbit.core.RabbitAdmin;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @ClassName RabbitConfig
// * @Description: TODO
// * @Author lite
// * @Date 2020/10/23
// * @Version V1.0
// **/
//@Configuration
//@Slf4j
//public class RabbitMqConfig {
//
//
//
//
//
//    @Bean
//    public Queue delayPayQueue() {
//        return new Queue(MqMessageConstant.ORDER_DELAY_QUEUE_NAME, true);
//    }
//
//
////    @Bean
////    FanoutExchange delayExchange(){
////        Map<String, Object> args = new HashMap<String, Object>();
////        args.put("x-delayed-type", "direct");
////        FanoutExchange topicExchange = new FanoutExchange(MqMessageConstant.ORDER_DELAY_Exchange_Name, true, false, args);
////        topicExchange.setDelayed(true);
////        return topicExchange;
////    }
//
//    //自定义交换机
//    @Bean
//    CustomExchange delayExchange(){
//        Map<String, Object> args = new HashMap<String, Object>();
//        args.put("x-delayed-type", "direct");
//        CustomExchange customExchange = new CustomExchange(MqMessageConstant.ORDER_DELAY_Exchange_Name,"x-delayed-message",true, false,args);
//
//        return customExchange;
//    }
//
//    // 绑定延时队列与交换机
//    @Bean
//    public Binding delayPayBind() {
//
//        return BindingBuilder.bind(delayPayQueue()).to(delayExchange()).with(MqMessageConstant.ORDER_ROUT_KEY_UPDATE).noargs();
//    }
//
//}
