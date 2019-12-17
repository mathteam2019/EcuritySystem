import {isLoggedInAsAdmin} from "./index";

export default (to, from, next) => {


  if (isLoggedInAsAdmin()) {

    next();

  } else {

    localStorage.removeItem('loginInfo');

    next('/auth/login');

  }
};
