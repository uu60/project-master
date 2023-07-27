<template>
  <div>
    <el-row class="container" style="display: flex; justify-content: center">
      <el-col :span="16" style="height: 800px">
        <!--     中容器-->
        <el-card class="itemCenter" shadow="always" style="min-width: 380px; height: 100%; text-align: center">
          <el-row v-if="!isShow" style="color: gray; font-size: x-large; margin-top: 50px">Please select or search for a
            stock to show here.
          </el-row>
          <el-row>
            <el-col :span="8" style="border: 1px solid transparent"></el-col>
            <el-col :span="8" style="text-align: center">
              <h1 style="font-size: 20px;margin-left: 10px; ">{{ stockName }}</h1>
            </el-col>
            <el-col :span="8" style="text-align: center">
              <el-row style="text-align: center">
                <el-button v-if="isShow" circle class="el-icon-refresh" size="mini" type="primary"
                           @click="refresh"/>
                <el-button v-if="isShow" circle size="mini" type="warning" v-bind:icon="iconData"
                           @click="collection"></el-button>
              </el-row>
            </el-col>
          </el-row>
          <div slot="empty" style="text-align: center">
            <el-empty :image-size="108" description="no records"/>
          </div>
          <div id="mychart" class="echart" style="width:100%; height: 400px;"></div>
          <el-row v-if="isShow" justify="center" style="margin-top: 30px; margin-bottom: -30px" type="flex">
            <h2 style="font-size: 20px; text-align: center">Forecast</h2>
          </el-row>
          <div id="myforecastchart" class="echart" style="width:100%; height: 300px;"></div>
        </el-card>
      </el-col>

      <!--      右容器-->
      <el-col :span="8" style="height: 800px">
        <ItemPage style="height: calc(10% - 7px)">
          <ItemFour />
        </ItemPage>

        <ItemPage style="height: calc(50%  - 7px)">
          <ItemThree/>
        </ItemPage>

        <ItemPage style="height: calc(40% - 7px) ">
          <ItemOne/>
        </ItemPage>


      </el-col>
    </el-row>
  </div>

</template>

<script>
import * as echarts from "echarts";
import ItemPage from "@/components/itemPage"
import ItemOne from "@/components/itemOne";
import ItemTwo from "@/components/itemTwo";
import ItemThree from "@/components/itemThree";
import ItemFour from "@/components/itemFour";
import pubsub from 'pubsub-js'
import moment from 'moment'
import axios from "axios";

