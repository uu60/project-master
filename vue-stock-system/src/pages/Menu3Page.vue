<template>
  <el-card shadow="always" class="container" style="height: 440px" :body-style="{width: '100%'}">
    <el-row type="flex" justify="center">
      <h1>Historical Machine Learning Model Prediction Hit Rate</h1>
    </el-row>

    <el-row type="flex" justify="center" gutter="20" style="margin: 20px">

      <el-button type="primary" plain @click="QueryCollectionList(7)">7 days</el-button>
      <el-button type="primary" plain @click="QueryCollectionList(15)">15 days</el-button>
      <el-button type="primary" plain @click="QueryCollectionList(30)">30 days</el-button>
    </el-row>


    <!--分页器绑定到数据中      -->
    <el-table
        :data="tableData.filter(data => !search || data.stockName.toLowerCase().includes(search.toLowerCase())).slice((currentPage-1)*pagesize,currentPage*pagesize)"
        border
        :cell-style="{padding: '5px'}"
        :row-style="{height: '20px'}"
        style="width:100%; height: calc(100% - 140px)">
      <el-table-column
          align="center"
          prop="stockName"
          label="Stock Name"
          min-width="25%">
      </el-table-column>

      <el-table-column
          align="center"
          prop=shuxing
          :label=xianshi
          min-width="25%">
      </el-table-column>

      <el-table-column
          align="center"
          prop="hit"
          label="Correctly predicted days"
          min-width="25%">
      </el-table-column>


      <el-table-column
          align="right"
          min-width="20%">
        <template slot="header" slot-scope="scope">
          <el-input
              v-model="search"
              size="mini"
              placeholder="输入关键字搜索"/>
        </template>
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
    this.QueryCollectionList(30);

  },
  data() {
    return {
      xianshi: '',
      value: '',
      pagesize: 5,
      currentPage: 1,
      pickerOptions: {
        disabledDate(time) {
          var now = new Date()
          return time.getTime() > Date.now() || time.getTime() < now.setMonth(now.getMonth() - 1);
        },
        shortcuts: [
          {
            text: '最近一天',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24);
              picker.$emit('pick', [start, end]);
            }
          }, {
            text: '最近一周',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
              picker.$emit('pick', [start, end]);
            }
          }, {
            text: '最近一个月',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
              picker.$emit('pick', [start, end]);
            }
          }]

      },
      search: '',
      tableData:[]
      // tableData: [],
      // tableData: [
      //   {
      //     stockName: "GOOGL",
      //     shuxing: '54.54%',
      //     hit: 12
      //   },
      //   {
      //     stockName: "META",
      //     shuxing: '45.45%',
      //     hit: 10
      //   },
      //   {
      //     stockName: 'AAPL',
      //     shuxing: '54.54%',
      //     hit: 12
      //   },
      //   {
      //     stockName: 'DIS',
      //     shuxing: '50%',
      //     hit: 11
      //   },
      //   {
      //     stockName: 'SBUX',
      //     shuxing: '40.9%',
      //     hit: 9
      //   },
      //   {
      //     stockName: 'SBUX',
      //     shuxing: '40.9%',
      //     hit: 9
      //   },{
      //     stockName: 'SBUX',
      //     shuxing: '40.9%',
      //     hit: 9
      //   },{
      //     stockName: 'SBUX',
      //     shuxing: '40.9%',
      //     hit: 9
      //   },{
      //     stockName: 'SBUX',
      //     shuxing: '40.9%',
      //     hit: 9
      //   },{
      //     stockName: 'SBUX',
      //     shuxing: '40.9%',
      //     hit: 9
      //   },{
      //     stockName: 'SBUX',
      //     shuxing: '40.9%',
      //     hit: 9
      //   },{
      //     stockName: 'SBUX',
      //     shuxing: '40.9%',
      //     hit: 9
      //   },{
      //     stockName: 'SBUX',
      //     shuxing: '40.9%',
      //     hit: 9
      //   },{
      //     stockName: 'SBUX',
      //     shuxing: '40.9%',
      //     hit: 9
      //   },{
      //     stockName: 'SBUX',
      //     shuxing: '40.9%',
      //     hit: 9
      //   },{
      //     stockName: 'SBUX',
      //     shuxing: '40.9%',
      //     hit: 9
      //   },
      // ]
    }
  },
  methods: {
    handleCurrentChange(val) {
      this.currentPage = val;
    },
    //获取当前日期
    getNow() {
      var now = new Date();
      var year = now.getFullYear(); //得到年份
      var month = now.getMonth(); //得到月份
      var date = now.getDate(); //得到日期
      month = month + 1;
      month = month.toString().padStart(2, '0');
      date = date.toString().padStart(2, '0');
      var defaultDate = `${year}-${month}-${date}`;
      return defaultDate;
    },
    //获取前一天日期
    getYesterday() {
      var now = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
      var year = now.getFullYear(); //得到年份
      var month = now.getMonth(); //得到月份
      var date = now.getDate(); //得到日期
      month = month + 1;
      month = month.toString().padStart(2, '0');
      date = date.toString().padStart(2, '0');
      var defaultDate = ` ${year}-${month}-${date}`;
      return defaultDate;
    },

    QueryCollectionList(a) {
      this.tableData = []
      this.xianshi = "Close price of " + a + " days"
      axios.get('/api/display/api/v1/collect/list', {
        headers: {
          'Authorization': localStorage.getItem('token'),
          'Content-Type': 'application/json'
        }
      })
          .then(res => {
            for (var i = 0; i < res.data.data.length; i++) {
              let code = res.data.data[i].code
              const today = new Date();

              // 获取 7 天前的时间
              const AWeekAgo = new Date(today.getTime() - a * 24 * 60 * 60 * 1000);
              axios.get(`/api/prediction/api/v1/score/${code}?fromDate=${AWeekAgo.toISOString().substring(0, 19) + 'Z'}&toDate=${today.toISOString().substring(0, 19) + 'Z'}`, {
                headers: {
                  // 'Authorization': 'Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ3enkiLCJhdXRob3JpdGllcyI6W10sImlhdCI6MTY4NzE4NDc3OCwiZXhwIjoxNjkyMzc0NDAwfQ.dcSj9KbPIlhum11f_93f6CkgEamQAjTUbD3HJ60U-CE',
                  'Authorization': localStorage.getItem('token'),
                  'Content-Type': 'application/json'
                }
              }).then(res1 => {
                // console.log("接口8", res1.data)
                if (res1.data.code === 0) {
                  this.tableData.push({
                    stockName: code,
                    shuxing: res1.data.data.close !== "NaN" ? res1.data.data.close * 100 + '%' : 'no data',
                    hit: res1.data.data.closeNum != null ? res1.data.data.closeNum : 'no data'
                  })
                }
              }).catch(err => {
                console.error(err);
              })
              console.log("tableData", this.tableData)
            }
          })
          .catch(err => {
            console.error(err);
          });
    },
  }
}
</script>

<style scoped lang="less">
.container {
  min-width: 1200px;
  max-width: 2048px;
  margin: 0 auto;
  padding: .125rem .125rem 0;
  display: flex;
}

</style>
