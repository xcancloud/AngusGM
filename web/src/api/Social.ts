import { http } from '@xcan-angus/tools';

let baseUrl: string;
export default class Social {
  constructor (prefix: string) {
    baseUrl = prefix + '/social';
  }

  bindOtherAccount (params: {
    type: 'WECHAT' | 'GITHUB' | 'GOOGLE',
    clientId: string,
    clientSecret: string
  }): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/social/bind/setting`, params);
  }
}
