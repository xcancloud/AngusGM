import { OrgTargetType } from '@/enums/enums';

/**
 * Organization tag entity
 */
export type OrgTag = {
  id: string;
  name: string;
  tenantId: string;
  tenantName: string;
  createdBy: string;
  creator: string;
  createdDate: string;
  modifiedBy: string;
  modifier: string;
  modifiedDate: string;
  isEdit: boolean;
}

/**
 * Tag target association entity
 */
export type Target = {
  id: string;
  targetType: OrgTargetType,
  targetId: string;
  targetName: string;
  tagId: string;
  tagName: string;
  createdDate: string;
  createdBy: string;
  creator: string;
  targetCreatedDate: string;
  targetCreatedBy: string;
  targetCreator: string;
}

/**
 * API request parameters for tag operations
 */
export type TagAddParams = {
  name: string;
}

export type TagUpdateParams = {
  id: string;
  name: string;
}

export type TagDeleteParams = {
  ids: string[];
}

export interface TagInfoProps {
  tag?: OrgTag;
  canModify?: boolean;
}

export interface TagAddProps {
  visible: boolean;
  loading?: boolean;
}

export interface TargetSearchParams {
  pageNo: number;
  pageSize: number;
  filters: any[];
  tagId: string;
  targetType: OrgTargetType;
}

/**
 * User and group data interfaces
 */
export interface UserData {
  id: string;
  fullName: string;
}

export interface GroupData {
  id: string;
  name: string;
}
