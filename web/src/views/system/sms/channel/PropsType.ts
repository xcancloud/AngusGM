/**
 * SMS sending states enumeration
 */
export const enum SendState {
  SENDSUCCESS = 'sentSuccessfully', // SMS sent successfully
  SENDFAILED = 'sendFailed', // SMS sending failed
  SENDING = 'sending', // SMS is being sent
}

/**
 * SMS channel configuration interface
 */
export interface Aisle {
  accessKeyId: string, // Access key ID for SMS service
  accessKeySecret: string, // Access key secret for SMS service
  endpoint: string, // SMS service endpoint URL
  thirdChannelNo?: string, // Optional third-party channel number
  enabled: boolean, // Whether the channel is enabled
  id: string, // Unique channel identifier
  logo: string, // Channel logo URL
  name: string, // Channel display name
  loading: boolean, // Loading state for operations
  display: boolean, // Configuration form display state
  visible: boolean // SMS sending dialog visibility
}
