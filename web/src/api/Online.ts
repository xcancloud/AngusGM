import { http } from '@xcan/utils';

let baseUrl: string;
export default class Online {
  constructor (prefix: string) {
    baseUrl = prefix + '/message/center';
  }

  searchOnlineUsers<T> (params: T): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/online/search`, params);
  }

  offlineUser<T> (params: T): Promise<[Error | null, any]> {
    return http.del(`${baseUrl}/online`, params, { dataType: true });
  }
}
