import VueRouter from "vue-router";
import MainPage from "@/pages/MainPage";
import Menu2Page from "@/pages/Menu2Page";


const router = new VueRouter({
    routes: [
        {
            path:'/',
            redirect:'/Home'
        },
        {
            path:'/Home',
            component:MainPage
        },
        {
            path:'/Menu2',
            component:Menu2Page
        }
    ]
})

export default router