export const defaultMenuType = 'menu-default'; // 'menu-default', 'menu-sub-hidden', 'menu-hidden';
export const defaultStartPath = '/app/piaf/start';
export const searchPath = '#';

export const subHiddenBreakpoint = 1440;
export const menuHiddenBreakpoint = 768;

export const defaultLocale = 'zh';
export const defaultDirection = 'ltr';
export const localeOptions = [
  {id: 'en', name: 'English LTR', direction: 'ltr'},
  {id: 'zh', name: '中文', direction: 'ltr'},
  {id: 'es', name: 'Español', direction: 'ltr'},
  {id: 'enrtl', name: 'English RTL', direction: 'rtl'}
];

export const apiBaseUrl = 'http://192.168.5.4:8080/haomibo-0.0.1-SNAPSHOT';
// export const apiBaseUrl = 'http://192.168.5.4:4546';
// export const apiBaseUrl = 'http://39.97.175.200:8080/haomibo';

export const refreshTokenTimeDiff = 30;

export const currentUser = {
  id: 0,
  name: '',
  img: '/assets/img/profile-pic-l.jpg',
};

export const defaultColor = 'light.blue';
export const colors = [
  'light.blue',
];
