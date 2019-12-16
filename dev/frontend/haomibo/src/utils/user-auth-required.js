import {isLoggedInAsUser} from "./index";

export default (to, from, next) => {


  if (isLoggedInAsUser()) {

    next();

  } else {

    localStorage.removeItem('loginInfo');

    next('/auth/login');

  }
};
