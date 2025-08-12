/**
 * Email protocol types
 */
export type Protocol = 'SMTP' | 'IMAP' | 'POP3'

/**
 * SMS record interface for tracking SMS sending history
 */
export type SmsRecord = {
  id: string; // Unique SMS record identifier
  templateCode: string; // Associated SMS template code
  language: { // Language information
    value: string; // Language code
    message: string; // Localized language name
  },
  bizKey: string; // Business key for tracking
  outId: string; // External system ID
  thirdOutputParam: any; // Third-party service output parameters
  inputParam: any; // Input parameters for SMS
  verificationCode: boolean; // Whether SMS contains verification code
  batch: boolean; // Whether SMS is part of batch sending
  sendTenantId: string; // Tenant ID of sender
  sendUserId: string; // User ID of sender
  urgent: boolean; // Whether SMS is marked as urgent
  sendStatus: boolean; // SMS sending status
  actualSendDate: boolean; // Actual sending date
  expectedSendDate: boolean; // Expected sending date
}
