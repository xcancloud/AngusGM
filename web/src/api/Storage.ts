import { http } from '@xcan-angus/tools';

let baseUrl: string;
export default class Storage {
  constructor (prefix: string) {
    baseUrl = prefix + '/storage';
  }

  putSetting (params, axiosConf = {}): Promise<[Error | null, any]> {
    return http.put(`${baseUrl}/settings`, params, axiosConf);
  }

  getSetting (): Promise<any> {
    return http.get(`${baseUrl}/settings`);
  }
}
