<script setup lang="ts">
import { ref, watch } from 'vue';
import { InputPassword } from 'ant-design-vue';

interface Props {
  value: string;
  password: string;
  disabled?: boolean;
  placeholder?: string;
}

const props = withDefaults(defineProps<Props>(), {
  value: '',
  password: '',
  disabled: false,
  placeholder: undefined
});

// eslint-disable-next-line func-call-spacing
const emit = defineEmits<{
  (e: 'update:value', value: string | undefined): void;
  (e: 'focus', evt): void;
  (e: 'pressEnter', value: string | undefined): void;
}>();

const error = ref(false);
const input = ref(false);
const inputValue = ref<string>();

watch(() => props.value, (newValue) => {
  inputValue.value = newValue;
}, { immediate: true });

const compositionend = (e) => {
  input.value = false;
  change(e);
};

const compositionstart = () => {
  input.value = true;
};

const blur = () => {
  validateData();
};

const focus = (e) => {
  emit('focus', e);
};

const validateData = () => {
  error.value = inputValue.value !== props.password;
  return !error.value;
};

const change = (event: any) => {
  const value = event.target.value;
  if (input.value) {
    return;
  }

  validateData();
  inputValue.value = value.replace(/\s+/gi, '').slice(0, 50);
  emit('update:value', inputValue.value);
};

const pressEnter = () => {
  emit('pressEnter', inputValue.value);
};

defineExpose({ validateData });
</script>

<template>
  <div class="relative" :class="{'error':error}">
    <InputPassword
      v-model:value="inputValue"
      :visibilityToggle="true"
      :allowClear="false"
      :disabled="disabled"
      :placeholder="placeholder"
      type="password"
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
    <div class="error-message">{{ $t('pass-no-same') }}</div>
  </div>
</template>
