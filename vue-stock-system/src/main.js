import Vue from 'vue'
import App from './App.vue'

import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import VueRouter from "vue-router";
import router from "@/router";
import 'lib-flexible/flexible.js'
import * as echarts from "echarts";
import store from "@/store/index";
import axios from "axios";


import qs from 'qs';
Vue.prototype.$qs = qs

Vue.prototype.$echarts = echarts
Vue.prototype.$axios = axios

Vue.config.productionTip = false
Vue.use(ElementUI)
Vue.use(VueRouter)

const originalPush = VueRouter.prototype.push

VueRouter.prototype.push = function push(location) {
    return originalPush.call(this, location).catch(err => err)
}


new Vue({
    render: h => h(App),
    router: router,
    store: store
}).$mount('#app')
