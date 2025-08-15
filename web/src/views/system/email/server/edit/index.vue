<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { Icon, IconRequired, Input, PureCard, Select } from '@xcan-angus/vue-ui';
import { app } from '@xcan-angus/infra';
import { Button, Checkbox, Form, FormItem, Switch, Textarea } from 'ant-design-vue';

import { email } from '@/api';
import { useI18n } from 'vue-i18n';
import { EditState } from '../types';
import {
  createProtocolOptions, createFormRules, createInitialFormState, createAddServerRequest,
  createReplaceServerRequest, hasFormChanges, formatFormDataForDisplay, isAuthenticationRequired
} from '../utils';

const { t } = useI18n();
const router = useRouter();
const route = useRoute();

// Route parameters and query
const id = route.params.id as string;
const source = ref(route.query.type as string);

// Component state management
const state = reactive<EditState>({
  loading: false,
  passType: false,
  formState: createInitialFormState(),
  oldFormState: undefined
});

// Protocol options for the select dropdown
const protocolOptions = ref(createProtocolOptions());

// Form validation rules
const formRules = ref(createFormRules(t));

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
  if (state.loading) return;

  try {
    const params = createAddServerRequest(state.formState);

    state.loading = true;
    const [error] = await email.addServer(params);

    if (error) {
      console.error('Failed to add mailbox service:', error);
      return;
    }

    cancel();
  } catch (err) {
    console.error('Unexpected error adding mailbox service:', err);
  } finally {
    state.loading = false;
  }
};

/**
 * Update existing mailbox service
 */
const updateMailboxService = async (): Promise<void> => {
  if (state.loading) return;

  try {
    // Check if form has changed
    if (!hasFormChanges(state.formState, state.oldFormState)) {
      cancel();
      return;
    }

    const params = createReplaceServerRequest(state.formState, id);

    state.loading = true;
    const [error] = await email.replaceServer(params);

    if (error) {
      console.error('Failed to update mailbox service:', error);
      return;
    }

    cancel();
  } catch (err) {
    console.error('Unexpected error updating mailbox service:', err);
  } finally {
    state.loading = false;
  }
};

/**
 * Load mailbox service details for editing
 */
const loadMailboxDetail = async (): Promise<void> => {
  try {
    state.loading = true;
    const [error, response] = await email.getServerDetail(id);

    if (error) {
      console.error('Failed to load mailbox service details:', error);
      return;
    }

    // Set form state with loaded data
    state.formState = formatFormDataForDisplay(response.data);

    // Store original state for comparison
    state.oldFormState = JSON.parse(JSON.stringify(state.formState));
  } catch (err) {
    console.error('Unexpected error loading mailbox service details:', err);
  } finally {
    state.loading = false;
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
      :model="state.formState"
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
            <IconRequired v-if="isAuthenticationRequired(state.formState)" class="mr-0.5" />
          </div>
          {{ t('email.labels.authAccount') }}
        </div>
        <div class="h-7 leading-7 mb-5 flex">
          <div class="w-2.25">
            <IconRequired v-if="isAuthenticationRequired(state.formState)" class="mr-0.5" />
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
          :rules="formRules.name">
          <Input
            v-model:value="state.formState.name"
            size="small"
            :disabled="source === 'detail'"
            :maxlength="100"
            :placeholder="t('email.placeholder.emailPlaceholder1')" />
        </FormItem>

        <!-- Protocol selection -->
        <FormItem name="protocol">
          <Select
            v-model:value="state.formState.protocol"
            size="small"
            internal
            :options="protocolOptions"
            :disabled="source === 'detail'" />
        </FormItem>

        <!-- Host input -->
        <FormItem
          name="host"
          :rules="formRules.host">
          <Input
            v-model:value="state.formState.host"
            size="small"
            :disabled="source === 'detail'"
            :maxlength="400"
            :placeholder="t('email.placeholder.emailPlaceholder2')" />
        </FormItem>

        <!-- Port input -->
        <FormItem
          name="port"
          :rules="formRules.port">
          <Input
            v-model:value="state.formState.port"
            size="small"
            dataType="number"
            :min="1"
            :max="65535"
            :disabled="source === 'detail'"
            :placeholder="t('email.placeholder.emailPlaceholder3')" />
        </FormItem>

        <!-- Authentication toggle -->
        <FormItem name="authEnabled" class="h-7 leading-7">
          <Switch
            v-model:checked="state.formState.authEnabled"
            size="small"
            class="-mt-0.5"
            :disabled="source === 'detail'" />
        </FormItem>

        <!-- Authentication account input -->
        <FormItem
          :name="['authAccount', 'account']"
          :rules="{
            required: isAuthenticationRequired(state.formState),
            message: formRules.authAccount.message
          }">
          <Input
            v-model:value="state.formState.authAccount.account"
            size="small"
            :maxlength="100"
            :disabled="source === 'detail' || !isAuthenticationRequired(state.formState)"
            :placeholder="t('email.placeholder.emailPlaceholder4')" />
        </FormItem>

        <!-- Authentication password input -->
        <FormItem
          :name="['authAccount', 'password']"
          :rules="{
            required: isAuthenticationRequired(state.formState),
            message: formRules.authPassword.message
          }">
          <Input
            v-model:value="state.formState.authAccount.password"
            size="small"
            :maxlength="500"
            :disabled="source === 'detail' || !isAuthenticationRequired(state.formState)"
            :type="state.passType ? 'input' : 'password'"
            :placeholder="t('email.placeholder.emailPlaceholder5')">
            <template #suffix>
              <Icon
                :icon="state.passType ? 'icon-zhengyan' : 'icon-biyan'"
                class="cursor-pointer"
                @click="state.passType = !state.passType" />
            </template>
          </Input>
        </FormItem>

        <!-- Security options -->
        <FormItem class="h-7 leading-7">
          <div class="flex items-center mt-0.5">
            <Checkbox
              v-model:checked="state.formState.sslEnabled"
              size="small"
              class="text-3 leading-3"
              :disabled="source === 'detail'">
              SSL
            </Checkbox>
            <Checkbox
              v-model:checked="state.formState.startTlsEnabled"
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
            v-model:value="state.formState.subjectPrefix"
            size="small"
            :maxlength="200"
            :disabled="source === 'detail'"
            :placeholder="t('email.placeholder.emailPlaceholder6')" />
        </FormItem>

        <!-- Remark textarea -->
        <FormItem name="remark">
          <Textarea
            v-model:value="state.formState.remark"
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
            :loading="state.loading">
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
