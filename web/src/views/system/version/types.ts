/**
 * Installed edition information interface
 * Contains complete details about the currently installed software edition
 */
export type InstallEdition = {
  goodsId: string, // Unique identifier for the goods/product
  goodsType: {
    value: string, // Goods type value (e.g., 'SOFTWARE', 'SERVICE')
    message: string, // Human-readable goods type description
  },
  goodsCode: string, // Product code identifier
  goodsName: string, // Product name
  editionType: {
    value: string, // Edition type value (e.g., 'ENTERPRISE', 'COMMUNITY')
    message: string, // Human-readable edition type description
  },
  provider: string, // Software provider/vendor name
  issuer: string, // License issuer name
  holderId: string, // License holder identifier
  holder: string, // License holder name
  licenseNo: string, // License number
  info: string, // Additional license information
  signature: string, // MD5 signature for license validation
  issuedDate: string, // Date when license was issued
  beginDate: string, // License validity start date
  endDate: string, // License expiration date
  goodsVersion: string, // Current installed version
}

/**
 * Upgradeable version information interface
 * Contains details about available version upgrades
 */
export type UpgradeableVersion = {
  goodsId: string, // Unique identifier for the goods/product
  name: string, // Product name
  code: string, // Product code
  version: string, // Available version number
  iconUrl: string, // Product icon URL
  introduction: string, // Version introduction/description
  information: string, // Additional version information
  features: string[], // List of new features in this version
  releaseDate: string, // Version release date
  upgradeable: boolean | null, // Whether upgrade is available
}

/**
 * Grid column configuration interface
 * Defines the structure for grid column definitions
 */
export interface GridColumn {
  label: string,
  dataIndex: string,
  hide?: boolean
}

/**
 * Grid columns configuration type
 * Array of grid column arrays for different layouts
 */
export type GridColumns = GridColumn[][]

/**
 * Component props interface for updatable version
 */
export interface UpdatableVersionProps {
  currentVersion: string,
  installGoodsCode: string
}

/**
 * Edition type constants
 * Maps edition types to their corresponding icon classes
 */
export interface EditionTypeIcons {
  DATACENTER: string,
  CLOUD_SERVICE: string,
  ENTERPRISE: string,
  COMMUNITY: string
}

/**
 * Copy operation result types
 * Defines possible states for copy operations
 */
export type CopyResult = 'copy' | 'copySuccess' | 'copyFailure'

/**
 * Version card configuration
 * Configuration for different version card layouts
 */
export interface VersionCardConfig {
  cloudService: GridColumns,
  private: GridColumns
}
