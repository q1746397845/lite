<template>
  <div>
    <v-card-title>
      <v-btn color="primary" @click="openModal">新增</v-btn>
      <v-spacer />
      <v-text-field
        append-icon="search"
        label="通过品牌名称搜索"
        type="text"
        hide-details
        @keyup.enter="getBrand"
        v-model="brandName"
      />
    </v-card-title>

    <!-- 模态框 -->
    <v-layout row justify-center>
      <v-dialog v-model="dialog" persistent max-width="500">
        <v-card>
          <v-card-title class="headline">{{isEdit?'修改':'新增'}}品牌</v-card-title>
          <mr-brand-form
            @RefreshList="getBrand"
            @closeModel="closeModel"
            :dialog="dialog"
            :brandDetail="brandDetail"
            :isEdit="isEdit"
          ></mr-brand-form>
        </v-card>
      </v-dialog>
    </v-layout>

    <v-data-table
      :headers="headers"
      :items="items"
      class="elevation-1"
      :pagination.sync="pagination"
      :total-items="total"
      :loading="loading"
    >
      <template slot="items" slot-scope="props">
        <!-- <template v-slot:items="props"> -->
        <td>{{ props.item.id }}</td>
        <td class="text-xs-center">{{ props.item.name }}</td>
        <td class="text-xs-center">
          <img :src="props.item.image" width="100px" />
        </td>
        <td class="text-xs-center">{{ props.item.letter }}</td>
        <td class="text-xs-center">
          <v-flex>
            <v-btn flat icon color="blue" @click="editData(props.item)">
              <v-icon>edit</v-icon>
            </v-btn>

            <v-btn flat icon color="pink" @click="deleteData(props.item.id)">
              <v-icon>delete</v-icon>
            </v-btn>
          </v-flex>
        </td>
      </template>
    </v-data-table>
  </div>
</template>

<script>
import MrBrandForm from "./MrBrandForm";

export default {
  components: {
    MrBrandForm,
  },
  //监控分页属性发生变化,在浏览器调试工具中vue模块可以看见数据发生变化
  watch: {
    pagination() {
      this.getBrand();
    },
  },
  //组件加载完毕之后执行的函数
  mounted() {
    this.getBrand();
  },
  methods: {
    deleteData(id) {
      this.$message.confirm("此操作将永久删除该品牌, 是否继续?").then(() => {
          //通过id删除
          this.$http.delete("brand/delete?id=" + id).then((resp) => {
              if (resp.data.code == 200) {
                //刷新列表
                this.getBrand();
              }else{
                this.$message.info(resp.data.message);
              }
            }).catch((error) => console.log(error));
        }).catch(() => {
          this.$message.info("删除已取消！");
        });
    },
    openModal() {
      this.dialog = true;
      this.isEdit = false; //新增是需要将修改状态改为false
    },
    editData(obj) {
      //将isEdit 变成true 是修改
      this.isEdit = true;
      //打开模态框
      this.dialog = true;
      //把需要回显的数据赋值
      this.brandDetail = obj;
    },
    closeModel() {
      this.dialog = false;
      this.brandDetail = {}; //关闭模态框重置回显数据为空
    },
    getBrand() {
      this.$http
        .get("brand/getBrand", {
          //查询传递参数-->分页和排序信息
          params: {
            page: this.pagination.page,
            rows: this.pagination.rowsPerPage,
            sort: this.pagination.sortBy,
            order: this.pagination.descending,
            name: this.brandName,
          },
        })
        .then((resp) => {
          if (resp.data.code == 200) {
            //给数据属性赋值
            this.items = resp.data.data.list;
            //给总数据条数赋值
            this.total = resp.data.data.total;
            this.loading = false;
          }
        })
        .catch((error) => console.log(error));
    },
  },
  name: "MrBrand",
  data() {
    return {
      brandDetail: {},
      isEdit: false, //
      brandName: "",
      dialog: false, //false 关闭模态框
      total: 0, // 总条数
      items: [], // 表格数据
      loading: true, //加载样式
      pagination: {}, // 分页信息
      headers: [
        // 表头
        { text: "id", align: "center", value: "id", sortable: true },
        { text: "名称", align: "center", sortable: false, value: "name" },
        { text: "LOGO", align: "center", sortable: false, value: "image" },
        { text: "首字母", align: "center", value: "letter", sortable: true },
        { text: "操作", align: "center", value: "id", sortable: false },
      ],
    };
  },
};
</script>