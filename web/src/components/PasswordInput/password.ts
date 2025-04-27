const SPECIAL_CHARS = "`-=[];',./~!@#$%^&*)(_+{}:\"<>?";
const charCodeSet = new Set();

SPECIAL_CHARS.split('').forEach(item => {
  charCodeSet.add(item);
});

const _toArray = (value) => {
  if (!value) {
    return [];
  }

  return (value + '').split('');
};

const _filterInvalidChar = (_values: string[]) => {
  const invalidSet = new Set();
  for (let i = _values.length; i--;) {
    if (!charCodeSet.has(_values[i])) {
      invalidSet.add(_values[i]);
    }
  }

  return Array.from(invalidSet);
};

const getSpecialCharNum = (_values: string[]): number => {
  let _num = 0;
  for (let i = _values.length; i--;) {
    if (charCodeSet.has(_values[i])) {
      _num++;
    }
  }

  return _num;
};

const getDigitNum = (_values: string[]): number => {
  let _num = 0;
  for (let i = _values.length; i--;) {
    if (!isNaN(+_values[i])) {
      _num++;
    }
  }

  return _num;
};

const getLowerCaseNum = (_values: string[]): number => {
  let _num = 0;
  for (let i = _values.length; i--;) {
    if (/[a-z]/g.test(_values[i])) {
      _num++;
    }
  }
  return _num;
};

const getUpperCaseNum = (_values: string[]): number => {
  let _num = 0;
  for (let i = _values.length; i--;) {
    if (/[A-Z]/g.test(_values[i])) {
      _num++;
    }
  }

  return _num;
};

const getTypesNum = (_values: string[]): number => {
  const hasSpecialChar = getSpecialCharNum(_values) ? 1 : 0;
  const hasDigit = getDigitNum(_values) ? 1 : 0;
  const hasLowerCase = getLowerCaseNum(_values) ? 1 : 0;
  const hasUpperCase = getUpperCaseNum(_values) ? 1 : 0;
  return hasSpecialChar + hasDigit + hasLowerCase + hasUpperCase;
};

const calcRepeatRate = (_values: string[]) => {
  const noRepeatChars = new Set(_values);
  const len = _values.length;
  return (len - noRepeatChars.size) / len;
};

const calcStrength = (value: string): 'weak' | 'strong' | 'medium' => {
  const chars = _toArray(value);
  const typesNum = getTypesNum(chars);
  const len = chars.length;
  if ((typesNum <= 2 && len < 10) || (typesNum === 3 && len < 9) || (typesNum === 4 && len < 8)) {
    return 'weak';
  }
  if ((typesNum === 2 && len >= 18) || (typesNum === 3 && len >= 15) || (typesNum === 4 && len >= 12)) {
    return 'strong';
  }
  return 'medium';
};

// 0-密码符合规则 1-密码重复度大于0.5 2-密码长度小于6或者大于50 3-密码组合小于2
const isInvalid = (value = ''): { code: 0 | 1 | 2 | 3 | 4, char: string | undefined } => {
  const _values = value.replace(/[a-zA-Z\d]/gi, '');
  const specialChars = _filterInvalidChar(_toArray(_values));
  if (specialChars.length) {
    return { code: 1, char: specialChars.join(' ') };
  }
  const chars = _toArray(value);
  if (calcRepeatRate(chars) > 0.5) {
    return { code: 2, char: undefined };
  }
  if (chars.length < 6 || chars.length > 50) {
    return { code: 3, char: undefined };
  }
  if (getTypesNum(chars) < 2) {
    return { code: 4, char: undefined };
  }
  return { code: 0, char: undefined };
};

export default {
  calcStrength,
  getSpecialCharNum,
  getDigitNum,
  getLowerCaseNum,
  getUpperCaseNum,
  getTypesNum,
  isInvalid
};
