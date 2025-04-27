<script setup lang="ts">
import { computed, ref, onMounted } from 'vue';
import { useI18n } from 'vue-i18n';
import { Button } from 'ant-design-vue';
import { DatePicker, Card, Input, Icon, Hints } from '@xcan-angus/vue-ui';
import { http, GM } from '@xcan-angus/tools';

import { userToken } from '@/api';
import TokenTable from './Table.vue';

const { t } = useI18n();
const prevName = ref('');
const name = ref('');
const password = ref('');
const expireDate = ref('');
const token = ref('');
const total = ref(0);
const isCopied = ref(false);
const confirmLoading = ref(false);

const tableChange = (_total: number) => {
  total.value = _total;
};

const reset = (): void => {
  name.value = '';
  password.value = '';
  expireDate.value = '';
};

const ok = async () => {
  const params = {
    name: name.value,
    expireDate: expireDate.value,
    password: password.value
  };

  confirmLoading.value = true;
  const [error, res] = await userToken.addToken(params);
  confirmLoading.value = false;
  if (error || !res?.data) {
    return;
  }

  token.value = res.data.value;
  prevName.value = name.value;
  isCopied.value = false;
  reset();
};

const disabled = computed(() => {
  return (!name.value || prevName.value === name.value || total.value >= tokenQuota.value || !password.value);
});

const tokenQuota = ref(0);
const getTokenQuota = async () => {
  // TODO 路径迁移到apis
  const [error, { data }] = await http.get(`${GM}/setting/tenant/quota/UserToken`);
  if (error) {
    return;
  }
  tokenQuota.value = +data.quota;
};

onMounted(() => {
  getTokenQuota();
});
</script>

<template>
  <div class="w-11/12 mx-auto">
    <div class="items-center pb-2 border-b border-theme-divider">
      <span class="text-3.5 text-theme-title">{{ t('personalCenter.token.accessToken') }}</span>
    </div>
    <Hints :text="t('personalCenter.token.tokenDescription', { n: tokenQuota })" class="pt-2" />
    <Card class="mt-2" bodyClass="px-8">
      <template #title>{{ t('personalCenter.token.addToken') }}</template>
      <div class="flex mt-3">
        <div class="flex-free-half">
          <div class="w-112.5">
            <span class="flex items-center text-3-multi text-theme-title">
              <Icon class="mr-1 text-danger text-3.5" icon="icon-xinghao" />
              {{ t('personalCenter.token.tokenName') }}
            </span>
            <Input
              v-model:value="name"
              class="mt-2.5"
              type="free-trim"
              :placeholder="t('personalCenter.token.placeholder')"
              :maxlength="100" />
          </div>
          <div class="w-112.5 mt-9">
            <span class="flex items-center text-3-multi text-theme-title">{{ t('personalCenter.token.expireTimeDescription') }}</span>
            <DatePicker
              v-model:value="expireDate"
              className="w-full"
              class="w-full mt-2.5"
              showTime
              :placeholder="t('personalCenter.token.expireTime')" />
          </div>
        </div>
        <div class="flex-free-half">
          <!-- 用于阻止浏览器给 datePicker 的 input 弹出自动填充框 -->
          <input class="h-0 block" />
          <div class="w-112.5">
            <span class="flex items-center text-3-multi text-theme-title">
              <Icon class="mr-1 text-danger text-3.5" icon="icon-xinghao" />
              <span>登录密码</span>
            </span>
            <Input
              v-model:value="password"
              class="mt-2.5"
              type="password"
              dataType="free"
              :allowClear="false"
              :placeholder="t('personalCenter.token.typeSignPassword')"
              :trimAll="true"
              :maxlength="50" />
          </div>
        </div>
      </div>
      <div class="flex my-10">
        <Button
          size="small"
          type="primary"
          :disabled="disabled"
          :loading="confirmLoading"
          @click="ok">
          <Icon class="mr-1 text-white text-3.5" icon="icon-tianjia" />
          {{ t('personalCenter.token.createToken') }}
        </Button>
      </div>
    </Card>
    <TokenTable
      :notify="token"
      :tokenQuota="tokenQuota"
      @change="tableChange" />
  </div>
</template>
<style scoped>
.ant-btn[disabled] > * {
  @apply text-gray-placeholder;
}

.position-left-20 {
  left: calc(100% + 20px);
}
</style>
