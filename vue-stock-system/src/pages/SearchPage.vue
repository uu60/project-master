<template>
  <div>
    <el-container>
      <!--菜单栏-->
      <el-header>
        <el-row>
          <!--导航栏-->
          <el-col span="10">
            <div>
              <el-menu :default-active="activeIndex" class="el-menu-demo" mode="horizontal" @select="handleSelect" style="min-width: 200px">
                <el-menu-item index="/Home">Home</el-menu-item>
              </el-menu>
            </div>
          </el-col>

          <!--用户-->
          <el-col offset="10" span="4">
            <i class="el-icon-user-solid" style="font-size: 15px; margin-top: 22px; color: gray"></i>
            <el-dropdown>
              <span class="el-dropdown-link">  {{ userName }}</span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item divided @click.native="logout">Logout</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </el-col>
        </el-row>
      </el-header>
      <el-main>
        <el-row type="flex" justify="center">
          <h1 style="font-size: 40px; margin-top: 200px">Investment Analytics System</h1>
        </el-row>
        <el-row type="flex" justify="center">
          <el-col :span="15" style="margin-top: 20px">
            <el-input
                v-model="search"
                @focus="focus"
                @blur="blur"
                @keyup.enter.native="searchHandler"
                placeholder="search stock name"
            >
              <el-button slot="append" icon="el-icon-search" id="search" @click="searchHandler"></el-button>
            </el-input>
            <!---设置z-index优先级,防止被走马灯效果遮挡-->
            <el-card
                @mouseenter="enterSearchBoxHanlder"
                v-if="isSearch"
                class="box-card"
                id="search-box"
                style="position:relative;z-index:15"
            >
              <dl v-if="isHistorySearch">
                <dt class="search-title" v-show="history">History search</dt>
                <dt class="remove-history" v-show="history" @click="removeAllHistory">
                  <i class="el-icon-delete"></i>clear history
                </dt>
                <el-tag
                    v-for="search in historySearchList"
                    :key="search.id"
                    closable
                    :type="search.type"
                    @close="closeHandler(search)"
                    style="margin-right:5px; margin-bottom:5px;"
                >{{ search.name }}
                </el-tag>
                <dt class="search-title">popular searches</dt>
                <dd v-for="search in hotSearchList" :key="search.id">{{ search }}</dd>
              </dl>
              <dl v-if="isSearchList">
                <dd v-for="search in searchList" :key="search.id">{{ search }}</dd>
              </dl>
            </el-card>
          </el-col>
        </el-row>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import RandomUtil from "../Utils/randomUtil";
import Store from "../Utils/storeUtil";
import axios from "axios";
import pubsub from "pubsub-js";

