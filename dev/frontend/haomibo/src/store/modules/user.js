import 'firebase/auth'
import {apiUrl, currentUser} from '../../constants/config'
import {responseMessages} from '../../constants/response-messages';
import {getApiManager} from "../../api";
import {getLoginInfo, isLoggedIn, removeLoginInfo, saveLoginInfo, scheduleRefreshToken} from "../../utils";
import app from "../../main";

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
    processing: false,
    registerStatus: null
  },
  getters: {
    currentUser: state => state.currentUser,
    processing: state => state.processing,
    loginError: state => state.loginError,
    registerStatus: state => state.registerStatus,
  },
  mutations: {
    setUser(state, payload) {
      state.currentUser = payload;
      state.processing = false;
      state.loginError = null
    },
    setLogout(state) {
      state.currentUser = null;
    },
    setProcessing(state, payload) {
      state.processing = payload;
      state.loginError = null
    },
    setError(state, payload) {
      state.loginError = payload;
      state.currentUser = null;
      state.processing = false
    },
    setRegisterStatus(state, payload) {
      state.registerStatus = payload;
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
        })
        .catch((error) => {
          commit('clearError');
          commit('setProcessing', false);
        });

    },
    register({commit}, payload) {


      commit('setProcessing', true);
      commit('setRegisterStatus', null);

      getApiManager()
        .post(`${apiUrl}/auth/register`, payload)
        .then(response => {
          let message = response.data.message;
          switch (message) {
            case responseMessages['ok']:

              app.$notify('success', app.$t('user.success'), app.$t(`user.register-success`), {
                duration: 3000,
                permanent: false
              });

              commit('setProcessing', false);

              setTimeout(() => {
                app.$router.push('/user/login')
              }, 100);


              break;
            case responseMessages['invalid-parameter']:

              commit('setRegisterStatus', 'invalid-parameter');

              commit('setProcessing', false);

              break;
            case responseMessages['used-email']:

              commit('setRegisterStatus', 'used-email');

              commit('setProcessing', false);

              break;

            case responseMessages['server-error']:

              commit('setRegisterStatus', 'server-error');

              commit('setProcessing', false);

              break;

          }
        })
        .catch((error) => {
          commit('setRegisterStatus', 'null');
          commit('setProcessing', false);
        });
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
        })
        .catch((error) => {

        });

    }
  }
}
