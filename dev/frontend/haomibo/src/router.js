import Vue from 'vue'
import Router from 'vue-router'
import AuthRequired from './utils/AuthRequired'

Vue.use(Router);

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
            path: 'assign-permission-management',
            component: () => import('./views/app/permission-management/assign-permission-management')
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
        path: 'app/site-management',
        component: () => import('./views/app/site-management')
      },
      {
        path: 'app/system-setting',
        component: () => import('./views/app/system-setting')
      },
      {
        path: 'app/log-management',
        component: () => import('./views/app/log-management'),
        redirect: '/app/log-management/operating-log',
        children: [
          {
            path: 'operating-log',
            component: () => import('./views/app/log-management/operating-log')
          },
          {
            path: 'device-log',
            component: () => import('./views/app/log-management/device-log')
          }
        ]
      },
      {
        path: 'app/personal-inspection',
        component: () => import('./views/app/personal-inspection'),
        redirect: '/app/personal-inspection/process-task',
        children: [
          {
            path: 'process-task',
            component: () => import('./views/app/personal-inspection/process-task')
          },
          {
            path: 'history-task',
            component: () => import('./views/app/personal-inspection/history-task')
          },
          {
            path: 'invalid-task',
            component: () => import('./views/app/personal-inspection/invalid-task')
          },
        ]
      },
      {
        path: 'app/black-list',
        component: () => import('./views/app/black-list'),
        redirect: '/app/black-list/personal-black-list',
        children: [
          {
            path: 'personal-black-list',
            component: () => import('./views/app/black-list/personal-black-list')
          }
        ]
      },
      {
        path: 'app/knowledge-base',
        component: () => import('./views/app/knowledge-base'),
        redirect: '/app/knowledge-base/pending-case',
        children: [
          {
            path: 'pending-case',
            component: () => import('./views/app/knowledge-base/pending-case')
          },
          {
            path: 'personal-case',
            component: () => import('./views/app/knowledge-base/personal-case')
          }
        ]
      },
      {
        path: 'app/maintenance-management',
        component: () => import('./views/app/maintenance-management'),
        redirect: '/app/maintenance-management/time-task',
        children: [
          {
            path: 'time-task',
            component: () => import('./views/app/maintenance-management/time-task')
          },
          {
            path: 'routine-plan',
            component: () => import('./views/app/maintenance-management/routine-plan')
          },
          {
            path: 'maintenance-task',
            component: () => import('./views/app/maintenance-management/maintenance-task')
          },
          {
            path: 'process-task',
            component: () => import('./views/app/maintenance-management/process-task')
          },
          {
            path: 'history-record',
            component: () => import('./views/app/maintenance-management/history-record')
          },

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
        component: () => import( './views/user/login')
      },
    ]
  },
  {
    path: '*',
    component: () => import( './views/error404')
  }
];

const router = new Router({
  linkActiveClass: 'active',
  routes,
  mode: 'history'
});

export default router
