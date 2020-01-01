const data = [
  {
    id: 'dashboard',
    icon: "simple-icon-home",
    label: "menu.dashboard",
    to: "/user/dashboard",
    newWindow: false,
    permissionId: null
  },
  {
    id: "personal-inspection",
    icon: "icofont-users-alt-4",
    label: "menu.personal-inspection",
    to: "/user/personal-inspection",
    newWindow: false,
    permissionId: 117,
    subs: [
      {
        id: 'personal-inspection-*-process-task',
        icon: "simple-icon-equalizer",
        label: "menu.process-task",
        to: "/user/personal-inspection/process-task",
        newWindow: false,
        permissionId: 118
      },
      {
        id: 'personal-inspection-*-history-task',
        icon: "simple-icon-equalizer",
        label: "menu.history-task",
        to: "/user/personal-inspection/history-task",
        newWindow: false,
        permissionId: 121
      },
      {
        id: 'personal-inspection-*-invalid-task',
        icon: "simple-icon-equalizer",
        label: "menu.invalid-task",
        to: "/user/personal-inspection/invalid-task",
        newWindow: false,
        permissionId: 124
      },
    ]
  },
  {
    id: "statistics",
    icon: "icofont-chart-pie",
    label: "menu.statistics",
    to: "/user/statistics",
    newWindow: false,
    permissionId: 127,
    subs: [
      {
        id: 'statistics-*-view',
        icon: "simple-icon-equalizer",
        label: "menu.statistics-view",
        to: "/user/statistics/view",
        newWindow: false,
        permissionId: 128,
      },
      {
        id: 'statistics-*-scan-devices',
        icon: "simple-icon-equalizer",
        label: "menu.statistics-scan-devices",
        to: "/user/statistics/scan-devices",
        newWindow: false,
        permissionId: 131,
      },
      {
        id: 'statistics-*-monitors',
        icon: "simple-icon-equalizer",
        label: "menu.statistics-monitors",
        to: "/user/statistics/monitors",
        newWindow: false,
        permissionId: 134,
      },
      {
        id: 'statistics-*-hand-checks',
        icon: "simple-icon-equalizer",
        label: "menu.statistics-hand-checks",
        to: "/user/statistics/hand-checks",
        newWindow: false,
        permissionId: 137,
      },
      {
        id: 'statistics-*-evaluate-monitors',
        icon: "simple-icon-equalizer",
        label: "menu.statistics-evaluate-monitors",
        to: "/user/statistics/evaluate-monitors",
        newWindow: false,
        permissionId: 140,
      },
      {
        id: 'statistics-*-working-hours',
        icon: "simple-icon-equalizer",
        label: "menu.statistics-working-hours",
        to: "/user/statistics/working-hours",
        newWindow: false,
        permissionId: 143,
      },
      {
        id: 'statistics-*-operating-hours',
        icon: "simple-icon-equalizer",
        label: "menu.statistics-operating-hours",
        to: "/user/statistics/operating-hours",
        newWindow: false,
        permissionId: 146,
      },
    ]
  },
  {
    id: "knowledge-base",
    icon: "icofont-notebook",
    label: "menu.knowledge-base",
    to: "/user/knowledge-base",
    newWindow: false,
    permissionId: 149,
    subs: [
      {
        id: 'knowledge-base-*-pending-case',
        icon: "simple-icon-equalizer",
        label: "menu.pending-case",
        to: "/user/knowledge-base/pending-case",
        newWindow: false,
        permissionId: 150,
      },
      {
        id: 'knowledge-base-*-personal-case',
        icon: "simple-icon-equalizer",
        label: "menu.personal-case",
        to: "/user/knowledge-base/personal-case",
        newWindow: false,
        permissionId: 153,
      },
    ]
  },

];

export default data;
