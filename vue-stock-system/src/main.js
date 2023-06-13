import Vue from 'vue'
import App from './App.vue'

import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import VueRouter from "vue-router";
import router from "@/router";
import 'lib-flexible/flexible.js'
import * as echarts from "echarts";

Vue.prototype.$echarts = echarts


Vue.config.productionTip = false
Vue.use(ElementUI)
Vue.use(VueRouter)

const originalPush = VueRouter.prototype.push

VueRouter.prototype.push = function push(location) {
    return originalPush.call(this, location).catch(err => err)
}


new Vue({
    render: h => h(App),
    router: router
}).$mount('#app')
