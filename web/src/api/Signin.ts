import { http } from '@xcan-angus/infra';

let baseUrl: string;
export default class Auth {
  constructor (prefix: string) {
    baseUrl = prefix + '/auth';
  }

  signin<T> (params: T, option = {}) : Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/user/signin`, params, option);
  }

  checkUserMobileCode (params) : Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/user/signsms/check`, params);
  }

  checkUserEmaillCode (params) : Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/user/signemail/check`, params);
  }

  getUserAcount (params) : Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/user/signin/account`, params);
  }

  resetPassword (params) : Promise<[Error | null, any]> {
    return http.patch(`${baseUrl}/user/password/forget`, params);
  }

  signup (params) : Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/user/signup`, params);
  }
}
