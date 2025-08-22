import { userToken, setting } from '@/api';
import type { TokenRecord, CreateTokenParams } from './types';

/**
 * Load token data from API
 * @returns Promise with token data and total count
 */
export const loadTokenData = async (): Promise<{ data: TokenRecord[]; total: number }> => {
  try {
    const [error, res] = await userToken.getToken();

    if (error || !res?.data) {
      console.error('Failed to load tokens:', error);
      return { data: [], total: 0 };
    }

    return { data: res.data, total: res.data.length };
  } catch (err) {
    console.error('Unexpected error loading tokens:', err);
    return { data: [], total: 0 };
  }
};

/**
 * Create a new token
 */
export const createToken = async (params: CreateTokenParams): Promise<string | null> => {
  try {
    const [error, res] = await userToken.addToken(params);

    if (error || !res?.data) {
      console.error('Failed to create token:', error);
      return null;
    }

    return res.data.value;
  } catch (err) {
    console.error('Unexpected error creating token:', err);
    return null;
  }
};

/**
 * Delete a token by ID
 */
export const deleteTokenById = async (record: TokenRecord): Promise<boolean> => {
  try {
    const [error] = await userToken.deleteToken({ ids: [record.id] });

    if (error) {
      console.error('Failed to delete token:', error);
      return false;
    }

    return true;
  } catch (err) {
    console.error('Unexpected error deleting token:', err);
    return false;
  }
};

/**
 * Fetch token value from API
 */
export const fetchTokenValue = async (tokenId: string): Promise<string | null> => {
  try {
    const [error, { data = {} }] = await userToken.getTokenValue(tokenId);

    if (error) {
      console.error('Failed to fetch token value:', error);
      return null;
    }

    return data.value || null;
  } catch (err) {
    console.error('Unexpected error fetching token value:', err);
    return null;
  }
};

/**
 * Get token quota from system settings
 * @returns Promise with token quota value
 */
export const getTokenQuota = async (): Promise<number> => {
  try {
    const [error, { data }] = await setting.getTokenQuota();

    if (error) {
      console.error('Failed to fetch token quota:', error);
      return 0;
    }

    return +data.quota;
  } catch (err) {
    console.error('Unexpected error fetching token quota:', err);
    return 0;
  }
};

/**
 * Check if token is expired
 */
export const isTokenExpired = (item: TokenRecord): boolean => {
  try {
    if (!item.expiredDate) {
      return false; // Consider not expired if no date is provided
    }

    const expiredDate = new Date(item.expiredDate);
    const currentDate = new Date();

    // Check if the date is valid
    if (isNaN(expiredDate.getTime())) {
      console.warn('Invalid expired date:', item.expiredDate);
      return false;
    }

    return expiredDate < currentDate;
  } catch (err) {
    console.error('Error checking token expiration:', err);
    return false; // Default to not expired on error
  }
};

/**
 * Validate form data for token creation
 */
export const validateTokenForm = (
  name: string,
  password: string,
  prevName: string,
  total: number,
  quota: number
): boolean => {
  const hasValidName = Boolean(name && name.trim());
  const isNameChanged = prevName !== name;
  const hasValidPassword = Boolean(password && password.trim());
  const hasAvailableQuota = total < quota;

  return hasValidName && isNameChanged && hasAvailableQuota && hasValidPassword;
};

/**
 * Reset form fields to initial state
 */
export const resetForm = (formRefs: {
  name: { value: string };
  password: { value: string };
  expireDate: { value: string };
}): void => {
  formRefs.name.value = '';
  formRefs.password.value = '';
  formRefs.expireDate.value = '';
};
