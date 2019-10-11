const data = [
  {
    id: 'dashboard',
    icon: "simple-icon-magic-wand",
    label: "menu.dashboard",
    to: "/app/dashboard",
    newWindow: false
  },
  {
    id: "permission-management",
    icon: "simple-icon-hourglass",
    label: "menu.permission-management",
    to: "/app/permission-management",
    newWindow: false,
    subs: [
      {
        id: 'permission-management-*-organization-management',
        icon: "simple-icon-graduation",
        label: "menu.organization-management",
        to: "/app/permission-management/organization-management",
        newWindow: false
      },
      {
        id: 'permission-management-*-role-management',
        icon: "simple-icon-ghost",
        label: "menu.role-management",
        to: "/app/permission-management/role-management",
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
        id: 'permission-management-*-user-management',
        icon: "simple-icon-fire",
        label: "menu.user-management",
        to: "/app/permission-management/user-management",
        newWindow: false
      },
    ]
  },
  {
    id: "device-management",
    icon: "simple-icon-eyeglass",
    label: "menu.device-management",
    to: "/app/device-management",
    newWindow: false,
    subs: [
      {
        id: 'device-management-*-device-classify',
        icon: "simple-icon-envelope-open",
        label: "menu.device-classify",
        to: "/app/device-management/device-classify",
        newWindow: false
      },
      {
        id: 'device-management-*-document-template',
        icon: "simple-icon-badge",
        label: "menu.document-template",
        to: "/app/device-management/document-template",
        newWindow: false
      },
      {
        id: 'device-management-*-document-management',
        icon: "simple-icon-anchor",
        label: "menu.document-management",
        to: "/app/device-management/document-management",
        newWindow: false
      },
      {
        id: 'device-management-*-device-table',
        icon: "simple-icon-wallet",
        label: "menu.device-table",
        to: "/app/device-management/device-table",
        newWindow: false
      },
      {
        id: 'device-management-*-device-config',
        icon: "simple-icon-vector",
        label: "menu.device-config",
        to: "/app/device-management/device-config",
        newWindow: false
      },
      {
        id: 'device-management-*-condition-monitoring',
        icon: "simple-icon-speech",
        label: "menu.condition-monitoring",
        to: "/app/device-management/condition-monitoring",
        newWindow: false
      },
    ]
  },
  {
    id: "system-setting",
    icon: "simple-icon-puzzle",
    label: "menu.system-setting",
    to: "/app/system-setting",
    newWindow: false,
    subs: [
      {
        id: 'system-setting-*-parameter-setting',
        icon: "simple-icon-printer",
        label: "menu.parameter-setting",
        to: "/app/system-setting/parameter-setting",
        newWindow: false
      },
      {
        id: 'system-setting-*-site-management',
        icon: "simple-icon-present",
        label: "menu.site-management",
        to: "/app/system-setting/site-management",
        newWindow: false
      },
    ]
  },
];

export default data;
