package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.dto.SpecGroupDTO;
import com.baidu.shop.dto.SpecParamDTO;
import com.baidu.shop.entity.SpecParamEntity;
import com.baidu.shop.mapper.SpecGroupMapper;
import com.baidu.shop.mapper.SpecParamMapper;
import com.baidu.shop.service.SpecificationService;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.entity.SpecGroupEntity;
import com.baidu.shop.util.BaiduBeanUtil;
import com.baidu.shop.util.ObjectUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName SpecGroupServiceImpl
 * @Description: TODO
 * @Author lite
 * @Date 2020/9/3
 * @Version V1.0
 **/
@RestController
public class SpecificationServiceImpl extends BaseApiService implements SpecificationService {

    @Resource
    private SpecGroupMapper specGroupMapper;

    @Resource
    private SpecParamMapper specParamMapper;

    @Override
    public Result<List<SpecGroupEntity>> getSpecGroupInfo(SpecGroupDTO specGroupDTO) {


        if(ObjectUtil.isNull(specGroupDTO.getCid())) return this.setResultError("商品分类id为空");

        Example example = new Example(SpecGroupEntity.class);

        example.createCriteria().andEqualTo("cid",specGroupDTO.getCid());

        List<SpecGroupEntity> specGroupEntities = specGroupMapper.selectByExample(example);


        return this.setResultSuccess(specGroupEntities);
    }


    @Transactional
    @Override
    public Result<List<JSONObject>> saveSpecGroup(SpecGroupDTO specGroupDTO) {

        specGroupMapper.insertSelective(BaiduBeanUtil.copyProperties(specGroupDTO,SpecGroupEntity.class));

        return this.setResultSuccess();
    }

    @Transactional
    @Override
    public Result<List<JSONObject>> editSpecGroup(SpecGroupDTO specGroupDTO) {

        specGroupMapper.updateByPrimaryKeySelective(BaiduBeanUtil.copyProperties(specGroupDTO,SpecGroupEntity.class));
        return this.setResultSuccess();
    }

    @Transactional
    @Override
    public Result<List<JSONObject>> deleteSpecGroup(Integer id) {



        //判断当前组有没有绑定参数,如果绑定了不能被删除
        Example example = new Example(SpecParamEntity.class);
        example.createCriteria().andEqualTo("groupId", id);
        List<SpecParamEntity> list = specParamMapper.selectByExample(example);

        if(list.size() > 0){
            return this.setResultError("当前组下有参数,不能被删除");
        }

        specGroupMapper.deleteByPrimaryKey(id);

        return this.setResultSuccess();
    }


    //-------------------------规格参数crud-------------------------------

    @Override
    public Result<List<SpecParamEntity>> getSpecParamInfo(SpecParamDTO specParamDTO) {

        //如果规格组id不是空的话 通过规格组id查询该规格组拥有的规格参数
        //if(ObjectUtil.isNull(specParamDTO.getGroupId())) return this.setResultError("没有规格组id");

        Example example = new Example(SpecParamEntity.class);

        Example.Criteria criteria = example.createCriteria();

        //通过规格组id查询
        if(ObjectUtil.isNotNull(specParamDTO.getGroupId())){
            criteria .andEqualTo("groupId", specParamDTO.getGroupId());
        }

        if(ObjectUtil.isNotNull(specParamDTO.getCid())){
            criteria.andEqualTo("cid",specParamDTO.getCid());
        }

        if(ObjectUtil.isNotNull(specParamDTO.getSearching())){
            criteria.andEqualTo("searching",specParamDTO.getSearching());
        }
        if(ObjectUtil.isNotNull(specParamDTO.getGeneric())){
            criteria.andEqualTo("generic",specParamDTO.getGeneric());
        }

        System.out.println(specParamDTO);
        List<SpecParamEntity> list = specParamMapper.selectByExample(example);


        return this.setResultSuccess(list);
    }

    @Transactional
    @Override
    public Result<List<JSONObject>> saveSpecParam(SpecParamDTO specParamDTO) {

        specParamMapper.insertSelective(BaiduBeanUtil.copyProperties(specParamDTO,SpecParamEntity.class));

        return this.setResultSuccess();
    }

    @Transactional
    @Override
    public Result<List<JSONObject>> editSpecParam(SpecParamDTO specParamDTO) {

        specParamMapper.updateByPrimaryKeySelective(BaiduBeanUtil.copyProperties(specParamDTO,SpecParamEntity.class));

        return this.setResultSuccess();
    }

    @Transactional
    @Override
    public Result<List<JSONObject>> deleteSpecParam(Integer id) {

        specParamMapper.deleteByPrimaryKey(id);

        return this.setResultSuccess();
    }

    @Override
    public Result<SpecParamEntity> getSpecParamById(Integer id) {
        SpecParamEntity specParamEntity = specParamMapper.selectByPrimaryKey(id);
        return this.setResultSuccess(specParamEntity);
    }
}
