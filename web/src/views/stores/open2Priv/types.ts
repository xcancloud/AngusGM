/**
 * Goods interface definition for open store items
 * Represents a product/goods item in the open store with all its properties
 * This type is used for managing open store items that can be converted to private goods
 */
export interface Goods {
  // Basic identification
  /** Unique identifier for the goods */
  id: string;
  /** Display name of the goods */
  name: string;
  /** Whether the goods is marked as hot/popular */
  hot: boolean;
  /** Description text for the goods */
  introduction: string;
  /** Optional array of tags associated with the goods */
  tags?: string[];

  // Classification and metadata
  /** Edition type with value and display message */
  editionType: { value: string, message: string };
  /** URL to the goods icon/image */
  iconUrl: string;
  /** Number of stars/likes for the goods */
  starNum: number;
  /** Unique goods identifier (may differ from id) */
  goodsId: string;
  /** Array of video resources for the goods */
  videos: { name: string, url: string }[];
  /** Optional array of feature descriptions */
  features?: string[];
  /** Optional array of banner image URLs */
  bannerUrls?: string[];
  /** Product type classification */
  productType: { value: string, message: string };
  /** Version string of the goods */
  version: string;

  // Licensing and purchase information
  /** Whether the goods requires payment */
  charge: boolean;
  /** Name of the user who created the goods */
  creator: string;
  /** Creation date string */
  createdDate: string;
  /** Pricing information */
  price: {
    /** Final price after discounts */
    finalPrice: string;
    /** Original total price */
    totalSpecPrice: string;
  };
  /** URL to the pricing page */
  pricingUrl: string;
  /** Date when goods went online */
  onlineDate: string;

  // User interaction flags
  /** Whether current user has starred this goods */
  star?: boolean;
  /** Whether comments are allowed for this goods */
  allowComment: boolean;
  /** Whether upgrade is allowed */
  allowUpgrade?: boolean;
  /** Whether installation is allowed */
  allowInstall?: boolean;
  /** Whether goods is currently installed */
  installed?: boolean;
  /** Whether goods has been purchased */
  purchased?: boolean;

  // Additional information
  /** Compatible edition types for this goods */
  applyEditionTypes: { value: string, message: string }[];
  /** Additional information text */
  information?: string;
  /** Goods type classification */
  type?: { value: string, message: string };
}

// Constants for goods type classification
/** Private edition types that are considered private goods */
const goodsPriTypes = ['DATACENTER', 'ENTERPRISE', 'COMMUNITY'];

/**
 * Check if goods is considered private goods
 */
export const isPriGoods = (goods: Goods): boolean => {
  return (goods.applyEditionTypes || []).some(i => goodsPriTypes.includes(i.value));
};

/**
 * Color mapping for different goods types
 * Maps goods type values to CSS color classes for consistent styling
 */
export const goodsTypeColor = {
  /** Plugin type - orange color */
  PLUGIN: 'text-orange-text',
  /** Plugin application type - orange color */
  PLUGIN_APPLICATION: 'text-orange-text',
  /** Application type - purple color */
  APPLICATION: 'text-purple-text',
  /** Resource quota type - green color */
  RESOURCE_QUOTA: 'text-green-text'
};

/**
 * Convert enum array to display string
 * Joins enum messages with Chinese separator (、)
 */
export const getEnumMessages = (enums: { value: string, message: string }[] = []): string => {
  return (enums || []).map(i => i.message).join('、');
};
