import { http } from '@xcan/utils';

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

  // getSystemLogfiles (ip: string | undefined) {
  //   return axios.get(`//${ip}/actuator/systemlog`);
  // }

  // getLogContent (data:any) {
  //   return axios.get(`//${data.ip}/actuator/systemlog/name`, { params: data.value });
  // }

  // clearLogContent (data:any) {
  //   return axios.post(`//${data.ip}/actuator/systemlog/name?name=${data.value.name}`);
  // }
}
