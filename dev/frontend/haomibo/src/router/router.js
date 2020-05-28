import Vue from 'vue'
import Router from 'vue-router'
import {Routes} from "./routes";
import {authRoutes} from "./auth";

Vue.use(Router);

const routes = [
  {
    path: '/',
    redirect: '/pages/dashboard'
  },

  Routes,

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
