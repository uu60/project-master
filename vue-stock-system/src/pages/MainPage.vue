<template>
  <div>
    <section class="container">

      <!--左容器-->
      <section class="itemLeft">
        <ItemPage>
          <ItemOne/>
        </ItemPage>
        <ItemPage>
          <ItemTwo/>
        </ItemPage>
      </section>

      <!--     中容器-->
      <section class="itemCenter">
        <!--        <el-button type="warning" icon="el-icon-star-off" circle></el-button>-->
        <el-row>
          <el-col span="4" offset="11">
            <h1 style="font-size: 20px;margin-left: 10px">{{ stockName }}</h1>
          </el-col>
          <el-col span="2" offset="7">
            <el-button v-bind:icon="iconData" type="warning" circle size="mini" v-if="isShow"
                       @click="collection"></el-button>
          </el-col>
        </el-row>
        <div class="echart" id="mychart" style="width:100%; height: 400px;"></div>
      </section>

      <!--      右容器-->
      <section class="itemRight">
        <ItemPage>
          <ItemThree/>
        </ItemPage>
        <ItemPage>
          <ItemFour/>
        </ItemPage>
      </section>
    </section>
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
      iconData: 'el-icon-star-off'
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
      // console.log(window.sessionStorage.getItem(name))
      this.initData(JSON.parse(window.sessionStorage.getItem(name)))
      this.initEcharts(this.stotitle, this.stodata);
    });
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
      //判断storage中是否存了
      if (window.sessionStorage.getItem(stockTitle + 'icon')) {
        this.iconData = window.sessionStorage.getItem(stockTitle + 'icon')
      } else {
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
            startValue: stockData.length - 15,
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
              opacity: .5
            }
          },
          {
            name: 'ma5',
            type: 'line',
            smooth: true,
            data: this.macd(stockData, 5),
            lineStyle: {
              opacity: .5
            }
          },
          {
            name: 'ma10',
            type: 'line',
            smooth: true,
            data: this.macd(stockData, 10),
            lineStyle: {
              opacity: .5
            }
          }
        ]
      };
      const myChart = echarts.init(document.getElementById("mychart"));
      myChart.setOption(option);
      //随着屏幕大小调节图表
      window.addEventListener("resize", () => {
        myChart.resize();
      });
    },
    collection() {
      if (this.iconData == 'el-icon-star-off') {
        //未收藏 --> 收藏
        this.iconData = 'el-icon-star-on'
        window.sessionStorage.setItem(this.stotitle + 'icon', this.iconData)
        pubsub.publish("stodata", this.stodata)
        console.log("发布完成")
      } else {
        //已收藏 --> 取消收藏
        this.iconData = 'el-icon-star-off'
        window.sessionStorage.setItem(this.stotitle + 'icon', this.iconData)
        pubsub.publish("取消收藏", this.stotitle)
      }
    },
  },

}
</script>

<style scoped lang="less">
.container {
  min-width: 1200px;
  max-width: 2048px;
  margin: 0 auto;
  padding: .125rem .125rem 0;
  display: flex;
  //设置左右份数
  .itemLeft, .itemRight {
    flex: 3;
  }


  .itemCenter {
    flex: 5;
    height: 10.5 rpx;
    border: 1px solid blue;
    padding: 0.1rem;
    margin: .25rem;
  }
}


</style>