<script setup lang="ts">
import { ref, watch, computed } from 'vue';
import { cookieUtils } from '@xcan-angus/infra';
import { useI18n } from 'vue-i18n';
import { InputPassword } from 'ant-design-vue';

import password from './password';

/**
 * Component props interface
 * Defines the structure for password input configuration
 */
interface Props {
  value: string;
  tipClass?: string;
  allowClear?: boolean;
  disabled?: boolean;
  defaultValue?: string;
  includes?: string;
  placeholder?: string;
}

const props = withDefaults(defineProps<Props>(), {
  value: '',
  tipClass: undefined,
  allowClear: false,
  disabled: false,
  defaultValue: undefined,
  includes: undefined,
  placeholder: undefined
});

/**
 * Component emits definition
 * Defines the events that this component can emit
 */
const emit = defineEmits<{
  (e: 'update:value', value: string): void;
  (e: 'pressEnter', value: string): void;
}>();

const { t } = useI18n();

/**
 * Error message mapping for different locales
 * Provides localized error messages for password validation
 */
const errorMap = {
  en: {
    1: 'Illegal character, input not allowed',
    2: 'The repetition rate cannot exceed 0.5, such as 6 characters, at least 3 characters are different',
    3: 'Password length between 6 and 50',
    4: 'Contains at least two types of combinations'
  },
  zh_CN: {
    1: '非法字符，不允许输入',
    2: '重复率不能超过0.5，如6个字符至少3个字符不相同',
    3: '密码长度在6到50之间',
    4: '至少包含两种类型组合'
  }
};

/**
 * Password strength mapping for different locales
 * Provides localized strength indicators
 */
const tipMap = {
  en: {
    weak: 'weak',
    medium: 'medium',
    strong: 'strong'
  },
  zh_CN: {
    weak: '弱',
    medium: '中',
    strong: '强'
  }
};

/**
 * Get current locale from cookie and set appropriate message maps
 * Dynamically switches between English and Chinese based on user preference
 */
const localeCookie = cookieUtils.get('localeCookie') as string;
const currentLocale = ['en', 'zh_CN'].includes(localeCookie) ? localeCookie : 'zh_CN';
const errorMessageMap = computed(() => errorMap[currentLocale]);
const strengthMap = computed(() => tipMap[currentLocale]);

// Reactive state variables
const errorMessage = ref(t('components.passwordInput.messages.invalidPass'));
const focused = ref(false);
const validLength = ref(false);
const blank = ref(false); // Password contains non-space characters
const group = ref(false); // Contains at least two types of characters
const strengthClass = ref<string>(); // Password strength CSS class
const strengthText = ref<string>(); // Password strength text
const showStrength = ref(false); // Whether to show password strength indicator
const error = ref(false);
const input = ref(false);
const inputValue = ref<string>('');

/**
 * Watch for changes in props.value and update local state
 * Ensures component stays in sync with parent component
 */
watch(() => props.value, (newValue) => {
  inputValue.value = newValue;
}, {
  immediate: true
});

/**
 * Handle input change events
 * Filters out invalid characters and spaces, limits length to 50
 */
