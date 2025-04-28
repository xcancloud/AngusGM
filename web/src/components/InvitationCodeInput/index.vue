<script setup lang="ts">
import { ref, watch } from 'vue';

import { Input } from '@xcan-angus/vue-ui';

interface Props {
  value: boolean;
}

withDefaults(defineProps<Props>(), {
  value: false
});

// eslint-disable-next-line func-call-spacing
const emit = defineEmits<{
  (e: 'update:value', value: string | undefined): void;
}>();

const inputValue = ref<string>();
const focused = ref(false);

watch(() => inputValue.value, (newValue) => {
  emit('update:value', newValue);
});

const focus = () => {
  focused.value = true;
};

const blur = () => {
  focused.value = false;
};
</script>

<template>
  <div class="relative" :class="{'focused':focused}">
    <Input
      v-model:value="inputValue"
      :maxlength="50"
      dataType="mixin-en"
      size="large"
      class="w-full"
      :placeholder="$t('invite-code-desc')"
      @blur="blur"
      @focus="focus">
      <template #prefix>
        <img src="./assets/invite.png" />
      </template>
    </Input>
    <div class="tip-container leading-5">
      {{ $t('invite-code-description') }}
    </div>
  </div>
</template>
