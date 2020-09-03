<template>
  <div>
    <v-form v-model="valid" ref="form">
      <v-container>
        <v-text-field v-model="brand.name" label="品牌名称" :rules="nameRules" required></v-text-field>
        <!-- <v-text-field v-model="brand.letter" label="品牌首字母" required></v-text-field> -->
        <!--选择商品分类,与品牌做绑定 one-to-many-->
        <v-cascader url="/category/list" required v-model="brand.category" multiple label="商品分类" />
        <!--文件上传-->
        <!--布局,不加此标签模态框样式会乱,因为联动组件占用了一部分位置-->
        <v-layout row>
          <!--栅格布局 将一行分成12分 xs3:当前组件占3分-->
          <v-flex xs3>
            <span style="font-size: 16px; color: #444">品牌LOGO：</span>
          </v-flex>
          <v-flex>
            <v-upload
              v-model="brand.image"
              url="/upload"
              :multiple="false"
              :pic-width="250"
              :pic-height="90"
            />
          </v-flex>
        </v-layout>
      </v-container>
    </v-form>
    <v-card-actions>
      <v-spacer></v-spacer>
      <v-btn color="green darken-1" flat @click="closeModel">取消</v-btn>
      <v-btn color="green darken-1" flat @click="submitBrand">保存</v-btn>
    </v-card-actions>
  </div>
</template>
<script>
export default {
  name: "MrBrandForm",
  props: {
    //接受父级组件传递过来的状态
    dialog: Boolean,
    //接收父组件要回显的数据
    brandDetail: Object,
    
    isEdit: Boolean,
  },
  watch: {
    dialog() {
      //监控模态框
      if (this.dialog) {
        //清空form表单
        this.$refs.form.reset();
      }
    },
    //监控回显数据变化
    brandDetail() {
      //判断是否执行修改
      if (this.isEdit) {
        //通过品牌id查询商品分类信息
        this.$http.get("category/getByBrand", {
            params: {
              brandId: this.brandDetail.id,
            },
          }).then((resp) => {
            console.log(resp);
            //注意:此处不能直接写this.brandDetail.category = resp.data.data;
            //如果使用上述方式写的话,会直接修改brandDetail,
            //当前监听函数再次监听到发生改变,会死循环
            if (resp.data.code == 200) {
              let showData = this.brandDetail;
              showData.category = resp.data.data;
              this.brand = showData; //回显
            }
          }).catch((error) => console.log(error));
      }
    },
  },
  methods: {
    closeModel() {
      //关闭模态框
      this.$emit("closeModel");
    },
    //入库
    submitBrand() {
      if (this.$refs.form.validate()) {
        const formData = this.brand;
        //将 formData.category 重新赋值  用.map会返回数据
        formData.category = this.brand.category.map((obj) => obj.id).join();

        this.$http({
          //判断是修改还是新增
          method: this.isEdit ? "put" : "post",
          url: "brand/save",
          data: formData, //传递的数据
        })
          .then((resp) => {
            if (resp.data.code == 200) {
              this.$message.success("保存成功");
              //刷新列表
              this.$emit("RefreshList");
              //关闭模态框
              this.closeModel();
            }
          })
          .catch((error) => {
            console.log(error);
          });
      }
    },
  },
  data() {
    return {
      //开启表单验证
      valid: true,
      //品牌名称的验证规则
      nameRules: [
        //v 是当前值
        //!!v-->不是null
        //如果是null的话输出:品牌名称不能为空
        (v) => {
          if (v) {
            return v.length <= 10 || "品牌名称长度超过10位";
          }
          return !!v || "品牌名称不能为空";
        },
      ],
      brand: {
        name: "",
        image: "",
        //letter: "",
        category: [],
      },
    };
  },
};
</script>