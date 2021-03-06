import {apiBaseUrl, defaultDirection, apiImageUrl, refreshTokenTimeDiff} from '../constants/config'
import {getApiManager} from "../api";
import {responseMessages} from "../constants/response-messages";
import app from '../main';
import Chobi from "../data/Chobi";
import axios from 'axios';

var imgObj = null;
var imgObj2 = null;


export const addCommas = nStr => {
  nStr += '';
  var x = nStr.split('.');
  var x1 = x[0];
  var x2 = x.length > 1 ? '.' + x[1] : '';
  var rgx = /(\d+)(\d{3})/;
  while (rgx.test(x1)) {
    x1 = x1.replace(rgx, '$1' + ',' + '$2')
  }
  return x1 + x2
};

export const mapOrder = (array, order, key) => {
  array.sort(function (a, b) {
    var A = a[key];
    var B = b[key];
    if (order.indexOf(A + '') > order.indexOf(B + '')) {
      return 1
    } else {
      return -1
    }
  });
  return array
};

export const getDateWithFormat = () => {
  const today = new Date();
  let dd = today.getDate();
  let mm = today.getMonth() + 1; // January is 0!

  var yyyy = today.getFullYear();
  if (dd < 10) {
    dd = '0' + dd
  }
  if (mm < 10) {
    mm = '0' + mm
  }
  return dd + '.' + mm + '.' + yyyy
};

let getDateTimeWithFormat = (datetime, lang = 'cn') => {
  //todo need to format datetime with its language value
  return moment(String(datetime)).format('MM/DD/YYYY HH:mm')
};

export const getCurrentTime = () => {
  const now = new Date();
  return now.getHours() + ':' + now.getMinutes()
};

export const ThemeColors = () => {
  let rootStyle = getComputedStyle(document.body);
  return {
    themeColor1: rootStyle.getPropertyValue('--theme-color-1').trim(),
    themeColor2: rootStyle.getPropertyValue('--theme-color-2').trim(),
    themeColor3: rootStyle.getPropertyValue('--theme-color-3').trim(),
    themeColor4: rootStyle.getPropertyValue('--theme-color-4').trim(),
    themeColor5: rootStyle.getPropertyValue('--theme-color-5').trim(),
    themeColor6: rootStyle.getPropertyValue('--theme-color-6').trim(),
    themeColor1_10: rootStyle.getPropertyValue('--theme-color-1-10').trim(),
    themeColor2_10: rootStyle.getPropertyValue('--theme-color-2-10').trim(),
    themeColor3_10: rootStyle.getPropertyValue('--theme-color-3-10').trim(),
    themeColor4_10: rootStyle.getPropertyValue('--theme-color-3-10').trim(),
    themeColor5_10: rootStyle.getPropertyValue('--theme-color-3-10').trim(),
    themeColor6_10: rootStyle.getPropertyValue('--theme-color-3-10').trim(),
    primaryColor: rootStyle.getPropertyValue('--primary-color').trim(),
    foregroundColor: rootStyle.getPropertyValue('--foreground-color').trim(),
    separatorColor: rootStyle.getPropertyValue('--separator-color').trim()
  }
};

export const chartTooltip = {
  backgroundColor: ThemeColors().foregroundColor,
  titleFontColor: ThemeColors().primaryColor,
  borderColor: ThemeColors().separatorColor,
  borderWidth: 0.5,
  bodyFontColor: ThemeColors().primaryColor,
  bodySpacing: 10,
  xPadding: 15,
  yPadding: 15,
  cornerRadius: 0.15
};

