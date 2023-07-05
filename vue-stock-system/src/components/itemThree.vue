<template>
  <div>
    <el-table :data="tableData" height="321.10px" style="width: 353.338px;  font-size: 5px;">
      <div slot="empty" style="text-align: center">
        <el-empty description="no records" image-size="108"/>
      </div>
      <el-table-column fixed prop="stockName" label="Stock" width="100"></el-table-column>
      <el-table-column prop="open" label="open" width="75"></el-table-column>
      <el-table-column prop="zhengfu" label="+/-" width="75"></el-table-column>
      <el-table-column
          label="operation"
          width="100">
        <template slot-scope="scope">
          <el-button @click="handleClick(scope.row)" type="text" size="mini">check</el-button>
          <el-button @click="handleDelete(scope.row)" type="text" size="mini">delete</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>

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
      // console.log("表格数据接收", stodata)
      this.tableData.push({
        stockName: stodata[0].code,
        open: (stodata[29].open).toFixed(2),
        zhengfu: (stodata[29].close - stodata[29].open).toFixed(2)
      })
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
          for (var i = 0; i < res.data.data.length; i++){
            this.tableData.push({
              stockName: res.data.data[i].code,
              open: res.data.data[i].openPrice.toFixed(2),
              zhengfu: (res.data.data[i].closePrice - res.data.data[i].openPrice).toFixed(2)
            })
            window.sessionStorage.setItem(res.data.data[i].code + 'icon', 'el-icon-star-on')
          }
        })
        .catch(err => {
          console.error(err);
        })
  },
  methods: {
    //点击check查看图表
    handleClick(row) {
      // console.log(row);
      pubsub.publish("请显示本行内容", row.stockName)
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

<style scoped>

</style>
