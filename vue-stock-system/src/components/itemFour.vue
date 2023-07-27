<template>
  <div>
    <div style="width: 100%; display: inline-block">
      <el-statistic :value="deadline2" time-indices title="Until the closing of the regular US stock exchange:"
                    format="HH:mm:ss">
        <template slot="suffix"> Trading Hours: UTC+8 5:00am ~ 22:00pm</template>
      </el-statistic>
    </div>
  </div>
</template>

<script>
export default {
  name: "itemFour",
  data(){
    return {
      deadline2: Date.now() + (new Date().setHours(22, 30, 0) - Date.now()),
    }
  }
}
</script>

<style scoped>

</style>