<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { Icon, IconRequired, Input, PureCard, Select } from '@xcan-angus/vue-ui';
import { app, utils } from '@xcan-angus/infra';
import { Button, Checkbox, Form, FormItem, Switch, Textarea } from 'ant-design-vue';

import { email } from '@/api';
import { useI18n } from 'vue-i18n';
import { FormState, Protocol } from '../types';

const { t } = useI18n();
const router = useRouter();
const route = useRoute();

// Route parameters and query
const id = route.params.id as string;
const source = ref(route.query.type as string);

// Reactive state management
const loading = ref(false);
const passType = ref(false);

// Form state with default values
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
    password: ''
  },
  subjectPrefix: ''
});

// Store original form state for comparison
const oldFormState = ref<FormState>();

// Protocol options for the select dropdown
const PROTOCOL_OPTIONS = [
  { label: 'IMAP', value: 'IMAP' as Protocol },
  { label: 'POP', value: 'POP' as Protocol },
  { label: 'SMTP', value: 'SMTP' as Protocol }
];

// Form validation rules
const FORM_RULES = {
  name: { required: true, message: t('email.messages.addNameTip') },
  host: { required: true, message: t('email.messages.rule2') },
  port: { required: true, message: t('email.messages.rulePort') },
  authAccount: { required: true, message: t('email.messages.userRule11') },
  authPassword: { required: true, message: t('email.messages.userRule0') }
};

/**
 * Initialize component based on source type
 */
const init = (): void => {
  if (source.value !== 'add') {
    loadMailboxDetail();
  }
};

/**
 * Handle form submission based on operation type
 */
const onFinish = (): void => {
  if (id) {
    updateMailboxService();
  } else {
    addMailboxService();
  }
};

/**
 * Add new mailbox service
 */
const addMailboxService = async (): Promise<void> => {
  if (loading.value) return;

  try {
    const { authAccount, ...others } = formState.value;
    const params = formState.value.authEnabled ? { authAccount, ...others } : others;

    loading.value = true;
    const [error] = await email.addServer(params);

    if (error) {
      console.error('Failed to add mailbox service:', error);
      return;
    }

    cancel();
  } catch (err) {
    console.error('Unexpected error adding mailbox service:', err);
  } finally {
    loading.value = false;
  }
};

/**
 * Update existing mailbox service
 */
const updateMailboxService = async (): Promise<void> => {
  if (loading.value) return;

  try {
    // Check if form has changed
    const isEqual = utils.deepCompare(oldFormState.value, formState.value);
    if (isEqual) {
      cancel();
      return;
    }

    const { authAccount, ...others } = formState.value;
    const params = formState.value.authEnabled ? { authAccount, ...others } : others;

    loading.value = true;
    const [error] = await email.replaceServer({ ...params, id });

    if (error) {
      console.error('Failed to update mailbox service:', error);
      return;
    }

    cancel();
  } catch (err) {
    console.error('Unexpected error updating mailbox service:', err);
  } finally {
    loading.value = false;
  }
};

/**
 * Load mailbox service details for editing
 */
const loadMailboxDetail = async (): Promise<void> => {
  loading.value = true;
  try {
    const [error, { data }] = await email.getServerDetail(id);

    if (error) {
      console.error('Failed to load mailbox service details:', error);
      return;
    }

    // Set form state with loaded data
    formState.value = {
      ...data,
      authAccount: data.authAccount || {
        account: '',
        password: ''
      }
    };

    // Store original state for comparison
    oldFormState.value = JSON.parse(JSON.stringify(formState.value));
  } catch (err) {
    console.error('Unexpected error loading mailbox service details:', err);
  } finally {
    loading.value = false;
  }
};

/**
 * Switch to edit mode from detail view
 */
const openEdit = (): void => {
  source.value = 'edit';
};

/**
 * Handle cancel action based on current mode
 */
const cancel = (): void => {
  if (route.query.type === 'detail') {
    source.value = 'detail';
    return;
  }
  router.push('/system/email');
};

// Initialize component on mount
onMounted(() => {
  init();
});
</script>

