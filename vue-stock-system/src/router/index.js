import VueRouter from "vue-router";
import MainPage from "@/pages/MainPage";
import Menu2Page from "@/pages/Menu2Page";
import SearchPage from "@/pages/SearchPage";
import MainComponent from "@/components/MainComponent";
import LoginPage from "@/pages/LoginPage";
import RegisterPage from "@/pages/RegisterPage";

const router = new VueRouter({
    routes: [
        {
            path: '/',
            redirect: '/login'
        },
        {
            path: '/login',
            component: LoginPage
        },
        {
            path: '/register',
            meta:{isLogin:true},
            component: RegisterPage
        },
        {
            path: '/MainComponent',
            meta:{isLogin:true},
            component: MainComponent,
            children: [
                {
                    path: 'Home',
                    meta:{isLogin:true},
                    component: MainPage
                },
                {
                    path: 'Menu2',
                    meta:{isLogin:true},
                    component: Menu2Page
                },
            ]
        },
        {
            path: '/SearchPage',
            meta:{isLogin:true},
            component: SearchPage
        },
    ]
})
router.beforeEach((to,from,next)=>{
    if(to.matched.some(res=>res.meta.isLogin)){//判断是否需要登录
        if (localStorage['token']) {
            next();
        }else{
            next({
                path:"/login"
            });
        }
    }else{
        next()
    }
});


export default router