export const authRoutes = [
  {
    path: '/auth',
    component: () => import('../views/auth/index'),
    redirect: '/auth/login',
    children: [
      {
        path: 'login',
        component: () => import( '../views/auth/login')
      },
    ]
  },
];
