import axios from 'axios';
import {getAuthTokenInfo, removeLoginInfo} from "../utils";
import {responseMessages} from "../constants/response-messages";
import app from '../main';
import moment from '../../node_modules/moment';
import {apiBaseUrl} from '../constants/config';
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

const getDateTimeWithFormat = (datetime, formatType = 'zh') => {
  if (datetime === "" || datetime == null)
    return "";

  //todo need to format datetime with its language value
  let format = 'MM/DD/YYYY HH:mm';
  switch (formatType) {
    case 'zh':
    case 'en':
      format = 'MM/DD/YYYY HH:mm';
      break;
    case 'default': //to set value on client side
      format = 'YYYY-MM-DD';
      break;
    case 'monitor':
      format = 'YYYYMMDD HH:mm';
      break;
  }
  return moment(String(datetime)).format(format)
};

const downLoadFileFromServer = (link,params, name = 'statics') => {
  getApiManager()
    .post(`${apiBaseUrl}/` + link,params,{
      responseType: 'blob'
    })
    .then((response) => {
      let message = response.data.message;
      switch (message) {
        case responseMessages['ok']:
          let fileURL = window.URL.createObjectURL(new Blob([response.data]));
          let fileLink = document.createElement('a');
          fileLink.href = fileURL;
          fileLink.setAttribute('download', name + '.xlsx');
          document.body.appendChild(fileLink);
          fileLink.click();
          fileLink.parentNode.removeChild(fileLink);
          break;
        case responseMessages['forbidden']:
          app.$notify('error', app.$t(`auth-token-messages.error-title`), app.$t(`api-call-error-messages.forbidden`), {
            duration: 3000,
            permanent: false
          });

          break;
      }
    })
    .catch(error => {
      throw new Error(error);
    });

};

const printFileFromServer = (link,params) => {
  getApiManager()
    .post(`${apiBaseUrl}/` + link,params,{
      responseType: 'blob'
    })
    .then((response) => {
      let message = response.data.message;
      switch (message) {
        case responseMessages['ok']:
          let els = document.querySelectorAll('body>iframe');
          els.forEach(item => {
            item.parentNode.removeChild(item);
          });
          let fileURL = window.URL.createObjectURL(new Blob([response.data], {type: "application/pdf"}));
          var objFra = document.createElement('iframe');
          objFra.style.visibility = "hidden";
          objFra.style.display = 'none';
          objFra.src = fileURL;
          document.body.appendChild(objFra);
          objFra.contentWindow.focus();
          objFra.contentWindow.print();
          break;
        case responseMessages['forbidden']:
          app.$notify('error', app.$t(`auth-token-messages.error-title`), app.$t(`api-call-error-messages.forbidden`), {
            duration: 3000,
            permanent: false
          });

          break;
      }

    })
    .catch(error => {
      throw new Error(error);
    });
};


const getDateTimeWithFormat1 = (datetime) => {
  //todo need to format datetime with its language value
  let format = 'YYYYMMDD HH:mm:ss';
  return moment(String(datetime)).format(format)
};

const getDateTimeWithFormat2 = (datetime) => {
  //todo need to format datetime with its language value
  let format = 'YYYY-MM-DD HH:mm:ss';
  return moment(String(datetime)).format(format)
};
export {getApiManager, getDateTimeWithFormat, getDateTimeWithFormat1, getDateTimeWithFormat2, downLoadFileFromServer,printFileFromServer};
