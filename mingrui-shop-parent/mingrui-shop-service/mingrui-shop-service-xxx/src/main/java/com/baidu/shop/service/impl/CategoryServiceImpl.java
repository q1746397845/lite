package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.entity.CategoryBrandEntity;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.entity.SpecGroupEntity;
import com.baidu.shop.mapper.CategoryMapper;
import com.baidu.shop.service.CategoryService;
import com.baidu.shop.util.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @ClassName CategoryServiceImpl
 * @Description: TODO
 * @Author lite
 * @Date 2020/8/27
 * @Version V1.0
 **/
@RestController
public class CategoryServiceImpl extends BaseApiService implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public Result<List<CategoryEntity>> getCategoryByPid(Integer pid) {

        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setParentId(pid);

        List<CategoryEntity> list = categoryMapper.select(categoryEntity);

        return  this.setResultSuccess(list);
    }

    @Transactional
    @Override
    public Result<List<JSONObject>> addCategory(CategoryEntity categoryEntity) {

        //新增是要把当前节点变成父节点
        //增加数据的pid 就是父级的id
        //通过父级id 修改父级的状态 是否是父级节点  0不是父级节点 ,1是父级节点

        CategoryEntity category = new CategoryEntity();
        category.setId(categoryEntity.getParentId());
        category.setIsParent(1);
        categoryMapper.updateByPrimaryKeySelective(category);


        //新增要增加的数据
        categoryMapper.insertSelective(categoryEntity);

        return this.setResultSuccess();
    }

    @Transactional
    @Override
    public Result<List<JSONObject>> editCategory(CategoryEntity categoryEntity) {

        categoryMapper.updateByPrimaryKeySelective(categoryEntity);

        return this.setResultSuccess();
    }

    @Transactional
    @Override
    public Result<List<JSONObject>> deleteCategory(Integer id) {


        //通过id查询出这条要删除的数据
        //判断有没有数据
        CategoryEntity category = categoryMapper.selectByPrimaryKey(id);
        if(ObjectUtil.isNull(id)){
            return this.setResultError("当前数据不存在");
        }
        //判断是不是父节点
        if(category.getIsParent() == 1){
            return this.setResultError("当前数据为父节点,不能删除");
        }



        StringBuilder groupName = new StringBuilder();
        //判断有没有被规格组绑定
        //如果被绑定了的话,被哪个组绑定
        List<SpecGroupEntity> groupList = categoryMapper.getGroupByCategoryId(id);
        if(groupList.size() > 0){
            groupList.forEach(group ->{
                groupName.append(" " + group.getName() + " ");
            });
            return this.setResultError("当前节点被"+ groupName + "规格绑定,不能被删除");
        }


        StringBuilder brandName = new StringBuilder();
        //判断有没有被品牌绑定
        //如果被绑定了,被哪个品牌绑定
        List<BrandEntity> BrandList = categoryMapper.getBrandByCategoryId(id);
        if(BrandList.size() > 0){
            BrandList.forEach(brand ->{
                brandName.append(" "+  brand.getName() + " ");
            });
            return this.setResultError("当前节点被"+ brandName +"品牌绑定,不能被删除");
        }



        //构建条件查询,通过当前节点的pid查询数据
        //判断查询出来的数据 是不是一条 ,是一条的话 把父级节点的isParent修改为0,修改为不是父节点
        //最后执行删除操作
        Example example = new Example(CategoryEntity.class);
        example.createCriteria().andEqualTo("parentId",category.getParentId());
        List<CategoryEntity> list = categoryMapper.selectByExample(example);
        if(list.size() == 1){

            CategoryEntity parentCategory = new CategoryEntity();
            parentCategory.setIsParent(0);
            parentCategory.setId(list.get(0).getParentId());
            categoryMapper.updateByPrimaryKeySelective(parentCategory);
        }


        categoryMapper.deleteByPrimaryKey(id);
        return this.setResultSuccess();
    }



    //修改品牌绑定的商品分类时,用来回显当前品牌绑定的分类
    @Override
    public Result<List<CategoryEntity>> getCatesByBrand(Integer brandId) {

        List<CategoryEntity> catesByBrand = categoryMapper.getCatesByBrand(brandId);

        return this.setResultSuccess(catesByBrand);
    }
}
