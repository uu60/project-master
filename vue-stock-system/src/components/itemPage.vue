<template>
  <div class="item">
    <!--设置插槽-->
    <slot></slot>
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
    border: blue;
    margin: .25rem;
    background-color: rgba(12, 130, 255, .85);
  }
</style>