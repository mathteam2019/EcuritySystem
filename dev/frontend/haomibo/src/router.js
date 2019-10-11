import Vue from 'vue'
import Router from 'vue-router'
import AuthRequired from './utils/AuthRequired'

Vue.use(Router)

const routes = [
  {
    path: '/',
    component: () => import('./views/app'),
    redirect: '/app',
    beforeEnter: AuthRequired,
    children: [
      {
        path: 'app',
        component: () => import( './views/app/dashboard/index'),
        redirect: '/app/dashboard',
        children: [
          {
            path: 'dashboard',
            component: () => import('./views/app/dashboard/dashboard')
          }
        ]
      },
      {
        path: 'app/permission-management',
        component: () => import('./views/app/permission-management'),
        redirect: '/app/permission-management/organization-management',
        children: [
          {
            path: 'organization-management',
            component: () => import('./views/app/permission-management/organization-management')
          },
          {
            path: 'role-management',
            component: () => import('./views/app/permission-management/role-management')
          },
          {
            path: 'permission-control',
            component: () => import('./views/app/permission-management/permission-control')
          },
          {
            path: 'user-management',
            component: () => import('./views/app/permission-management/user-management')
          },
        ]
      },
      {
        path: 'app/device-management',
        component: () => import('./views/app/device-management'),
        redirect: '/app/device-management/device-classify',
        children: [
          {
            path: 'device-classify',
            component: () => import('./views/app/device-management/device-classify')
          },
          {
            path: 'document-template',
            component: () => import('./views/app/device-management/document-template')
          },
          {
            path: 'document-management',
            component: () => import('./views/app/device-management/document-management')
          },
          {
            path: 'device-table',
            component: () => import('./views/app/device-management/device-table')
          },
          {
            path: 'device-config',
            component: () => import('./views/app/device-management/device-config')
          },
          {
            path: 'condition-monitoring',
            component: () => import('./views/app/device-management/condition-monitoring')
          },
        ]
      },
      {
        path: 'app/system-setting',
        component: () => import('./views/app/system-setting'),
        redirect: '/app/system-setting/paramenter-setting',
        children: [
          {
            path: 'parameter-setting',
            component: () => import('./views/app/system-setting/parameter-setting')
          },
          {
            path: 'site-management',
            component: () => import('./views/app/system-setting/site-management')
          }
        ]
      },
    ]
  },
  {
    path: '/error',
    component: () => import( './views/error404')
  },
  {
    path: '/error/404',
    component: () => import( './views/error404')
  },
  {
    path: '/error/405',
    component: () => import( './views/error405')
  },
  {
    path: '/user',
    component: () => import('./views/user'),
    redirect: '/user/login',
    children: [
      {
        path: 'login',
        component: () => import( './views/user/Login')
      },
      {
        path: 'register',
        component: () => import( './views/user/Register')
      },
      {
        path: 'forgot-password',
        component: () => import( './views/user/ForgotPassword')
      }
    ]
  },
  {
    path: '*',
    component: () => import( './views/error404')
  }
]

const router = new Router({
  linkActiveClass: 'active',
  routes,
  mode: 'history'
})

export default router
