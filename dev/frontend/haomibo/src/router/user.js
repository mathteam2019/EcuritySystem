import AuthRequired from "../utils/user-auth-required";


export const userRoutes = {
  path: '/user',
  component: () => import('../views/user/index'),
  redirect: '/user/dashboard',
  beforeEnter: AuthRequired,
  children: [
    {
      path: 'dashboard',
      beforeEnter: AuthRequired,
      component: () => import('../views/user/dashboard/dashboard')
    },
    {
      path: 'personal-inspection',
      beforeEnter: AuthRequired,
      component: () => import('../views/user/personal-inspection/index'),
      redirect: '/user/personal-inspection/process-task',
      children: [
        {
          path: 'process-task',
          beforeEnter: AuthRequired,
          component: () => import('../views/user/personal-inspection/process-task')
        },
        {
          path: 'history-task',
          beforeEnter: AuthRequired,
          component: () => import('../views/user/personal-inspection/history-task')
        },
        {
          path: 'invalid-task',
          beforeEnter: AuthRequired,
          component: () => import('../views/user/personal-inspection/invalid-task')
        },
      ]
    },
    {
      path: 'statistics',
      beforeEnter: AuthRequired,
      component: () => import('../views/user/statistics/index'),
      redirect: 'user/statistics/view',
      children: [
        {
          path: 'view',
          beforeEnter: AuthRequired,
          component: () => import('../views/user/statistics/view')
        },
        {
          path: 'scan-devices',
          beforeEnter: AuthRequired,
          component: () => import('../views/user/statistics/scan-devices')
        },
        {
          path: 'monitors',
          beforeEnter: AuthRequired,
          component: () => import('../views/user/statistics/monitors')
        },
        {
          path: 'hand-checks',
          beforeEnter: AuthRequired,
          component: () => import('../views/user/statistics/hand-checks')
        },
        {
          path: 'evaluate-monitors',
          beforeEnter: AuthRequired,
          component: () => import('../views/user/statistics/evaluate-monitors')
        },
        {
          path: 'working-hours',
          beforeEnter: AuthRequired,
          component: () => import('../views/user/statistics/working-hours')
        },
        {
          path: 'operating-hours',
          beforeEnter: AuthRequired,
          component: () => import('../views/user/statistics/operating-hours')
        },
      ]
    },
    {
      path: 'black-list',
      beforeEnter: AuthRequired,
      component: () => import('../views/user/black-list/index'),
      redirect: '/user/black-list/personal-black-list',
      children: [
        {
          path: 'personal-black-list',
          beforeEnter: AuthRequired,
          component: () => import('../views/user/black-list/personal-black-list')
        }
      ]
    },
    {
      path: 'knowledge-base',
      beforeEnter: AuthRequired,
      component: () => import('../views/user/knowledge-base/index'),
      redirect: '/user/knowledge-base/pending-case',
      children: [
        {
          path: 'pending-case',
          beforeEnter: AuthRequired,
          component: () => import('../views/user/knowledge-base/pending-case')
        },
        {
          path: 'personal-case',
          beforeEnter: AuthRequired,
          component: () => import('../views/user/knowledge-base/personal-case')
        }
      ]
    },
    {
      path: 'maintenance-management',
      beforeEnter: AuthRequired,
      component: () => import('../views/user/maintenance-management/index'),
      redirect: '/user/maintenance-management/time-task',
      children: [
        {
          path: 'time-task',
          beforeEnter: AuthRequired,
          component: () => import('../views/user/maintenance-management/time-task')
        },
        {
          path: 'routine-plan',
          beforeEnter: AuthRequired,
          component: () => import('../views/user/maintenance-management/routine-plan')
        },
        {
          path: 'maintenance-task',
          beforeEnter: AuthRequired,
          component: () => import('../views/user/maintenance-management/maintenance-task')
        },
        {
          path: 'process-task',
          beforeEnter: AuthRequired,
          component: () => import('../views/user/maintenance-management/process-task')
        },
        {
          path: 'history-record',
          beforeEnter: AuthRequired,
          component: () => import('../views/user/maintenance-management/history-record')
        },

      ]
    },
  ]

};
