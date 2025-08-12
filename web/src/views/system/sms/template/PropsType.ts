/**
 * SMS template configuration interface
 */
export interface Template {
  id: string, // Unique template identifier
  code: string, // Template code for identification
  name: string, // Template display name
  thirdCode: string, // Third-party service template code
  channelId: string, // Associated SMS channel ID
  signature: string, // SMS signature text
  content: string, // SMS template content
  subject: string, // SMS subject (if applicable)
  language: string, // Template language code
  verificationCode: boolean, // Whether template contains verification code
  verificationCodeValidSecond: string, // Verification code validity period in seconds
  showEdit: boolean, // Edit mode display state
  editValues: { // Editable template values
    name: string; // Editable name
    thirdCode: string; // Editable third-party code
    language: string; // Editable language
    signature: string; // Editable signature
    content: string; // Editable content
  }
}

/**
 * Generic options interface for select components
 */
export interface Options {
  label: string, // Display label
  value: string // Option value
}
