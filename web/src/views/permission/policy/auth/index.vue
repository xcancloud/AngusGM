<script setup lang='ts'>
import { onMounted, reactive, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { Button, Form, FormItem, Radio, RadioGroup } from 'ant-design-vue';
import { Input, notification, PureCard, SelectDept, SelectGroup, SelectUser } from '@xcan-angus/vue-ui';

import { auth } from '@/api';
import { AuthFormState } from '../types';

const route = useRoute();
const router = useRouter();
const { t } = useI18n();

/**
 * Reference to the form component
 * Used for form validation
 */
const formRef = ref();

/**
 * Component reactive state
 * Manages loading states, form data, and validation rules
 */
const state = reactive<AuthFormState>({
  loading: false,
  saving: false,
  form: {
    id: '',
    name: '',
    appId: '',
    targetType: 'USER',
    targetId: []
  },
  rules: {
    targetId: [
      {
        required: true,
        message: t('permission.policy.auth.rule'),
        trigger: 'change'
      }
    ]
  }
});

/**
 * Load policy details for authorization
 * Fetches policy information to populate the form
 */
const load = async () => {
  state.loading = true;
  const [error, res] = await auth.getPolicyDetail(route.params.id as string);
  state.loading = false;
  if (error) {
    return;
  }

  state.form.id = res.data.id;
  state.form.name = res.data.name;
  state.form.appId = res.data.appId;
};

/**
 * Handle target selection changes
 * Updates the form with selected target identifiers
 * @param {string[]} value - Array of selected target IDs
 */
const targetChange = (value: string[]) => {
  state.form.targetId = value;
};

/**
 * Navigate back to policy list
 * Redirects user to the main policy management page
 */
const linkToList = () => {
  router.push('/permissions/policy');
};

/**
 * Save authorization changes
 * Validates form and submits authorization request based on target type
 */
const save = () => {
  formRef.value.validate().then(async () => {
    let res: [Error | null, any];
    const ids = state.form.targetId;
    state.saving = true;
    if (state.form.targetType === 'USER') {
      res = await auth.addPolicyUser(state.form.id, ids);
    } else if (state.form.targetType === 'DEPT') {
      res = await auth.addPolicyDept(state.form.id, ids);
    } else {
      res = await auth.addPolicyGroup(state.form.id, ids);
    }

    const [error] = res;
    state.saving = false;
    if (error) {
      return;
    }

    notification.success(t('permission.policy.auth.success'));
    linkToList();
  });
};

/**
 * Initialize component data on mount
 * Loads policy details when component is mounted
 */
onMounted(() => {
  load();
});
</script>

<template>
  <!-- Authorization management page for policy targets -->
  <PureCard class="flex-1 py-10">
    <!-- Authorization form -->
    <Form
      ref="formRef"
      class="w-1/2 my-0 mx-auto"
      :model="state.form"
      :rules="state.rules"
      v-bind="{labelCol: {span: 5}, wrapperCol: {span: 18}}">
      <!-- Policy name display (read-only) -->
      <FormItem :label="t('permission.policy.auth.name')" name="name">
        <Input
          disabled
          :value="state.form.name"
          size="small" />
      </FormItem>

      <!-- Target type selection -->
      <FormItem :label="t('permission.policy.auth.targetType')" name="targetType">
        <RadioGroup v-model:value="state.form.targetType">
          <Radio value="USER">{{ t('permission.policy.auth.user') }}</Radio>
          <Radio value="DEPT">{{ t('permission.policy.auth.dept') }}</Radio>
          <Radio value="GROUP">{{ t('permission.policy.auth.group') }}</Radio>
        </RadioGroup>
      </FormItem>

      <!-- User selection (conditional) -->
      <FormItem
        v-if="state.form.targetType === 'USER'"
        :label="t('permission.policy.auth.userLabel')"
        name="targetId">
        <SelectUser
          :placeholder="t('permission.policy.auth.userPlaceholder')"
          :allowClear="false"
          :internal="true"
          mode="multiple"
          size="small"
          @change="targetChange" />
      </FormItem>

      <!-- Department selection (conditional) -->
      <FormItem
        v-if="state.form.targetType === 'DEPT'"
        :label="t('permission.policy.auth.deptLabel')"
        name="targetId">
        <SelectDept
          :placeholder="t('permission.policy.auth.deptPlaceholder')"
          :allowClear="false"
          :internal="true"
          mode="multiple"
          size="small"
          @change="targetChange" />
      </FormItem>

      <!-- Group selection (conditional) -->
      <FormItem
        v-if="state.form.targetType === 'GROUP'"
        :label="t('permission.policy.auth.groupLabel')"
        name="targetId">
        <SelectGroup
          :placeholder="t('permission.policy.auth.groupPlaceholder')"
          :allowClear="false"
          :internal="true"
          mode="multiple"
          size="small"
          @change="targetChange" />
      </FormItem>

      <!-- Action buttons -->
      <FormItem label=" " :colon="false">
        <Button
          size="small"
          type="primary"
          :loading="state.saving"
          @click="save">
          {{ t('permission.policy.auth.auth') }}
        </Button>
        <Button
          size="small"
          class="ml-5"
          @click="linkToList">
          {{ t('permission.policy.auth.cancel') }}
        </Button>
      </FormItem>
    </Form>
  </PureCard>
</template>
