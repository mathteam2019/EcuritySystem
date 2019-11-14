export const authRoutes = [
  {
    path: '/admin/auth',
    component: () => import('../views/admin/auth/index'),
    redirect: '/admin/auth/login',
    children: [
      {
        path: 'login',
        component: () => import( '../views/admin/auth/login')
      },
    ]
  },
  {
    path: '/user/auth',
    component: () => import('../views/user/auth/index'),
    redirect: '/user/auth/login',
    children: [
      {
        path: 'login',
        component: () => import( '../views/user/auth/login')
      },
    ]
  }
];
