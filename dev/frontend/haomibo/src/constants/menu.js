const data = [
  {
    id: 'dashboard',
    icon: "simple-icon-home",
    label: "menu.dashboard",
    to: "/admin/dashboard",
    newWindow: false
  },
  {
    id: "permission-management",
    icon: "icofont-shield",
    label: "menu.permission-management",
    to: "/admin/permission-management",
    newWindow: false,
    subs: [
      {
        id: 'permission-management-*-organization-management',
        icon: "icofont-caret-right",
        label: "menu.organization-management",
        to: "/admin/permission-management/organization-management",
        newWindow: false
      },
      {
        id: 'permission-management-*-user-management',
        icon: "icofont-caret-right",
        label: "menu.user-management",
        to: "/admin/permission-management/user-management",
        newWindow: false
      },
      {
        id: 'permission-management-*-permission-control',
        icon: "icofont-caret-right",
        label: "menu.permission-control",
        to: "/admin/permission-management/permission-control",
        newWindow: false
      },
      {
        id: 'permission-management-*-assign-permission-management',
        icon: "icofont-caret-right",
        label: "menu.assign-permission-management",
        to: "/admin/permission-management/assign-permission-management",
        newWindow: false
      },


    ]
  },
  {
    id: "site-management",
    icon: "icofont-location-pin",
    label: "menu.site-management",
    to: "/admin/site-management",
    newWindow: false,
  },
  {
    id: "system-setting",
    icon: "icofont-gears",
    label: "menu.system-setting",
    to: "/admin/system-setting",
    newWindow: false,
  },
  {
    id: "device-management",
    icon: "icofont-site-map",
    label: "menu.device-management",
    to: "/admin/device-management",
    newWindow: false,
    subs: [
      {
        id: 'device-management-*-device-classify',
        icon: "simple-icon-chemistry",
        label: "menu.device-classify",
        to: "/admin/device-management/device-classify",
        newWindow: false
      },
      {
        id: 'device-management-*-document-template',
        icon: "simple-icon-docs",
        label: "menu.document-template",
        to: "/admin/device-management/document-template",
        newWindow: false
      },
      {
        id: 'device-management-*-document-management',
        icon: "simple-icon-folder",
        label: "menu.document-management",
        to: "/admin/device-management/document-management",
        newWindow: false
      },
      {
        id: 'device-management-*-device-table',
        icon: "simple-icon-grid",
        label: "menu.device-table",
        to: "/admin/device-management/device-table",
        newWindow: false
      },
      {
        id: 'device-management-*-device-config',
        icon: "simple-icon-settings",
        label: "menu.device-config",
        to: "/admin/device-management/device-config",
        newWindow: false
      },
      {
        id: 'device-management-*-condition-monitoring',
        icon: "simple-icon-screen-desktop",
        label: "menu.condition-monitoring",
        to: "/admin/device-management/condition-monitoring",
        newWindow: false
      },
    ]
  },
  {
    id: "log-management",
    icon: "icofont-list",
    label: "menu.log-management",
    to: "/admin/log-management",
    newWindow: false,
    subs: [
      {
        id: 'log-management-*-operating-log',
        icon: "simple-icon-equalizer",
        label: "menu.operating-log",
        to: "/admin/log-management/operating-log",
        newWindow: false
      },
      {
        id: 'log-management-*-device-log',
        icon: "simple-icon-settings",
        label: "menu.device-log",
        to: "/admin/log-management/device-log",
        newWindow: false
      },
    ]
  },
  {
    id: "personal-inspection",
    icon: "icofont-users-alt-4",
    label: "menu.personal-inspection",
    to: "/admin/personal-inspection",
    newWindow: false,
    subs: [
      {
        id: 'personal-inspection-*-process-task',
        icon: "simple-icon-equalizer",
        label: "menu.process-task",
        to: "/admin/personal-inspection/process-task",
        newWindow: false
      },
      {
        id: 'personal-inspection-*-history-task',
        icon: "simple-icon-equalizer",
        label: "menu.history-task",
        to: "/admin/personal-inspection/history-task",
        newWindow: false
      },
      {
        id: 'personal-inspection-*-invalid-task',
        icon: "simple-icon-equalizer",
        label: "menu.invalid-task",
        to: "/admin/personal-inspection/invalid-task",
        newWindow: false
      },
    ]
  },
  {
    id: "statistics",
    icon: "icofont-chart-pie",
    label: "menu.statistics",
    to: "/admin/statistics",
    newWindow: false,
    subs: [
      {
        id: 'statistics-*-view',
        icon: "simple-icon-equalizer",
        label: "menu.statistics-view",
        to: "/admin/statistics/view",
        newWindow: false
      },
      {
        id: 'statistics-*-scan-devices',
        icon: "simple-icon-equalizer",
        label: "menu.statistics-scan-devices",
        to: "/admin/statistics/scan-devices",
        newWindow: false
      },
      {
        id: 'statistics-*-monitors',
        icon: "simple-icon-equalizer",
        label: "menu.statistics-monitors",
        to: "/admin/statistics/monitors",
        newWindow: false
      },
      {
        id: 'statistics-*-hand-checks',
        icon: "simple-icon-equalizer",
        label: "menu.statistics-hand-checks",
        to: "/admin/statistics/hand-checks",
        newWindow: false
      },
      {
        id: 'statistics-*-evaluate-monitors',
        icon: "simple-icon-equalizer",
        label: "menu.statistics-evaluate-monitors",
        to: "/admin/statistics/evaluate-monitors",
        newWindow: false
      },
      {
        id: 'statistics-*-working-hours',
        icon: "simple-icon-equalizer",
        label: "menu.statistics-working-hours",
        to: "/admin/statistics/working-hours",
        newWindow: false
      },
      {
        id: 'statistics-*-operating-hours',
        icon: "simple-icon-equalizer",
        label: "menu.statistics-operating-hours",
        to: "/admin/statistics/operating-hours",
        newWindow: false
      },
    ]
  },
  {
    id: "black-list",
    icon: "icofont-thief",
    label: "menu.black-list",
    to: "/admin/black-list",
    newWindow: false,
    subs: [
      {
        id: 'black-list-*-personal-black-list',
        icon: "simple-icon-equalizer",
        label: "menu.personal-black-list",
        to: "/admin/black-list/personal-black-list",
        newWindow: false
      },
    ]
  },
  {
    id: "knowledge-base",
    icon: "icofont-notebook",
    label: "menu.knowledge-base",
    to: "/admin/knowledge-base",
    newWindow: false,
    subs: [
      {
        id: 'knowledge-base-*-pending-case',
        icon: "simple-icon-equalizer",
        label: "menu.pending-case",
        to: "/admin/knowledge-base/pending-case",
        newWindow: false
      },
      {
        id: 'knowledge-base-*-personal-case',
        icon: "simple-icon-equalizer",
        label: "menu.personal-case",
        to: "/admin/knowledge-base/personal-case",
        newWindow: false
      },
    ]
  },
  {
    id: "maintenance-management",
    icon: "icofont-tools",
    label: "menu.maintenance-management",
    to: "/admin/maintenance-management",
    newWindow: false,
    subs: [
      {
        id: 'maintenance-management-*-time-task',
        icon: "simple-icon-equalizer",
        label: "menu.time-task",
        to: "/admin/maintenance-management/time-task",
        newWindow: false
      },
      {
        id: 'maintenance-management-*-routine-plan',
        icon: "simple-icon-equalizer",
        label: "menu.routine-plan",
        to: "/admin/maintenance-management/routine-plan",
        newWindow: false
      },
      {
        id: 'maintenance-management-*-maintenance-task',
        icon: "simple-icon-equalizer",
        label: "menu.maintenance-task",
        to: "/admin/maintenance-management/maintenance-task",
        newWindow: false
      },
      {
        id: 'maintenance-management-*-process-task',
        icon: "simple-icon-equalizer",
        label: "menu.process-task",
        to: "/admin/maintenance-management/process-task",
        newWindow: false
      },
      {
        id: 'maintenance-management-*-history-record',
        icon: "simple-icon-equalizer",
        label: "menu.history-record",
        to: "/admin/maintenance-management/history-record",
        newWindow: false
      },
    ]
  },
];

export default data;
