<template>
  <v-card>
    <v-toolbar class="elevation-0">
      <v-btn @click="addItem" color="primary">新增商品</v-btn>
      <v-spacer/>
      <v-flex xs3>
        状态：
        <v-btn-toggle mandatory v-model="search.saleable">
          <v-btn flat :value="2">
            全部
          </v-btn>
          <v-btn flat :value="1">
            上架
          </v-btn>
          <v-btn flat :value="0">
            下架
          </v-btn>
        </v-btn-toggle>
      </v-flex>
      <v-flex xs3>
        <v-text-field
          append-icon="search"
          label="搜索"
          single-line
          hide-details
          v-model="search.key"
        />
      </v-flex>
    </v-toolbar>
    <v-divider/>
    <v-data-table
      :headers="headers"
      :items="items"
      :pagination.sync="pagination"
      :total-items="totalItems"
      :loading="loading"
      class="elevation-1"
    >
      <template slot="items" slot-scope="props">
        <td class="text-xs-center">{{ props.item.id }}</td>
        <td class="text-xs-center">{{ props.item.title }}</td>
        <td class="text-xs-center">{{ props.item.categoryName}}</td>
        <td class="text-xs-center">{{ props.item.brandName }}</td>
        <td class="justify-center layout px-0">
          <v-btn icon small @click="editItem(props.item)">
            <i class="el-icon-edit"/>
          </v-btn>
          <v-btn icon small @click="deleteItem(props.item.id)">
            <i class="el-icon-delete"/>
          </v-btn>
          <v-btn icon small v-if="props.item.saleable" @click="goodsUpDown(props.item.id,0)">下架</v-btn>
          <v-btn icon v-else @click="goodsUpDown(props.item.id,1)">上架</v-btn>
        </td>
      </template>
      <template slot="no-data">
        <v-alert :value="true" color="error" icon="warning">
          对不起，没有查询到任何数据 :(
        </v-alert>
      </template>
      <template slot="pageText" slot-scope="props">
        共{{props.itemsLength}}条,当前:{{ props.pageStart }} - {{ props.pageStop }}
      </template>
    </v-data-table>

    <v-dialog v-if="show" v-model="show" max-width="900" scrollable persistent>
      <v-card>
        <v-toolbar dense dark color="primary">
          <v-toolbar-title>{{isEdit ? '修改商品' : '新增商品'}}</v-toolbar-title>
          <v-spacer/>
          <v-btn icon @click="closeForm">
            <v-icon>close</v-icon>
          </v-btn>
        </v-toolbar>
        <v-card-text style="height: 600px;">
          <!-- 表单 -->
          <goods-form  :refresh="getDataFromApi" :oldGoods="selectedGoods" :isEdit="isEdit" :step="step" ref="goodsForm" @closeForm="closeForm"/>
        </v-card-text>
        <v-card-actions>
          <v-layout row justify-center>
            <v-flex xs2>
              <v-btn :disabled="step === 1" @click="lastStep">上一步</v-btn>
            </v-flex>
            <v-flex xs2>
              <v-btn :disabled="step === 4" color="primary" @click="nextStep">下一步</v-btn>
            </v-flex>
          </v-layout>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-card>
</template>

<script>
  import GoodsForm from './GoodsForm'
  import {goodsData} from "../../mockDB";

  export default {
    name: "item",
    components: {
      GoodsForm
    },
    data() {
      return {
        search: {
          key: '',
          saleable: 1,
        },// 过滤字段
        totalItems: 0,// 总条数
        items: [],// 表格数据
        loading: true,
        pagination: {},// 分页信息
        headers: [// 表头
          {text: 'id', align: 'center', value: 'id'},
          {text: '标题', align: 'center', sortable: false, value: 'name'},
          {text: '商品分类', align: 'center', sortable: false, value: 'image'},
          {text: '品牌', align: 'center', value: 'brandName', sortable: false,},
          {text: '操作', align: 'center', value: 'id', sortable: false}
        ],
        show: false,// 是否弹出窗口
        selectedGoods: null, // 选中的商品信息
        isEdit: false, // 判断是编辑还是新增
        step: 1// 表单中的导航条
      }
    },
    watch: {
      pagination: {
        handler() {
          this.getDataFromApi();
        },
        deep: true
      },
      search: {
        handler() {
          this.getDataFromApi();
        },
        deep: true
      }
    },
    mounted() {
      this.getDataFromApi();
    },
    methods: {
      //上下架方法
      goodsUpDown(spuId,saleableState){
        this.$http.put("goods/updateSaleable",{
          id:spuId,
          saleable:saleableState
        }
        ).then(resp =>{
          if(resp.data.code == 200){ 
            if(saleableState == 0){
              this.$message.success("下架成功");
            }else if(saleableState == 1){
              this.$message.success("上架成功");
            }
            //刷新列表
            this.getDataFromApi();
          }else if(resp.data.code == 500){
            this.$message.error("操作失败"); 
          }
        }).catch(error => console.log(error))
      },
      closeForm() {
        this.isEdit = false;
        this.show = false;
        this.step = 1;
        this.selectedGoods = null;
        this.getDataFromApi();
      },
      lastStep() {
        if (this.step === 1) {
          return;
        }
        this.step--;
      },
      nextStep() {
        if (this.step === 4) {
          return;
        }
        if (this.$refs.goodsForm.$refs.basic.validate()) {
          this.step++;
        }
      },
      addItem() {
        this.selectedGoods = null;
        this.isEdit = false;
        this.show = true;
      },
      editItem(item) {
      //弹出模态框,页面bug,理论上不应该这么做
      this.isEdit = true;
      this.show = true;

        //不能直接给this.selectedGoods赋值,在这赋值以后GoodsForm会监控到oldGoods发生变化了
        // this.selectedGoods = item;
        // const names = item.categoryName.split("/");
        // this.selectedGoods.categories = [
        //   {id: item.cid1, name: names[0]},
        //   {id: item.cid2, name: names[1]},
        //   {id: item.cid3, name: names[2]}
        // ];

        //列表中传输过来的数据
        let obj = item;
        // 查询商品详情
        this.$http.get("goods/getSpuDetailById",{
          params:{
            spuId: item.id
          }
        }).then(resp => {
            //准备分类需要回显的数据
            obj.categories = [];
            //商品详情
            obj.spuDetail = resp.data.data;
            //特殊规格数据
            obj.spuDetail.specTemplate = resp.data.specialSpec;
            //通用规格数据
            obj.spuDetail.specifications = resp.data.genericSpec;
            //查询sku
            this.$http.get("goods/getSkuBySpuId",{
              params:{
                spuId: item.id
              }
            }).then(resp =>{
    
                obj.skus = resp.data.data;
                //需要回显的数据
                this.selectedGoods = obj;//最后再给selectedGoods赋值
            }).catch(error => console.log(error))
          }).catch(error => console.log(error))
      },
      deleteItem(id) {
        this.$message.confirm('此操作将永久删除该商品, 是否继续?')
          .then(() => {
            // 发起删除请求
            this.$http.delete("goods/deleteGoods?spuId=" + id)
              .then(resp => {
                if(resp.data.code == 200){
                  // 删除成功，重新加载数据
                  this.getDataFromApi();
                  this.$message.info('删除成功!');
                }      
              })
          })
          .catch(() => {
            this.$message.info('已取消删除');
          });

      },
      getDataFromApi() {
        this.loading = true;
        this.$http.get("goods/getSpuInfo",{
          params:{
            page: this.pagination.page,
            rows: this.pagination.rowsPerPage,
            sort: this.pagination.sortBy,
            order: this.pagination.descending,
            saleable: this.search.saleable,
            title: this.search.key
          }
        }).then(resp =>{
          if(resp.data.code == 200){
            this.items = resp.data.data;
            this.totalItems = resp.data.message - 0;
            this.loading = false;
          }   
        }).catch(error =>console.log(error))

        // setTimeout(() => {
        //   // 返回假数据
        //   this.items = goodsData.slice(0, 4);
        //   this.totalItems = 25;
        //   this.loading = false;
        // }, 300)
      }
    }
  }
</script>

<style scoped>
  label {
    font-size: 14px;
  }
</style>
