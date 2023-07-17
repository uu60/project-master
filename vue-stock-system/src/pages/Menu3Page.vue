<template>

</template>

<script>
import axios from "axios";

export default {
  name: "Menu3Page",
  mounted() {
    const today = new Date();
    // 获取 30 天前的时间
    const thirtyDaysAgo = new Date(today.getTime() - 5 * 24 * 60 * 60 * 1000);
    axios.get(`/api/prediction/api/v1/score/AAPL?fromDate=${thirtyDaysAgo.toISOString().substring(0,19)+'Z'}&toDate=${today.toISOString().substring(0,19)+'Z'}`, {
      headers: {
        // 'Authorization': 'Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ3enkiLCJhdXRob3JpdGllcyI6W10sImlhdCI6MTY4NzE4NDc3OCwiZXhwIjoxNjkyMzc0NDAwfQ.dcSj9KbPIlhum11f_93f6CkgEamQAjTUbD3HJ60U-CE',
        'Authorization': localStorage.getItem('token'),
        'Content-Type': 'application/json'
      }
    }) .then(res=> {
      console.log("接口8", res.data)
    }) .catch(err => {
      console.error(err);
    })
  }
}
</script>

<style scoped>

</style>