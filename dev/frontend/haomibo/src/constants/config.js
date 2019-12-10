export const defaultMenuType = 'menu-default'; // 'menu-default', 'menu-sub-hidden', 'menu-hidden';
export const defaultStartPath = '/app/piaf/start';
export const searchPath = '#';

export const subHiddenBreakpoint = 4500;
export const menuHiddenBreakpoint = 768;

export const defaultLocale = 'zh';
export const defaultDirection = 'ltr';
export const localeOptions = [
  {id: 'en', name: 'English LTR', direction: 'ltr', icon: '/assets/img/flags/united-kingdom.svg'},
  {id: 'zh', name: '中文', direction: 'ltr', icon: '/assets/img/flags/china.svg'},
  {id: 'es', name: 'Español', direction: 'ltr', icon: '/assets/img/flags/spain.svg'},
  {id: 'enrtl', name: 'English RTL', direction: 'rtl', icon: '/assets/img/flags/united-kingdom.svg'}
];

// export const apiBaseUrl = 'http://192.168.5.7:8080/ecuritycheckitem-0.0.1-SNAPSHOT';
export const apiBaseUrl = 'http://localhost:4546';
//export const apiBaseUrl = 'http://47.95.246.188:4546';

export const refreshTokenTimeDiff = 30;

export const currentUser = {};

export const defaultColor = 'light.blue';
export const colors = [
  'light.blue',
];
