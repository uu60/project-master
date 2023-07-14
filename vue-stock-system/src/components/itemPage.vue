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

<style lang="less" scoped>
.item {
  width: 400px;
  height: 5.125rem;
  margin: .25rem;
}
</style>
