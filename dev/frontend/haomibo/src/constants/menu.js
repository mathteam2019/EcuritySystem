const data = [
  {
    id: 'dashboard',
    icon: "simple-icon-home",
    label: "menu.dashboard",
    to: "/app/dashboard",
    newWindow: false
  },
  {
    id: "permission-management",
    icon: "simple-icon-shield",
    label: "menu.permission-management",
    to: "/app/permission-management",
    newWindow: false,
    subs: [
      {
        id: 'permission-management-*-organization-management',
        icon: "icofont-caret-right",
        label: "menu.organization-management",
        to: "/app/permission-management/organization-management",
        newWindow: false
      },
      {
        id: 'permission-management-*-user-management',
        icon: "icofont-caret-right",
        label: "menu.user-management",
        to: "/app/permission-management/user-management",
        newWindow: false
      },
      {
        id: 'permission-management-*-permission-control',
        icon: "icofont-caret-right",
        label: "menu.permission-control",
        to: "/app/permission-management/permission-control",
        newWindow: false
      },
      {
        id: 'permission-management-*-assign-permission-management',
        icon: "icofont-caret-right",
        label: "menu.assign-permission-management",
        to: "/app/permission-management/assign-permission-management",
        newWindow: false
      },


    ]
  },
  {
    id: "site-management",
    icon: "simple-icon-location-pin",
    label: "menu.site-management",
    to: "/app/site-management",
    newWindow: false,
  },
  {
    id: "device-management",
    icon: "simple-icon-puzzle",
    label: "menu.device-management",
    to: "/app/device-management",
    newWindow: false,
    subs: [
      {
        id: 'device-management-*-device-classify',
        icon: "simple-icon-chemistry",
        label: "menu.device-classify",
        to: "/app/device-management/device-classify",
        newWindow: false
      },
      {
        id: 'device-management-*-document-template',
        icon: "simple-icon-docs",
        label: "menu.document-template",
        to: "/app/device-management/document-template",
        newWindow: false
      },
      {
        id: 'device-management-*-document-management',
        icon: "simple-icon-folder",
        label: "menu.document-management",
        to: "/app/device-management/document-management",
        newWindow: false
      },
      {
        id: 'device-management-*-device-table',
        icon: "simple-icon-grid",
        label: "menu.device-table",
        to: "/app/device-management/device-table",
        newWindow: false
      },
      {
        id: 'device-management-*-device-config',
        icon: "simple-icon-settings",
        label: "menu.device-config",
        to: "/app/device-management/device-config",
        newWindow: false
      },
      {
        id: 'device-management-*-condition-monitoring',
        icon: "simple-icon-screen-desktop",
        label: "menu.condition-monitoring",
        to: "/app/device-management/condition-monitoring",
        newWindow: false
      },
    ]
  },
  {
    id: "system-setting",
    icon: "simple-icon-settings",
    label: "menu.system-setting",
    to: "/app/system-setting",
    newWindow: false,
  },
  {
    id: "log-management",
    icon: "simple-icon-note",
    label: "menu.log-management",
    to: "/app/log-management",
    newWindow: false,
    subs: [
      {
        id: 'log-management-*-operating-log',
        icon: "simple-icon-equalizer",
        label: "menu.operating-log",
        to: "/app/log-management/operating-log",
        newWindow: false
      },
      {
        id: 'log-management-*-device-log',
        icon: "simple-icon-settings",
        label: "menu.device-log",
        to: "/app/log-management/device-log",
        newWindow: false
      },
    ]
  },
  {
    id: "personal-inspection",
    icon: "simple-icon-user-following",
    label: "menu.personal-inspection",
    to: "/app/personal-inspection",
    newWindow: false,
    subs: [
      {
        id: 'personal-inspection-*-process-task',
        icon: "simple-icon-equalizer",
        label: "menu.process-task",
        to: "/app/personal-inspection/process-task",
        newWindow: false
      },
      {
        id: 'personal-inspection-*-history-task',
        icon: "simple-icon-equalizer",
        label: "menu.history-task",
        to: "/app/personal-inspection/history-task",
        newWindow: false
      },
      {
        id: 'personal-inspection-*-invalid-task',
        icon: "simple-icon-equalizer",
        label: "menu.invalid-task",
        to: "/app/personal-inspection/invalid-task",
        newWindow: false
      },
    ]
  },
  {
    id: "black-list",
    icon: "simple-icon-user-unfollow",
    label: "menu.black-list",
    to: "/app/black-list",
    newWindow: false,
    subs: [
      {
        id: 'black-list-*-personal-black-list',
        icon: "simple-icon-equalizer",
        label: "menu.personal-black-list",
        to: "/app/black-list/personal-black-list",
        newWindow: false
      },
    ]
  },
  {
    id: "knowledge-base",
    icon: "simple-icon-layers",
    label: "menu.knowledge-base",
    to: "/app/knowledge-base",
    newWindow: false,
    subs: [
      {
        id: 'knowledge-base-*-pending-case',
        icon: "simple-icon-equalizer",
        label: "menu.pending-case",
        to: "/app/knowledge-base/pending-case",
        newWindow: false
      },
      {
        id: 'knowledge-base-*-personal-case',
        icon: "simple-icon-equalizer",
        label: "menu.personal-case",
        to: "/app/knowledge-base/personal-case",
        newWindow: false
      },
    ]
  },
  {
    id: "maintenance-management",
    icon: "simple-icon-wrench",
    label: "menu.maintenance-management",
    to: "/app/maintenance-management",
    newWindow: false,
    subs: [
      {
        id: 'maintenance-management-*-time-task',
        icon: "simple-icon-equalizer",
        label: "menu.time-task",
        to: "/app/maintenance-management/time-task",
        newWindow: false
      },
      {
        id: 'maintenance-management-*-routine-plan',
        icon: "simple-icon-equalizer",
        label: "menu.routine-plan",
        to: "/app/maintenance-management/routine-plan",
        newWindow: false
      },
      {
        id: 'maintenance-management-*-maintenance-task',
        icon: "simple-icon-equalizer",
        label: "menu.maintenance-task",
        to: "/app/maintenance-management/maintenance-task",
        newWindow: false
      },
      {
        id: 'maintenance-management-*-process-task',
        icon: "simple-icon-equalizer",
        label: "menu.process-task",
        to: "/app/maintenance-management/process-task",
        newWindow: false
      },
      {
        id: 'maintenance-management-*-history-record',
        icon: "simple-icon-equalizer",
        label: "menu.history-record",
        to: "/app/maintenance-management/history-record",
        newWindow: false
      },
    ]
  },
];

export default data;
