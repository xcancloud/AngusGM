import {http} from '@xcan/utils';

let baseUrl: string;
export default class Tenant {
  constructor(prefix: string) {
    baseUrl = prefix + '/user/directory';
  }

  addDirectory(params: {
    groupSchema: any,
    membershipSchema: any,
    schema: any,
    server: any,
    userSchema: any,
    sequence: number
  }): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}`, params);
  }

  updateDirectory(params: {
    id: string,
    groupSchema: any,
    membershipSchema: any,
    schema: any,
    server: any,
    userSchema: any,
    sequence: number
  }): Promise<[Error | null, any]> {
    return http.put(`${baseUrl}`, params);
  }

  testDirectory(params: {
    groupSchema: any,
    membershipSchema: any,
    schema: any,
    server: any,
    userSchema: any
  }): Promise<[Error | null, any]> {
    return http.post(`${baseUrl}/test`, params);
  }

  updateDirectorySequence(params: { id: string, sequence: string | number }[]): Promise<[Error | null, any]> {
    return http.patch(`${baseUrl}/reorder`, params);
  }

  toggleDirectoryEnabled(params: { enabled: boolean, id: string }[]): Promise<[Error | null, any]> {
    return http.patch(`${baseUrl}/enabled`, params);
  }

  syncDirectory(id: string): Promise<[Error | null, any]> {
    return http.put(`${baseUrl}/${id}/sync`);
  }

  deleteDirectory(id: string, deleteSync: boolean): Promise<[Error | null, any]> {
    return http.del(`${baseUrl}/${id}`, {deleteSync: deleteSync});
  }

  getDirectories(): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}`);
  }

  getDirectoryDetail(id: string): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/${id}`);
  }
}
