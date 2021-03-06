package com.baidu.shop.constant;

/**
 * @ClassName MqMessageConstant
 * @Description: TODO
 * @Author lite
 * @Date 2020/10/12
 * @Version V1.0
 **/
public class MqMessageConstant {
    //spu交换机，routingkey
    public static final String SPU_ROUT_KEY_SAVE="spu.save";
    public static final String SPU_ROUT_KEY_UPDATE="spu.update";
    public static final String SPU_ROUT_KEY_DELETE="spu.delete";

    //spu-es的队列
    public static  final String SPU_QUEUE_SEARCH_SAVE="spu_queue_es_save";
    public static  final String SPU_QUEUE_SEARCH_UPDATE="spu_queue_es_update";
    public static  final String SPU_QUEUE_SEARCH_DELETE="spu_queue_es_delete";

    //spu-page的队列
    public static  final String SPU_QUEUE_PAGE_SAVE="spu_queue_page_save";
    public static  final String SPU_QUEUE_PAGE_UPDATE="spu_queue_page_update";
    public static  final String SPU_QUEUE_PAGE_DELETE="spu_queue_page_delete";


    public static final String ALTERNATE_EXCHANGE = "exchange.ae";
    public static final String EXCHANGE = "exchange.mr";



    // 订单延期交换机 name
    public static final String ORDER_DELAY_Exchange_Name = "delay.exchange";

    //订单延期 队列name
    public static final String ORDER_DELAY_QUEUE_NAME = "delay.queue";

    //订单的routingkey
    public static final String ORDER_ROUT_KEY_UPDATE = "order.update";



    //Dead Letter Exchanges
    public static final String EXCHANGE_DLX = "exchange.dlx";
    public static final String EXCHANGE_DLRK = "dlx.rk";
    public static final Integer MESSAGE_TIME_OUT = 5000;
    public static final String QUEUE = "queue.mr";
    public static final String QUEUE_AE = "queue.ae";
    public static final String QUEUE_DLX = "queue.dlx";
    public static final String ROUTING_KEY = "mrkey";
}
