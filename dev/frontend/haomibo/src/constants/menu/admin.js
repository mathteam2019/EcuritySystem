const data = [
  {
    id: 'dashboard',
    icon: "simple-icon-home",
    label: "menu.dashboard",
    to: "/admin/dashboard",
    newWindow: false,
    permissionId: null,
  },
  {
    id: "permission-management",
    icon: "icofont-shield",
    label: "menu.permission-management",
    to: "/admin/permission-management",
    newWindow: false,
    permissionId: 2,
    subs: [
      {
        id: 'permission-management-*-organization-management',
        icon: "icofont-caret-right",
        label: "menu.organization-management",
        to: "/admin/permission-management/organization-management",
        newWindow: false,
        permissionId: 3,
      },
      {
        id: 'permission-management-*-user-management',
        icon: "icofont-caret-right",
        label: "menu.user-management",
        to: "/admin/permission-management/user-management",
        newWindow: false,
        permissionId: 10,
      },
      {
        id: 'permission-management-*-permission-control',
        icon: "icofont-caret-right",
        label: "menu.permission-control",
        to: "/admin/permission-management/permission-control",
        newWindow: false,
        permissionId: 24,
      },
      {
        id: 'permission-management-*-assign-permission-management',
        icon: "icofont-caret-right",
        label: "menu.assign-permission-management",
        to: "/admin/permission-management/assign-permission-management",
        newWindow: false,
        permissionId: 37,
      },


    ]
  },
  {
    id: "site-management",
    icon: "icofont-location-pin",
    label: "menu.site-management",
    to: "/admin/site-management",
    newWindow: false,
    permissionId: 50,
  },
  {
    id: "system-setting",
    icon: "icofont-gears",
    label: "menu.system-setting",
    to: "/admin/system-setting",
    newWindow: false,
    permissionId: 57,
  },
  {
    id: "device-management",
    icon: "icofont-site-map",
    label: "menu.device-management",
    to: "/admin/device-management",
    newWindow: false,
    permissionId: 65,
    subs: [
      {
        id: 'device-management-*-device-classify',
        icon: "simple-icon-chemistry",
        label: "menu.device-classify",
        to: "/admin/device-management/device-classify",
        newWindow: false,
        permissionId: 66,
      },
      {
        id: 'device-management-*-document-template',
        icon: "simple-icon-docs",
        label: "menu.document-template",
        to: "/admin/device-management/document-template",
        newWindow: false,
        permissionId: 73,
      },
      {
        id: 'device-management-*-document-management',
        icon: "simple-icon-folder",
        label: "menu.document-management",
        to: "/admin/device-management/document-management",
        newWindow: false,
        permissionId: 84,
      },
      {
        id: 'device-management-*-device-table',
        icon: "simple-icon-grid",
        label: "menu.device-table",
        to: "/admin/device-management/device-table",
        newWindow: false,
        permissionId: 91,
      },
      {
        id: 'device-management-*-device-config',
        icon: "simple-icon-settings",
        label: "menu.device-config",
        to: "/admin/device-management/device-config",
        newWindow: false,
        permissionId: 98,
      },
      {
        id: 'device-management-*-condition-monitoring',
        icon: "simple-icon-screen-desktop",
        label: "menu.condition-monitoring",
        to: "/admin/device-management/condition-monitoring",
        newWindow: false,
        permissionId: 163,
      },
    ]
  },
  {
    id: "log-management",
    icon: "icofont-list",
    label: "menu.log-management",
    to: "/admin/log-management",
    newWindow: false,
    permissionId: 105,
    subs: [
      {
        id: 'log-management-*-operating-log',
        icon: "simple-icon-equalizer",
        label: "menu.operating-log",
        to: "/admin/log-management/operating-log",
        newWindow: false,
        permissionId: 106,
      },
      {
        id: 'log-management-*-device-log',
        icon: "simple-icon-settings",
        label: "menu.device-log",
        to: "/admin/log-management/device-log",
        newWindow: false,
        permissionId: 113,
      },
    ]
  },
];

export default data;