const change = (event: Event) => {
  const target = event.target as HTMLInputElement;
  let value = target.value;

  if (input.value) {
    return;
  }

  // Remove all spaces and invalid characters
  value = value?.replace(/\s+/g, '');
  // eslint-disable-next-line no-useless-escape
  inputValue.value = value?.replace(/[^\da-zA-Z@#~`\$%\^&\*-_+=\(\)\}\{\]\[;:'",\.<>?/|!]/g, '').slice(0, 50);
  emit('update:value', inputValue.value);
};

/**
 * Handle Enter key press
 * Emits pressEnter event with current input value
 */
const pressEnter = () => {
  emit('pressEnter', inputValue.value);
};

/**
 * Validate password data against security rules
 * Returns true if password is valid, false otherwise
 */
const validateData = (): boolean => {
  const { code, char } = password.isInvalid(inputValue.value);
  if (code) {
    error.value = true;
    errorMessage.value = char ? `${char} ${errorMessageMap.value[code]}` : errorMessageMap.value[code];
    return false;
  }

  error.value = false;
  return true;
};

/**
 * Watch for changes in input value and update validation state
 * Updates password strength indicators and validation flags
 */
watch(() => inputValue.value, (newValue) => {
  if (!newValue) {
    newValue = '';
  }

  // Validate input characters against password rules
  validateData();

  // Update validation flags
  validLength.value = newValue.length >= 6 && newValue.length <= 50;
  blank.value = newValue.length > 0;
  group.value = password.getTypesNum(newValue.split('')) >= 2;

  // Calculate and update password strength
  const strength = password.calcStrength(newValue);
  strengthClass.value = strength;
  strengthText.value = strengthMap.value[strength];
  showStrength.value = !password.isInvalid(newValue).code;
});

/**
 * Handle blur event
 * Removes focus state and validates data
 */
const blur = () => {
  focused.value = false;
  validateData();
};

/**
 * Handle focus event
 * Sets focus state to true
 */
const focus = () => {
  focused.value = true;
};

/**
 * Handle composition end event for IME input
 * Processes input after IME composition is complete
 */
const compositionend = (e: Event) => {
  input.value = false;
  change(e);
};

/**
 * Handle composition start event for IME input
 * Prevents processing during IME composition
 */
const compositionstart = () => {
  input.value = true;
};

// Expose validation method for parent components
defineExpose({ validateData });
</script>

<template>
  <div class="relative" :class="{'error':error,'focused':focused}">
    <InputPassword
      v-model:value="inputValue"
      :visibilityToggle="true"
      :allowClear="props.allowClear"
      :disabled="props.disabled"
      :placeholder="props.placeholder"
      :trim="true"
      size="large"
      autocomplete="new-password"
      @blur="blur"
      @compositionstart="compositionstart"
      @compositionend="compositionend"
      @change="change"
      @pressEnter="pressEnter"
      @focus="focus">
      <template #prefix>
        <img src="./assets/lock.png" />
      </template>
    </InputPassword>
    <div :class="tipClass" class="tip-container leading-4.5">
      <div class="relative">
        <img
          v-if="validLength"
          class="absolute top-0.5"
          src="./assets/gou.png">
        <img
          v-else
          class="absolute top-0.5"
          src="./assets/gou1.png">
        <div class="ml-7.25 whitespace-pre-wrap">{{ t('components.passwordInput.messages.passLength') }}</div>
      </div>
      <div class="relative mt-2">
        <img
          v-if="group"
          class="absolute top-0.5"
          src="./assets/gou.png">
        <img
          v-else
          class="absolute top-0.5"
          src="./assets/gou1.png">
        <div class="ml-7.25 whitespace-pre-wrap">{{ t('components.passwordInput.messages.passRules') }}@#~`$%^&*)(-_+=}{][;:,.'>?"/|!</div>
      </div>
      <div class="relative mt-2">
        <img
          v-if="blank"
          class="absolute top-0.5"
          src="./assets/gou.png">
        <img
          v-else
          class="absolute top-0.5"
          src="./assets/gou1.png">
        <div class="ml-7.25 whitespace-pre-wrap">{{ t('components.passwordInput.messages.noSpace') }}</div>
      </div>
    </div>
    <div class="error-message">{{ errorMessage }}</div>
    <div
      v-show="showStrength"
      :class="strengthClass"
      class="strength w-full flex items-center flex-no-wrap whitespace-nowrap text-3.5 leading-3 mt-2 px-5">
      <div class="text-tip mr-4 whitespace-nowrap">{{ t('components.passwordInput.messages.strengthDesc',{text:strengthText}) }}</div>
      <div class="w-full h-1 rounded bg-gray-bg overflow-hidden">
        <div class="icon-tip h-full rounded"></div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.weak .text-tip {
  color: #f5222d;
}

.weak .icon-tip {
  width: 33.33%;
  background-color: #f5222d;
}

.medium .text-tip {
  color: #ff8100;
}

.medium .icon-tip {
  width: 66.66%;
  background-color: #ff8100;
}

.strong .text-tip {
  color: #52c41a;
}

.strong .icon-tip {
  width: 100%;
  background-color: #52c41a;
}

.absolute-fixed .strength {
  @apply absolute;

  bottom: -15px;
}
</style>
