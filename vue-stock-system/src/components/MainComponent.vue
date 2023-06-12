<template>
  <div>
    <el-container>
      <el-header>
        <el-row>
          <el-col span="2">
            <div>logo</div>
          </el-col>
          <el-col span="20">
            <el-menu :default-active="activeIndex" class="el-menu-demo" mode="horizontal" @select="handleSelect">
              <el-menu-item index="/Home">首页</el-menu-item>
              <el-menu-item index="/Menu2">菜单二</el-menu-item>
            </el-menu>
          </el-col>
            <el-col span="1">
              <div>Name</div>
            </el-col>
            <el-col span="1">
              <div>Icon</div>
            </el-col>
        </el-row>
      </el-header>
      <el-main>
        <router-view></router-view>
      </el-main>
    </el-container>
  </div>
</template>


<script>

export default {
  name: "MainComponent",
  components: {},
  data() {
    return {
      activeIndex: '/Home',
    };
  },
  methods: {
    handleSelect(key, keyPath) {
      console.log(key, keyPath);
      this.$router.push(key);
    }
  }
}
</script>

<style scoped>
.col1 {
  background-color: rgb(237, 250, 177);
  color: aliceblue;
}

.col2 {
  background-color: rgb(230, 164, 250);
  color: aliceblue;
}

.col3 {
  background-color: rgb(141, 123, 241);
  color: aliceblue;
}
</style>