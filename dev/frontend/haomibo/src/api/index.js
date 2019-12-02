import axios from 'axios';
import {getAuthTokenInfo, removeLoginInfo} from "../utils";
import {responseMessages} from "../constants/response-messages";
import app from '../main';
import moment from '../../node_modules/moment';

const getApiManager = function () {

  const apiManager = axios.create({
    headers: {'X-AUTH-TOKEN': getAuthTokenInfo().token}
  });
  apiManager.interceptors.response.use((response) => {
    // Any status code that lie within the range of 2xx cause this function to trigger
    // Do something with response data

    let message = response.data.message;


    switch (message) {

      case responseMessages['invalid-token']:
        removeLoginInfo();

        app.$notify('error', app.$t(`auth-token-messages.error-title`), app.$t(`auth-token-messages.invalid-token`), {
          duration: 3000,
          permanent: false
        });

        app.$router.push('/admin/auth/login').catch(error => {
        });
        break;
      case responseMessages['token-expired']:
        removeLoginInfo();

        app.$notify('error', app.$t(`auth-token-messages.error-title`), app.$t(`auth-token-messages.token-expired`), {
          duration: 3000,
          permanent: false
        });

        app.$router.push('/admin/auth/login').catch(error => {
        });
        break;

      case responseMessages['forbidden']:
        app.$notify('error', app.$t(`auth-token-messages.error-title`), app.$t(`api-call-error-messages.forbidden`), {
          duration: 3000,
          permanent: false
        });

        break;

      case responseMessages['invalid-parameter']:
        app.$notify('error', app.$t(`auth-token-messages.error-title`), app.$t(`api-call-error-messages.invalid-parameter`), {
          duration: 3000,
          permanent: false
        });

        break;

      default:

    }

    return response;
  }, (error) => {
    // Any status codes that falls outside the range of 2xx cause this function to trigger
    // Do something with response error
    console.error(error);
    app.$notify('error', app.$t(`api-call-error-messages.error-title`), app.$t(`api-call-error-messages.network-error`), {
      duration: 3000,
      permanent: false
    });
    return Promise.reject(error);
  });

  return apiManager;
};

const getDateTimeWithFormat = (datetime,lang = 'cn') => {
  if(datetime === "" || datetime == null)
    return "";
  //todo need to format datetime with its language value
  let format = 'MM/DD/YYYY HH:mm';
  switch (lang) {
    case 'cn':
    case 'en':
      format = 'MM/DD/YYYY HH:mm';
      break;
    case 'default': //to set value on client side
      format = 'YYYY-MM-DD';
      break;
  }
  return moment(String(datetime)).format(format)
};

export {getApiManager, getDateTimeWithFormat};
