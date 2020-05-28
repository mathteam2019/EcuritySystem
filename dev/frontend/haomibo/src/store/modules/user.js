import 'firebase/auth'
import {apiBaseUrl, currentUser} from '../../constants/config'
import {responseMessages} from '../../constants/response-messages';
import {getApiManager} from "../../api";
import {getLoginInfo, isLoggedIn, removeLoginInfo, saveLoginInfo, scheduleRefreshToken} from "../../utils";
import app from "../../main";

let loadCurrentUser = () => {
  if (isLoggedIn()) {
    let loginInfo = getLoginInfo();

    return {
      ...loginInfo.user,
      img: '/assets/img/user_placeholder.png',
    };
  }
  return null;
};

export default {
  state: {
    currentUser: loadCurrentUser(),
  },
  getters: {
    currentUser: state => state.currentUser,
  },
  mutations: {
    setCurrentUser(state, payload) {
      state.currentUser = payload;
    },
    setLogout(state) {
      state.currentUser = null;
    },
  },
  actions: {
    setCurrentUser({commit}, payload) {
      commit('setCurrentUser', payload);
    },

  }
}
