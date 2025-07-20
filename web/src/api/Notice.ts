import { http } from '@xcan-angus/tools';

let baseUrl: string;
export default class Notice {
  constructor (prefix: string) {
    baseUrl = prefix + '/notice';
  }

  addNotice<T> (params: T): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}`, params);
  }

  deleteNotice (ids: string[]): Promise<[Error | null, any]> {
    return http.del(`${baseUrl}`, { ids });
  }

  getNoticeDetail (id: string): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/${id}`);
  }

  getNoticeList<T> (params: T): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}`, params);
  }
}
