import {http} from '@xcan/utils';

let baseUrl: string;
export default class OrgTag {
  constructor(prefix: string) {
    baseUrl = prefix + '/org';
  }

  addTag(params) {
    return http.post(`${baseUrl}/tag`, params);
  }

  updateTag(params) {
    return http.patch(`${baseUrl}/tag`, params);
  }

  deleteTag(params) {
    return http.del(`${baseUrl}/tag`, params);
  }

  getDetail(id: string) {
    return http.get(`${baseUrl}/tag/${id}`);
  }

  searchTags(params) {
    return http.get(`${baseUrl}/tag/search`, params);
  }

  addTagTarget(tagId, dtos) {
    return http.post(`${baseUrl}/tag/${tagId}/target`, dtos);
  }

  deleteTagTarget(tagId, targetIds) {
    return http.del(`${baseUrl}/tag/${tagId}/target`, {targetIds: targetIds});
  }

  getTagTargets(params) {
    return http.get(`${baseUrl}/tag/target`, params);
  }
}
