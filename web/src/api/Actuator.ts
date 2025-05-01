import { http, PUB_GM } from '@xcan-angus/tools';

let metricsBaseUrl: string;
export default class Actuator {
  constructor () {
    metricsBaseUrl = '/eureka/actuator/metrics';
  }

  getDiscoveryStatus (): Promise<[Error | null, any]> {
    return http.get(`${PUB_GM}/status`);
  }

  getDiscoveryLastn (): Promise<[Error | null, any]> {
    return http.get(`${PUB_GM}/lastn`);
  }

  getCpuMax (): Promise<[Error | null, any]> {
    return http.get(`${metricsBaseUrl}/system.cpu.count`);
  }

  getCpuUsage (): Promise<[Error | null, any]> {
    return http.get(`${metricsBaseUrl}/system.cpu.usage`);
  }

  getJvmMax (): Promise<[Error | null, any]> {
    return http.get(`${metricsBaseUrl}/jvm.memory.max`);
  }

  getJvmUsage (): Promise<[Error | null, any]> {
    return http.get(`${metricsBaseUrl}/jvm.memory.used`);
  }

  getMaxDisk (): Promise<[Error | null, any]> {
    return http.get(`${metricsBaseUrl}/diskspace.total`);
  }

  getDisUsage (): Promise<[Error | null, any]> {
    return http.get(`${metricsBaseUrl}/diskspace.usage`);
  }
}
