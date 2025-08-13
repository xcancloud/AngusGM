/**
 * Password validation utility module
 * Provides comprehensive password strength analysis and validation
 */

/**
 * Special characters allowed in passwords
 * Defines the set of special characters that are considered valid
 */
const SPECIAL_CHARS = "`-=[];',./~!@#$%^&*)(_+{}:\"<>?";
const charCodeSet = new Set<string>();

// Initialize character set for efficient lookup
SPECIAL_CHARS.split('').forEach(item => {
  charCodeSet.add(item);
});

/**
 * Convert value to array of characters
 * @param value - Input value to convert
 * @returns Array of characters or empty array if value is falsy
 */
const _toArray = (value: string | number | null | undefined): string[] => {
  if (!value) {
    return [];
  }
  return String(value).split('');
};

/**
 * Filter out invalid characters from input array
 * @param _values - Array of characters to validate
 * @returns Array of invalid characters found
 */
const _filterInvalidChar = (_values: string[]): string[] => {
  const invalidSet = new Set<string>();
  for (let i = _values.length; i--;) {
    if (!charCodeSet.has(_values[i])) {
      invalidSet.add(_values[i]);
    }
  }
  return Array.from(invalidSet);
};

/**
 * Count special characters in input array
 * @param _values - Array of characters to analyze
 * @returns Number of special characters found
 */
const getSpecialCharNum = (_values: string[]): number => {
  let _num = 0;
  for (let i = _values.length; i--;) {
    if (charCodeSet.has(_values[i])) {
      _num++;
    }
  }
  return _num;
};

/**
 * Count digit characters in input array
 * @param _values - Array of characters to analyze
 * @returns Number of digit characters found
 */
const getDigitNum = (_values: string[]): number => {
  let _num = 0;
  for (let i = _values.length; i--;) {
    if (!isNaN(+_values[i])) {
      _num++;
    }
  }
  return _num;
};

/**
 * Count lowercase letters in input array
 * @param _values - Array of characters to analyze
 * @returns Number of lowercase letters found
 */
const getLowerCaseNum = (_values: string[]): number => {
  let _num = 0;
  for (let i = _values.length; i--;) {
    if (/[a-z]/g.test(_values[i])) {
      _num++;
    }
  }
  return _num;
};

/**
 * Count uppercase letters in input array
 * @param _values - Array of characters to analyze
 * @returns Number of uppercase letters found
 */
const getUpperCaseNum = (_values: string[]): number => {
  let _num = 0;
  for (let i = _values.length; i--;) {
    if (/[A-Z]/g.test(_values[i])) {
      _num++;
    }
  }
  return _num;
};

/**
 * Calculate the number of different character types in password
 * @param _values - Array of characters to analyze
 * @returns Number of character types (special, digit, lowercase, uppercase)
 */
const getTypesNum = (_values: string[]): number => {
  const hasSpecialChar = getSpecialCharNum(_values) ? 1 : 0;
  const hasDigit = getDigitNum(_values) ? 1 : 0;
  const hasLowerCase = getLowerCaseNum(_values) ? 1 : 0;
  const hasUpperCase = getUpperCaseNum(_values) ? 1 : 0;
  return hasSpecialChar + hasDigit + hasLowerCase + hasUpperCase;
};

/**
 * Calculate character repetition rate in password
 * @param _values - Array of characters to analyze
 * @returns Repetition rate as a decimal (0.0 to 1.0)
 */
const calcRepeatRate = (_values: string[]): number => {
  const noRepeatChars = new Set(_values);
  const len = _values.length;
  return (len - noRepeatChars.size) / len;
};

/**
 * Calculate password strength based on complexity and length
 * @param value - Password string to evaluate
 * @returns Password strength level: 'weak', 'medium', or 'strong'
 */
const calcStrength = (value: string): 'weak' | 'strong' | 'medium' => {
  const chars = _toArray(value);
  const typesNum = getTypesNum(chars);
  const len = chars.length;

  // Weak password criteria
  if ((typesNum <= 2 && len < 10) || (typesNum === 3 && len < 9) || (typesNum === 4 && len < 8)) {
    return 'weak';
  }

  // Strong password criteria
  if ((typesNum === 2 && len >= 18) || (typesNum === 3 && len >= 15) || (typesNum === 4 && len >= 12)) {
    return 'strong';
  }

  return 'medium';
};

/**
 * Password validation result interface
 */
interface ValidationResult {
  code: 0 | 1 | 2 | 3 | 4;
  char: string | undefined;
}

/**
 * Validate password against security rules
 * @param value - Password string to validate
 * @returns Validation result with error code and invalid character details
 *
 * Error codes:
 * 0 - Password meets all requirements
 * 1 - Contains illegal characters
 * 2 - Repetition rate exceeds 0.5
 * 3 - Length outside 6-50 character range
 * 4 - Insufficient character type variety (less than 2 types)
 */
const isInvalid = (value = ''): ValidationResult => {
  // Check for illegal special characters
  const _values = value.replace(/[a-zA-Z\d]/gi, '');
  const specialChars = _filterInvalidChar(_toArray(_values));
  if (specialChars.length) {
    return { code: 1, char: specialChars.join(' ') };
  }

  // Check character repetition rate
  const chars = _toArray(value);
  if (calcRepeatRate(chars) > 0.5) {
    return { code: 2, char: undefined };
  }

  // Check password length
  if (chars.length < 6 || chars.length > 50) {
    return { code: 3, char: undefined };
  }

  // Check character type variety
  if (getTypesNum(chars) < 2) {
    return { code: 4, char: undefined };
  }

  return { code: 0, char: undefined };
};

// Export all password utility functions
export default {
  calcStrength,
  getSpecialCharNum,
  getDigitNum,
  getLowerCaseNum,
  getUpperCaseNum,
  getTypesNum,
  isInvalid
};
