package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.comment.MyLog;
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

import java.util.*;

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


    /**
     *
     * @param token  前台传来的用户信息
     * @param carPre redis库 key的前缀
     * @param car  购物车数据
     */
    private void updateRedisByTokenAndUserCarPre(String token,String carPre,Car car){

        //获取用户信息
        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());

            Car redisCar = redisRepository.getHash(carPre + userInfo.getId(), car.getSkuId() + "", Car.class);
            log.debug("通过key : {} ,skuid : {} 获取到的数据为 : {}",carPre + userInfo.getId(),car.getSkuId(),redisCar);

            if(ObjectUtil.isNotNull(redisCar)){
                redisCar.setNum(redisCar.getNum() + car.getNum());
                redisRepository.setHash(carPre + userInfo.getId(),car.getSkuId() + "", JSONUtil.toJsonString(redisCar));
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
                redisRepository.setHash(carPre + userInfo.getId(),car.getSkuId() + "", JSONUtil.toJsonString(car));
                log.debug("新增商品到购物车redis,key: {},skuId: {},car: {}" , carPre + userInfo.getId() ,car.getSkuId() , JSONUtil.toJsonString(car));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private List<Car> getGoodsCarAndDelGoodsCar(String carPre,Integer userId){

        List<Car> carList = new ArrayList<>();

        Map<String, String> map = redisRepository.getHash(carPre + userId);

        map.forEach((k,v) ->{
            carList.add(JSONUtil.toBean(v,Car.class));
        });

        return carList;
    }

    //增加购物车
    @Override
    @MyLog(operationType = "新增",operationModel = "购物车模块",operation = "用户新增商品到购物车")
    public Result<JSONObject> addCar(Car car,String token) {

        this.updateRedisByTokenAndUserCarPre(token,MRshopConstant.USER_GOODS_CAR_PRE_,car);


        return this.setResultSuccess();
    }

    //合并购物车
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
        //获取用户信息
        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());

            List<Car> carList = this.getGoodsCarAndDelGoodsCar(MRshopConstant.USER_GOODS_CAR_PRE_, userInfo.getId());

            return this.setResultSuccess(carList);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return this.setResultError("获取错误");
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
    public Result<JSONObject> delCarData(String skuIds, String token) {
        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());

            Arrays.asList(skuIds.split(",")).stream().forEach(skuId ->{

                //获取到要从购物车里删除的商品
                Car car = redisRepository.getHash(MRshopConstant.USER_GOODS_CAR_PRE_ + userInfo.getId(), skuId, Car.class);

                //将删除的商品放到删除的redis库里
                this.updateRedisByTokenAndUserCarPre(token, MRshopConstant.USER_DEL_GOODS_CAR_PRE_, car);

                //删除购物车里的数据
                redisRepository.delHash(MRshopConstant.USER_GOODS_CAR_PRE_ + userInfo.getId(), car.getSkuId() + "");

            });

            //将删除的商品查询出返回去
            List<Car> delCarList = this.getGoodsCarAndDelGoodsCar(MRshopConstant.USER_DEL_GOODS_CAR_PRE_, userInfo.getId());

            return this.setResultSuccess(delCarList);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.setResultError("错误");
    }

    @Override
    public Result<JSONObject> getUserDelGoodsCar(String token) {
        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
            List<Car> carList = this.getGoodsCarAndDelGoodsCar(MRshopConstant.USER_DEL_GOODS_CAR_PRE_, userInfo.getId());

            return this.setResultSuccess(carList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.setResultError("数据错误");
    }

    @Override
    public Result<JSONObject> addUserGoodsCarAndDelUserDelGoodsCar(Car car, String token) {

        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());

            //清除用户删除的购物车数据
            redisRepository.delHash(MRshopConstant.USER_DEL_GOODS_CAR_PRE_+userInfo.getId(),car.getSkuId() + "");

            //增加到购物车
            this.updateRedisByTokenAndUserCarPre(token,MRshopConstant.USER_GOODS_CAR_PRE_,car);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.setResultSuccess();
    }
}
