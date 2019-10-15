import axios from 'axios';
import {getAuthToken} from "../utils";
import {responseMessages} from "../constants/response-messages";
import router from '../router';

const getApiManager = function () {

  const apiManager = axios.create({
    headers: {'X-AUTH-TOKEN': getAuthToken().token}
  });
  apiManager.interceptors.response.use((response) => {
    // Any status code that lie within the range of 2xx cause this function to trigger
    // Do something with response data

    let message = response.data.message;

    console.log(message);

    switch (message) {

      case responseMessages['invalid-token']:
        localStorage.removeItem('authToken');
        router.push('/');
        break;
      case responseMessages['token-expired']:
        localStorage.removeItem('authToken');
        router.push('/');
        break;
    }

    return response;
  }, (error) => {
    // Any status codes that falls outside the range of 2xx cause this function to trigger
    // Do something with response error
    return Promise.reject(error);
  });

  return apiManager;
};

export {getApiManager};
