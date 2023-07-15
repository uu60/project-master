<template>
  <div>
    <div class="block">
      <el-descriptions class="margin-top" title="The increase probability" :column="1" :size="'medium'" border>
        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-user"></i>
            Stock Name
          </template>
          kooriookami
        </el-descriptions-item>

        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-mobile-phone"></i>
            Open Price
          </template>
          18100000000
        </el-descriptions-item>

        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-location-outline"></i>
            Close Price
          </template>
          苏州市
        </el-descriptions-item>

        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-tickets"></i>
            High Price
          </template>
          <el-tag size="small">学校</el-tag>
        </el-descriptions-item>

        <el-descriptions-item>
          <template slot="label">
            <i class="el-icon-office-building"></i>
            Low Price
          </template>
          江苏省苏州市吴中区吴中大道 1188 号
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
    return {}
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
      }).catch(err => {
        console.error(err);
      })
    });
  }
}
</script>

<style scoped>

</style>