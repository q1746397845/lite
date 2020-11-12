package com.baidu.shop.business.impl;

import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.business.OrderService;
import com.baidu.shop.component.MrRabbitMQ;
import com.baidu.shop.config.JwtConfig;
import com.baidu.shop.constant.MRshopConstant;
import com.baidu.shop.dto.Car;
import com.baidu.shop.dto.OrderDTO;
import com.baidu.shop.dto.OrderInfo;
import com.baidu.shop.dto.UserInfo;
import com.baidu.shop.entity.OrderDetailEntity;
import com.baidu.shop.entity.OrderEntity;
import com.baidu.shop.entity.OrderStatusEntity;
import com.baidu.shop.feign.GoodsFeign;
import com.baidu.shop.mapper.OrderDetailMapper;
import com.baidu.shop.mapper.OrderMapper;
import com.baidu.shop.mapper.OrderStatusMapper;
import com.baidu.shop.redis.repository.RedisRepository;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName OrderServiceImpl
 * @Description: TODO
 * @Author lite
 * @Date 2020/10/21
 * @Version V1.0
 **/
@RestController
public class OrderServiceImpl extends BaseApiService implements OrderService {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private OrderDetailMapper orderDetailMapper;

    @Resource
    private OrderStatusMapper orderStatusMapper;

    @Resource
    private RedisRepository redisRepository;

    @Resource
    private IdWorker idWorker;

    @Resource
    private JwtConfig jwtConfig;

    @Resource
    private GoodsFeign goodsFeign;

    @Resource
    private MrRabbitMQ mrRabbitMQ;


    @Transactional
    @Override
    public Result<Long> createOrder(OrderDTO orderDTO, String token) {
        //通过雪花算法生成id
        long orderId = idWorker.nextId();
        try {
            //key --> 订单id,value-->订单的商品数量和商品的id
            Map<String, String> orderIdAndOrderDetailMap = new HashMap<>();
            //key --> 订单商品的Id,value --> 订单商品的数量
            Map<String, String> orderDetail = new HashMap<>();

            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
            Date date = new Date();

            //生成订单
            OrderEntity orderEntity = new OrderEntity();
            orderEntity.setOrderId(orderId);//订单id
            orderEntity.setCreateTime(date);//创建时间
            orderEntity.setSourceType(1);//写死的PC端,如果项目健全了以后,这个值应该是常量
            orderEntity.setBuyerRate(1);//买家是否已经评价,1没有
            orderEntity.setInvoiceType(1);//发票类型
            orderEntity.setUserId(userInfo.getId() + "");//用户id
            orderEntity.setBuyerMessage("商品very good");//买家留言
            orderEntity.setBuyerNick(userInfo.getUsername());//用户昵称
            orderEntity.setPaymentType(orderDTO.getPayType());//订单类型

            //用来存放总金额
            List<Long> longs = Arrays.asList(0L);

            List<OrderDetailEntity> orderDetailList = Arrays.asList(orderDTO.getSkuIds().split(",")).stream().map(skuId -> {

                Car car = redisRepository.getHash(MRshopConstant.USER_GOODS_CAR_PRE_ + userInfo.getId(), skuId, Car.class);

                if (ObjectUtil.isNull(car)) {
                    throw new RuntimeException("数据异常");
                }
                OrderDetailEntity orderDetailEntity = new OrderDetailEntity();
                orderDetailEntity.setImage(car.getImage());
                orderDetailEntity.setNum(car.getNum());
                orderDetailEntity.setOrderId(orderId);
                orderDetailEntity.setOwnSpec(car.getOwnSpec());
                orderDetailEntity.setTitle(car.getTitle());
                orderDetailEntity.setPrice(car.getPrice());
                orderDetailEntity.setSkuId(car.getSkuId());

                longs.set(0,car.getPrice() * car.getNum() + longs.get(0));


                //更新库存-->用户已经下单了,这个时候减库存
                goodsFeign.updateGoodsStock(car.getNum(),car.getSkuId());


                orderDetail.put(car.getSkuId()+"",car.getNum()+"");


                return orderDetailEntity;
            }).collect(Collectors.toList());

            orderEntity.setTotalPay(longs.get(0));
            orderEntity.setActualPay(longs.get(0));

            OrderStatusEntity orderStatusEntity = new OrderStatusEntity();
            orderStatusEntity.setCreateTime(date);
            orderStatusEntity.setOrderId(orderId);
            orderStatusEntity.setStatus(1);//1未付款
            //orderStatusEntity.setPaymentTime();

            //入库
            orderMapper.insertSelective(orderEntity);
            orderDetailMapper.insertList(orderDetailList);
            orderStatusMapper.insertSelective(orderStatusEntity);

            //通过用户id和skuid删除购物车中的数据
            Arrays.asList(orderDTO.getSkuIds().split(",")).stream().forEach(skuId ->{
                redisRepository.delHash(MRshopConstant.USER_GOODS_CAR_PRE_ + userInfo.getId() ,skuId);
            });
            //此时要保证redis和myqsl双写一致性


            //发送一条消息,如果用户半个小时没付款,将订单取消,库存恢复
            orderIdAndOrderDetailMap.put(orderId + "", JSONUtil.toJsonString(orderDetail));
            mrRabbitMQ.send(orderIdAndOrderDetailMap,60);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return this.setResult(HTTPStatus.OK,"支付成功",orderId + "");
    }

    @Override
    public Result<OrderInfo> getOrderInfoByOrderId(Long orderId) {
        OrderEntity orderEntity = orderMapper.selectByPrimaryKey(orderId);
        OrderInfo orderInfo = BaiduBeanUtil.copyProperties(orderEntity, OrderInfo.class);

        Example example = new Example(OrderDetailEntity.class);
        example.createCriteria().andEqualTo("orderId",orderId);
        List<OrderDetailEntity> OrderDetailList = orderDetailMapper.selectByExample(example);

        orderInfo.setOrderDetailList(OrderDetailList);

        OrderStatusEntity orderStatusEntity = orderStatusMapper.selectByPrimaryKey(orderId);

        orderInfo.setOrderStatusEntity(orderStatusEntity);

        return this.setResultSuccess(orderInfo);
    }

    //修改订单状态,修改为已支付
    @Override
    public Result<OrderInfo> updateOrderStatus(String orderId) {
        OrderStatusEntity orderStatusEntity = new OrderStatusEntity();
        orderStatusEntity.setOrderId(Long.valueOf(orderId));
        orderStatusEntity.setStatus(2);
        orderStatusMapper.updateByPrimaryKeySelective(orderStatusEntity);

        return this.setResultSuccess();
    }
}