export default {
  name: "MainPage",
  components: {ItemFour, ItemThree, ItemTwo, ItemOne, ItemPage},
  conponent: {
    ItemPage,
    ItemOne,
    ItemTwo,
    ItemThree,
    ItemFour
  },
  data() {
    return {
      stockName: '',
      isShow: false,
      iconData: 'el-icon-star-off',
      deadline2: Date.now() + (new Date().setHours(22, 30, 0) - Date.now()),
    }
  },

  mounted() {
    pubsub.subscribe('数据', (msgName, data) => {
      // console.log("收到了数据")
      console.log(data);
      this.initData(data);
      this.initEcharts(this.stotitle, this.stodata);
    });

    //接收check按钮信息
    pubsub.subscribe('请显示本行内容', (msg, name) => {
      // console.log(window.localStorage.getItem(name))
      const today = new Date();
      // 获取 30 天前的时间
      const thirtyDaysAgo = new Date(today.getTime() - 565 * 24 * 60 * 60 * 1000);
      // 将时间转换为 ISO 时间字符串
      const isoString = thirtyDaysAgo.toISOString();

      axios.get(`/api/display/api/v1/data/daily/${name}?fromDate=${thirtyDaysAgo.toISOString()}&toDate=${today.toISOString()}`, {
        headers: {
          // 'Authorization': 'Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ3enkiLCJhdXRob3JpdGllcyI6W10sImlhdCI6MTY4NzE4NDc3OCwiZXhwIjoxNjkyMzc0NDAwfQ.dcSj9KbPIlhum11f_93f6CkgEamQAjTUbD3HJ60U-CE',
          'Authorization': localStorage.getItem('token'),
          'Content-Type': 'application/json'
        }
      })
          .then(res => {
            console.log(res.data)
            if (res.data.code == 0) {
              this.initData(res.data);
              this.initEcharts(this.stotitle, this.stodata);
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

    });

    pubsub.subscribe("请显示本行预测数据", (msg, name) => {
      axios.get(`/api/prediction/api/v1/trend/${name}?fromDate=${new Date().toISOString()}`, {
        headers: {
          // 'Authorization': 'Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ3enkiLCJhdXRob3JpdGllcyI6W10sImlhdCI6MTY4NzE4NDc3OCwiZXhwIjoxNjkyMzc0NDAwfQ.dcSj9KbPIlhum11f_93f6CkgEamQAjTUbD3HJ60U-CE',
          'Authorization': localStorage.getItem('token'),
          'Content-Type': 'application/json'
        }
      })
          .then(res => {
            // console.log("yuce",res.data)
            if (res.data.code === 0) {
              this.initForecastChart(res.data.data)
            } else {
              this.$message.error("日期格式有问题");
            }
          })
          .catch(err => {
            console.error(err);
          })
    })

    pubsub.subscribe("取消收藏图标按钮", (msgName, data) => {
      // console.log("取消收藏图标按钮回调",data.stockName)
      window.localStorage.setItem(data.stockName + 'icon', 'el-icon-star-off')
      if (data.stockName === this.stockName) {
        this.iconData = 'el-icon-star-off'
      }
    });

    //接收预测数据
    pubsub.subscribe("yuce", (msgName, foredata) => {
      console.log("jieshouyuceshuju", foredata)
      this.initForecastChart(foredata.data)
    })


  },
  methods: {
    initData(data) {
      this.stotitle = data.data[0].code;
      this.stodata = data.data;
      console.log("初始化完成")
    },
    macd(stockData, n) {
      let result = []
      let sum = 0
      return stockData.map((item, index) => {
        sum += item.close;

        if (index < n) {
          return ''
        } else {
          sum -= stockData[index - n].close
          return (sum / n).toFixed(3)
        }
      });
    },



    initEcharts(stockTitle, stockData) {
      //按钮是否可见
      this.isShow = true
      // console.log("判断了", stockTitle)
      //判断storage中是否存了
      var x = stockTitle + 'icon'
      // console.log("fanhui",window.localStorage.getItem(x))
      if (window.localStorage.getItem(x)) {
        this.iconData = window.localStorage.getItem(stockTitle + 'icon')
        // console.log("图标",this.iconData)
      } else {
        // console.log("没有图标")
        this.iconData = 'el-icon-star-off'
      }
      //接收清除图表消息
      pubsub.subscribe('clear', (msg, code) => {
        myChart.clear()
        this.stockName = ''
        this.isShow = false
      })

      //定义title
      this.stockName = stockTitle

      //图表配置
      const option = {
        // title: {
        //   // left: '45%',
        //   text: stockTitle
        // },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross',
          }
        },
        legend: {},
        xAxis: {
          data: stockData.map(item => moment(new Date(item.time)).format('YYYY-MM-DD HH:mm:ss'))
        },
        yAxis: {
          scale: true,
          splitArea: {
            show: true
          }
        },
        dataZoom: [
          {
            show: true,
            startValue: stockData.length - 30,
            endValue: stockData.length
          }
        ],
        series: [
          {
            type: "candlestick",
            data: stockData.map(item => {
              return [item.open, item.close, item.low, item.high]
            })
          },
          {
            name: 'ma2',
            type: 'line',
            smooth: true,
            data: this.macd(stockData, 2),
            lineStyle: {
              opacity: .7
            }
          },
          {
            name: 'ma5',
            type: 'line',
            smooth: true,
            data: this.macd(stockData, 5),
            lineStyle: {
              opacity: .7
            }
          },
          {
            name: 'ma15',
            type: 'line',
            smooth: true,
            data: this.macd(stockData, 15),
            lineStyle: {
              opacity: .7
            }
          },
          {
            name: 'Yearly Line',
            type: 'line',
            smooth: true,
            data: this.macd(stockData, 200),
            lineStyle: {
              opacity: 1
            }
          },

        ]
      };
      const myChart = echarts.init(document.getElementById("mychart"));
      myChart.setOption(option);
      //随着屏幕大小调节图表
      window.addEventListener("resize", () => {
        myChart.resize();
      });
    },
    initForecastChart(forecastData) {
      pubsub.subscribe('clear', (msg, code) => {
        myForecastChart.clear()
      })

      const option = {
        // title: {
        //     text: "Forecast",
        //     textAlign: "center"
        // },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross',
          }
        },
        xAxis: {
          data: forecastData.map(item => moment(new Date(item.time)).format('YYYY-MM-DD HH:mm:ss'))
        },
        yAxis: {
          scale: true,
          splitArea: {
            show: true
          }
        },
        series: [
          {
            type: "candlestick",
            data: forecastData.map(item => {
              return [item.open, item.close, item.low, item.high]
            })
          },
          {
            name: 'ma2',
            type: 'line',
            smooth: true,
            data: this.macd(forecastData, 2),
            lineStyle: {
              opacity: .5
            }
          },
        ],
      };
      const myForecastChart = echarts.init(document.getElementById("myforecastchart"));
      myForecastChart.setOption(option);
      //随着屏幕大小调节图表
      window.addEventListener("resize", () => {
        myForecastChart.resize();
      });
      console.log("yucewancheng")
    },
    collection() {
      if (this.iconData == 'el-icon-star-off') {
        //未收藏 --> 收藏
        axios.post(`/api/display/api/v1/collect/${this.stockName}`, {}, {
          headers: {
            'Authorization': localStorage.getItem('token'),
            'Content-Type': 'application/json'
          }
        }).then(res => {
          // console.log("收藏回调",res.data)
          if (res.data.code == 0 || res.data.code == 1) {
            this.iconData = 'el-icon-star-on'
            window.localStorage.setItem(this.stotitle + 'icon', this.iconData)
            pubsub.publish("stodata", this.stodata)
            console.log("发布完成")
          }
        }).catch(err => {
          console.error(err);
        })
      } else {
        //已收藏 --> 取消收藏
        axios.delete(`/api/display/api/v1/collect/${this.stockName}`, {
          headers: {
            'Authorization': localStorage.getItem('token'),
            'Content-Type': 'application/json'
          }
        }).then(res => {
          if (res.data.code == 0) {
            this.iconData = 'el-icon-star-off'
            window.localStorage.setItem(this.stotitle + 'icon', this.iconData)
            pubsub.publish("取消收藏", this.stotitle)
          }
        }).catch(err => {
          console.error(err);
        })


      }
    },
    refresh() {
      axios.get(`/api/display/api/v1/data/update/${this.stockName}`, {
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
    }
  },
}
</script>

<style lang="less" scoped>
.container {
  min-width: 1200px;
  max-width: 2048px;
  margin: 0 auto;
  padding: .125rem .125rem 0;
  display: flex;
}


.itemCenter {
  flex: 13;
  height: 10.5 rpx;
  //border: 1px solid blue;
  padding: 0.1rem;
  margin: .25rem;
}

</style>
