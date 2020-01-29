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

        app.$router.push('/auth/login').catch(error => {
        });
        break;
      case responseMessages['token-expired']:
        removeLoginInfo();

        app.$notify('error', app.$t(`auth-token-messages.error-title`), app.$t(`auth-token-messages.token-expired`), {
          duration: 3000,
          permanent: false
        });

        app.$router.push('/auth/login').catch(error => {
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
    app.$notify('error', app.$t(`api-call-error-messages.error-title`), app.$t(`api-call-error-messages.network-error`), {
      duration: 3000,
      permanent: false
    });
    return Promise.reject(error);
  });

  return apiManager;
};

const getApiManagerError = function () {

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

        app.$router.push('/auth/login').catch(error => {
        });
        break;
      case responseMessages['token-expired']:
        removeLoginInfo();

        app.$router.push('/auth/login').catch(error => {
        });
        break;

      case responseMessages['forbidden']:

        break;

      case responseMessages['invalid-parameter']:

        break;

      default:

    }

    return response;
  }, (error) => {
    // Any status codes that falls outside the range of 2xx cause this function to trigger
    // Do something with response error
    return Promise.reject(error);
  });

  return apiManager;
};

const getDateTimeWithFormat = (datetime, formatType = 'zh',lang = 'zh') => {
  if (datetime === "" || datetime == null)
    return "";
  let array;
  array=datetime.split(".");
  datetime = array[0];
  datetime = datetime + "." + "000+1400";


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
    case 'monitor-diff':
      let type = '天';
      if(lang !== 'zh')
        type = 'D';
      return moment.utc(moment().diff(moment(String(datetime)))).format(`D[${type}] HH:mm:ss`);
  }
 // datetime = '2019-12-31T18:10:49.000+0000';
  return moment(String(datetime)).format(format)
 //return moment.parseZone(String(datetime)).format(format) //parse without timezone
 // return moment.utc(String(datetime)).format(format) //parse with timezone
};

const downLoadFileFromServer = (link,params, name = 'statics', ext) => {
  let ext2 =  ['docx','pdf','xlsx'];
  let ext1 = [];

  if(ext !== null){
    for(let i=0; i<ext.length; i++){
      ext1[i] = ext[i].value;
    }
  }else {
    ext1=ext2;
  }

  console.log(ext1);
  for(let i=0; i<ext1.length; i++) {
  getApiManager()
      .post(`${apiBaseUrl}/` + link + '/' + ext1[i], params, {
      responseType: 'blob'
    })
    .then((response) => {
      let status = response.status;
      if(status === 200) {
        let fileURL = window.URL.createObjectURL(new Blob([response.data]));
        let fileLink = document.createElement('a');
        fileLink.href = fileURL;
          fileLink.setAttribute('download', name + '.' + ext1[i]);
        document.body.appendChild(fileLink);
        fileLink.click();
        fileLink.parentNode.removeChild(fileLink);
      } else if(status === 201) {
        app.$notify('error', app.$t(`auth-token-messages.error-title`), app.$t(`api-call-error-messages.forbidden`), {
          duration: 3000,
          permanent: false
        });
      }

    })
    .catch(error => {
      throw new Error(error);
    });
  }

};

const printFileFromServer = (link,params) => {
  getApiManager()
    .post(`${apiBaseUrl}/` + link + '/' + 'pdf',params,{
      responseType: 'blob'
    })
    .then((response) => {
      let status = response.status;
      if(status === 200) {
        let els = document.querySelectorAll('body>iframe');
        els.forEach(item => {
          item.parentNode.removeChild(item);
        });
        let fileURL = window.URL.createObjectURL(new Blob([response.data], {type: "application/pdf"}));
        let objFra = document.createElement('iframe');
        objFra.style.visibility = "hidden";
        objFra.style.display = 'none';
        objFra.src = fileURL;
        document.body.appendChild(objFra);
        objFra.contentWindow.focus();
        objFra.contentWindow.print();
      } else if(status === 201) {
        app.$notify('error', app.$t(`auth-token-messages.error-title`), app.$t(`api-call-error-messages.forbidden`), {
          duration: 3000,
          permanent: false
        });
      }
    })
    .catch(error => {
      throw new Error(error);
    });
};

function isPhoneValid(value) {
  if(value === "")
    return true;
  let phoneNumber = /^(\+\d{1,2}\s)?\(?\d{3}\)?[\s.-]?\d{4}[\s.-]?\d{4}$/;
  return !!value.match(phoneNumber);
}

function isGuidValid(value) {
  if(value === "")
    return false;
  let regGuid = /^(\{{0,1}([0-9A-Z]){8}-([0-9A-Z]){4}-([0-9A-Z]){4}-([0-9A-Z]){4}-([0-9A-Z]){12}\}{0,1})$/;
  return regGuid.test(value);
}

function isDataCodeValid(value) {
  if(value === "")
    return false;
  let Reg = /^[0-9]+$/;
  return Reg.test(value);
}



function isAccountValid(value) {
  let accountReg = /^[A-Za-z0-9._-]+$/;
  let arrReg = [/^[A-Z]+$/, /^[a-z]+$/, /^[0-9]+$/, /^[._-]+$/];
  let regId=0;
  if(value === null) {
    return false;
  }else {
    let arrPassword = value.split('');
    for (let i = 0; i < arrPassword.length; i++) {
      if (accountReg.test(arrPassword[i])) {
        for(let j=0; j<arrReg.length; j++){
          if(arrReg[j].test(arrPassword[i])){
            if(i>0&&regId!==j){
              return true;
            }
            regId = j;
            break;
          }
        }
      } else {
        return false;
      }
    }
    return false;
  }

}

// function downloadPath() {
//   var browser;
//   browser.downloads.showDefaultFolder();
// }


export {getApiManager, getApiManagerError, getDateTimeWithFormat, downLoadFileFromServer, printFileFromServer,isPhoneValid, isAccountValid, isDataCodeValid, isGuidValid};
