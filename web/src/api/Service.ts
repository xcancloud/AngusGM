import { http } from '@xcan-angus/tools';

let baseUrl: string;
export default class Service {
  constructor (prefix: string) {
    baseUrl = prefix + '/service';
  }

  getDiscoveryServices (): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/discovery`);
  }

  getServiceInstances (code: string): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/${code}/discovery/instances`);
  }

  getApisByServiceOrResource (params) {
    return http.get(`${baseUrl}/resource/api`, params);
  }

  getServiceResources (params) {
    return http.get(`${baseUrl}/resource`, params);
  }
}
