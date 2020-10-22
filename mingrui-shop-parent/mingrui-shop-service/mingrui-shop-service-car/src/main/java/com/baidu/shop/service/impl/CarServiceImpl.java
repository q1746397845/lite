package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.config.JwtConfig;
import com.baidu.shop.constant.MRshopConstant;
import com.baidu.shop.document.GoodsDoc;
import com.baidu.shop.dto.Car;
import com.baidu.shop.dto.UserInfo;
import com.baidu.shop.entity.SkuEntity;
import com.baidu.shop.entity.SpecParamEntity;
import com.baidu.shop.feign.GoodsFeign;
import com.baidu.shop.feign.SpecificationFeign;
import com.baidu.shop.repository.RedisRepository;
import com.baidu.shop.service.CarService;
import com.baidu.shop.service.GoodsService;
import com.baidu.shop.util.JSONUtil;
import com.baidu.shop.util.JwtUtils;
import com.baidu.shop.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.spatial3d.geom.GeoOutsideDistance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName CarServiceImpl
 * @Description: TODO
 * @Author lite
 * @Date 2020/10/19
 * @Version V1.0
 **/
@RestController
@Slf4j
public class CarServiceImpl extends BaseApiService implements CarService {

    @Autowired
    private RedisRepository redisRepository;

    @Autowired
    private GoodsFeign goodsFeign;

    @Autowired
    private JwtConfig jwtConfig;

    @Autowired
    private SpecificationFeign specificationFeign;
    @Override
    public Result<JSONObject> addCar(Car car,String token) {

        try {
            //获取用户信息
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());

            Car redisCar = redisRepository.getHash(MRshopConstant.USER_GOODS_CAR_PRE_ + userInfo.getId(), car.getSkuId() + "", Car.class);
            log.debug("通过key : {} ,skuid : {} 获取到的数据为 : {}",MRshopConstant.USER_GOODS_CAR_PRE_ + userInfo.getId(),car.getSkuId(),redisCar);

            if(ObjectUtil.isNotNull(redisCar)){//不是null的话说明购物车里有这件商品,只需要修改数量即可

                redisCar.setNum(redisCar.getNum() + car.getNum());
                redisRepository.setHash(MRshopConstant.USER_GOODS_CAR_PRE_ + userInfo.getId(),car.getSkuId() + "", JSONUtil.toJsonString(redisCar));
                log.debug("重新设置num: {}" , redisCar.getNum());
            }else{
                //通过skuid查到这件商品的信息,添加到redis的购物车里
                Result<SkuEntity> skuInfo = goodsFeign.getSkuBySkuId(car.getSkuId());

                if(skuInfo.getCode() == 200){
                    SkuEntity skuEntity = skuInfo.getData();
                    car.setPrice(skuEntity.getPrice().longValue());
                    car.setImage(skuEntity.getImages());
                    car.setTitle(skuEntity.getTitle());
                    car.setUserId(userInfo.getId());

                    JSONObject jsonObject = JSONObject.parseObject(skuEntity.getOwnSpec());
                    HashMap<String, Object> map = new HashMap<>();
                    jsonObject.forEach((k,v) ->{
                        Result<SpecParamEntity> specParamEntity = specificationFeign.getSpecParamById(Integer.valueOf(k));
                        map.put(specParamEntity.getData().getName(),v);
                    });

                    car.setOwnSpec(JSONUtil.toJsonString(map));
                }
                redisRepository.setHash(MRshopConstant.USER_GOODS_CAR_PRE_ + userInfo.getId(),car.getSkuId() + "", JSONUtil.toJsonString(car));
                log.debug("新增商品到购物车redis,key: {},skuId: {},car: {}" , MRshopConstant.USER_GOODS_CAR_PRE_ + userInfo.getId() ,car.getSkuId() , JSONUtil.toJsonString(car));
            }



        } catch (Exception e) {
            e.printStackTrace();
        }


        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> mergeCar(String clientCarList, String token) {

        //将json字符串转换成json对象
        JSONObject jsonObject = JSONObject.parseObject(clientCarList);

        //将json对象属性为clientCarList的数据取出来,并且转换成List集合
        List<Car> carList = JSONObject.parseArray(jsonObject.getJSONArray("clientCarList").toJSONString(), Car.class);
        carList.forEach(car -> {
            this.addCar(car,token);
        });


        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> getUserGoodsCar(String token) {
        List<Car> carList = new ArrayList<>();
        //获取用户信息
        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
            Map<String, String> map = redisRepository.getHash(MRshopConstant.USER_GOODS_CAR_PRE_ + userInfo.getId());

            map.forEach((k,v) ->{
                carList.add(JSONUtil.toBean(v,Car.class));
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        return this.setResultSuccess(carList);
    }

    @Override
    public Result<JSONObject> carNumUpdate(Integer type, Long skuId, String token) {
        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
            Car car = redisRepository.getHash(MRshopConstant.USER_GOODS_CAR_PRE_ + userInfo.getId(), skuId + "", Car.class);
            //type=1 购物车里的商品+1,type=2 购物车里的商品-1
            car.setNum(type == 1 ? car.getNum() + 1 : car.getNum() - 1);
            redisRepository.setHash(MRshopConstant.USER_GOODS_CAR_PRE_ + userInfo.getId(),skuId + "",JSONUtil.toJsonString(car));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> delCarData(Long skuId, String token) {
        List<Car> carList = new ArrayList<>();
        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());

            //删除购物车里的商品
            redisRepository.delHash(MRshopConstant.USER_GOODS_CAR_PRE_ + userInfo.getId(),skuId + "");

            //重新获取商品
            Map<String, String> map = redisRepository.getHash(MRshopConstant.USER_GOODS_CAR_PRE_ + userInfo.getId());


            map.forEach((k,v) ->{
                carList.add(JSONUtil.toBean(v,Car.class));
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.setResultSuccess(carList);
    }
}
