export type ListGroup = {
  id: string;
  name: string;
  code: string;
  userNum: string;
  enabled: boolean;
  remark: string;
  tenantId: string;
  tenantName: string;
  createdBy: string;
  createdByName: string;
  createdDate: string;
  lastModifiedBy: string;
  lastModifiedByName: string;
  lastModifiedDate: string;
}

export type Detail = ListGroup & { tags: null | { id: string, name: string }[] }

export type FormState = {
  code: string;
  name: string;
  remark: string;
  tagIds: string[]
};