export const centerTextPlugin = {
  afterDatasetsUpdate: function (chart) {
  },
  beforeDraw: function (chart) {
    var width = chart.chartArea.right;
    var height = chart.chartArea.bottom;
    var ctx = chart.chart.ctx;
    ctx.restore();

    var activeLabel = chart.data.labels[0];
    var activeValue = chart.data.datasets[0].data[0];
    var dataset = chart.data.datasets[0];
    var meta = dataset._meta[Object.keys(dataset._meta)[0]];
    var total = meta.total;

    var activePercentage = parseFloat(((activeValue / total) * 100).toFixed(1));
    activePercentage = chart.legend.legendItems[0].hidden
      ? 0
      : activePercentage;

    if (chart.pointAvailable) {
      activeLabel = chart.data.labels[chart.pointIndex];
      activeValue =
        chart.data.datasets[chart.pointDataIndex].data[chart.pointIndex];

      dataset = chart.data.datasets[chart.pointDataIndex];
      meta = dataset._meta[Object.keys(dataset._meta)[0]];
      total = meta.total;
      activePercentage = parseFloat(((activeValue / total) * 100).toFixed(1));
      activePercentage = chart.legend.legendItems[chart.pointIndex].hidden
        ? 0
        : activePercentage
    }

    ctx.font = '36px Nunito, sans-serif';
    ctx.fillStyle = ThemeColors().primaryColor;
    ctx.textBaseline = 'middle';

    var text = activePercentage + '%';
    var textX = Math.round((width - ctx.measureText(text).width) / 2);
    var textY = height / 2;
    ctx.fillText(text, textX, textY);

    ctx.font = '14px Nunito, sans-serif';
    ctx.textBaseline = 'middle';

    var text2 = activeLabel;
    var textX2 = Math.round((width - ctx.measureText(text2).width) / 2);
    var textY2 = height / 2 - 30;
    ctx.fillText(text2, textX2, textY2);

    ctx.save()
  },
  beforeEvent: function (chart, event, options) {
    var firstPoint = chart.getElementAtEvent(event)[0];

    if (firstPoint) {
      chart.pointIndex = firstPoint._index;
      chart.pointDataIndex = firstPoint._datasetIndex;
      chart.pointAvailable = true
    }
  }
};
// export const getDirection = () => {
//   let direction = defaultDirection;
//   if (localStorage.getItem('direction')) {
//     const localValue = localStorage.getItem('direction');
//     if (localValue === 'rtl' || localValue === 'ltr') {
//       direction = localValue
//     }
//   }
//   return {
//     direction,
//     isRtl: direction === 'rtl'
//   }
// };
export const getDirection = () => {
  let direction = defaultDirection;
  if (sessionStorage.getItem('direction')) {
    const localValue = sessionStorage.getItem('direction');
    if (localValue === 'rtl' || localValue === 'ltr') {
      direction = localValue
    }
  }
  return {
    direction,
    isRtl: direction === 'rtl'
  }
};

// export const setDirection = localValue => {
//   let direction = 'ltr';
//   if (localValue === 'rtl' || localValue === 'ltr') {
//     direction = localValue
//   }
//   localStorage.setItem('direction', direction)
// };

export const setDirection = localValue => {
  let direction = 'ltr';
  if (localValue === 'rtl' || localValue === 'ltr') {
    direction = localValue
  }
  sessionStorage.setItem('direction', direction)
};

// export const getLocale = () => {
//   return localStorage.getItem('currentLanguage');
// };
// export const setLocale = localValue => {
//
//   localStorage.setItem('currentLanguage', localValue);
//
// };

export const getLocale = () => {
  return sessionStorage.getItem('currentLanguage');
};
export const setLocale = localValue => {

  sessionStorage.setItem('currentLanguage', localValue);

};


// export const saveLoginInfo = (loginInfo, account) => {
//   localStorage.set('loginInfo', JSON.stringify(loginInfo));
//   localStorage.set('loginAccount', account);
// };

export const saveLoginInfo = (loginInfo, account) => {
  sessionStorage.setItem('loginInfo', JSON.stringify(loginInfo));
  sessionStorage.setItem('loginAccount', account);
};

