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
    this.initEcharts();
  },
  methods: {
    initEcharts() {
      const option = {
        title: {
          text: "K线示例"
        },
        tooltip: {},
        legend: {},
        xAxis: {
          data: ["2017-10-24", "2017-10-25", "2017-10-26", "2017-10-27"]
        },
        yAxis: {
          scale: true
        },
        series: [
          {
            type: "candlestick",
            data: [
              [20, 34, 10, 38], //今开、当前价格、最低价格、最高价
              [40, 35, 30, 50],
              [31, 38, 33, 44],
              [38, 15, 5, 42]
            ]
          }
        ]
      };
      const myChart = echarts.init(document.getElementById("mychart"));
      myChart.setOption(option);
      //随着屏幕大小调节图表
      window.addEventListener("resize", () => {
        myChart.resize();
      });
    }
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
  .itemLeft, .itemRight {
    flex: 3;
  }

  .itemCenter {
    flex: 5;
    height: 10.5 rpx;
    border: 1px solid blue;
    padding: 0.125rem;
    margin: .25rem;
  }
}
</style>