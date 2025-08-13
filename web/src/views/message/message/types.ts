/**
 * Table column configuration interface
 * Defines the structure for table column configuration including sorting, title, and custom rendering
 */
export interface TableColumnType {
  sorter?: boolean, // Enable sorting for this column
  title: string, // Column header text
  dataIndex: string, // Data field key for this column
  key: string, // Unique identifier for the column
  slots?: { customRender: string } // Custom rendering slot configuration
}

/**
 * Individual receive object data structure
 * Represents a single recipient of a message with basic information
 */
export interface ReceiveObjectDataType {
  id: string, // Unique identifier for the receive object
  avatar: string, // Avatar image URL or identifier
  name: string, // Display name of the receive object
  mobile: string // Mobile phone number
}

/**
 * Complete message data structure
 * Contains all information about a message including recipients, status, and metadata
 */
export interface ReceiveObjectData {
  fullName?: string | undefined, // Full name of the message creator
  id?: string | undefined, // Unique message identifier
  readNum?: string | undefined, // Number of recipients who have read the message
  receiveObjectData?: Array<ReceiveObjectDataType>, // Array of individual recipients
  receiveObjectType?: Record<string, any>, // Type of receive object (user, dept, group)
  receiveObjectDataName?: string, // Display name for receive object type
  receiveObjectDataLength?: number, // Total number of recipients
  timingDate?: string, // Scheduled send time for the message
  sentNum?: string, // Number of recipients the message was sent to
  status?: string, // Current status of the message
  title?: string, // Message title/subject
  userId?: string, // ID of the user who created the message
  content?: string, // Message content/body
  sentType?: string, // Type of send operation (immediate, scheduled)
  receiveType?: string, // Type of receive operation (push, email, SMS)
}
