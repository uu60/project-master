<template>
  <div>
    <div class="block">
      <div style="margin: 5px; font-size: 14px">Obtain the increase probability of a specified date.</div>
      <el-date-picker
          v-model="value1"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="选择日期"
      style="margin: 5px">
      </el-date-picker>
      <el-button type="primary" plain size="mini" @click="Confirmation">comfirm</el-button>
    </div>
  </div>
</template>

<script>
export default {
  name: "itemOne",
  data(){
    return{
      value1: '',
    }
  },
  methods:{
    Confirmation() {
      console.log("时间", this.value1)
    }
  }
}
</script>

<style scoped>

</style>