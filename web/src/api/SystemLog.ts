import { http } from '@xcan-angus/tools';

let baseUrl: string;
export default class SystemLog {
  constructor (prefix: string) {
    baseUrl = prefix + '/systemlog';
  }

  getInstanceLogFiles (id: string): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/instance/${id}`);
  }

  getInstanceLogFile (id: string, name: string, params: {
    linesNum: string,
    tail: boolean
  }): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/instance/${id}/file/${name}`, params);
  }

}
