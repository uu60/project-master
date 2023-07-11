<template>
  <div>
    <!--设置插槽-->
    <el-card class="item" shadow="always">
      <slot></slot>
    </el-card>
  </div>
</template>

<script>

export default {
  name: "itemPage"
}
</script>

<style scoped lang="less">
  .item{
    height: 5.125rem;
    //border: 1px solid blue;
    margin: .25rem;
    //background-color: rgba(12, 130, 255, .85);
  }
</style>