// export const saveLanguageInfo = () => {
//   let account =  localStorage.getItem('loginAccount');
//
//   let currentLanguage = localStorage.getItem('currentLanguage');
//
//   let data, tmp=false;
//   if(localStorage.getItem('languageInfo')===null) {
//     data = [];
//   }else {
//     data = JSON.parse(localStorage.getItem('languageInfo'));
//   }
//   for(let i=0; i<data.length; i++){
//     if(data[i].account===account){
//       data[i].currentLanguage = currentLanguage;
//       tmp = true;
//     }
//   }
//   if(tmp===false) {
//     data.push({
//       account: account,
//       currentLanguage: currentLanguage
//     });
//   }
//   localStorage.setItem('languageInfo', JSON.stringify(data));
//
// };

export const saveLanguageInfo = () => {
  let account =  sessionStorage.getItem('loginAccount');

  let currentLanguage = sessionStorage.getItem('currentLanguage');

  let data, tmp=false;
  if(sessionStorage.getItem('languageInfo')===null) {
    data = [];
  }else {
    data = JSON.parse(sessionStorage.getItem('languageInfo'));
  }
  for(let i=0; i<data.length; i++){
    if(data[i].account===account){
      data[i].currentLanguage = currentLanguage;
      tmp = true;
    }
  }
  if(tmp===false) {
    data.push({
      account: account,
      currentLanguage: currentLanguage
    });
  }
  sessionStorage.setItem('languageInfo', JSON.stringify(data));

};

// export const getLanguageInfo= (account) => {
//   if(localStorage.getItem('languageInfo')===null){
//     return null;
//   }else{
//     let data = JSON.parse(localStorage.getItem('languageInfo'));
//     for(let i=0; i<data.length; i++) {
//       if (data[i].account === account)
//         return data[i].currentLanguage;
//     }
//     return null;
//   }
//
// };
export const getLanguageInfo = (account) => {
  if(sessionStorage.getItem('languageInfo')===null){
    return null;
  }else{
    let data = JSON.parse(sessionStorage.getItem('languageInfo'));
    for(let i=0; i<data.length; i++) {
      if (data[i].account === account)
        return data[i].currentLanguage;
    }
    return null;
  }

};
// export const setInvalidCount = (account) => {
//   if(localStorage.getItem(account)==null){
//     localStorage.setItem(account, '2');
//   }
//   else{
//     let countValue = localStorage.getItem(account);
//     localStorage.removeItem(account);
//     countValue = (parseInt(countValue) +1).toString();
//     localStorage.setItem(account, countValue);
//   }
// };

export const setInvalidCount = (account) => {
  if(sessionStorage.getItem(account)==null){
    sessionStorage.setItem(account, '2');
  }
  else{
    let countValue = sessionStorage.getItem(account);
    sessionStorage.removeItem(account);
    countValue = (parseInt(countValue) +1).toString();
    sessionStorage.setItem(account, countValue);
  }
};

// export const getInvalidCount = (account) => {
//   if(localStorage.getItem(account)==null){
//     return "1";
//   }
//   else{
//     let countValue = localStorage.getItem(account);
//     return countValue;
//   }
// };

export const getInvalidCount = (account) => {
  if(sessionStorage.getItem(account)==null){
    return "1";
  }
  else{
    let countValue = sessionStorage.getItem(account);
    return countValue;
  }
};

// export const removeCount = (account) =>{
//   localStorage.removeItem(account);
// };
export const removeCount = (account) =>{
  sessionStorage.removeItem(account);
};

// export const savePermissionInfo = (info) => {
//   let data = [];
//   info.forEach(item => {
//     if (item.resourceName != null)
//       data.push(item.resourceName);
//   });
//   localStorage.setItem('permInfo', JSON.stringify(data));
// };
export const savePermissionInfo = (info) => {
  let data = [];
  info.forEach(item => {
    if (item.resourceName != null)
      data.push(item.resourceName);
  });
  sessionStorage.setItem('permInfo', JSON.stringify(data));
};

// export const checkPermissionItem = (item) => {
//   let data = localStorage.getItem('permInfo');
//   if (data === null)
//     return true;
//   data = JSON.parse(data);
//   return data.indexOf(item) === -1;
// };
export const checkPermissionItem = (item) => {
  let data = sessionStorage.getItem('permInfo');
  if (data === null)
    return true;
  data = JSON.parse(data);
  return data.indexOf(item) === -1;
};

