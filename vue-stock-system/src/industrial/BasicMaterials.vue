<template>
    <div style="min-width: 1300px">
        <section class="up">
            <el-row :gutter="20" style="height: 80px">
                <!--1        -->
                <el-col span="4">
                    <el-card shadow="always">
                        <span class="title">{{ StockList[0].label }}</span>
                        <br>
                        <span class="price">{{ getClosePrice(0) }}</span>
                        <div :class=chooseStyle(percentTwoDays(0))>
                            {{ differenceTwoDays(0) }}
                            ({{ percentTwoDays(0) }})
                        </div>
                    </el-card>
                </el-col>
                <!--2        -->
                <el-col span="4">
                    <el-card shadow="always">
                        <span class="title">{{ StockList[1].label }}</span>
                        <br>
                        <span class="price">{{ getClosePrice(1) }}</span>
                        <div :class=chooseStyle(percentTwoDays(1))>
                            {{ differenceTwoDays(1) }}
                            ({{ percentTwoDays(1) }})
                        </div>
                    </el-card>
                </el-col>
                <!--3        -->
                <el-col span="4">
                    <el-card shadow="always">
                        <span class="title">{{ StockList[2].label }}</span>
                        <br>
                        <span class="price">{{ getClosePrice(2) }}</span>
                        <div :class=chooseStyle(percentTwoDays(2))>
                            {{ differenceTwoDays(2) }}
                            ({{ percentTwoDays(2) }})
                        </div>
                    </el-card>
                </el-col>
                <!--4        -->
                <el-col span="4">
                    <el-card shadow="always">
                        <span class="title">{{ StockList[3].label }}</span>
                        <br>
                        <span class="price">{{ getClosePrice(3) }}</span>
                        <div :class=chooseStyle(percentTwoDays(3))>
                            {{ differenceTwoDays(3) }}
                            ({{ percentTwoDays(3) }})
                        </div>
                    </el-card>
                </el-col>
                <!--5        -->
                <el-col span="4">
                    <el-card shadow="always">
                        <span class="title">{{ StockList[4].label }}</span>
                        <br>
                        <span class="price">{{ getClosePrice(4) }}</span>
                        <div :class=chooseStyle(percentTwoDays(4))>
                            {{ differenceTwoDays(4) }}
                            ({{ percentTwoDays(4) }})
                        </div>
                    </el-card>
                </el-col>
                <!--6        -->
                <el-col span="4">
                    <el-card shadow="always">
                        <span class="title">{{ StockList[5].label }}</span>
                        <br>
                        <span class="price">{{ getClosePrice(5) }}</span>
                        <div :class=chooseStyle(percentTwoDays(5))>
                            {{ differenceTwoDays(5) }}
                            ({{ percentTwoDays(5) }})
                        </div>
                    </el-card>
                </el-col>
            </el-row>
        </section>


        <section class="container">
            <!--左容器-->
            <section class="itemLeft">
                <el-card :body-style="{height: '100%',padding: '5px'}" shadow="always" style="height: 500px">
                    <el-row style="height: 100%" type="flex" justify="center" align="middle">
                        <el-transfer
                                :titles="['Candidates', 'Chosen']"
                                v-model="selectList"
                                :data="StockList"
                                filter-placeholder="请输入股票单词"
                                style="padding: 0; margin: 0; text-align: start">
                        </el-transfer>
                    </el-row>
                </el-card>
            </section>

            <!--右容器-->
            <section class="itemRight">
                <el-card :body-style="{height: '100%'}" shadow="always" style="height: 500px">
                    <el-row v-if="!hasChosen" style="color: gray; font-size: x-large; margin-top: 50px; text-align: center">Please select two stocks to show here.</el-row>
                    <el-row v-if="hasChosen" style="text-align: center">
                        <h1 style="font-size: x-large">Comparison</h1>
                    </el-row>
                    <div id="mychart" class="echart" style="width:100%; height: 400px;"></div>
                </el-card>
            </section>
        </section>

    </div>
</template>

<script>
import * as echarts from "echarts";
import moment from "moment";
import axios from "axios";

