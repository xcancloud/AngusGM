<script setup lang="ts">
import { Card, Input } from '@xcan-angus/vue-ui';
import { debounce } from 'throttle-debounce';
import { duration } from '@xcan-angus/infra';
import { useI18n } from 'vue-i18n';

import { PasswordPolicy } from '../types';

const { t } = useI18n();

interface Props {
  passwordPolicy: PasswordPolicy;
  loading: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  passwordPolicy: undefined,
  loading: false
});

const emit = defineEmits<{(e: 'change', value: string, type: string): void }>();

/**
 * Handle minimum password length change with debounce
 * Validates input value and emits change event
 */
const minLengthChange = debounce(duration.search, (event: any) => {
  const value = event.target.value;
  if (!value || value === props.passwordPolicy?.minLength || +value < 6 || +value > 50) {
    return;
  }
  emit('change', value, 'minLength');
});
</script>
<template>
  <Card bodyClass="px-8 py-5">
    <template #title>
      <span>{{ t('security.titles.passwordPolicy') }}</span>
    </template>
    <div class="flex items-center text-3 leading-3 text-theme-content">
      {{ t('security.labels.minimumPasswordLength') }}
      <Input
        class="w-20 mx-2"
        size="small"
        dataType="number"
        :value="String((+props.passwordPolicy?.minLength) || '6')"
        :min="6"
        :max="50"
        :disabled="props.loading"
        @change="minLengthChange" />
      {{ t('security.labels.characters') }}，{{ t('security.labels.passwordLengthRangeAllowed') }}6-50{{ t('security.labels.characters') }}
    </div>
    <div class="flex items-center text-3 leading-3 text-theme-content mt-5">
      {{ t('security.messages.passwordPolicyRule') }}
      <span class="font-bold"><pre class="inline"> -=[];\',./~!@#$%^&*()_+{}:"?`</pre></span>
    </div>
  </Card>
</template>
