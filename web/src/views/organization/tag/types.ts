import { OrgTargetType } from '@/enums/enums';

export type OrgTag = {
  id: string;
  name: string;
  tenantId: string;
  tenantName: string;
  createdBy: string;
  createdByName: string;
  createdDate: string;
  lastModifiedBy: string;
  lastModifiedByName: string;
  lastModifiedDate: string;
  isEdit: boolean;
}

export type Target = {
  id: string;
  targetType: OrgTargetType,
  targetId: string;
  targetName: string;
  tagId: string;
  tagName: string;
  createdDate: string;
  createdBy: string;
  createdByName: string;
  targetCreatedDate: string;
  targetCreatedBy: string;
  targetCreatedByName: string;
}