// export const savePermissionInfoId = (info) => {
//   let data = [];
//   info.forEach(item => {
//     if (item.resourceId != null)
//       data.push(item.resourceId);
//   });
//   localStorage.setItem('permInfoId', JSON.stringify(data));
// };
export const savePermissionInfoId = (info) => {
  let data = [];
  info.forEach(item => {
    if (item.resourceId != null)
      data.push(item.resourceId);
  });
  sessionStorage.setItem('permInfoId', JSON.stringify(data));
};

// export const getPermissionInfoId = () => {
//   let data = localStorage.getItem('permInfoId');
//   return data;
// };
export const getPermissionInfoId = () => {
  let data = sessionStorage.getItem('permInfoId');
  return data;
};

// export const checkPermissionItemById = (item) => {
//   let data = localStorage.getItem('permInfoId');
//   if (data === null)
//     return true;
//   data = JSON.parse(data);
//   return data.indexOf(item) === -1;
// };
export const checkPermissionItemById = (item) => {
  let data = sessionStorage.getItem('permInfoId');
  if (data === null)
    return true;
  data = JSON.parse(data);
  return data.indexOf(item) === -1;
};

// export const removeLoginInfo = () => {
//   localStorage.removeItem('loginInfo');
//   localStorage.removeItem('permInfo'); //remove permission Info too
//   localStorage.removeItem('permInfoId');
//   localStorage.removeItem('loginAccount');
//  // localStorage.removeItem('languageInfo');
// };
export const removeLoginInfo = () => {
  sessionStorage.removeItem('loginInfo');
  sessionStorage.removeItem('permInfo'); //remove permission Info too
  sessionStorage.removeItem('permInfoId');
  sessionStorage.removeItem('loginAccount');
  // sessionStorage.remove('languageInfo');
};

// export const saveDicDataGroupByDicId = (data) => {
//   let dicData = data.dictionaryDataList;
//   let deviceDicData = data.deviceDictionaryDataList;
//   let ids = [...new Set(dicData.map(item => item.dictionaryId))];
//   let filterData = Object.assign(...ids.map(id => ({[id]: dicData.filter(item => item.dictionaryId === id)})));
//   localStorage.setItem('dicDataGroupByDicId', JSON.stringify(filterData));
//   saveDeviceDicDataGroupByDicId(deviceDicData);
// };

export const saveDicDataGroupByDicId = (data) => {
  let dicData = data.dictionaryDataList;
  let deviceDicData = data.deviceDictionaryDataList;
  let ids = [...new Set(dicData.map(item => item.dictionaryId))];
  let filterData = Object.assign(...ids.map(id => ({[id]: dicData.filter(item => item.dictionaryId === id)})));
  sessionStorage.setItem('dicDataGroupByDicId', JSON.stringify(filterData));
  saveDeviceDicDataGroupByDicId(deviceDicData);
};

// export const saveDeviceDicDataGroupByDicId = (dicData) => {
//   let ids = [...new Set(dicData.map(item => item.dictionaryId))];
//   let filterData = Object.assign(...ids.map(id => ({[id]: dicData.filter(item => item.dictionaryId === id)})));
//   localStorage.setItem('deviceDicDataGroupByDicId', JSON.stringify(filterData));
// };
export const saveDeviceDicDataGroupByDicId = (dicData) => {
  let ids = [...new Set(dicData.map(item => item.dictionaryId))];
  let filterData = Object.assign(...ids.map(id => ({[id]: dicData.filter(item => item.dictionaryId === id)})));
  sessionStorage.setItem('deviceDicDataGroupByDicId', JSON.stringify(filterData));
};

