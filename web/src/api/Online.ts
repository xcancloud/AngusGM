import { http } from '@xcan-angus/tools';

let baseUrl: string;
export default class Online {
  constructor (prefix: string) {
    baseUrl = prefix + '/message/center';
  }

  getOnlineUserList<T> (params: T): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/online`, params);
  }

  offlineUser<T> (params: T): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/online/off`, params);
  }
}
