import { debounce } from 'throttle-debounce';
import { duration, utils } from '@xcan-angus/infra';
import { OrgTargetType } from '@/enums/enums';

/**
 * Debounced search handler for tag name search
 */
export const createSearchHandler = (callback: (filters: any[]) => void) => {
  return debounce(duration.search, async (event: any) => {
    const value = event.target.value;
    if (value) {
      callback([{ key: 'name', value: value, op: 'MATCH_END' }]);
    } else {
      callback([]);
    }
  });
};

/**
 * Debounced search handler for target name search
 */
export const createTargetSearchHandler = (callback: (filters: any[], pageNo: number) => void) => {
  return debounce(duration.search, async (event: any) => {
    const value = event.target.value;
    const pageNo = 1;
    if (value) {
      callback([{ key: 'targetName', op: 'MATCH_END', value }], pageNo);
    } else {
      callback([], pageNo);
    }
  });
};

/**
 * Get target type display text based on enum value
 */
export const getTargetTypeText = (value: OrgTargetType, t: any): string => {
  switch (value) {
    case OrgTargetType.USER:
      return t('user.title');
    case OrgTargetType.DEPT:
      return t('department.title');
    case OrgTargetType.GROUP:
      return t('group.title');
    default:
      return '';
  }
};

/**
 * Get column titles based on target type
 */
export const getColumnTitles = (targetType: OrgTargetType, t: any) => {
  switch (targetType) {
    case 'USER':
      return {
        id: t('user.columns.assocUser.id'),
        name: t('user.columns.assocUser.name')
      };
    case 'DEPT':
      return {
        id: t('department.columns.userDept.code'),
        name: t('department.columns.userDept.name')
      };
    case 'GROUP':
      return {
        id: t('group.columns.assocGroup.code'),
        name: t('group.columns.assocGroup.name')
      };
    default:
      return {
        id: t('user.columns.assocUser.id'),
        name: t('user.columns.assocUser.name')
      };
  }
};

/**
 * Get placeholder text based on target type
 */
export const getPlaceholderText = (targetType: OrgTargetType, t: any): string => {
  switch (targetType) {
    case 'USER':
      return t('user.placeholder.search');
    case 'DEPT':
      return t('department.placeholder.name');
    case 'GROUP':
      return t('group.placeholder.name');
    default:
      return t('user.placeholder.search');
  }
};

/**
 * Get cancel button disabled state based on target type
 */
export const getCancelBtnDisabled = (app: any) => ({
  USER: !app.has('TagUserUnassociate'),
  DEPT: !app.has('TagDeptUnassociate'),
  GROUP: !app.has('TagGroupUnassociate')
});

/**
 * Get cancel button text based on target type
 */
export const getCancelText = (app: any) => ({
  USER: app.getName('TagUserUnassociate'),
  DEPT: app.getName('TagDeptUnassociate'),
  GROUP: app.getName('TagGroupUnassociate')
});

/**
 * Calculate current page after deletion
 */
export const calculateCurrentPage = (pageNo: number, pageSize: number, total: number): number => {
  return utils.getCurrentPage(pageNo, pageSize, total);
};

/**
 * Table change handler
 */
export const createTableChangeHandler = (callback: (pagination: any) => void) => {
  return async (_pagination: any) => {
    const { current, pageSize } = _pagination;
    callback({ current, pageSize });
  };
};

/**
 * Get table columns based on target type
 */
export const columns = (t: any) => [
  {
    key: 'id',
    title: t('user.columns.assocUser.id'),
    dataIndex: 'id',
    width: '20%'
  },
  {
    key: 'targetName',
    title: t('user.columns.assocUser.name'),
    dataIndex: 'targetName',
    ellipsis: true
  },
  {
    key: 'createdDate',
    title: t('user.columns.assocUser.createdDate'),
    dataIndex: 'createdDate',
    width: '20%'
  },
  {
    key: 'creator',
    title: t('user.columns.assocUser.creator'),
    dataIndex: 'creator',
    width: '20%'
  },
  {
    key: 'action',
    title: t('common.actions.operation'),
    dataIndex: 'action',
    width: '15%',
    align: 'center' as const
  }
];
