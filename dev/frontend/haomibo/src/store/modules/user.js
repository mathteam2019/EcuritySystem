import 'firebase/auth'
import {apiUrl, currentUser} from '../../constants/config'
import {responseMessages} from '../../constants/response-messages';
import {getApiManager} from "../../api";
import {getLoginInfo, isLoggedIn, removeLoginInfo, saveLoginInfo, scheduleRefreshToken} from "../../utils";

let loadCurrentUser = () => {
  if (isLoggedIn()) {
    let loginInfo = getLoginInfo();

    return {
      ...loginInfo.user,
      img: '/assets/img/profile-pic-l.jpg',
    };
  }
  return null;
};

export default {
  state: {
    currentUser: loadCurrentUser(),
    loginError: null,
    processing: false
  },
  getters: {
    currentUser: state => state.currentUser,
    processing: state => state.processing,
    loginError: state => state.loginError
  },
  mutations: {
    setUser(state, payload) {
      state.currentUser = payload
      state.processing = false
      state.loginError = null
    },
    setLogout(state) {
      state.currentUser = null
      state.processing = false
      state.loginError = null
    },
    setProcessing(state, payload) {
      state.processing = payload
      state.loginError = null
    },
    setError(state, payload) {
      state.loginError = payload
      state.currentUser = null
      state.processing = false
    },
    clearError(state) {
      state.loginError = null
    }
  },
  actions: {

    login({commit}, payload) {
      commit('clearError');
      commit('setProcessing', true);


      getApiManager()
        .post(`${apiUrl}/auth/login`, payload)
        .then(response => {
          let message = response.data.message;
          let data = response.data.data;
          switch (message) {
            case responseMessages['ok']:

              saveLoginInfo(data);
              scheduleRefreshToken();

              commit('setUser', {...currentUser, id: data.user.id, name: data.user.name});
              break;
            case responseMessages['invalid-parameter']:
              commit('setError', 'invalid-parameter');
              break;
            case responseMessages['user-not-found']:
              commit('setError', 'user-not-found');
              break;
            case responseMessages['invalid-password']:
              commit('setError', 'invalid-password');
              break;

          }
        });


      // firebase
      //   .auth()
      //   .signInWithEmailAndPassword(payload.email, payload.password)
      //   .then(
      //     user => {
      //       const item = {uid: user.user.uid, ...currentUser}
      //       localStorage.setItem('user', JSON.stringify(item))
      //       commit('setUser', {uid: user.user.uid, ...currentUser})
      //     },
      //     err => {
      //       localStorage.removeItem('user')
      //       commit('setError', err.message)
      //     }
      //   )

    },
    signOut({commit}) {

      return getApiManager()
        .post(`${apiUrl}/auth/logout`, {})
        .then(response => {
          let message = response.data.message;
          let data = response.data.data;
          switch (message) {
            case responseMessages['ok']:

              removeLoginInfo();

              commit('setLogout');
              break;

          }
        });

    }
  }
}
