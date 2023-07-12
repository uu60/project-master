<template>
  <div>
    <section class="container">
      <!--左容器-->
      <section class="itemLeft">
        <el-card shadow="always">
          <el-transfer
              filter-placeholder="请输入股票单词"
              v-model="selectList"
              :data="StockList">
          </el-transfer>
        </el-card>
      </section>

      <!--右容器-->
      <section class="itemRight">
        <el-card shadow="always">
          <div class="echart" id="mychart" style="width:100%; height: 400px;"></div>
        </el-card>
      </section>
    </section>
  </div>
</template>

<script>
import pubsub from "pubsub-js";
import axios from "axios";
import moment from "moment/moment";
import * as echarts from "echarts";

export default {
  name: "Menu2Page",
  data(){
    return{
      StockList: this.QueryCollectionList(),
      selectList: [],
    }
  },
  mounted() {

  },
  watch:{
    selectList(item1,item2){
      console.log("表格值", this.selectList)
    }
  },
  methods: {
    QueryCollectionList() {
      var StockList = []
      axios.get('/api/display/api/v1/collect/list', {
        headers: {
          'Authorization': localStorage.getItem('token'),
          'Content-Type': 'application/json'
        }
      })
          .then(res=>{
            for (var i = 0; i < res.data.data.length; i++) {
            StockList.push({
              label: res.data.data[i].code,
              key: i,
            })
            }
          })
          .catch(err => {
            console.error(err);
          });
      return StockList
    },
    initData(data) {
      this.stotitle = data.data[0].code;
      this.stodata = data.data;
      console.log("初始化完成")
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
  }
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
  .itemLeft{
    flex: 5;
    margin-right: 5px;
  }
  .itemRight{
    flex: 6;
    margin-left: 5px;
  }
}
.el-transfer-panel {
  height: 400px;
  margin: 10px;
}
</style>