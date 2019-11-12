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
        beforeEnter: AuthRequired,
        children: [
          {
            path: 'dashboard',
            beforeEnter: AuthRequired,
            component: () => import('./views/app/dashboard/dashboard')
          }
        ]
      },
      {
        path: 'app/permission-management',
        component: () => import('./views/app/permission-management'),
        redirect: '/app/permission-management/organization-management',
        beforeEnter: AuthRequired,
        children: [
          {
            path: 'organization-management',
            beforeEnter: AuthRequired,
            component: () => import('./views/app/permission-management/organization-management')
          },
          {
            path: 'assign-permission-management',
            beforeEnter: AuthRequired,
            component: () => import('./views/app/permission-management/assign-permission-management')
          },
          {
            path: 'permission-control',
            beforeEnter: AuthRequired,
            component: () => import('./views/app/permission-management/permission-control')
          },
          {
            path: 'user-management',
            beforeEnter: AuthRequired,
            component: () => import('./views/app/permission-management/user-management')
          },
        ]
      },
      {
        path: 'app/device-management',
        component: () => import('./views/app/device-management'),
        redirect: '/app/device-management/device-classify',
        beforeEnter: AuthRequired,
        children: [
          {
            path: 'device-classify',
            beforeEnter: AuthRequired,
            component: () => import('./views/app/device-management/device-classify')
          },
          {
            path: 'document-template',
            beforeEnter: AuthRequired,
            component: () => import('./views/app/device-management/document-template')
          },
          {
            path: 'document-management',
            beforeEnter: AuthRequired,
            component: () => import('./views/app/device-management/document-management')
          },
          {
            path: 'device-table',
            beforeEnter: AuthRequired,
            component: () => import('./views/app/device-management/device-table')
          },
          {
            path: 'device-config',
            beforeEnter: AuthRequired,
            component: () => import('./views/app/device-management/device-config')
          },
          {
            path: 'condition-monitoring',
            beforeEnter: AuthRequired,
            component: () => import('./views/app/device-management/condition-monitoring')
          },
        ]
      },
      {
        path: 'app/site-management',
        beforeEnter: AuthRequired,
        component: () => import('./views/app/site-management')
      },
      {
        path: 'app/system-setting',
        beforeEnter: AuthRequired,
        component: () => import('./views/app/system-setting')
      },
      {
        path: 'app/log-management',
        beforeEnter: AuthRequired,
        component: () => import('./views/app/log-management'),
        redirect: '/app/log-management/operating-log',
        children: [
          {
            path: 'operating-log',
            beforeEnter: AuthRequired,
            component: () => import('./views/app/log-management/operating-log')
          },
          {
            path: 'device-log',
            beforeEnter: AuthRequired,
            component: () => import('./views/app/log-management/device-log')
          }
        ]
      },
      {
        path: 'app/personal-inspection',
        beforeEnter: AuthRequired,
        component: () => import('./views/app/personal-inspection'),
        redirect: '/app/personal-inspection/process-task',
        children: [
          {
            path: 'process-task',
            beforeEnter: AuthRequired,
            component: () => import('./views/app/personal-inspection/process-task')
          },
          {
            path: 'history-task',
            beforeEnter: AuthRequired,
            component: () => import('./views/app/personal-inspection/history-task')
          },
          {
            path: 'invalid-task',
            beforeEnter: AuthRequired,
            component: () => import('./views/app/personal-inspection/invalid-task')
          },
        ]
      },
      {
        path: 'app/black-list',
        beforeEnter: AuthRequired,
        component: () => import('./views/app/black-list'),
        redirect: '/app/black-list/personal-black-list',
        children: [
          {
            path: 'personal-black-list',
            beforeEnter: AuthRequired,
            component: () => import('./views/app/black-list/personal-black-list')
          }
        ]
      },
      {
        path: 'app/knowledge-base',
        beforeEnter: AuthRequired,
        component: () => import('./views/app/knowledge-base'),
        redirect: '/app/knowledge-base/pending-case',
        children: [
          {
            path: 'pending-case',
            beforeEnter: AuthRequired,
            component: () => import('./views/app/knowledge-base/pending-case')
          },
          {
            path: 'personal-case',
            beforeEnter: AuthRequired,
            component: () => import('./views/app/knowledge-base/personal-case')
          }
        ]
      },
      {
        path: 'app/maintenance-management',
        beforeEnter: AuthRequired,
        component: () => import('./views/app/maintenance-management'),
        redirect: '/app/maintenance-management/time-task',
        children: [
          {
            path: 'time-task',
            beforeEnter: AuthRequired,
            component: () => import('./views/app/maintenance-management/time-task')
          },
          {
            path: 'routine-plan',
            beforeEnter: AuthRequired,
            component: () => import('./views/app/maintenance-management/routine-plan')
          },
          {
            path: 'maintenance-task',
            beforeEnter: AuthRequired,
            component: () => import('./views/app/maintenance-management/maintenance-task')
          },
          {
            path: 'process-task',
            beforeEnter: AuthRequired,
            component: () => import('./views/app/maintenance-management/process-task')
          },
          {
            path: 'history-record',
            beforeEnter: AuthRequired,
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
