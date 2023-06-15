<template>
  <div>
    <el-container>
      <!--菜单栏-->
      <el-header>
        <el-row>
          <!--导航栏-->
          <el-col span="10">
            <div>
              <el-menu :default-active="activeIndex" class="el-menu-demo" mode="horizontal" @select="handleSelect">
                <el-menu-item index="/Home">首页</el-menu-item>
                <el-menu-item index="/Menu2">菜单二</el-menu-item>
              </el-menu>
            </div>
          </el-col>
          <!--搜索框-->
          <el-col offset="5" span="9" style="padding: 15px">
            <div>
              <el-input
                  v-model="keyword"
                  :placeholder="placeholder"
                  prefix-icon="el-icon-search"
                  style="width: 350px;margin-right: 10px"
                  clearable></el-input>
              <el-button
                  icon="el-icon-search"
                  type="primary"
                  @click="search">
                Search
              </el-button>
            </div>
          </el-col>
        </el-row>
      </el-header>

      <!--主页面-->
      <el-main>
        <router-view></router-view>
      </el-main>
    </el-container>
  </div>
</template>


<script>
import axios from "axios"
import pubsub from 'pubsub-js'

export default {
  name: "MainComponent",
  components: {},
  props: {
    placeholder: {
      type: String,
      default: '请输入进行搜索'
    }
  },

  data() {
    return {
      activeIndex: '/Home',
      keyword: ""
    };
  },
  methods: {
    handleSelect(key, keyPath) {
      console.log(key, keyPath);
      this.$router.push(key);
    },
    search() {
      axios.get('http://localhost:8080/stock/display/data/AAPL')
          .then(res => {
            // console.log(res.data)
            pubsub.publish("数据",res.data)
            console.log("查到了数据")
          })
          .catch(err => {
            console.error(err);
          })
    },
  }
}
</script>

<style scoped lang="less">
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

//header {
//  height: 1rem;
//  width: 100%;
//  background-color: rgba(0, 0, 255, .2);
//
//  h1 {
//    font-size: 0.375rem;
//    color: white;
//    text-align: center;
//    line-height: 1rem;
//  }
//}


</style>