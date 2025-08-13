/**
 * Enum type definition for dropdown/select options
 * Used for displaying human-readable messages with corresponding values
 */
export type Enum = {
  /** The actual value stored in the system */
  value: string;
  /** Human-readable display message */
  message: string;
}

/**
 * Cloud goods interface representing products available in the cloud store
 * This type is used for managing cloud store items with purchase and licensing details
 */
export interface Goods {
  // Basic identification
  /** Unique identifier for the goods record */
  id: string;
  /** Tenant identifier for multi-tenancy support */
  tenantId: string;
  /** Tenant display name */
  tenantName: string;

  // Goods classification and metadata
  /** Edition type (e.g., Standard, Professional, Enterprise) */
  goodsEditionType: Enum;
  /** Goods category (e.g., Application, Plugin, Library) */
  goodsType: Enum;
  /** Unique goods identifier */
  goodsId: string;
  /** Goods code for system reference */
  goodsCode: string;
  /** Display name of the goods */
  goodsName: string;
  /** Version number */
  goodsVersion: string;
  /** URL to the goods icon/image */
  goodsIconUrl: string;
  /** Description or introduction text */
  goodsIntroduction: string;

  // Licensing and purchase information
  /** Whether the goods requires payment */
  charge: boolean;
  /** Purchase order identifier */
  orderId: string;
  /** Purchase order number */
  orderNo: string;
  /** Whether the license has expired */
  expired: boolean;
  /** Expiration date */
  expiredDate: string;
  /** User ID of the purchaser */
  purchaseBy: string;
  /** Name of the purchaser */
  purchaseByName: string;
  /** Date of purchase */
  purchaseDate: string;

  // Edition compatibility
  /** Array of edition types this goods can be applied to */
  applyEditionTypes: { value: string, message: string }[];
}

// Constants for goods type classification
/** Private edition types that are considered private goods */
const goodsPriTypes = ['DATACENTER', 'ENTERPRISE', 'COMMUNITY'];
/** Cloud service types */
const goodsCloudTypes = ['CLOUD_SERVICE'];

/**
 * Check if goods is considered private goods
 * @param applyEditionTypes - Array of edition types to check
 * @returns true if goods applies to private edition types
 */
export const isPriGoods = (applyEditionTypes: { value: string, message: string }[] = []): boolean => {
  return (applyEditionTypes || []).some(i => goodsPriTypes.includes(i.value));
};

/**
 * Check if goods is a cloud service
 * @param applyEditionTypes - Array of edition types to check
 * @returns true if goods is a cloud service type
 */
export const isCloudGoods = (applyEditionTypes: { value: string, message: string }[] = []): boolean => {
  return (applyEditionTypes || []).some(i => goodsCloudTypes.includes(i.value));
};

/**
 * Check if goods has multiple private edition types
 * @param goods - The goods object to check
 * @returns true if goods has multiple private edition types
 */
export const multipleEditionTypes = (goods: Goods): boolean => {
  return (goods.applyEditionTypes || []).filter(i => goodsPriTypes.includes(i.value)).length > 1;
};

/**
 * Get downloadable edition types for private goods
 * @param goods - The goods object to check
 * @returns Array of edition types that can be downloaded
 */
export const downloadEditionTypes = (goods: Goods) => {
  return (goods.applyEditionTypes || []).filter(i => goodsPriTypes.includes(i.value));
};
