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
        icon: "simple-icon-organization",
        label: "menu.organization-management",
        to: "/app/permission-management/organization-management",
        newWindow: false
      },
      {
        id: 'permission-management-*-user-management',
        icon: "simple-icon-user",
        label: "menu.user-management",
        to: "/app/permission-management/user-management",
        newWindow: false
      },
      {
        id: 'permission-management-*-permission-control',
        icon: "simple-icon-game-controller",
        label: "menu.permission-control",
        to: "/app/permission-management/permission-control",
        newWindow: false
      },
      {
        id: 'permission-management-*-assign-permission-management',
        icon: "simple-icon-magic-wand",
        label: "menu.assign-permission-management",
        to: "/app/permission-management/assign-permission-management",
        newWindow: false
      },


    ]
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
    subs: [
      {
        id: 'system-setting-*-parameter-setting',
        icon: "simple-icon-equalizer",
        label: "menu.parameter-setting",
        to: "/app/system-setting/parameter-setting",
        newWindow: false
      },
      {
        id: 'system-setting-*-site-management',
        icon: "simple-icon-settings",
        label: "menu.site-management",
        to: "/app/system-setting/site-management",
        newWindow: false
      },
    ]
  },
];

export default data;
