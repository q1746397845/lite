package com.baidu.shop.service.impl;

import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.BrandDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.entity.SpuEntity;
import com.baidu.shop.mapper.BrandMapper;
import com.baidu.shop.mapper.CategoryMapper;
import com.baidu.shop.mapper.SpuMapper;
import com.baidu.shop.service.GoodsService;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.util.BaiduBeanUtil;
import com.baidu.shop.util.ObjectUtil;
import com.baidu.shop.util.StringUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName GoodsService
 * @Description: TODO
 * @Author lite
 * @Date 2020/9/7
 * @Version V1.0
 **/
@RestController
public class GoodsServiceImpl extends BaseApiService implements GoodsService {

    @Resource
    private SpuMapper spuMapper;

    @Resource
    private BrandServiceImpl brandService;

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private BrandMapper brandMapper;


    @Override
    public Result<PageInfo<SpuDTO>> getSpuInfo(SpuDTO spuDTO) {

        //分页
        if(ObjectUtil.isNotNull(spuDTO.getPage()) && ObjectUtil.isNotNull(spuDTO.getRows())){
            PageHelper.startPage(spuDTO.getPage(),spuDTO.getRows());
        }

        //构建条件查询
        Example example = new Example(SpuEntity.class);
        Example.Criteria criteria = example.createCriteria();

        if(ObjectUtil.isNotNull(spuDTO.getSaleable()) && spuDTO.getSaleable() != 2){
            criteria.andEqualTo("saleable",spuDTO.getSaleable());
        }
        if(StringUtil.isNotEmpty(spuDTO.getTitle())){
            criteria.andLike("title","%" + spuDTO.getTitle() + "%");
        }

        //排序
        if(StringUtil.isNotEmpty(spuDTO.getSort())) example.setOrderByClause(spuDTO.getOrderByClause());

        List<SpuEntity> list = spuMapper.selectByExample(example);



        //查询品牌名称
//        List<SpuDTO> spuDtoList  = list.stream().map(spuEntity -> {
//            //转换实体类
//            SpuDTO spuDTO1 = BaiduBeanUtil.copyProperties(spuEntity, SpuDTO.class);
//
//            //通过品牌id查询品牌名称
//            BrandDTO brandDTO = new BrandDTO();
//            brandDTO.setId(spuEntity.getBrandId());
//            Result<PageInfo<BrandEntity>> brandInfo = brandService.getBrand(brandDTO);
//            if (ObjectUtil.isNotNull(brandInfo)) {
//                List<BrandEntity> list1 = brandInfo.getData().getList();
//                if (!list1.isEmpty() && list1.size() == 1) {
//                    spuDTO1.setBrandName(list1.get(0).getName());
//                }
//            }
//
//
//            //通过品牌id查询品牌名称
//           List<BrandEntity> brnadList = brandMapper.getBrnadNameByBrandId(spuEntity.getBrandId());
//           if (!brnadList.isEmpty() && brnadList.size() == 1) spuDTO1.setBrandName(brnadList.get(0).getName());
//
//            //查询商品分类
//            //selectByIdList:当你这个参数是个list集合,这个list集里面又存储了任意个id, 这个方法会自动的遍历集合 ,根据每个id进行查询封装,返回一个list
//            //将查到的商品分类name用 / 拼接在一起
//            List<CategoryEntity> categoryEntityList = categoryMapper.selectByIdList(Arrays.asList(spuEntity.getCid1(), spuEntity.getCid2(), spuEntity.getCid3()));
//
//            String caterogyName  = categoryEntityList.stream().map(category -> {
//                return category.getName();
//            }).collect(Collectors.joining("/"));
//
//
//            spuDTO1.setCategoryName(caterogyName);
//
//            return spuDTO1;
//
//        }).collect(Collectors.toList());


        //代码简化
        List<SpuDTO> spuDtoList = list.stream().map(spuEntity -> {
            List<SpuDTO> spuList = spuMapper.getSpuDTOBySpuId(spuEntity.getId());
            return spuList.get(0);
        }).collect(Collectors.toList());

        List<Integer> listId = list.stream().map(spuEntity -> spuEntity.getId()).collect(Collectors.toList());



//        String idArr = "";
//        for (SpuEntity spuEntity : list) {
//            idArr += spuEntity.getId() + ",";
//        }
//        String ids = idArr.substring(0, idArr.length() - 1);
//
//        List<SpuDTO> spuDtoList = spuMapper.getSpuDTOBySpuIds(ids);


        PageInfo<SpuEntity> pageInfo = new PageInfo<>(list);

        //要返回的是dto的数据,但是pageinfo中没有总条数
        long total = pageInfo.getTotal();
        //借用一下message属性
        return this.setResult(HTTPStatus.OK,"" + total, spuDtoList);
    }
}
