<template>
  <div>
    <h2>图表2</h2>
    <div class="chart">
      图表的容器
    </div>
  </div>
</template>

<script>
import pubsub from "pubsub-js";
import axios from "axios";
import moment from "moment";

export default {
  name: "itemTwo",
  mounted() {
    pubsub.subscribe('数据', (msgName, data) => {
      name = data.data[0].code
      const today = new Date();
      const todayISO = today.toISOString().substring(0, 19) + 'Z'
      // 获取 30 天前的时间
      const yesterday = new Date(today.getTime() - 24 * 60 * 60 * 1000);
      // 将时间转换为 ISO 时间字符串
      const isoString = yesterday.toISOString().substring(0, 19) + 'Z';

      axios.get(`/api/prediction/api/v1/score/${name}?fromDate=${isoString}&toDate=${todayISO}`, {
        headers: {
          // 'Authorization': 'Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ3enkiLCJhdXRob3JpdGllcyI6W10sImlhdCI6MTY4NzE4NDc3OCwiZXhwIjoxNjkyMzc0NDAwfQ.dcSj9KbPIlhum11f_93f6CkgEamQAjTUbD3HJ60U-CE',
          'Authorization': localStorage.getItem('token'),
          'Content-Type': 'application/json'
        }
      }).then(res => {
        console.log("zhunquelv res", res.data)
      }).catch(err => {
        console.error(err);
      })
    })
  }
}
</script>

<style scoped>

</style>