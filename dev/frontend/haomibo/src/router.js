import Vue from 'vue'
import Router from 'vue-router'
import AuthRequired from './utils/AuthRequired'

Vue.use(Router);

const routes = [
  {
    path: '/',
    component: () => import('./views/admin'),
    redirect: '/admin',
    beforeEnter: AuthRequired,
    children: [
      {
        path: 'admin',
        component: () => import( './views/admin/dashboard/index'),
        redirect: '/admin/dashboard',
        beforeEnter: AuthRequired,
        children: [
          {
            path: 'dashboard',
            beforeEnter: AuthRequired,
            component: () => import('./views/admin/dashboard/dashboard')
          }
        ]
      },
      {
        path: 'admin/permission-management',
        component: () => import('./views/admin/permission-management'),
        redirect: '/admin/permission-management/organization-management',
        beforeEnter: AuthRequired,
        children: [
          {
            path: 'organization-management',
            beforeEnter: AuthRequired,
            component: () => import('./views/admin/permission-management/organization-management')
          },
          {
            path: 'assign-permission-management',
            beforeEnter: AuthRequired,
            component: () => import('./views/admin/permission-management/assign-permission-management')
          },
          {
            path: 'permission-control',
            beforeEnter: AuthRequired,
            component: () => import('./views/admin/permission-management/permission-control')
          },
          {
            path: 'user-management',
            beforeEnter: AuthRequired,
            component: () => import('./views/admin/permission-management/user-management')
          },
        ]
      },
      {
        path: 'admin/device-management',
        component: () => import('./views/admin/device-management'),
        redirect: '/admin/device-management/device-classify',
        beforeEnter: AuthRequired,
        children: [
          {
            path: 'device-classify',
            beforeEnter: AuthRequired,
            component: () => import('./views/admin/device-management/device-classify')
          },
          {
            path: 'document-template',
            beforeEnter: AuthRequired,
            component: () => import('./views/admin/device-management/document-template')
          },
          {
            path: 'document-management',
            beforeEnter: AuthRequired,
            component: () => import('./views/admin/device-management/document-management')
          },
          {
            path: 'device-table',
            beforeEnter: AuthRequired,
            component: () => import('./views/admin/device-management/device-table')
          },
          {
            path: 'device-config',
            beforeEnter: AuthRequired,
            component: () => import('./views/admin/device-management/device-config')
          },
          {
            path: 'condition-monitoring',
            beforeEnter: AuthRequired,
            component: () => import('./views/admin/device-management/condition-monitoring')
          },
        ]
      },
      {
        path: 'admin/site-management',
        beforeEnter: AuthRequired,
        component: () => import('./views/admin/site-management')
      },
      {
        path: 'admin/system-setting',
        beforeEnter: AuthRequired,
        component: () => import('./views/admin/system-setting')
      },
      {
        path: 'admin/log-management',
        beforeEnter: AuthRequired,
        component: () => import('./views/admin/log-management'),
        redirect: '/admin/log-management/operating-log',
        children: [
          {
            path: 'operating-log',
            beforeEnter: AuthRequired,
            component: () => import('./views/admin/log-management/operating-log')
          },
          {
            path: 'device-log',
            beforeEnter: AuthRequired,
            component: () => import('./views/admin/log-management/device-log')
          }
        ]
      },
      {
        path: 'admin/personal-inspection',
        beforeEnter: AuthRequired,
        component: () => import('./views/admin/personal-inspection'),
        redirect: '/admin/personal-inspection/process-task',
        children: [
          {
            path: 'process-task',
            beforeEnter: AuthRequired,
            component: () => import('./views/admin/personal-inspection/process-task')
          },
          {
            path: 'history-task',
            beforeEnter: AuthRequired,
            component: () => import('./views/admin/personal-inspection/history-task')
          },
          {
            path: 'invalid-task',
            beforeEnter: AuthRequired,
            component: () => import('./views/admin/personal-inspection/invalid-task')
          },
        ]
      },
      {
        path: 'admin/statistics',
        beforeEnter: AuthRequired,
        component: () => import('./views/admin/statistics'),
        redirect: 'admin/statistics/view',
        children: [
          {
            path: 'view',
            beforeEnter: AuthRequired,
            component: () => import('./views/admin/statistics/view')
          },
          {
            path: 'scan-devices',
            beforeEnter: AuthRequired,
            component: () => import('./views/admin/statistics/scan-devices')
          },
          {
            path: 'monitors',
            beforeEnter: AuthRequired,
            component: () => import('./views/admin/statistics/monitors')
          },
          {
            path: 'hand-checks',
            beforeEnter: AuthRequired,
            component: () => import('./views/admin/statistics/hand-checks')
          },
          {
            path: 'evaluate-monitors',
            beforeEnter: AuthRequired,
            component: () => import('./views/admin/statistics/evaluate-monitors')
          },
          {
            path: 'working-hours',
            beforeEnter: AuthRequired,
            component: () => import('./views/admin/statistics/working-hours')
          },
          {
            path: 'operating-hours',
            beforeEnter: AuthRequired,
            component: () => import('./views/admin/statistics/operating-hours')
          },
        ]
      },
      {
        path: 'admin/black-list',
        beforeEnter: AuthRequired,
        component: () => import('./views/admin/black-list'),
        redirect: '/admin/black-list/personal-black-list',
        children: [
          {
            path: 'personal-black-list',
            beforeEnter: AuthRequired,
            component: () => import('./views/admin/black-list/personal-black-list')
          }
        ]
      },
      {
        path: 'admin/knowledge-base',
        beforeEnter: AuthRequired,
        component: () => import('./views/admin/knowledge-base'),
        redirect: '/admin/knowledge-base/pending-case',
        children: [
          {
            path: 'pending-case',
            beforeEnter: AuthRequired,
            component: () => import('./views/admin/knowledge-base/pending-case')
          },
          {
            path: 'personal-case',
            beforeEnter: AuthRequired,
            component: () => import('./views/admin/knowledge-base/personal-case')
          }
        ]
      },
      {
        path: 'admin/maintenance-management',
        beforeEnter: AuthRequired,
        component: () => import('./views/admin/maintenance-management'),
        redirect: '/admin/maintenance-management/time-task',
        children: [
          {
            path: 'time-task',
            beforeEnter: AuthRequired,
            component: () => import('./views/admin/maintenance-management/time-task')
          },
          {
            path: 'routine-plan',
            beforeEnter: AuthRequired,
            component: () => import('./views/admin/maintenance-management/routine-plan')
          },
          {
            path: 'maintenance-task',
            beforeEnter: AuthRequired,
            component: () => import('./views/admin/maintenance-management/maintenance-task')
          },
          {
            path: 'process-task',
            beforeEnter: AuthRequired,
            component: () => import('./views/admin/maintenance-management/process-task')
          },
          {
            path: 'history-record',
            beforeEnter: AuthRequired,
            component: () => import('./views/admin/maintenance-management/history-record')
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
    path: '/admin/auth',
    component: () => import('./views/user'),
    redirect: '/admin/auth/login',
    children: [
      {
        path: 'login',
        component: () => import( './views/admin/auth/login')
        // component: () => import( './views/user/login')
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
