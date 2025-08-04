<script lang="ts" setup>
import { ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { InputPassword } from 'ant-design-vue';

const { t } = useI18n();

interface Props {
  value: string;
  disabled?: boolean;
  placeholder?: string;
  allowClear?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  value: '',
  disabled: false,
  placeholder: '',
  allowClear: false
});

// eslint-disable-next-line func-call-spacing
const emit = defineEmits<{
  (e: 'update:value', value: string): void;
  (e: 'pressEnter'): void;
  (e: 'blur', value: ChangeEvent): void;
  (e: 'focus', value: ChangeEvent): void
}>();

const error = ref(false);
const input = ref(false);
const inputValue = ref();

watch(() => props.value, newValue => {
  inputValue.value = newValue;
}, {
  immediate: true
});

const change = (event: any) => {
  const value = event.target.value;
  if (input.value) {
    return;
  }
  inputValue.value = value.replace(/\s+/gi, '').slice(0, 50);
  emit('update:value', inputValue.value);
};

const pressEnter = () => {
  emit('pressEnter');
};
const compositionend = (e) => {
  input.value = false;
  change(e);
};

const compositionstart = () => {
  input.value = true;
};

const validateData = () => {
  if (!inputValue.value) {
    error.value = true;
    return false;
  }

  error.value = false;
  return true;
};

defineExpose({ validateData });
</script>
<template>
  <div class="relative" :class="{ 'error': error }">
    <InputPassword
      v-model:value="inputValue"
      :visibilityToggle="true"
      :allowClear="props.allowClear"
      :disabled="props.disabled"
      :placeholder="props.placeholder"
      type="password"
      size="large"
      autocomplete="current-password"
      @compositionstart="compositionstart"
      @compositionend="compositionend"
      @change="change"
      @pressEnter="pressEnter">
      <template #prefix>
        <img src="./assets/lock.png" />
      </template>
    </InputPassword>
    <div class="error-message">{{ t('empty-pass') }}</div>
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
</style>
