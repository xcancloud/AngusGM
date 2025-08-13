/**
 * Enum type definition for dropdown/select options
 * Used for displaying human-readable messages with corresponding values
 */
type EnumType = {
  /** The actual value stored in the system */
  value: string;
  /** Human-readable display message */
  message: string;
}

/**
 * Server hardware information for license validation
 * Contains unique identifiers for hardware fingerprinting
 */
type Servers = {
  /** Server IP address */
  ipAddress: string;
  /** MAC address of the network interface */
  macAddress: string;
  /** CPU serial number for hardware identification */
  cpuSerialNumber: string;
  /** Motherboard serial number */
  mainBoardSerial: string;
  /** Generated machine code based on hardware characteristics */
  machineCode: string;
}

/**
 * Private goods interface representing installed software/goods in the system
 * This type is used for managing private store items with installation details
 */
export interface PrivateGoods {
  // Basic identification
  /** Unique identifier for the private goods record */
  id: string;
  /** Reference to the original goods in the store */
  goodsId: string;
  
  // Goods classification and metadata
  /** Edition type (e.g., Standard, Professional, Enterprise) */
  editionType: EnumType;
  /** Goods category (e.g., Application, Plugin, Library) */
  type: EnumType;
  /** Display name of the goods */
  name: string;
  /** Unique code identifier */
  code: string;
  /** Version number */
  version: string;
  /** Icon or text representation */
  iconText: string;
  /** Array of tags for categorization */
  tags: string[];
  /** Description or introduction text */
  introduction: string;
  
  // Licensing and purchase information
  /** Whether the goods requires payment */
  charge: boolean;
  /** Purchase order number */
  orderNo: string;
  /** Name of the purchaser */
  purchaseByName: string;
  /** Date of purchase */
  purchaseDate: string;
  /** Entity that issued the license */
  issuer: string;
  
  // Upgrade path information
  /** ID of the goods this is upgrading from */
  upgradeFromGoodsId: string;
  /** Code of the previous version */
  upgradeFromCode: string;
  /** Version being upgraded from */
  upgradeFromVersion: string;
  
  // Installation details
  /** Server hardware information for license validation */
  servers: Servers;
  /** Type of installation (e.g., Manual, Auto, Script) */
  installType: EnumType;
  /** Current installation status */
  installStatus: EnumType;
  /** Installation process messages or notes */
  installMessage: string;
  /** Whether the goods is currently online/active */
  online: boolean;
  
  // Tenant and ownership
  /** Tenant identifier */
  tenantId: string;
  /** Tenant name */
  tenantName: string;
  
  // Audit fields
  /** User ID who created the record */
  createdBy: string;
  /** Name of the user who created the record */
  createdByName: string;
  /** Creation timestamp */
  createdDate: string;
  
  // Operational flags
  /** Whether the goods can be uninstalled */
  uninstallable: boolean;
  /** Whether the license has expired */
  expired: boolean;
  /** Expiration date */
  expiredDate: string;
}