export default {
    name: "CommunicationServices",
    data() {
        return {
            queryList: ["LIN", "BHP", "RIO", "SHW", "APD", "CTA-PB", "VALE", "SCCO", "FCX", "ECL"],
            StockList: this.QueryCollectionList(),
            selectList: [],
            hasChosen: false
        }
    },
    mounted() {

    },
    watch: {
        selectList(item1, item2) {
            this.hasChosen = true;
            console.log("选中表格值", this.selectList)
            const myChart = echarts.init(document.getElementById("mychart"));
            myChart.clear()

            console.log("item1", item1)
            console.log("item2", item2)
            let flag = 0
            const option = {
                tooltip: {
                    trigger: 'axis',
                    axisPointer: {
                        type: 'cross',
                    }
                },
                legend: {},
                xAxis: {},
                yAxis: {
                    scale: true,
                    splitArea: {
                        show: true
                    }
                },
                dataZoom: [
                    {
                        show: true,
                    }
                ],
                series: []
            }
            item1.forEach(a => {
                console.log("a", this.StockList[a])
                if (sessionStorage.key(this.StockList[a].label)) {
                    const stockData = JSON.parse(sessionStorage.getItem(this.StockList[a].label)).data
                    // console.log("stockData", stockData)
                    if (flag === 0) {
                        option.xAxis.data = stockData.map(item => moment(new Date(item.time)).format('YYYY-MM-DD HH:mm:ss'))
                        option.dataZoom.startValue = stockData.length - 15
                        option.dataZoom.endValue = stockData.length
                        flag = 1
                    }
                    option.series.push({
                            type: "candlestick",
                            data: stockData.map(item => {
                                return [item.open, item.close, item.low, item.high]
                            })
                        },
                        {
                            name: stockData[0].code,
                            type: 'line',
                            smooth: true,
                            data: stockData.map(item => {
                                return item.open
                            }),
                            lineStyle: {
                                opacity: .5
                            }
                        })
                } else {
                    const today = new Date();
                    // 获取 30 天前的时间
                    const thirtyDaysAgo = new Date(today.getTime() - 365 * 24 * 60 * 60 * 1000);
                    axios.get(`/api/display/api/v1/data/daily/${this.StockList[a]}?fromDate=${thirtyDaysAgo.toISOString()}&toDate=${today.toISOString()}`, {
                        headers: {
                            // 'Authorization': 'Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ3enkiLCJhdXRob3JpdGllcyI6W10sImlhdCI6MTY4NzE4NDc3OCwiZXhwIjoxNjkyMzc0NDAwfQ.dcSj9KbPIlhum11f_93f6CkgEamQAjTUbD3HJ60U-CE',
                            'Authorization': localStorage.getItem('token'),
                            'Content-Type': 'application/json'
                        }
                    }).then(res => {
                        window.sessionStorage.setItem(res.data.data[0].code, JSON.stringify(res.data))
                        const stockData = res.data.data
                        if (flag === 0) {
                            option.xAxis.data = stockData.map(item => moment(new Date(item.time)).format('YYYY-MM-DD HH:mm:ss'))
                            option.dataZoom.startValue = stockData.length - 15
                            option.dataZoom.endValue = stockData.length
                            flag = 1
                        }
                        option.series.push({
                                type: "candlestick",
                                data: stockData.map(item => {
                                    return [item.open, item.close, item.low, item.high]
                                })
                            },
                            {
                                name: stockData[0].code,
                                type: 'line',
                                smooth: true,
                                data: stockData.map(item => {
                                    return item.open
                                }),
                                lineStyle: {
                                    opacity: .5
                                }
                            })
                    })
                }
            })

            myChart.setOption(option);
            //随着屏幕大小调节图表
            window.addEventListener("resize", () => {
                myChart.resize();
            });
        }
    },

    methods: {
        getClosePrice(i) {
            var stockdata = JSON.parse(sessionStorage.getItem(this.StockList[i].label)).data
            // console.log(stockdata[stockdata.length - 1].close)
            return stockdata[stockdata.length - 1].close
        },
        percentTwoDays(i) {
            var stockdata = JSON.parse(sessionStorage.getItem(this.StockList[i].label)).data
            var today = stockdata[stockdata.length - 1].close.toFixed(2)
            var yesterday = stockdata[stockdata.length - 2].close.toFixed(2)
            var res = ((((today - yesterday) / yesterday).toFixed(4)) * 100).toFixed(2)
            console.log("11", ((today - yesterday) / yesterday).toFixed(4))
            return res >= 0 ? "+" + res + "%" : res + "%"
        },
        differenceTwoDays(i) {
            var stockdata = JSON.parse(sessionStorage.getItem(this.StockList[i].label)).data
            var today = stockdata[stockdata.length - 1].close.toFixed(2)
            var yesterday = stockdata[stockdata.length - 2].close.toFixed(2)
            return (today - yesterday).toFixed(2)
        },
        chooseStyle(a) {
            if (a.indexOf('+') !== -1) {
                return "positive"
            } else {
                return "negative"
            }
        },

        QueryCollectionList() {
            var aaa =  ["LIN", "BHP", "RIO", "SHW", "APD", "CTA-PB", "VALE", "SCCO", "FCX", "ECL"]
            var StockList = []
            console.log("a", aaa)
            for (var i = 0; i < aaa.length; i++) {
                if (window.sessionStorage.getItem(aaa[i])) {
                    var result = JSON.parse(window.sessionStorage.getItem(aaa[i]))
                    console.log("result", result)
                    StockList.push({
                        label: result.data[0].code,
                        key: i
                    })
                } else {
                    const today = new Date();
                    // 获取 30 天前的时间
                    const thirtyDaysAgo = new Date(today.getTime() - 365 * 24 * 60 * 60 * 1000);
                    // 将时间转换为 ISO 时间字符串
                    const isoString = thirtyDaysAgo.toISOString();

                    axios.get(`/api/display/api/v1/data/daily/${aaa[i]}?fromDate=${thirtyDaysAgo.toISOString()}&toDate=${today.toISOString()}`, {
                        headers: {
                            // 'Authorization': 'Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ3enkiLCJhdXRob3JpdGllcyI6W10sImlhdCI6MTY4NzE4NDc3OCwiZXhwIjoxNjkyMzc0NDAwfQ.dcSj9KbPIlhum11f_93f6CkgEamQAjTUbD3HJ60U-CE',
                            'Authorization': localStorage.getItem('token'),
                            'Content-Type': 'application/json'
                        }
                    })
                        .then(res => {
                            console.log(res.data)
                            if (res.data.code === 0) {
                                // console.log("查到了数据",res.data)
                                // console.log(res.data.data[0].code)
                                window.sessionStorage.setItem(res.data.data[0].code, JSON.stringify(res.data))
                                StockList.push({
                                    label: res.data.data[0].code,
                                    key: i,
                                })

                            } else if (res.data.code === 1) {
                                this.$message.error("The data has not been queried, please wait patiently before querying");
                            } else {
                                this.$message.error(this.$store.state.serverErrMsg);
                            }
                        })
                        .catch(err => {
                            console.error(err);
                        })
                }

            }


            // console.log("stockList", StockList)
            return StockList
        },

    }
}

</script>

<style lang="less">
.container {
  min-width: 1200px;
  max-width: 2048px;
  margin: 0 auto;
  padding: .125rem .125rem 0;
  display: flex;
  //设置左右份数
  .itemLeft {
    flex: 5;
    margin-right: 5px;
  }

  .itemRight {
    flex: 6;
    margin-left: 5px;
  }
}

.el-transfer-panel {
  height: 400px;
  margin: 10px;
}

.up {
  min-width: 1200px;
  max-width: 2048px;
  margin: 0 auto;
  padding: .125rem .125rem 0;
}

.positive {
  color: #b61718;
  font-weight: bold;
  font-size: 16px;
}

.negative {
  color: #0b715e;
  font-weight: bold;
  font-size: 16px;
}

.title {
  color: dodgerblue;
  font-size: 16px;
  font-weight: bold
}

.price {
  font-size: 15px;
  font-weight: bold;
}

.el-transfer__buttons {
  margin-left: 5px;
  margin-right: 5px;
  padding-left: 5px;
  padding-right: 5px;
}

.el-transfer-panel {
  margin: 0;
}
</style>
