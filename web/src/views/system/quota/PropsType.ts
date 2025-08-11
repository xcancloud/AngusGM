/**
 * Quota type definition for tenant resource quota management
 */
export type Quota = {
  /** Unique identifier for the quota record */
  id: string;
  /** Application code that the quota belongs to */
  appCode: string;
  /** Service code identifier */
  serviceCode: string;
  /** Quota name with display value and message */
  name: {
    /** Display value for the quota name */
    value: string;
    /** Message description for the quota */
    message: string;
  },
  /** Whether the quota can be changed by users */
  allowChange: string;
  /** Calculated remaining quota value */
  calcRemaining: string;
  /** Current quota value */
  quota: string;
  /** Default quota value */
  default0: string;
  /** Minimum allowed quota value */
  min: string;
  /** Maximum allowed quota value */
  max: string;
  /** Tenant identifier */
  tenantId: string;
  /** Tenant name */
  tenantName: string;
}
