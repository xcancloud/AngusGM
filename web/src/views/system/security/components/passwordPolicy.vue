<script setup lang="ts">
import { Input, Card } from '@xcan-angus/vue-ui';
import { debounce } from 'throttle-debounce';
import { duration } from '@xcan-angus/infra';

import { PasswordPolicy } from '../PropsType';

interface Props {
  passwordPolicy: PasswordPolicy;
  loading: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  passwordPolicy: undefined,
  loading: false
});

const emit = defineEmits<{(e: 'change', value: string, type: string): void }>();

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
      <span>密码策略</span>
    </template>
    <div class="flex items-center text-3 leading-3 text-theme-content">
      密码最小长度
      <Input
        class="w-20 mx-2"
        size="small"
        dataType="number"
        :value="(+props.passwordPolicy?.minLength)|| '6'"
        :min="6"
        :max="50"
        :disabled="props.loading"
        @change="minLengthChange" />
      位，密码长度范围允许为6-50位。
    </div>
    <div class="flex items-center text-3 leading-3 text-theme-content mt-5">
      强制要求密码字符类型至少包含“大写字母、小写字母、数字、特殊符号”中的两种组合，其中特殊符号包括“-=[];',./~!@#$%^&*()_+{}:"&lt;
      >?”。
    </div>
  </Card>
</template>
