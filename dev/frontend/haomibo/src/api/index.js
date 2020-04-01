import axios from 'axios';
import {getAuthTokenInfo, removeLoginInfo, saveLanguageInfo} from "../utils";
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
        saveLanguageInfo();
        removeLoginInfo();

        app.$notify('error', app.$t(`auth-token-messages.error-title`), app.$t(`auth-token-messages.invalid-token`), {
          duration: 3000,
          permanent: false
        });

        app.$router.push('/auth/login').catch(error => {
        });
        break;
      case responseMessages['token-expired']:
        saveLanguageInfo();
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
        saveLanguageInfo();
        removeLoginInfo();

        app.$router.push('/auth/login').catch(error => {
        });
        break;
      case responseMessages['token-expired']:
        saveLanguageInfo();
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
  // let array;
  // array=datetime.split(".");
  // datetime = array[0];
  // datetime = datetime + "." + "000+1400";


  //todo need to format datetime with its language value
  let format = 'MM/DD/YYYY HH:mm';
  switch (formatType) {
    case 'zh':
    case 'en':
      format = 'MM/DD/YYYY HH:mm:ss';
      break;
    case 'default': //to set value on client side
      format = 'YYYY-MM-DD';
      break;
    case 'monitor':
      format = 'YYYYMMDD HH:mm';
      break;
    case 'monitor-diff':
      let type = '天';
      let monthType = '月';
      if(lang !== 'zh') {
        type = 'D';
        monthType = 'M';
      }
      let today = new Date();
      let diff = today - moment(datetime);
      let msec = diff;
      let hh = Math.floor(msec / 1000 / 60 / 60);
      let dd = Math.floor(hh / 24);
      let h = hh%24;
      msec -= hh * 1000 * 60 * 60;
      let mm = Math.floor(msec / 1000 / 60);
      msec -= mm * 1000 * 60;
      let ss = Math.floor(msec / 1000);
      msec -= ss * 1000;
      let diffString = dd + type + h + ':' + mm + ':' + ss;

      // return moment.utc(moment().diff(moment(String(datetime)))).format(`MM/DD/YYYY HH:mm:ss`);
      return diffString;
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
  if(params.isAll===true&&params.idList===""){
    app.$notify('warning', app.$t('permission-management.warning'), app.$t(`response-messages.select-data`), {
      duration: 3000,
      permanent: false
    });
  }
  else {
    getApiManager()
      .post(`${apiBaseUrl}/` + link + '/' + 'pdf', params, {
        responseType: 'blob'
      })
      .then((response) => {
        let status = response.status;
        if (status === 200) {
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
        } else if (status === 201) {
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

const downLoadImageFromUrl = (url) => {
  let urlSplit = url.toString().split("/");
  let splitLength = urlSplit.length;
  let img = urlSplit[splitLength-1];
  let imgExt = img.toString().split('.');
  if(imgExt.length === 2) {
    //url = apiImageUrl + url;

    //let fileURL = window.URL.createObjectURL(new Blob([response.data]));
    let fileLink = document.createElement('a');
    fileLink.href = url;
    //fileLink.download=img;
    fileLink.setAttribute('download', img);
    document.body.appendChild(fileLink);
    fileLink.click();
    //document.body.removeChild(fileLink);
    fileLink.parentNode.removeChild(fileLink);
  }

  // var a = $("<a>")
  //   .attr("href", url)
  //   .attr("download", img)
  //   .appendTo("body");
  //
  // a[0].click();
  //
  // a.remove();

  // var x=new XMLHttpRequest();
  // x.open( "GET", "/diff6.png" , true);
  // x.responseType="blob";
  // x.onload= function(e){download(e.target.response, "awesomesauce.png", "image/png");};
  // x.send();
};

function isPhoneValid(value) {
  if(value === "" || value ===null)
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

function isGroupNumberValid(value) {
  if(value === "")
    return false;
  value = value.toString();
  let pg = value.substr(0, 2); // Gets the first part
  if(pg!=="PG") return false;
  let number = value.substr(2);  // Gets the text part
  let Reg = /^([0-9A-Z]){8}$/;
  return Reg.test(number);
}

function isRoleNumberValid(value) {
  if(value === "")
    return false;
  value = value.toString();
  let r = value.substr(0, 1); // Gets the first part
  if(r!=="R") return false;
  let number = value.substr(1);  // Gets the text part
  let Reg = /^([0-9A-Z]){8}$/;
  return Reg.test(number);
}

function isDataGroupNumberValid(value) {
  if(value === "")
    return false;
  value = value.toString();
  let r = value.substr(0, 2); // Gets the first part
  if(r!=="DG") return false;
  let number = value.substr(2);  // Gets the text part
  let Reg = /^([0-9A-Z]){8}$/;
  return Reg.test(number);
}

function isSpaceContain(value) {
  if(value === "")
    return true;
  return value.indexOf(' ') < 0;
}

function isDataCodeValid(value) {
  if(value === "")
    return false;
  let Reg = /^[0-9]+$/;
  return Reg.test(value);
}

function isColorValid(value) {
  if(value === "")
    return false;
  let Reg = /^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$/;
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


export {getApiManager, getApiManagerError, getDateTimeWithFormat, downLoadFileFromServer, printFileFromServer, downLoadImageFromUrl, isPhoneValid, isAccountValid, isDataCodeValid, isGuidValid, isColorValid, isGroupNumberValid, isRoleNumberValid, isSpaceContain, isDataGroupNumberValid};
