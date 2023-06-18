<template>
  <div>
    <el-container>
      <!--菜单栏-->
      <el-header>
        <el-row>
          <!--导航栏-->
          <el-col span="7">
            <div>
              <el-menu :default-active="activeIndex" class="el-menu-demo" mode="horizontal" @select="handleSelect">
                <el-menu-item index="/MainComponent/Home">首页</el-menu-item>
                <el-menu-item index="/MainComponent/Menu2">菜单二</el-menu-item>
              </el-menu>
            </div>
          </el-col>
          <!--搜索框-->
          <el-col offset="1" span="9" style="padding: 15px">
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

          <el-col span="4" offset="3">
            <i class="el-icon-user-solid" style="font-size: 15px; margin-top: 22px;"></i>
            <el-dropdown>
              <span class="el-dropdown-link">{{ userName }}</span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item>个人信息</el-dropdown-item>
                <el-dropdown-item divided>退出</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
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
      activeIndex: '/MainComponent/Home',
      keyword: '',
      userName: "未登录",
    };
  },
  methods: {
    handleSelect(key, keyPath) {
      console.log(key, keyPath);
      this.$router.push(key);
    },
    search() {
      ///display/api/v1/data/daily/{code}
      // console.log(this.$data.keyword)
      if (window.sessionStorage.getItem(this.keyword)) {
        pubsub.publish("数据", JSON.parse(window.sessionStorage.getItem(this.keyword)) )
      } else {
        axios.get(`http://localhost:8080/stock/display/api/v1/data/daily/${this.$data.keyword}`)
            .then(res => {
              console.log(res.data)
              pubsub.publish("数据", res.data)
              // console.log("查到了数据")
              window.sessionStorage.setItem(this.keyword, JSON.stringify(res.data))
            })
            .catch(err => {
              console.error(err);
            })
      }

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