<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { Icon, IconRequired, Input, PureCard, Select } from '@xcan-angus/vue-ui';
import { app, utils } from '@xcan-angus/tools';
import { Button, Checkbox, Form, FormItem, Switch, Textarea } from 'ant-design-vue';

import { email } from '@/api';
import { useI18n } from 'vue-i18n';
import { FormState } from '../../PropsType';

const { t } = useI18n();
const router = useRouter();
const route = useRoute();
const id = route.params.id as string;
const source = ref(route.query.type as string);

const loading = ref(false);
const formState = ref<FormState>({
  name: '',
  protocol: 'SMTP',
  remark: '',
  enabled: false,
  host: '',
  port: '',
  startTlsEnabled: false,
  sslEnabled: false,
  authEnabled: false,
  authAccount: {
    account: '',
    paasd: ''
  },
  subjectPrefix: ''
});

const oldFormState = ref();

const passType = ref(false);
const init = () => {
  if (source.value !== 'add') {
    loadMailboxDetail();
  }
};

const cancel = function () {
  if (route.query.type === 'detail') {
    source.value = 'detail';
    return;
  }
  router.push('/system/email');
};

// 提交表单
const onFinish = () => {
  if (id) {
    updatedMailboxService();
    return;
  }
  addMailboxService();
};

const addMailboxService = async () => {
  if (loading.value) {
    return;
  }
  const { authAccount, ...others } = formState.value;
  const params = formState.value.authEnabled ? { authAccount, ...others } : others;
  loading.value = true;
  const [error] = await email.addServer(params);
  loading.value = false;
  if (error) {
    return;
  }
  cancel();
};

const updatedMailboxService = async () => {
  if (loading.value) {
    return;
  }
  const isEqual = utils.deepCompare(oldFormState.value, formState.value);
  if (isEqual) {
    cancel();
    return;
  }

  const { authAccount, ...others } = formState.value;
  const params = formState.value.authEnabled ? { authAccount, ...others } : others;
  loading.value = true;
  const [error] = await email.replaceServer({ ...params, id: id });
  loading.value = false;
  if (error) {
    return;
  }
  cancel();
};

const loadMailboxDetail = async () => {
  loading.value = true;
  const [error, { data }] = await email.getServerDetail(id);
  loading.value = false;
  if (error) {
    return;
  }
  formState.value = {
    ...data,
    authAccount: data.authAccount || {
      account: '',
      paasd: ''
    }
  };

  oldFormState.value = JSON.parse(JSON.stringify(formState.value));
};

const openEdit = () => {
  source.value = 'edit';
};

onMounted(async () => {
  init();
});

const selectOption = [
  { label: 'IMAP', value: 'IMAP' },
  { label: 'POP', value: 'POP' },
  { label: 'SMTP', value: 'SMTP' }
];

