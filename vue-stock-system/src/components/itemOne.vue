<template>
  <div>
    <div class="block">
      <el-row justify="center" type="flex" style="margin-bottom: 10px">
        <h1>Prediction of Increasing Probability</h1>
      </el-row>

      <el-row justify="center" type="flex" style="margin-bottom: 10px">
        <el-progress type="circle" :percentage="percentage" stroke-width="10" width="180" class="yw"
                     :color="color"></el-progress>
      </el-row>
      <el-row justify="center" type="flex" style="margin-bottom: 5px; color: #bdbaba">
        <div style="font-size: 8pt">
          This value is obtained by the machine learning model provided by the system based on the joint training of multiple stocks in the same industry of this stock. Please combine the annual line and K-line chart to judge the comprehensive judgment of bull market or bear market.
        </div>
      </el-row>



    </div>
  </div>
</template>

<script>
import pubsub from "pubsub-js";
import axios from "axios";

export default {
  name: "itemOne",
  data() {
    return {
      stockName: "--",
      openPrice: "--",
      closePrice: "--",
      highPrice: "--",
      lowPrice: "--",
      percentage: "0",
      color: 'rgb(32, 160, 255)'
    }
  },
  methods: {},

  mounted() {
    pubsub.subscribe('数据', (msgName, data) => {
      name = data.data[0].code
      var today = new Date()
      console.log("name", name)
      axios.get(`/api/prediction/api/v1/up/${name}?date=${new Date().toISOString()}`, {
        headers: {
          // 'Authorization': 'Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ3enkiLCJhdXRob3JpdGllcyI6W10sImlhdCI6MTY4NzE4NDc3OCwiZXhwIjoxNjkyMzc0NDAwfQ.dcSj9KbPIlhum11f_93f6CkgEamQAjTUbD3HJ60U-CE',
          'Authorization': localStorage.getItem('token'),
          'Content-Type': 'application/json'
        }
      }).then(res => {
        console.log("yuce res", res.data)
        if (res.data.code === 0) {
          if (res.data.data != null) {
            this.stockName = res.data.data[0].code;
            this.openPrice = res.data.data[0].up.toFixed(4);
            this.closePrice = res.data.data[3].up.toFixed(4);
            this.highPrice = res.data.data[1].up.toFixed(4);
            this.lowPrice = res.data.data[2].up.toFixed(4)
            this.percentage = res.data.data[3].up.toFixed(4) * 100
            if (this.percentage > 50) {
              this.color = '#b61718'
            }
            if (this.percentage === 0) {
              this.color = 'rgb(32, 160, 255)'
            } else {
              this.color = '#0b715e'
            }
          }
        } else {
          this.$message.error("日期格式有问题")
        }
      }).catch(err => {
        console.error(err);
      })
    });

    pubsub.subscribe("请显示本行上涨概率", (msgName, foredata) => {
      axios.get(`/api/prediction/api/v1/up/${foredata}?date=${new Date().toISOString()}`, {
        headers: {
          // 'Authorization': 'Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ3enkiLCJhdXRob3JpdGllcyI6W10sImlhdCI6MTY4NzE4NDc3OCwiZXhwIjoxNjkyMzc0NDAwfQ.dcSj9KbPIlhum11f_93f6CkgEamQAjTUbD3HJ60U-CE',
          'Authorization': localStorage.getItem('token'),
          'Content-Type': 'application/json'
        }
      }).then(res => {
        console.log("yuce res", res.data)
        if (res.data.code === 0) {
          if (res.data.data != null) {
            this.stockName = res.data.data[0].code;
            this.openPrice = res.data.data[0].up.toFixed(4);
            this.closePrice = res.data.data[3].up.toFixed(4);
            this.highPrice = res.data.data[1].up.toFixed(4);
            this.lowPrice = res.data.data[2].up.toFixed(4)
            this.percentage = res.data.data[3].up.toFixed(4) * 100
            if (this.percentage > 50) {
              this.color = '#b61718'
            }
            if (this.percentage === 0) {
              this.color = 'rgb(32, 160, 255)'
            } else {
              this.color = '#0b715e'
            }
          }
        } else {
          this.$message.error("日期格式有问题")
        }
      }).catch(err => {
        console.error(err);
      })
    })
  }
}
</script>

<style scoped lang="less">
.yw {
  .el-progress__text {
    color: #b61718;
    font-size: 50px;
    font-weight: bold;
  }
}

</style>
