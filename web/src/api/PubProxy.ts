import { http } from '@xcan-angus/tools';

let baseUrl: string;
export default class PubProxy {
  constructor (prefix: string) {
    baseUrl = prefix + '/proxy';
  }

  getApp (path: string) : Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/actuator/appworkspace?targetAddr=http://${path}`);
  }
}
