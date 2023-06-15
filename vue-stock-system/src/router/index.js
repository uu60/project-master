import VueRouter from "vue-router";
import MainPage from "@/pages/MainPage";
import Menu2Page from "@/pages/Menu2Page";
import SearchPage from "@/pages/SearchPage";
import MainComponent from "@/components/MainComponent";
const router = new VueRouter({
    routes: [
        {
            path:'/',
            redirect:'/SearchPage'
        },
        {
            path:'/Home',
            component:MainPage
        },
        {
            path:'/Menu2',
            component:Menu2Page
        },
        {
            path: '/SearchPage',
            component: SearchPage
        }
    ]
})

export default router