<template>
  <PureCard class="h-full p-3.5">
    <!-- Edit button for detail view -->
    <div class="text-right h-6">
      <Button
        v-if="source === 'detail'"
        :disabled="!app.has('MailServerModify')"
        type="primary"
        size="small"
        class="px-4"
        @click="openEdit">
        {{ app.getName('MailServerModify') }}
      </Button>
    </div>

    <!-- Mailbox service configuration form -->
    <Form
      :model="formState"
      class="flex mx-auto w-150"
      @finish="onFinish">
      <!-- Form labels column -->
      <div class="mr-5 text-3">
        <div class="h-7 leading-7 mb-5">
          <IconRequired class="mr-0.5" />
          {{ t('email.labels.name') }}
        </div>
        <div class="h-7 leading-7 mb-5">
          <IconRequired class="mr-0.5" />
          {{ t('email.labels.protocol') }}
        </div>
        <div class="h-7 leading-7 mb-5">
          <IconRequired class="mr-0.5" />
          {{ t('email.labels.host') }}
        </div>
        <div class="h-7 leading-7 mb-5">
          <IconRequired class="mr-0.5" />
          {{ t('email.labels.port') }}
        </div>
        <div class="h-7 leading-7 mb-5">
          {{ t('email.messages.openCertification') }}
        </div>
        <div class="h-7 leading-7 mb-5 flex">
          <div class="w-2.25">
            <IconRequired v-if="formState.authEnabled" class="mr-0.5" />
          </div>
          {{ t('email.labels.authAccount') }}
        </div>
        <div class="h-7 leading-7 mb-5 flex">
          <div class="w-2.25">
            <IconRequired v-if="formState.authEnabled" class="mr-0.5" />
          </div>
          {{ t('email.labels.authPassword') }}
        </div>
        <div class="h-7 leading-7 mb-5 pl-2.25">
          {{ t('email.messages.connectionSecurity') }}
        </div>
        <div class="h-7 leading-7 mb-5 pl-2.25">
          {{ t('email.labels.subjectPrefix') }}
        </div>
        <div class="h-7 leading-7 mb-5 pl-2.25">
          {{ t('email.labels.remark') }}
        </div>
      </div>

      <!-- Form inputs column -->
      <div class="flex-1">
        <!-- Service name input -->
        <FormItem
          name="name"
          :rules="FORM_RULES.name">
          <Input
            v-model:value="formState.name"
            size="small"
            :disabled="source === 'detail'"
            :maxlength="100"
            :placeholder="t('email.placeholder.emailPlaceholder1')" />
        </FormItem>

        <!-- Protocol selection -->
        <FormItem name="protocol">
          <Select
            v-model:value="formState.protocol"
            size="small"
            internal
            :options="PROTOCOL_OPTIONS"
            :disabled="source === 'detail'" />
        </FormItem>

        <!-- Host input -->
        <FormItem
          name="host"
          :rules="FORM_RULES.host">
          <Input
            v-model:value="formState.host"
            size="small"
            :disabled="source === 'detail'"
            :maxlength="400"
            :placeholder="t('email.placeholder.emailPlaceholder2')" />
        </FormItem>

        <!-- Port input -->
        <FormItem
          name="port"
          :rules="FORM_RULES.port">
          <Input
            v-model:value="formState.port"
            size="small"
            :disabled="source === 'detail'"
            dataType="number"
            :min="1"
            :max="65535"
            :placeholder="t('email.placeholder.emailPlaceholder3')" />
        </FormItem>

        <!-- Authentication toggle -->
        <FormItem name="authEnabled" class="h-7 leading-7">
          <Switch
            v-model:checked="formState.authEnabled"
            size="small"
            class="-mt-0.5"
            :disabled="source === 'detail'" />
        </FormItem>

        <!-- Authentication account input -->
        <FormItem
          :name="['authAccount', 'account']"
          :rules="{ required: formState.authEnabled, message: FORM_RULES.authAccount.message }">
          <Input
            v-model:value="formState.authAccount.account"
            size="small"
            :maxlength="100"
            :disabled="source === 'detail' || !formState.authEnabled"
            :placeholder="t('email.placeholder.emailPlaceholder4')" />
        </FormItem>

        <!-- Authentication password input -->
        <FormItem
          :name="['authAccount', 'password']"
          :rules="{ required: formState.authEnabled, message: FORM_RULES.authPassword.message }">
          <Input
            v-model:value="formState.authAccount.password"
            size="small"
            :maxlength="500"
            :disabled="source === 'detail' || !formState.authEnabled"
            :type="passType ? 'input' : 'password'"
            :placeholder="t('email.placeholder.emailPlaceholder5')">
            <template #suffix>
              <Icon
                :icon="passType ? 'icon-zhengyan' : 'icon-biyan'"
                class="cursor-pointer"
                @click="passType = !passType" />
            </template>
          </Input>
        </FormItem>

        <!-- Security options -->
        <FormItem class="h-7 leading-7">
          <div class="flex items-center mt-0.5">
            <Checkbox
              v-model:checked="formState.sslEnabled"
              size="small"
              class="text-3 leading-3"
              :disabled="source === 'detail'">
              SSL
            </Checkbox>
            <Checkbox
              v-model:checked="formState.startTlsEnabled"
              size="small"
              class="ml-5 text-3 leading-3"
              :disabled="source === 'detail'">
              StartTls
            </Checkbox>
          </div>
        </FormItem>

        <!-- Subject prefix input -->
        <FormItem name="subjectPrefix">
          <Input
            v-model:value="formState.subjectPrefix"
            size="small"
            :maxlength="200"
            :disabled="source === 'detail'"
            :placeholder="t('email.placeholder.emailPlaceholder6')" />
        </FormItem>

        <!-- Remark textarea -->
        <FormItem name="remark">
          <Textarea
            v-model:value="formState.remark"
            size="small"
            :maxlength="200"
            :disabled="source === 'detail'"
            :placeholder="t('email.labels.remark')" />
        </FormItem>

        <!-- Action buttons -->
        <FormItem v-if="source !== 'detail'" class="pl-50">
          <Button
            type="primary"
            class="mr-5 px-3"
            size="small"
            htmlType="submit"
            :loading="loading">
            {{ t('email.messages.save') }}
          </Button>
          <Button
            size="small"
            class="px-3"
            @click="cancel">
            {{ t('email.messages.cancel') }}
          </Button>
        </FormItem>
      </div>
    </Form>
  </PureCard>
</template>
