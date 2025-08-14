import { EnumMessage, Gender, UserSource, PasswordStrength } from '@xcan-angus/infra';

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

// ==================== Detail Directory Types ====================

/**
 * User group association interface
 * Represents the relationship between a user and a group
 */
export type UserGroup = {
  id: string; // Unique identifier
  groupId: string; // Group identifier
  groupName: string; // Group display name
  groupCode: string; // Group code
  groupEnabled: true, // Whether the group is enabled
  userId: string; // User identifier
  fullName: string; // User's full name
  mobile: string; // User's mobile number
  avatar: string; // User's avatar URL
  createdDate: string; // When the association was created
  createdBy: string; // Who created the association
  tenantId: string; // Tenant identifier
  groupRemark: string; // Group remarks
  createdByName: string; // Name of who created the association
}

/**
 * User department association interface
 * Represents the relationship between a user and a department
 */
export type UserDept = {
  id: string; // Unique identifier
  deptId: string; // Department identifier
  deptCode: string; // Department code
  deptName: string; // Department name
  deptHead: boolean; // Whether user is department head
  mainDept: boolean; // Whether this is the main department
  userId: string; // User identifier
  fullName: string; // User's full name
  mobile: string; // User's mobile number
  avatar: string; // User's avatar URL
  hasSubDept: boolean; // Whether department has sub-departments
  createdDate: string; // When the association was created
  createdBy: string; // Who created the association
  tenantId: string; // Tenant identifier
  createdByName: string; // Name of who created the association
}

/**
 * User tag association interface
 * Represents the relationship between a user and a tag
 */
export type UserTag = {
  tagId: string; // Tag identifier
  tagName: string; // Tag name
  targetId: string; // Target object identifier
  targetType: string; // Type of target object
  targetName: string; // Name of target object
  createdBy: string; // Who created the association
  createdByName: string; // Name of who created the association
  createdDate: string; // When the association was created
  targetCreatedBy: string; // Who created the target
  targetCreatedByName: string; // Name of who created the target
  targetCreatedDate: string; // When the target was created
}

/**
 * User detail interface
 * Comprehensive user information including personal details, status, and associations
 */
export interface Detail {
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
  contactAddress: string; // User's contact address
  sysAdmin: boolean; // Whether user is a system administrator
  deptHead: boolean; // Whether user is a department head
  enabled: boolean; // Whether user account is enabled
  source: EnumMessage<UserSource>; // User source with localized message
  locked: boolean; // Whether user account is locked
  lockStartDate: string; // Date when user was locked
  lockEndDate: string; // Date when user lock expires
  tenantId: string; // Tenant identifier
  tenantName: string; // Tenant name
  createdBy: string; // ID of user who created this user
  createdByName: string; // Name of user who created this user
  createdDate: string; // Date when user was created
  lastModifiedBy: string; // ID of user who last modified this user
  lastModifiedByName: string; // Name of user who last modified this user
  lastModifiedDate: string; // Date when user was last modified
  passwordStrength: EnumMessage<PasswordStrength>; // Password strength with localized message
  passwordExpired: boolean; // Whether password has expired
  passwordExpiredDate: string; // Date when password expires
  tenantRealNameStatus: string; // Real name verification status
  online: boolean; // Whether user is currently online
  onlineDate: string; // Date when user last went online
  offlineDate: string; // Date when user last went offline
  tags: [ // User's associated tags
    {
      id: string; // Tag identifier
      name: string; // Tag name
    }
  ],
  depts: string; // User's associated departments
  groups: [ // User's associated groups
    {
      id: string; // Group identifier
      name: string; // Group name
    }
  ]
}
