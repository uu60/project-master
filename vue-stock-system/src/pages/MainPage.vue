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

  mounted() {
    pubsub.subscribe('数据', (msgName, data) => {
      console.log("收到了数据")
      // console.log(moment(new Date(stockdata[0].time)).format('YYYY-MM-DD HH:mm:ss'));
      this.initData(data);
      this.initEcharts(this.stotitle, this.stodata);
    });

  },
  methods: {
    initData(data) {
      this.stotitle = data.data[0].code;
      this.stodata = data.data;
      console.log("初始化完成")
    },

    initEcharts(stockTitle, stockData) {
      // var date = []
      // for (var i = 0; i < stockData.length; i++) {
      //   date.add(stockData[i].data)
      // }
      // console.log(this.title)
      const option = {
        title: {
          left: '45%',
          text: stockTitle
        },
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
          // {
          //   name: 'ma5',
          //   type: 'line',
          //   smooth: true,
          //   data: this.macd(data, 5),
          //   lineStyle: {
          //     opacity: .5
          //   }
          // }
        ]
      };
      const myChart = echarts.init(document.getElementById("mychart"));
      myChart.setOption(option);
      //随着屏幕大小调节图表
      window.addEventListener("resize", () => {
        myChart.resize();
      });
    },
    // macd(data, n) {
    //   let  result = []
    //   let sum = 0
    //
    //   data.map((item, index)=>{
    //     sum += item.close
    //
    //     if(index<n) {
    //       return '';
    //     } else {
    //       sum -= data[index-n].close;
    //       return (sum/n).toFixed(2);
    //     }
    //   })
    // }
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