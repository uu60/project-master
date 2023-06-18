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

          <!--用户-->
          <el-col offset="10" span="4">
            <i class="el-icon-user-solid" style="font-size: 15px; margin-top: 22px; color: gray"></i>
            <el-dropdown>
              <span class="el-dropdown-link">{{userName}}</span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item>个人信息</el-dropdown-item>
                <el-dropdown-item divided>退出</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </el-col>
        </el-row>
      </el-header>
      <el-main>
        <el-row>
          <!--      <el-col :span="3" class="left">-->
          <!--        <img src="//s0.meituan.net/bs/fe-web-meituan/fa5f0f0/img/logo.png" alt="美团" />-->
          <!--      </el-col>-->
          <el-col span="14" offset="10" style="margin-top: 200px">
            <h1>Stock Price System</h1>
          </el-col>
          <el-col :span="10" class="center" offset="7">
            <el-input
                v-model="search"
                @focus="focus"
                @blur="blur"
                @keyup.enter.native="searchHandler"
                placeholder="搜索股票名称"
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
                <dt class="search-title" v-show="history">历史搜索</dt>
                <dt class="remove-history" v-show="history" @click="removeAllHistory">
                  <i class="el-icon-delete"></i>清空历史记录
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
                <dt class="search-title">热门搜索</dt>
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
      hotSearchList: ["暂无热门搜索"], //热门搜索数据
      historySearchList: [], //历史搜索数据
      searchList: ["暂无数据"], //搜索返回数据,
      history: false,
      types: ["", "success", "info", "warning", "danger"], //搜索历史tag式样
      userName: "未登录",
    };
  },
  methods: {
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
      axios.get(`http://localhost:8080/stock/display/data/{{search}}`)
          .then(res => {
            // console.log(res.data)
            pubsub.publish("数据",res.data)
            console.log("查到了数据")
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
  }
};
</script>

<style scoped lang="less">

.center {
  margin-top: 50px;
  //alignment-baseline: central;
}

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