// export const getDicDataByDicIdForOptions = (dicId) => {
//   let data = localStorage.getItem('dicDataGroupByDicId');
//   if (data == null)
//     return [];
//   data = JSON.parse(data);
//   let options = [];
//   if (Object.keys(data).indexOf(dicId + "") !== -1) {
//     data[dicId].forEach(item => {
//       options.push({
//         value: item.dataCode, text: (item.dataValue).trim()
//       })
//     });
//     return options;
//   }
//   return [];
// };

export const getDicDataByDicIdForOptions = (dicId) => {
  let data = sessionStorage.getItem('dicDataGroupByDicId');
  if (data == null)
    return [];
  data = JSON.parse(data);
  let options = [];
  if (Object.keys(data).indexOf(dicId + "") !== -1) {
    data[dicId].forEach(item => {
      options.push({
        value: item.dataCode, text: (item.dataValue).trim()
      })
    });
    return options;
  }
  return [];
};

// export const getDeviceDicDataByDicIdForOptions = (dicId) => {
//   let data = localStorage.getItem('deviceDicDataGroupByDicId');
//   if (data == null)
//     return [];
//   data = JSON.parse(data);
//   let options = [];
//   if (Object.keys(data).indexOf(dicId + "") !== -1) {
//     data[dicId].forEach(item => {
//       options.push({
//         value: item.dataCode, text: item.dataValue
//       })
//     });
//     return options;
//   }
//   return [];
// };

export const getDeviceDicDataByDicIdForOptions = (dicId) => {
  let data = sessionStorage.getItem('deviceDicDataGroupByDicId');
  if (data == null)
    return [];
  data = JSON.parse(data);
  let options = [];
  if (Object.keys(data).indexOf(dicId + "") !== -1) {
    data[dicId].forEach(item => {
      options.push({
        value: item.dataCode, text: item.dataValue
      })
    });
    return options;
  }
  return [];
};

// export const getLoginInfo = () => {
//   let loginInfo = localStorage.getItem('loginInfo');
//   try {
//     loginInfo = JSON.parse(loginInfo);
//     if (loginInfo) {
//       return loginInfo
//     }
//   } catch (e) {
//   }
//
//   return {};
// };
export const getLoginInfo = () => {
  let loginInfo = sessionStorage.getItem('loginInfo');
  try {
    loginInfo = JSON.parse(loginInfo);
    if (loginInfo) {
      return loginInfo
    }
  } catch (e) {
  }

  return {};
};

// export const getAuthTokenInfo = () => {
//   let loginInfo = localStorage.getItem('loginInfo');
//   try {
//     loginInfo = JSON.parse(loginInfo);
//     if (loginInfo && loginInfo.token && loginInfo.token.token) {//&& loginInfo.token.expirationTimestamp
//       return loginInfo.token;
//     }
//   } catch (e) {
//   }
//
//   return {
//     token: '',
//     //expirationTimestamp: 0
//   };
// };

export const getAuthTokenInfo = () => {
  let loginInfo = sessionStorage.getItem('loginInfo');
  try {
    loginInfo = JSON.parse(loginInfo);
    if (loginInfo && loginInfo.token && loginInfo.token.token) {//&& loginInfo.token.expirationTimestamp
      return loginInfo.token;
    }
  } catch (e) {
  }

  return {
    token: '',
    //expirationTimestamp: 0
  };
};

export const isLoggedIn = () => {
  let loginInfo = getLoginInfo();

  let now = Math.floor(Date.now() / 1000);

  if (loginInfo.user
    && loginInfo.token) {
    return true;
  }

  // && loginInfo.token.expirationTimestamp
  //   && loginInfo.token.expirationTimestamp > now

  return false;
};

export const doRefreshToken = () => {

  if (!isLoggedIn()) {
    return;
  }

  getApiManager().post(`${apiBaseUrl}/auth/refresh-token`).then((response) => {
    let message = response.data.message;
    let data = response.data.data;
    switch (message) {
      case responseMessages['ok']:

        // let loginInfo = getLoginInfo();
        // loginInfo.token = data;
        //
        // saveLoginInfo(loginInfo);
        //
        // scheduleRefreshToken();

        break;
    }

  });
};

