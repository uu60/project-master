<template>
  <el-card shadow="always" class="container">
    <!--分页器绑定到数据中      -->
    <el-table :data="tableData.slice((currentPage-1)*pagesize,currentPage*pagesize)"
              border
              :cell-style="{padding: '5px'}"
              :row-style="{height: '0'}"
              style="width:100%"
              height="calc(8.5rpx - 26px)">

      <el-table-column
          prop="date"
          label="日期"
          width="180">
      </el-table-column>
      <el-table-column
          prop="name"
          label="姓名"
          width="180">
      </el-table-column>
      <el-table-column
          prop="address"
          label="地址">
      </el-table-column>

    </el-table>

    <el-pagination small
                   layout="total,prev,pager,next,jumper"
                   :total="tableData.length"
                   :page-size="pagesize"
                   pager-count="5"
                   :current-page="currentPage"
                   @current-change="handleCurrentChange"
    ></el-pagination>


  </el-card>

</template>

<script>
import axios from "axios";

export default {
  name: "Menu3Page",
  mounted() {
    const today = new Date();
    // 获取 30 天前的时间
    const thirtyDaysAgo = new Date(today.getTime() - 5 * 24 * 60 * 60 * 1000);
    axios.get(`/api/prediction/api/v1/score/AAPL?fromDate=${thirtyDaysAgo.toISOString().substring(0, 19) + 'Z'}&toDate=${today.toISOString().substring(0, 19) + 'Z'}`, {
      headers: {
        // 'Authorization': 'Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ3enkiLCJhdXRob3JpdGllcyI6W10sImlhdCI6MTY4NzE4NDc3OCwiZXhwIjoxNjkyMzc0NDAwfQ.dcSj9KbPIlhum11f_93f6CkgEamQAjTUbD3HJ60U-CE',
        'Authorization': localStorage.getItem('token'),
        'Content-Type': 'application/json'
      }
    }).then(res => {
      console.log("接口8", res.data)
    }).catch(err => {
      console.error(err);
    })
  },
  data() {
    return {
      pagesize: 5,
      currentPage: 1,
      tableData: [
        {
          date: '2016-05-02',
          name: '王小虎',
          address: '上海市普陀区金沙江路 1518 弄'
        }, {
          date: '2016-05-04',
          name: '王小虎',
          address: '上海市普陀区金沙江路 1517 弄'
        }, {
          date: '2016-05-01',
          name: '王小虎',
          address: '上海市普陀区金沙江路 1519 弄'
        }
      ]
    }
  },
  methods: {
    handleCurrentChange(val) {
      this.currentPage = val;

    }
  }
}
</script>

<style scoped lang="less">
.container {
  min-width: 1200px;
  max-width: 2048px;
  height: 8.5 rpx;
  margin: 0 auto;
  padding: .125rem .125rem 0;
  display: flex;
}

</style>