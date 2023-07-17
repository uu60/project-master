<template>
  <div>
    <div class="block">
      <el-descriptions class="margin-top" title="The increase probability" :column="1" :size="'medium'" border>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-user"></i>
            Stock Name
          </template>
          {{stockName}}
        </el-descriptions-item>

        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-mobile-phone"></i>
            Open Price
          </template>
          {{openPrice}}
        </el-descriptions-item>

        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-location-outline"></i>
            Close Price
          </template>
          {{closePrice}}
        </el-descriptions-item>

        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-tickets"></i>
            High Price
          </template>
          {{highPrice}}
        </el-descriptions-item>

        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-office-building"></i>
            Low Price
          </template>
          {{lowPrice}}
        </el-descriptions-item>
      </el-descriptions>


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
      lowPrice: "--"
    }
  },
  methods: {},

  mounted() {
    pubsub.subscribe('数据', (msgName, data) => {
      name = data.data[0].code
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
            this.stockName = res.data.data.code;
            this.openPrice = res.data.data.open;
            this.closePrice = res.data.data.close;
            this.highPrice = res.data.data.high;
            this.lowPrice = res.data.data.low
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
            this.stockName = res.data.data.code;
            this.openPrice = res.data.data.open;
            this.closePrice = res.data.data.close;
            this.highPrice = res.data.data.high;
            this.lowPrice = res.data.data.low
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

<style scoped>

</style>