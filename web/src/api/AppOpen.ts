import { http } from '@xcan-angus/tools';

let baseUrl: string;
export default class App {
  constructor (prefix: string) {
    baseUrl = prefix + '/appopen';
  }

  getList (params = {}) : Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/list`, params);
  }
}
