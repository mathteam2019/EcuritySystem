import {isLoggedIn} from "./index";

export default (to, from, next) => {


  console.log(to, from, next);

  if (isLoggedIn()) {

    next();

  } else {

    localStorage.removeItem('loginInfo');



    next('/admin/auth/login');

  }
};
