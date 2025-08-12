/**
 * Email record status enumeration
 */
export type EmailSendStatus = 'SUCCESS' | 'PENDING' | 'FAILURE';

/**
 * Email type enumeration
 */
export type EmailType = 'VERIFICATION' | 'NOTIFICATION' | 'MARKETING' | 'SYSTEM';

/**
 * Language configuration interface
 */
export interface LanguageConfig {
  value: string;
  message: string;
}

/**
 * Email record interface representing a single email record
 */
export interface EmailRecord {
  /** Unique identifier for the email record */
  id: string;
  /** Template code used for email generation */
  templateCode: string;
  /** Language configuration for internationalization */
  language: LanguageConfig;
  /** Business key for tracking purposes */
  bizKey: string;
  /** External identifier */
  outId: string;
  /** Sender user identifier */
  sendId: string;
  /** Type of email (verification, notification, etc.) */
  emailType: EmailType;
  /** Email subject line */
  subject: string;
  /** Sender email address */
  fromAddr: string;
  /** Whether this email contains verification code */
  verificationCode: boolean;
  /** Validity period for verification code in seconds */
  verificationCodeValidSecond: string;
  /** Array of recipient email addresses */
  toAddress: string[];
  /** Carbon copy email addresses */
  ccAddress: string;
  /** Whether email content is HTML format */
  html: boolean;
  /** Current send status */
  sendStatus: EmailSendStatus;
  /** Reason for failure if send failed */
  failureReason: string;
  /** Email content body */
  content: string;
  /** Template parameters for dynamic content */
  templateParams: Record<string, any>;
  /** File attachments */
  attachments: string;
  /** Expected send date/time */
  expectedSendDate: string;
  /** Actual send date/time */
  actualSendDate: string;
  /** Whether email is marked as urgent */
  urgent: boolean;
  /** Tenant identifier for multi-tenancy */
  sendTenantId: string;
  /** Whether email is part of a batch send */
  batch: boolean;
}
