package com.baidu.shop.consumer;

import com.baidu.shop.constant.MqMessageConstant;
import com.baidu.shop.entity.OrderDetailEntity;
import com.baidu.shop.entity.OrderStatusEntity;
import com.baidu.shop.feign.GoodsFeign;
import com.baidu.shop.mapper.OrderDetailMapper;
import com.baidu.shop.mapper.OrderMapper;
import com.baidu.shop.mapper.OrderStatusMapper;
import com.baidu.shop.util.JSONUtil;
import com.rabbitmq.client.Channel;
import io.lettuce.core.dynamic.annotation.Key;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.amqp.core.Message;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @ClassName RabbitmqConsumer
 * @Description: TODO
 * @Author lite
 * @Date 2020/10/23
 * @Version V1.0
 **/
@Component
@Slf4j
public class RabbitmqConsumer {

    @Resource
    private OrderDetailMapper orderDetailMapper;

    @Resource
    private OrderStatusMapper orderStatusMapper;

    @Resource
    private GoodsFeign goodsFeign;


    //@RabbitListener(queues = MqMessageConstant.ORDER_DELAY_QUEUE_NAME)
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(
                            value = MqMessageConstant.ORDER_DELAY_QUEUE_NAME,
                            durable = "true"
                    ),
                    exchange = @Exchange(
                            value = MqMessageConstant.ORDER_DELAY_Exchange_Name,
                            ignoreDeclarationExceptions = "true",
                            type = "x-delayed-message",
                            durable = "true",
                            arguments = @Argument(name = "x-delayed-type",value = "direct")
                    ),
                    key = {MqMessageConstant.ORDER_ROUT_KEY_UPDATE}
            )
    )
    @Transactional
    public void process(Map<String,String> sendMsg, Message message, Channel channel) throws IOException {
        try {
            log.info("处理延迟队列的内容:[{}]", sendMsg);
            sendMsg.forEach((orderId,orderDetail) ->{
                //查询订单状态
                OrderStatusEntity orderStatusEntity = orderStatusMapper.selectByPrimaryKey(Long.valueOf(orderId));

                if(orderStatusEntity.getStatus() == 1){//订单未支付
                    //将订单状态修改为 5 ,交易关闭,将库存释放
                    OrderStatusEntity orderStatusEntity1 = new OrderStatusEntity();
                    orderStatusEntity1.setOrderId(orderStatusEntity.getOrderId());
                    orderStatusEntity1.setStatus(5);
                    orderStatusEntity1.setCloseTime(new Date());
                    orderStatusMapper.updateByPrimaryKeySelective(orderStatusEntity1);

                    //释放库存
                    Map<String, String> OrderDetailMap = JSONUtil.toMapValueString(orderDetail);
                    OrderDetailMap.forEach((skuId,num) ->{
                        goodsFeign.addGoodsStock(Integer.valueOf(num),Long.valueOf(skuId));
                    });

                }else{
                    log.info("订单已支付");
                }
            });

            channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
            log.info("超时信息处理完毕");
        } catch (Exception e) {
            log.error("处理失败:{}", e.getMessage());
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }
}