export const scheduleRefreshToken = () => {

  if (!isLoggedIn()) {
    return;
  }

  let authTokenInfo = getAuthTokenInfo();

  let now = Math.floor(Date.now() / 1000);

  // if (authTokenInfo.expirationTimestamp < now) {
  //   // token expired, need to re-login
  //   removeLoginInfo();
  //
  //   app.$router.push('/').catch(error => {
  //   });
  //
  //   return;
  // }

  // if (authTokenInfo.expirationTimestamp - refreshTokenTimeDiff > now) {
  //
  //   setTimeout(() => doRefreshToken(), (authTokenInfo.expirationTimestamp - refreshTokenTimeDiff - now) * 1000);
  //
  //   return;
  // }

  doRefreshToken();

};

export function toDataUrl(url, callback) {
  axios({
    url: url,
    method: 'GET'
  }).then((response) => {
    callback(response.data);
  }).catch(error => {
    callback(null);
  });
}

export const loadImageCanvas = (url1, url2, rectInfoL, rectInfoR, isToggled) => {
  if(url1==null){
    url1 = '';
  }
  if(url2==null){
    url2 = '';
  }

  url1 = apiImageUrl + url1;
  url2 = apiImageUrl + url2;
  toDataUrl(url1, function (url) {
    if(url != null){

      url1 = 'data:image/png;base64,' + url;
    }
    imgObj = new Chobi(url1, isToggled, true);
    imgObj.ready(function () {
      this.canvas = document.getElementById("firstcanvas");
      this.loadImageToCanvas(null, rectInfoL, isToggled, true);
    });
  });

  toDataUrl(url2, function (url) {
    if(url != null){
      //let urlspl = url.split(':');
      url2 = 'data:image/png;base64,' + url;
    }
    imgObj2 = new Chobi(url2, isToggled);
    imgObj2.ready(function () {
      this.canvas = document.getElementById("secondcanvas");
      this.loadImageToCanvas(null, rectInfoR, isToggled, false);
    });
  });


  // imgObj2 = new Chobi(url2, isToggled);
  // imgObj2.ready(function () {
  //   this.canvas = document.getElementById("secondcanvas");
  //   this.loadImageToCanvas(null, rectInfoR, isToggled, false);
  // });
};

export const imageFilterById = (id, rectInfoL, rectInfoR, value=0) => {
  if (imgObj == null || imgObj2 == null) {
    alert("Choose an image first!");
    return;
  }
  if (id == 0) {
    imgObj.blackAndWhite();
    imgObj2.blackAndWhite();
  } else if (id == 1) {
    imgObj.sepia();
    imgObj2.sepia();
  } else if (id == 2) {
    imgObj.negative();
    imgObj2.negative();
  } else if (id == 3) {
    imgObj.vintage();
    imgObj2.vintage();
  } else if (id == 4) {
    imgObj.crossProcess();
    imgObj2.crossProcess();
  } else if (id == 5) {
    imgObj.brightness(1.5*value);
    imgObj2.brightness(1.5*value);
  } else if (id == 6) {
    imgObj.brightness(-1.5*value);
    imgObj2.brightness(-1.5*value);
  } else if (id == 7) {
    imgObj.contrast(10*value);
    imgObj2.contrast(10*value);
  } else if (id == 8) {
    imgObj.contrast(-4*value);
    imgObj2.contrast(-4*value);
  } else if (id == 9) {
    imgObj.noise();
    imgObj2.noise();
  } else if (id == 10) {
    imgObj.blackAndWhite2();
    imgObj2.blackAndWhite2();
  } else if (id == 11) {
    imgObj.crayon();
    imgObj2.crayon();
  } else if (id == 12) {
    imgObj.cartoon();
    imgObj2.cartoon();
  } else if (id == 13) {
    imgObj.vignette();
    imgObj2.vignette();
  }
  imgObj.loadImageToCanvas(null, rectInfoL);
  imgObj2.loadImageToCanvas(null, rectInfoR);
};
