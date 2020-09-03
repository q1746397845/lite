<template>
  <v-card>
      <v-flex xs12 sm10>
        <v-tree url="/category/list"
                :key="refreshKey"
                :isEdit="isEdit"
                @handleAdd="handleAdd"
                @handleEdit="handleEdit"
                @handleDelete="handleDelete"
                @handleClick="handleClick"
        />
      </v-flex>
  </v-card>
</template>

<script>
  import {treeData} from '../../mockDB'
  export default {
    name: "category",
    data() {
      return {
        // treeData: treeData,
        isEdit:true,
        refreshKey:0
      }
    },
    methods: {
      handleAdd(node) {
        console.log("add .... ");
        console.log(node);
        node.isParent  = node.isParent?1:0
        this.$http.post('category/add',node).then(resp =>{

          this.$message.success('新增成功');
          this.refreshKey = new Date().getTime();//避免key值重复,key值改变可以刷新页面

        }).catch(error => console.log(error))
      },
      handleEdit(id, name) {
        console.log("edit... id: " + id + ", name: " + name)
        this.$http.put('category/edit',{
          id:id,
          name:name
        }).then(resp =>{

          this.$message.success('修改成功');
          this.refreshKey = new Date().getTime();//避免key值重复,key值改变可以刷新页面

        }).catch(error => console.log(error))
      },
      handleDelete(id) {
        console.log("delete ... " + id)
        this.$http.delete('category/delete?id=' + id).then(resp =>{

          if(resp.data.code == 200){
            this.$message.success('删除成功');
            this.refreshKey = new Date().getTime();//避免key值重复,key值改变可以刷新页面
          }else{
            this.$message.error(resp.data.message);
            this.refreshKey = new Date().getTime();
          }
        }).catch(error => console.log(error));
      },
      handleClick(node) {
        console.log(node)
      }
    }
  };
</script>

<style scoped>

</style>
