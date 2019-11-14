import AuthRequired from "../utils/admin-auth-required";


export const adminRoutes = {
  path: '/admin',
  component: () => import('../views/admin/index'),
  redirect: '/admin/dashboard',
  beforeEnter: AuthRequired,
  children: [
    {
      path: 'dashboard',
      beforeEnter: AuthRequired,
      component: () => import('../views/admin/dashboard/dashboard')
    },
    {
      path: 'permission-management',
      component: () => import('../views/admin/permission-management/index'),
      redirect: '/admin/permission-management/organization-management',
      beforeEnter: AuthRequired,
      children: [
        {
          path: 'organization-management',
          beforeEnter: AuthRequired,
          component: () => import('../views/admin/permission-management/organization-management')
        },
        {
          path: 'assign-permission-management',
          beforeEnter: AuthRequired,
          component: () => import('../views/admin/permission-management/assign-permission-management')
        },
        {
          path: 'permission-control',
          beforeEnter: AuthRequired,
          component: () => import('../views/admin/permission-management/permission-control')
        },
        {
          path: 'user-management',
          beforeEnter: AuthRequired,
          component: () => import('../views/admin/permission-management/user-management')
        },
      ]
    },
    {
      path: 'device-management',
      component: () => import('../views/admin/device-management/index'),
      redirect: '/admin/device-management/device-classify',
      beforeEnter: AuthRequired,
      children: [
        {
          path: 'device-classify',
          beforeEnter: AuthRequired,
          component: () => import('../views/admin/device-management/device-classify')
        },
        {
          path: 'document-template',
          beforeEnter: AuthRequired,
          component: () => import('../views/admin/device-management/document-template')
        },
        {
          path: 'document-management',
          beforeEnter: AuthRequired,
          component: () => import('../views/admin/device-management/document-management')
        },
        {
          path: 'device-table',
          beforeEnter: AuthRequired,
          component: () => import('../views/admin/device-management/device-table')
        },
        {
          path: 'device-config',
          beforeEnter: AuthRequired,
          component: () => import('../views/admin/device-management/device-config')
        },
        {
          path: 'condition-monitoring',
          beforeEnter: AuthRequired,
          component: () => import('../views/admin/device-management/condition-monitoring')
        },
      ]
    },
    {
      path: 'site-management',
      beforeEnter: AuthRequired,
      component: () => import('../views/admin/site-management/index')
    },
    {
      path: 'system-setting',
      beforeEnter: AuthRequired,
      component: () => import('../views/admin/system-setting/index')
    },
    {
      path: 'log-management',
      beforeEnter: AuthRequired,
      component: () => import('../views/admin/log-management/index'),
      redirect: '/admin/log-management/operating-log',
      children: [
        {
          path: 'operating-log',
          beforeEnter: AuthRequired,
          component: () => import('../views/admin/log-management/operating-log')
        },
        {
          path: 'device-log',
          beforeEnter: AuthRequired,
          component: () => import('../views/admin/log-management/device-log')
        }
      ]
    },
  ]

};