export default {
  data() {
    return {
      search: "", //当前输入框的值
      isFocus: false, //是否聚焦
      hotSearchList: ["No popular searches yet"], //热门搜索数据
      historySearchList: [], //历史搜索数据
      searchList: ["No data"], //搜索返回数据,
      history: false,
      types: ["", "success", "info", "warning", "danger"], //搜索历史tag式样
      userName: this.$store.state.username,
    };
  },
  methods: {
    logout() {
      window.localStorage.clear();
      // location.reload()
      // this.$router.push('/login')
      window.location = "/"
    },
    focus() {
      this.isFocus = true;
      this.historySearchList =
          Store.loadHistory() == null ? [] : Store.loadHistory();
      this.history = this.historySearchList.length == 0 ? false : true;
    },
    blur() {
      var self = this;
      this.searchBoxTimeout = setTimeout(function () {
        self.isFocus = false;
      }, 300);
    },
    enterSearchBoxHanlder() {
      clearTimeout(this.searchBoxTimeout);
    },
    searchHandler() {
      //发送网络请求并发布订阅
      if (window.sessionStorage.getItem(this.keyword)) {
        pubsub.publish("数据", JSON.parse(window.sessionStorage.getItem(this.keyword)))
      } else {
        const today = new Date();
        // 获取 30 天前的时间
        const thirtyDaysAgo = new Date(today.getTime() - 565 * 24 * 60 * 60 * 1000);
        // 将时间转换为 ISO 时间字符串
        const isoString = thirtyDaysAgo.toISOString();

        axios.get(`/api/display/api/v1/data/daily/${this.$data.search}?fromDate=${thirtyDaysAgo.toISOString()}&toDate=${today.toISOString()}`, {
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
                axios.get(`/api/display/api/v1/data/update/${this.search}`, {
                  headers: {
                    // 'Authorization': 'Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ3enkiLCJhdXRob3JpdGllcyI6W10sImlhdCI6MTY4NzE4NDc3OCwiZXhwIjoxNjkyMzc0NDAwfQ.dcSj9KbPIlhum11f_93f6CkgEamQAjTUbD3HJ60U-CE',
                    'Authorization': localStorage.getItem('token'),
                    'Content-Type': 'application/json'
                  }
                }).then(res => {
                  console.log("刷新", res.data)
                  if (res.data.code === 0) {
                    this.$message.success('It is already the latest.')
                  } else {
                    this.$message.info('Please wait for update.')
                  }
                }).catch(err => {
                  console.error(err);
                })
                // this.$message.error("The data has not been queried, please wait patiently before querying");
                pubsub.publish("clear", res.data.code)
              } else {
                this.$message.error(this.$store.state.serverErrMsg);
              }
            })
            .catch(err => {
              console.error(err);
            })
      }

      axios.get(`/api/prediction/api/v1/trend/${this.$data.search}?fromDate=${new Date().toISOString()}`, {
        headers: {
          // 'Authorization': 'Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ3enkiLCJhdXRob3JpdGllcyI6W10sImlhdCI6MTY4NzE4NDc3OCwiZXhwIjoxNjkyMzc0NDAwfQ.dcSj9KbPIlhum11f_93f6CkgEamQAjTUbD3HJ60U-CE',
          'Authorization': localStorage.getItem('token'),
          'Content-Type': 'application/json'
        }
      })
          .then(res => {
            // console.log("yuce",res.data)
            if (res.data.code === 0) {
              pubsub.publish("yuce", res.data)
            } else {
              this.$message.error("日期格式有问题");
            }
          })
          .catch(err => {
            console.error(err);
          })

      //页面跳转
      this.$router.push('/MainComponent/Home')
      //随机生成搜索历史tag式样
      let n = RandomUtil.getRandomNumber(0, 5);

      let exist =
          this.historySearchList.filter(value => {
            return value.name == this.search;
          }).length == 0
              ? false
              : true;
      if (!exist) {
        this.historySearchList.push({name: this.search, type: this.types[n]});
        Store.saveHistory(this.historySearchList);
      }
      this.history = this.historySearchList.length == 0 ? false : true;
    },
    closeHandler(search) {
      this.historySearchList.splice(this.historySearchList.indexOf(search), 1);
      Store.saveHistory(this.historySearchList);
      clearTimeout(this.searchBoxTimeout);
      if (this.historySearchList.length == 0) {
        this.history = false;
      }
    },
    removeAllHistory() {
      Store.removeAllHistory();
    }
  },
  computed: {
    isHistorySearch() {
      return this.isFocus && !this.search;
    },
    isSearchList() {
      return this.isFocus && this.search;
    },
    isSearch() {
      return this.isFocus;
    }
  },
  mounted() {
    axios.get('/api/display/api/v1/collect/list', {
      headers: {
        'Authorization': localStorage.getItem('token'),
        'Content-Type': 'application/json'
      }
    }).then(res => {
      for (var i = 0; i < res.data.data.length; i++) {
        window.localStorage.setItem(res.data.data[i].code + 'icon', 'el-icon-star-on')
      }
    })
  }
};
</script>

<style scoped lang="less">
#search {
  //background-color: ;
  border-radius: 0%;
}

.search-title {
  color: #bdbaba;
  font-size: 15px;
  margin-bottom: 5px;
}

.remove-history {
  color: #bdbaba;
  font-size: 15px;
  float: right;
  margin-top: -22px;
}

#search-box {
  width: 555px;
  height: 300px;
  margin-top: 0px;
  padding-bottom: 20px;
}

.el-dropdown-link {
  cursor: pointer;
  color: #409EFF;
}

.el-icon-arrow-down {
  font-size: 12px;
}

</style>
