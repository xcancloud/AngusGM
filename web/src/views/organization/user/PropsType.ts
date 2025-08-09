import { Gender, UserSource, EnumMessage, SearchCriteria } from '@xcan-angus/infra';
import { OrgTargetType } from '@/enums/enums';

/**
 * User entity interface representing a user in the system
 * Contains all user-related information including personal details, status, and metadata
 */
export interface User {
  id: string; // Unique user identifier
  username: string; // User login name
  fullName: string; // User's full display name
  firstName: string; // User's first name
  lastName: string; // User's last name
  itc: string; // ITC (Information Technology Code) identifier
  country: string; // User's country
  mobile: string; // User's mobile phone number
  email: string; // User's email address
  landline: string; // User's landline phone number
  avatar: string; // User's profile picture URL
  title: string; // User's job title or position
  gender: EnumMessage<Gender>; // User's gender with localized message
  address: {
    provinceCode: string; // Province/state code
    provinceName: string; // Province/state name
    cityCode: string; // City code
    cityName: string; // City name
    street: string; // Street address
  };
  sysAdmin: boolean; // Whether user is a system administrator
  deptHead: boolean; // Whether user is a department head
  enabled: boolean; // Whether user account is enabled
  source: EnumMessage<UserSource>; // User source with localized message
  locked: boolean; // Whether user account is locked
  lockStartDate: string; // Date when user was locked
  lockEndDate: string; // Date when user lock expires
  online: boolean; // Whether user is currently online
  onlineDate: string; // Date when user last went online
  offlineDate: string; // Date when user last went offline
  tenantId: string; // Tenant identifier
  tenantName: string; // Tenant name
  createdBy: string; // ID of user who created this user
  createdByName: string; // Name of user who created this user
  createdDate: string; // Date when user was created
  lastModifiedBy: string; // ID of user who last modified this user
  lastModifiedByName: string; // Name of user who last modified this user
  lastModifiedDate: string; // Date when user was last modified
}

/**
 * Form state interface for user creation and editing
 * Represents the data structure used in user forms
 */
export interface FormState {
  address: string; // User's address
  avatar: string; // User's profile picture URL
  country: string; // User's country
  email: string; // User's email address
  firstName: string; // User's first name
  fullName: string; // User's full display name
  gender: Gender; // User's gender
  itc: string; // ITC identifier
  landline: string; // User's landline phone number
  lastName: string; // User's last name
  mobile: string | null; // User's mobile phone number (nullable)
  password: string; // User's password
  sysAdmin: boolean; // Whether user is a system administrator
  title: string; // User's job title
  username: string; // User's login name
  confirmPassword?: string; // Password confirmation for validation
}

/**
 * Department entity interface
 * Represents organizational department information
 */
export interface Dept {
  id: string; // Unique department identifier
  code: string; // Department code
  name: string; // Department name
  pid: string; // Parent department ID
  parentName: string; // Parent department name
  tenantId: string; // Tenant identifier
  tenantName: string; // Tenant name
  createdBy: string; // ID of user who created this department
  createdByName: string; // Name of user who created this department
  createdDate: string; // Date when department was created
  lastModifiedBy: string; // ID of user who last modified this department
  lastModifiedByName: string; // Name of user who last modified this department
  lastModifiedDate: string; // Date when department was last modified
  hasSubDept: boolean; // Whether department has sub-departments
}

/**
 * User state management interface
 * Controls modal visibility and current user selection for operations
 */
export interface UserState {
  updatePasswdVisible: boolean; // Password reset modal visibility flag
  lockModalVisible: boolean; // User lock modal visibility flag
  currentUserId: string | undefined; // Currently selected user ID for modal operations
}

/**
 * Search option type definitions
 * Defines all available search field types for the SearchPanel component
 */
export type SearchOptionType =
  | 'input' // Text input field
  | 'select-enum' // Dropdown with enum values
  | 'date-range' // Date range picker
  | 'select' // Dropdown with custom options
  | 'date' // Single date picker
  | 'select-app' // Application selector
  | 'select-dept' // Department selector
  | 'select-group' // Group selector
  | 'select-intl' // International selector
  | 'select-itc' // ITC selector
  | 'select-user' // User selector
  | 'select-service' // Service selector
  | 'select-tag' // Tag selector
  | 'select-tenant' // Tenant selector
  | 'tree-select'; // Tree structure selector

/**
 * Search option configuration interface
 * Defines the structure and properties of search field configurations
 */
export interface SearchOption {
  placeholder?: string; // Placeholder text for the search field
  valueKey: string; // Key used to identify the search field value
  type: SearchOptionType; // Type of search field
  op?: SearchCriteria.OpEnum; // Search operation type (equal, contains, etc.)
  allowClear?: boolean; // Whether the field can be cleared
  enumKey?: any; // Enum values for select-enum type
  action?: string; // API endpoint for dynamic options
  fieldNames?: { label: string; value: string }; // Field mapping for dynamic options
  showSearch?: boolean; // Whether to show search functionality in dropdown
  lazy?: boolean; // Whether to load options lazily
}

/**
 * Factory to create i18n-aware columns inside component setup.
 * Must be called within Vue component `setup` where `t` is available.
 */
export const createAuthPolicyColumns = (t: (key: string) => string, orgTargetType?: OrgTargetType) => {
  const columns = [
    {
      title: t('permission.columns.assocPolicies.id'),
      dataIndex: 'id',
      width: '15%',
      customCell: () => ({ style: 'white-space:nowrap;' })
    },
    {
      title: t('permission.columns.assocPolicies.name'),
      dataIndex: 'name',
      width: '15%'
    },
    {
      title: t('permission.columns.assocPolicies.code'),
      dataIndex: 'code'
    }
  ] as const;

  // Conditionally include orgType column: exclude when target type is DEPT or GROUP
  const result: any[] = [...(columns as unknown as any[])];
  const shouldHideOrgType = orgTargetType === OrgTargetType.DEPT || orgTargetType === OrgTargetType.GROUP;
  if (!shouldHideOrgType) {
    result.push({
      title: t('permission.columns.assocPolicies.source'),
      dataIndex: 'orgType',
      width: '15%',
      customCell: () => ({ style: 'white-space:nowrap;' })
    });
  }

  result.push(
    {
      title: t('permission.columns.assocPolicies.authByName'),
      dataIndex: 'authByName',
      width: '15%'
    },
    {
      title: t('permission.columns.assocPolicies.authDate'),
      dataIndex: 'authDate',
      width: '13%',
      customCell: () => ({ style: 'white-space:nowrap;' })
    },
    {
      title: t('common.actions.operation'),
      dataIndex: 'action',
      width: '10%',
      align: 'center' as const
    }
  );

  return result;
};
