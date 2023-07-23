<template>
  <!--设置插槽-->
    <el-card :body-style="{width: '100%', height: '100%'}" class="item" shadow="always"
             style="min-width: 155px; width: calc(100% - 27px); margin: 13.5px">
        <slot></slot>
    </el-card>
</template>

<script>

export default {
    name: "itemPage"
}
</script>

<style lang="less" scoped>
.item {
  width: 400px;
  height: 6.125rem;
  margin: .25rem;
}
</style>
