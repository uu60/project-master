<template>
  <el-card shadow="always" class="container" style="height: 10.5rpx">
    <div class="block" style="padding: 10px">
      <el-date-picker
          type="daterange"
          v-model="value"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :picker-options="pickerOptions">
      </el-date-picker>
    </div>
    <!--分页器绑定到数据中      -->
    <el-table :data="tableData.slice((currentPage-1)*pagesize,currentPage*pagesize)"
              border
              :cell-style="{padding: '5px'}"
              :row-style="{height: '20px'}"
              style="width:1400px"
              height="calc(8.5rpx - 26px)">

      <el-table-column
          prop="stockName"
          label="Stock Name"
          min-width="10%">
      </el-table-column>
      <el-table-column
          prop="open"
          label="Open Price"
          min-width="10%">
      </el-table-column>
      <el-table-column
          prop="close"
          label="Close Price"
          min-width="10%">
      </el-table-column>
      <el-table-column
          prop="high"
          label="High Price"
          min-width="10%">
      </el-table-column>
      <el-table-column
          prop="low"
          label="Low Price"
          min-width="10%">
      </el-table-column>
      <el-table-column
          prop="volume"
          label="Volume"
          min-width="10%">
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
    this.QueryCollectionList();

  },
  data() {
    return {
      value: '',
      pagesize: 5,
      currentPage: 1,
      tableData: [
        {
          stockName: 'AAPL',
          open: '100',
          close: '100',
          high: '100',
          low: '100',
          volume: '100',
        }
      ],
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

    QueryCollectionList() {
      axios.get('/api/display/api/v1/collect/list', {
        headers: {
          'Authorization': localStorage.getItem('token'),
          'Content-Type': 'application/json'
        }
      })
          .then(res => {
            for (var i = 0; i < res.data.data.length; i++) {
              var code =  res.data.data[i].code
              const today = new Date();
              // 获取 7 天前的时间
              const AWeekAgo = new Date(today.getTime() - 7 * 24 * 60 * 60 * 1000);
              axios.get(`/api/prediction/api/v1/score/${code}?fromDate=${AWeekAgo.toISOString().substring(0, 19) + 'Z'}&toDate=${today.toISOString().substring(0, 19) + 'Z'}`, {
                headers: {
                  // 'Authorization': 'Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ3enkiLCJhdXRob3JpdGllcyI6W10sImlhdCI6MTY4NzE4NDc3OCwiZXhwIjoxNjkyMzc0NDAwfQ.dcSj9KbPIlhum11f_93f6CkgEamQAjTUbD3HJ60U-CE',
                  'Authorization': localStorage.getItem('token'),
                  'Content-Type': 'application/json'
                }
              }).then(res1 => {
                console.log("接口8", res1.data)
                if (res1.data.code === 0) {
                  this.tableData.push({
                    stockName: code,
                    open: res1.data.data.open,
                    close: res1.data.data.close,
                    high: res1.data.data.high,
                    low: res1.data.data.low,
                    volume: res1.data.data.volume
                  })
                }
              }).catch(err => {
                console.error(err);
              })
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
  height: 8.5 rpx;
  margin: 0 auto;
  padding: .125rem .125rem 0;
  display: flex;
}

</style>