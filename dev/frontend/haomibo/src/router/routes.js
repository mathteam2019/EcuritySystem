import AuthRequired from "../utils/auth-required";


export const Routes = {
  path: '/pages',
  component: () => import('../views/pages/index'),
  redirect: '/pages/dashboard',
  beforeEnter: AuthRequired,
  children: [
    {
      path: 'dashboard',
      beforeEnter: AuthRequired,
      component: () => import('../views/pages/dashboard/dashboard')
    },
    {
      path: 'permission-management',
      component: () => import('../views/pages/permission-management/index'),
      redirect: '/pages/permission-management/organization-management',
      beforeEnter: AuthRequired,
      children: [
        {
          path: 'organization-management',
          beforeEnter: AuthRequired,
          component: () => import('../views/pages/permission-management/organization-management')
        },
        {
          path: 'assign-permission-management',
          beforeEnter: AuthRequired,
          component: () => import('../views/pages/permission-management/assign-permission-management')
        },
        {
          path: 'permission-control',
          beforeEnter: AuthRequired,
          component: () => import('../views/pages/permission-management/permission-control')
        },
        {
          path: 'user-management',
          beforeEnter: AuthRequired,
          component: () => import('../views/pages/permission-management/user-management')
        },
      ]
    },
    {
      path: 'device-management',
      component: () => import('../views/pages/device-management/index'),
      redirect: '/pages/device-management/device-classify',
      beforeEnter: AuthRequired,
      children: [
        {
          path: 'document-template',
          beforeEnter: AuthRequired,
          component: () => import('../views/pages/device-management/document-template')
        },
        {
          path: 'document-management',
          beforeEnter: AuthRequired,
          component: () => import('../views/pages/device-management/document-management')
        },
        {
          path: 'device-table',
          beforeEnter: AuthRequired,
          component: () => import('../views/pages/device-management/device-table')
        },
        {
          path: 'device-config',
          beforeEnter: AuthRequired,
          component: () => import('../views/pages/device-management/device-config')
        },
        {
          path: 'condition-monitoring',
          beforeEnter: AuthRequired,
          component: () => import('../views/pages/device-management/condition-monitoring')
        },
      ]
    },
    {
      path: 'site-management',
      beforeEnter: AuthRequired,
      component: () => import('../views/pages/site-management/index')
    },
    {
      path: 'system-setting',
      beforeEnter: AuthRequired,
      component: () => import('../views/pages/system-setting/index'),
      redirect: '/pages/system-setting/system-setting',
      children: [
        {
          path: 'system-setting',
          beforeEnter: AuthRequired,
          component: () => import('../views/pages/system-setting/system-setting')
        },
        {
          path: 'goods-management',
          beforeEnter: AuthRequired,
          component: () => import('../views/pages/system-setting/goods-management')
        },
        {
          path: 'dictionary-table',
          beforeEnter: AuthRequired,
          component: () => import('../views/pages/system-setting/dictionary-table')
        },
      ]
    },
    {
      path: 'log-management',
      beforeEnter: AuthRequired,
      component: () => import('../views/pages/log-management/index'),
      redirect: '/pages/log-management/operating-log',
      children: [
        {
          path: 'operating-log',
          beforeEnter: AuthRequired,
          component: () => import('../views/pages/log-management/operating-log')
        },
        {
          path: 'device-log',
          beforeEnter: AuthRequired,
          component: () => import('../views/pages/log-management/device-log')
        }
      ]
    },
    {
      path: 'personal-inspection',
      beforeEnter: AuthRequired,
      component: () => import('../views/pages/personal-inspection/index'),
      redirect: '/pages/personal-inspection/process-task',
      children: [
        {
          path: 'process-task',
          beforeEnter: AuthRequired,
          component: () => import('../views/pages/personal-inspection/process-task')
        },
        {
          path: 'history-task',
          beforeEnter: AuthRequired,
          component: () => import('../views/pages/personal-inspection/history-task')
        },
        {
          path: 'invalid-task',
          beforeEnter: AuthRequired,
          component: () => import('../views/pages/personal-inspection/invalid-task')
        },
      ]
    },
    {
      path: 'statistics',
      beforeEnter: AuthRequired,
      component: () => import('../views/pages/statistics/index'),
      redirect: 'pages/statistics/view',
      children: [
        {
          path: 'view',
          beforeEnter: AuthRequired,
          component: () => import('../views/pages/statistics/view')
        },
        {
          path: 'scan-devices',
          beforeEnter: AuthRequired,
          component: () => import('../views/pages/statistics/scan-devices')
        },
        {
          path: 'monitors',
          beforeEnter: AuthRequired,
          component: () => import('../views/pages/statistics/monitors')
        },
        {
          path: 'hand-checks',
          beforeEnter: AuthRequired,
          component: () => import('../views/pages/statistics/hand-checks')
        },
        {
          path: 'evaluate-monitors',
          beforeEnter: AuthRequired,
          component: () => import('../views/pages/statistics/evaluate-monitors')
        },
        {
          path: 'working-hours',
          beforeEnter: AuthRequired,
          component: () => import('../views/pages/statistics/working-hours')
        },
        {
          path: 'operating-hours',
          beforeEnter: AuthRequired,
          component: () => import('../views/pages/statistics/operating-hours')
        },
      ]
    },
    {
      path: 'knowledge-base',
      beforeEnter: AuthRequired,
      component: () => import('../views/pages/knowledge-base/index'),
      redirect: '/pages/knowledge-base/pending-case',
      children: [
        {
          path: 'pending-case',
          beforeEnter: AuthRequired,
          component: () => import('../views/pages/knowledge-base/pending-case')
        },
        {
          path: 'personal-case',
          beforeEnter: AuthRequired,
          component: () => import('../views/pages/knowledge-base/personal-case')
        }
      ]
    },
  ]

};
