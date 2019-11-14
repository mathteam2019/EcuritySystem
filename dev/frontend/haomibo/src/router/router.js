import Vue from 'vue'
import Router from 'vue-router'
import {adminRoutes} from "./admin";
import {authRoutes} from "./auth";
import {userRoutes} from "./user";

Vue.use(Router);

const routes = [
  {
    path: '/',
    redirect: '/user/dashboard'
  },

  adminRoutes,
  userRoutes,

  {
    path: '/error',
    component: () => import( '../views/error404')
  },
  {
    path: '/error/404',
    component: () => import( '../views/error404')
  },
  {
    path: '/error/405',
    component: () => import( '../views/error405')
  },
  ...authRoutes,
  {
    path: '*',
    component: () => import( '../views/error404')
  }
];

const index = new Router({
  linkActiveClass: 'active',
  routes,
  mode: 'history'
});

export default index
