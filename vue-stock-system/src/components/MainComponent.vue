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
                <el-menu-item index="/MainComponent/Home">Home</el-menu-item>
                <el-menu-item index="/MainComponent/Menu2">Menu 2</el-menu-item>
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
                <el-dropdown-item divided @click.native="logout">退出</el-dropdown-item>
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
      default: 'Please enter to search'
    }
  },

  data() {
    return {
      activeIndex: '/MainComponent/Home',
      keyword: '',
      userName: this.$store.state.username,
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
        pubsub.publish("数据", JSON.parse(window.sessionStorage.getItem(this.keyword)))
      } else {
        axios.get(`/api/display/api/v1/data/daily/${this.$data.keyword}`, {
          headers: {
            // 'Authorization': 'Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ3enkiLCJhdXRob3JpdGllcyI6W10sImlhdCI6MTY4NzE4NDc3OCwiZXhwIjoxNjkyMzc0NDAwfQ.dcSj9KbPIlhum11f_93f6CkgEamQAjTUbD3HJ60U-CE',
            'Authorization': localStorage.getItem('token'),
            'Content-Type': 'application/json'
          }
        })
            .then(res => {
              console.log(res.data)
              if (res.data.code == 0) {
                pubsub.publish("数据", res.data)
                // console.log("查到了数据",res.data)
                // console.log(res.data.data[0].code)
                window.sessionStorage.setItem(res.data.data[0].code, JSON.stringify(res.data))

              } else if (res.data.code == 1) {
                this.$message.error("The data has not been queried, please wait patiently before querying");
                pubsub.publish("clear", res.data.code)
              } else {
                this.$message.error(this.$store.state.serverErrMsg);
              }
            })
            .catch(err => {
              console.error(err);
            })
      }

    },
    logout() {
      window.localStorage.clear();
      // location.reload()
      // this.$router.push('/login')
      window.location = "/"


    }
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
