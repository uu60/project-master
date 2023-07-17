<template>
    <el-row style="width: 100%; height: 100%">
        <el-row justify="center" type="flex">
            <h1>My Collections</h1>
        </el-row>
        <el-table
                :cell-style="{padding: '5px'}"
                :data="tableData"
                :row-class-name="tableRowClassName"
                :row-style="{height: '0'}"
                style="width: 100%; "
                height="calc(6.125rem - 54px)"
        >
            <div slot="empty" style="text-align: center">
                <el-empty :image-size="108" description="no records"/>
            </div>
            <el-table-column label="Stock" prop="stockName" style="color: red"></el-table-column>
            <el-table-column label="Open" prop="open"></el-table-column>
            <el-table-column label="Delta" prop="delta"></el-table-column>
            <el-table-column label="Operation" width="110">
                <template slot-scope="scope">
                    <el-button icon="el-icon-search" size="mini" style="padding: 10px" type="primary"
                               @click="handleClick(scope.row)"></el-button>
                    <el-button icon="el-icon-delete
" size="mini" style="padding: 10px" type="danger" @click="handleDelete(scope.row)"></el-button>
                </template>
            </el-table-column>
        </el-table>
    </el-row>
</template>

<script>
import pubsub from "pubsub-js";
import axios from "axios";

export default {
    name: "itemThree",

    data() {
        return {
            tableData: [],
            editObj: {},
        }
    },
    watch: {
        // tableData() {
        //
        // }
    },
    mounted() {
        pubsub.subscribe('stodata', (msgName, stodata) => {
            console.log("表格数据接收", stodata)
            let item = {
                stockName: stodata[0].code,
                open: (stodata[this.tableData.length - 1].open).toFixed(2),
                delta: (stodata[this.tableData.length - 1].close - stodata[this.tableData.length - 1].open).toFixed(2)
            };
            if (item.delta >= 0) {
                item.delta = "+" + item.delta;
            }
            this.tableData.push(item)
        });

        //订阅取消收藏的信息
        pubsub.subscribe("取消收藏", (msgName, stotitle) => {
            // console.log("接收到取消收藏", stotitle)
            let objstock = this.tableData.find(o => o.stockName === stotitle)
            // console.log(objstock)
            this.removeObjWithArr(this.tableData, objstock)
        });

        //获取收藏列表信息
        axios.get('/api/display/api/v1/collect/list', {
            headers: {
                'Authorization': localStorage.getItem('token'),
                'Content-Type': 'application/json'
            }
        })
            .then(res => {
                // console.log("收藏列表信息", res.data)
                for (var i = 0; i < res.data.data.length; i++) {
                    let item = {
                        stockName: res.data.data[i].code,
                        open: res.data.data[i].openPrice.toFixed(2),
                        delta: (res.data.data[i].closePrice - res.data.data[i].openPrice).toFixed(2)
                    };
                    if (item.delta >= 0) {
                        item.delta = "+" + item.delta;
                    }
                    this.tableData.push(item)
                    window.localStorage.setItem(res.data.data[i].code + 'icon', 'el-icon-star-on')
                }
            })
            .catch(err => {
                console.error(err);
            });

    },
    methods: {
        tableRowClassName({row, rowIndex}) {
            if (row.delta.indexOf("+") !== -1) {
                return "positive-row";
            } else {
                return "negative-row";
            }
        },
        //点击check查看图表
        handleClick(row) {
            // console.log(row);
            pubsub.publish("请显示本行内容", row.stockName)
            pubsub.publish("请显示本行预测数据", row.stockName)
            pubsub.publish("请显示本行上涨概率", row.stockName)
        },
        //点击delete
        handleDelete(row) {
            console.log(row)
            axios.delete(`/api/display/api/v1/collect/${row.stockName}`, {
                headers: {
                    'Authorization': localStorage.getItem('token'),
                    'Content-Type': 'application/json'
                }
            }).then(res => {
                // console.log(res.data)
                if (res.data.code == 0) {
                    this.removeObjWithArr(this.tableData, row)
                    pubsub.publish("取消收藏图标按钮", row)
                }
            }).catch(err => {
                console.error(err);
            })

        },
        //判断对象是否相等
        isObjectValueEqual(a, b) {
            if (typeof (a) != "object" && typeof (b) != "object") {
                if (a == b) {
                    return true;
                } else {
                    return false;
                }
            }
            var aProps = Object.getOwnPropertyNames(a);
            var bProps = Object.getOwnPropertyNames(b);

            if (aProps.length != bProps.length) {
                return false;
            }

            for (var i = 0; i < aProps.length; i++) {
                var propName = aProps[i];

                if (a[propName] !== b[propName]) {
                    return false;
                }
            }

            return true;
        },
        //从数组中移除对象
        removeObjWithArr(_arr, _obj) {
            var length = _arr.length;
            for (var i = 0; i < length; i++) {
                if (this.isObjectValueEqual(_arr[i], _obj)) {
                    if (i == 0) {
                        _arr.shift(); //删除并返回数组的第一个元素
                        return;
                    } else if (i == length - 1) {
                        _arr.pop();  //删除并返回数组的最后一个元素
                        return;
                    } else {
                        _arr.splice(i, 1); //删除下标为i的元素
                        return;
                    }
                }
            }
        }
    }
}
</script>

<style>
.el-table .positive-row {
    color: #b61718;
    font-weight: bold;
}

.el-table .negative-row {
    color: #0b715e;
    font-weight: bold;
}

.el-table--scrollable-x .el-table__body-wrapper {
    overflow-x: hidden;
}
</style>
