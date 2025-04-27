import { http } from '@xcan-angus/tools';

let baseUrl: string;
export default class Edition {
  constructor (prefix: string) {
    baseUrl = prefix + '/edition';
  }

  getEditionInstalled (goodsCode: string): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/installed`, { goodsCode });
  }

  getEditionUpgradeable (params: { goodsCode: string, goodsId?: string }): Promise<[Error | null, any]> {
    return http.get(`${baseUrl}/upgradeable`, params);
  }
}