</script>
<template>
  <PureCard class="h-full p-3.5">
    <div class="text-right h-6">
      <Button
        v-if="source==='detail'"
        :disbaled="!app.has('MailServerModify')"
        type="primary"
        size="small"
        class="px-4"
        @click="openEdit">
        {{ app.getName('MailServerModify') }}
      </Button>
    </div>
    <Form
      :model="formState"
      class="flex mx-auto w-150"
      @finish="onFinish">
      <div class="mr-5 text-3">
        <div class="h-7 leading-7 mb-5">
          <IconRequired class="mr-0.5" />
          {{ t('name') }}
        </div>
        <div class="h-7 leading-7 mb-5">
          <IconRequired class="mr-0.5" />
          {{ t('protocol') }}
        </div>
        <div class="h-7 leading-7 mb-5">
          <IconRequired class="mr-0.5" />
          {{ t('address') }}
        </div>
        <div class="h-7 leading-7 mb-5">
          <IconRequired class="mr-0.5" />
          {{ t('port') }}
        </div>
        <div class="h-7 leading-7 mb-5">
          <IconRequired class="mr-0.5" />
          {{ t('openCertification') }}
        </div>
        <div class="h-7 leading-7 mb-5  flex">
          <div class="w-2.25">
            <IconRequired v-if="formState.authEnabled" class="mr-0.5" />
          </div>
          {{ t('userName') }}
        </div>
        <div class="h-7 leading-7 mb-5 flex">
          <div class="w-2.25">
            <IconRequired v-if="formState.authEnabled" class="mr-0.5" />
          </div>
          {{ t('password') }}
        </div>
        <div class="h-7 leading-7 mb-5 pl-2.25">{{ t('connectionSecurity') }}</div>
        <div class="h-7 leading-7 mb-5 pl-2.25">{{ t('prefix') }}</div>
        <div class="h-7 leading-7 mb-5 pl-2.25">{{ t('remark') }}</div>
      </div>
      <div class="flex-1">
        <FormItem
          name="name"
          :rules="{ required: true, message: t('addNameTip') }">
          <Input
            v-model:value="formState.name"
            size="small"
            :disabled="source==='detail'"
            :maxlength="100"
            :placeholder="t('emailPlaceholder1')" />
        </FormItem>
        <FormItem name="protocol">
          <Select
            v-model:value="formState.protocol"
            size="small"
            internal
            :options="selectOption"
            disabled />
        </FormItem>
        <FormItem name="host" :rules="{ required: true, message: t('rule2') }">
          <Input
            v-model:value="formState.host"
            size="small"
            :disabled="source==='detail'"
            :maxlength="400"
            :placeholder="t('emailPlaceholder2')" />
        </FormItem>
        <FormItem name="port" :rules="{ required: true, message: t('rulePort') }">
          <Input
            v-model:value="formState.port"
            size="small"
            :disabled="source==='detail'"
            dataType="number"
            :min="1"
            :max="65535"
            :placeholder="t('emailPlaceholder3')" />
        </FormItem>
        <FormItem name="authEnabled" class="h-7 leading-7">
          <Switch
            v-model:checked="formState.authEnabled"
            size="small"
            class="-mt-0.5"
            :disabled="source==='detail'" />
        </FormItem>
        <FormItem
          :name="['authAccount', 'account']"
          :rules="{ required: formState.authEnabled, message: t('userRule11') }">
          <Input
            v-model:value="formState.authAccount.account"
            size="small"
            :maxlength="100"
            :disabled="source==='detail' || !formState.authEnabled"
            :placeholder="t('emailPlaceholder4')" />
        </FormItem>
        <FormItem
          :name="['authAccount', 'paasd']"
          :rules="{ required: formState.authEnabled, message: t('userRule0') }">
          <Input
            v-model:value="formState.authAccount.paasd"
            size="small"
            :maxlength="500"
            :disabled="source === 'detail' || !formState.authEnabled"
            :type="passType ? 'text' : 'password'"
            :placeholder="t('emailPlaceholder5')">
            <template #suffix>
              <Icon
                :icon="passType ? 'icon-zhengyan' : 'icon-biyan'"
                class="cursor-pointer"
                @click="passType = !passType" />
            </template>
          </Input>
        </FormItem>
        <FormItem class="h-7 leading-7">
          <div class="flex items-center mt-0.5">
            <Checkbox
              v-model:checked="formState.sslEnabled"
              size="small"
              class="text-3 leading-3"
              :disabled="source==='detail'">
              SSL
            </Checkbox>
            <Checkbox
              v-model:checked="formState.startTlsEnabled"
              size="small"
              class="ml-5 text-3 leading-3"
              :disabled="source==='detail'">
              StartTls
            </Checkbox>
          </div>
        </FormItem>
        <FormItem name="subjectPrefix">
          <Input
            v-model:value="formState.subjectPrefix"
            size="small"
            :maxlength="200"
            :disabled="source==='detail'"
            :placeholder="t('emailPlaceholder6')" />
        </FormItem>
        <FormItem name="remark">
          <Textarea
            v-model:value="formState.remark"
            size="small"
            :maxlength="200"
            :disabled="source==='detail'"
            :placeholder="t('remark')" />
        </FormItem>
        <FormItem v-if="source!=='detail'" class="pl-50">
          <Button
            type="primary"
            class="mr-5 px-3"
            size="small"
            htmlType="submit"
            :loading="loading">
            {{ t('save') }}
          </Button>
          <Button
            size="small"
            class="px-3"
            @click="cancel">
            {{ t('cancel') }}
          </Button>
        </FormItem>
      </div>
    </Form>
  </PureCard>
</template>
