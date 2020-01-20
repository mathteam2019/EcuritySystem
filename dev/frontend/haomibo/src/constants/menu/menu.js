const data = [
  {
    id: 'dashboard',
    icon: "simple-icon-home",
    label: "menu.dashboard",
    to: "/pages/dashboard",
    newWindow: false,
    permissionId: null,
    subs:null
  },
  {
    id: "permission-management",
    icon: "icofont-shield",
    label: "menu.permission-management",
    to: "/pages/permission-management",
    newWindow: false,
    permissionId: 2,
    subs: [
      {
        id: 'permission-management-*-organization-management',
        icon: "icofont-caret-right",
        label: "menu.organization-management",
        to: "/pages/permission-management/organization-management",
        newWindow: false,
        permissionId: 3,
      },
      {
        id: 'permission-management-*-user-management',
        icon: "icofont-caret-right",
        label: "menu.user-management",
        to: "/pages/permission-management/user-management",
        newWindow: false,
        permissionId: 10,
      },
      {
        id: 'permission-management-*-permission-control',
        icon: "icofont-caret-right",
        label: "menu.permission-control",
        to: "/pages/permission-management/permission-control",
        newWindow: false,
        permissionId: 24,
      },
      {
        id: 'permission-management-*-assign-permission-management',
        icon: "icofont-caret-right",
        label: "menu.assign-permission-management",
        to: "/pages/permission-management/assign-permission-management",
        newWindow: false,
        permissionId: 37,
      },
    ]
  },
  {
    id: "site-management",
    icon: "icofont-location-pin",
    label: "menu.site-management",
    to: "/pages/site-management",
    newWindow: false,
    permissionId: 50,
    subs:null
  },
  {
    id: "system-setting",
    icon: "icofont-gears",
    label: "menu.system-setting",
    to: "/pages/system-setting",
    newWindow: false,
    permissionId: 57,
    subs:[
      {
        id: 'system-setting-*-system-setting',
        icon: "icofont-caret-right",
        label: "menu.system-setting",
        to: "/pages/system-setting/system-setting",
        newWindow: false,
        permissionId: 164,
      },
      {
        id: 'system-setting-*-goods-management',
        icon: "icofont-caret-right",
        label: "menu.goods-management",
        to: "/pages/system-setting/goods-management",
        newWindow: false,
        permissionId: 165,
      },
      {
        id: 'system-setting-*-permission-control',
        icon: "icofont-caret-right",
        label: "menu.dictionary-table",
        to: "/pages/system-setting/dictionary-table",
        newWindow: false,
        permissionId: 169,
      }
    ]
  },
  {
    id: "device-management",
    icon: "icofont-site-map",
    label: "menu.device-management",
    to: "/pages/device-management",
    newWindow: false,
    permissionId: 65,
    subs: [
      {
        id: 'device-management-*-document-template',
        icon: "simple-icon-docs",
        label: "menu.document-template",
        to: "/pages/device-management/document-template",
        newWindow: false,
        permissionId: 73,
      },
      {
        id: 'device-management-*-document-management',
        icon: "simple-icon-folder",
        label: "menu.document-management",
        to: "/pages/device-management/document-management",
        newWindow: false,
        permissionId: 84,
      },
      {
        id: 'device-management-*-device-table',
        icon: "simple-icon-grid",
        label: "menu.device-table",
        to: "/pages/device-management/device-table",
        newWindow: false,
        permissionId: 91,
      },
      {
        id: 'device-management-*-device-config',
        icon: "simple-icon-settings",
        label: "menu.device-config",
        to: "/pages/device-management/device-config",
        newWindow: false,
        permissionId: 98,
      },
      {
        id: 'device-management-*-condition-monitoring',
        icon: "simple-icon-screen-desktop",
        label: "menu.condition-monitoring",
        to: "/pages/device-management/condition-monitoring",
        newWindow: false,
        permissionId: 163,
      },
    ]
  },
  {
    id: "log-management",
    icon: "icofont-list",
    label: "menu.log-management",
    to: "/pages/log-management",
    newWindow: false,
    permissionId: 105,
    subs: [
      {
        id: 'log-management-*-operating-log',
        icon: "simple-icon-equalizer",
        label: "menu.operating-log",
        to: "/pages/log-management/operating-log",
        newWindow: false,
        permissionId: 106,
      },
      {
        id: 'log-management-*-device-log',
        icon: "simple-icon-settings",
        label: "menu.device-log",
        to: "/pages/log-management/device-log",
        newWindow: false,
        permissionId: 113,
      },
    ]
  },
  {
    id: "personal-inspection",
    icon: "icofont-users-alt-4",
    label: "menu.personal-inspection",
    to: "/pages/personal-inspection",
    newWindow: false,
    permissionId: 117,
    subs: [
      {
        id: 'personal-inspection-*-process-task',
        icon: "simple-icon-equalizer",
        label: "menu.process-task",
        to: "/pages/personal-inspection/process-task",
        newWindow: false,
        permissionId: 118
      },
      {
        id: 'personal-inspection-*-history-task',
        icon: "simple-icon-equalizer",
        label: "menu.history-task",
        to: "/pages/personal-inspection/history-task",
        newWindow: false,
        permissionId: 121
      },
      {
        id: 'personal-inspection-*-invalid-task',
        icon: "simple-icon-equalizer",
        label: "menu.invalid-task",
        to: "/pages/personal-inspection/invalid-task",
        newWindow: false,
        permissionId: 124
      },
    ]
  },
  {
    id: "statistics",
    icon: "icofont-chart-pie",
    label: "menu.statistics",
    to: "/pages/statistics",
    newWindow: false,
    permissionId: 127,
    subs: [
      {
        id: 'statistics-*-view',
        icon: "simple-icon-equalizer",
        label: "menu.statistics-view",
        to: "/pages/statistics/view",
        newWindow: false,
        permissionId: 128,
      },
      {
        id: 'statistics-*-scan-devices',
        icon: "simple-icon-equalizer",
        label: "menu.statistics-scan-devices",
        to: "/pages/statistics/scan-devices",
        newWindow: false,
        permissionId: 131,
      },
      {
        id: 'statistics-*-monitors',
        icon: "simple-icon-equalizer",
        label: "menu.statistics-monitors",
        to: "/pages/statistics/monitors",
        newWindow: false,
        permissionId: 134,
      },
      {
        id: 'statistics-*-hand-checks',
        icon: "simple-icon-equalizer",
        label: "menu.statistics-hand-checks",
        to: "/pages/statistics/hand-checks",
        newWindow: false,
        permissionId: 137,
      },
      {
        id: 'statistics-*-evaluate-monitors',
        icon: "simple-icon-equalizer",
        label: "menu.statistics-evaluate-monitors",
        to: "/pages/statistics/evaluate-monitors",
        newWindow: false,
        permissionId: 140,
      },
      {
        id: 'statistics-*-working-hours',
        icon: "simple-icon-equalizer",
        label: "menu.statistics-working-hours",
        to: "/pages/statistics/working-hours",
        newWindow: false,
        permissionId: 143,
      },
      {
        id: 'statistics-*-operating-hours',
        icon: "simple-icon-equalizer",
        label: "menu.statistics-operating-hours",
        to: "/pages/statistics/operating-hours",
        newWindow: false,
        permissionId: 146,
      },
    ]
  },
  {
    id: "knowledge-base",
    icon: "icofont-notebook",
    label: "menu.knowledge-base",
    to: "/pages/knowledge-base",
    newWindow: false,
    permissionId: 149,
    subs: [
      {
        id: 'knowledge-base-*-pending-case',
        icon: "simple-icon-equalizer",
        label: "menu.pending-case",
        to: "/pages/knowledge-base/pending-case",
        newWindow: false,
        permissionId: 150,
      },
      {
        id: 'knowledge-base-*-personal-case',
        icon: "simple-icon-equalizer",
        label: "menu.personal-case",
        to: "/pages/knowledge-base/personal-case",
        newWindow: false,
        permissionId: 153,
      },
    ]
  },
];

export default data;