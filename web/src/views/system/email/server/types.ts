/**
 * Email protocol types supported by the system
 */
export type Protocol = 'SMTP' | 'IMAP' | 'POP3';

/**
 * Authentication account configuration
 */
export interface AuthAccount {
  /** Email account username */
  account: string;
  /** Email account password */
  password: string;
}

/**
 * Mailbox service configuration interface
 */
export interface MailboxService {
  /** Unique identifier for the mailbox service */
  id: string;
  /** Display name for the mailbox service */
  name: string;
  /** Email protocol type */
  protocol: Protocol;
  /** Additional remarks or description */
  remark: string;
  /** Whether this service is enabled as default */
  enabled: boolean;
  /** SMTP/IMAP server hostname */
  host: string;
  /** Server port number */
  port: string | number;
  /** Whether STARTTLS is enabled */
  startTlsEnabled: boolean;
  /** Whether SSL/TLS encryption is enabled */
  sslEnabled: boolean;
  /** Whether authentication is required */
  authEnabled: boolean;
  /** Authentication credentials */
  authAccount: AuthAccount;
  /** Subject prefix for outgoing emails */
  subjectPrefix: string;
}

/**
 * Form state interface for editing mailbox services
 * Extends MailboxService with optional id for create/update operations
 */
export interface FormState extends Omit<MailboxService, 'id'> {
  /** Optional identifier for existing services */
  id?: string;
}
