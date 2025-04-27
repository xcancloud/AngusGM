<script setup lang="ts">
import { ref, watch } from 'vue';
import { cookie } from '@xcan/utils';
import { useI18n } from 'vue-i18n';
import { InputPassword } from 'ant-design-vue';

import password from './password';

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

const emit = defineEmits<{(e: 'update:value', value: string): void, (e: 'pressEnter', value: string): void }>();

const { t } = useI18n();
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
let errorMessageMap = errorMap.zh_CN;
let strengthMap = tipMap.zh_CN;
const localeCookie = cookie.get('localeCookie') as string;
if (['en', 'zh_CN'].includes(localeCookie)) {
  errorMessageMap = errorMap[localeCookie];
  strengthMap = tipMap[localeCookie];
}
const errorMessage = ref(t('invalid-pass'));
const focus = ref(false);

const validLength = ref(false);
const blank = ref(false); // 密码是否包含空格
const group = ref(false); // 至少包含数字和字母两种组合
const strengthClass = ref(); // 密码强度提示class
const strengthText = ref(); // 密码强度提示文字
const showStrength = ref(false); // 是否展示密码强度

const error = ref(false);
const input = ref(false);
const inputValue = ref();

watch(() => props.value, newValue => {
  inputValue.value = newValue;
}, {
  immediate: true
});

const change = (event: any) => {
  let value = event.target.value;
  if (input.value) {
    return;
  }

  value = value?.replace(/\s+/g, '');
  // eslint-disable-next-line no-useless-escape
  inputValue.value = value?.replace(/[^\da-zA-Z@#~`\$%\^&\*-_+=\(\)\}\{\]\[;:'",\.<>?/|!]/g, '').slice(0, 50);
  emit('update:value', inputValue.value);
};

const pressEnter = () => {
  emit('pressEnter', inputValue.value);
};

const validateData = () => {
  const { code, char } = password.isInvalid(inputValue.value);
  if (code) {
    error.value = true;
    errorMessage.value = char ? (char + ' ' + errorMessageMap[code]) : errorMessageMap[code];
    return false;
  }

  error.value = false;
  return true;
};

watch(() => inputValue.value, newValue => {
  if (!newValue) {
    newValue = '';
  }

  validateData();// 校验输入字符是否符合密码规则

  validLength.value = newValue.length >= 6 && newValue.length <= 50;
  blank.value = newValue.length;
  group.value = password.getTypesNum(newValue) >= 2;

  const strength = password.calcStrength(newValue);
  strengthClass.value = strength;
  strengthText.value = strengthMap[strength];
  showStrength.value = !password.isInvalid(newValue).code;
});

const blur = () => {
  focus.value = false;
  validateData();
};

const focus = () => {
  focus.value = true;
};
const compositionend = (e) => {
  input.value = false;
  change(e);
};
const compositionstart = () => {
  input.value = true;
};
defineExpose({ validateData });
</script>

<template>
  <div class="relative" :class="{'error':error,'focus':focus}">
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
        <div class="ml-7.25 whitespace-pre-wrap">{{ t('pass-length') }}</div>
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
        <div class="ml-7.25 whitespace-pre-wrap">{{ t('pass-rules') }}@#~`$%^&*)(-_+=}{][;:,.'>?"/|!</div>
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
        <div class="ml-7.25 whitespace-pre-wrap">{{ t('no-space') }}</div>
      </div>
    </div>
    <div class="error-message">{{ errorMessage }}</div>
    <div
      v-show="showStrength"
      :class="strengthClass"
      class="strength w-full flex items-center flex-no-wrap whitespace-nowrap text-3.5 leading-3 mt-2 px-5">
      <div class="text-tip mr-4 whitespace-nowrap">{{ t('strength-desc',{text:strengthText}) }}</div>
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
