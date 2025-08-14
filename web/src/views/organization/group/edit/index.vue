<script setup lang='ts'>
import { onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { Button, Form, FormItem, Textarea } from 'ant-design-vue';
import { Colon, Hints, IconRequired, Input, PureCard, Select } from '@xcan-angus/vue-ui';
import { GM, utils } from '@xcan-angus/infra';

import { Detail, FormState } from '../types';
import {
  loadGroupDetail as loadGroupDetailUtil, loadGroupTagList as loadGroupTagListUtil,
  addGroup as addGroupUtil, editGroup as editGroupUtil
} from '../utils';

const { t } = useI18n();
const route = useRoute();
const router = useRouter();

/**
 * Current group identifier from route params.
 * - Presence indicates Edit mode; absence indicates Add mode.
 */
const groupId = route.params.id as string;

/**
 * From which page user navigated. Used to decide where to go after submit/cancel.
 */
const source = route.query.source as string;

/**
 * Group detail data while editing. Used to hydrate form fields.
 */
const groupDetail = ref<Detail>();

/**
 * Load group detail by id and sync it into the form state.
 * Ensures `tagIds` is mapped from tag objects to id array.
 */
const loadGroupDetail = async () => {
  const data = await loadGroupDetailUtil(groupId);
  if (!data) {
    return;
  }

  groupDetail.value = data;
  // Map all form fields from loaded data, handling special case for tags
  Object.keys(formState.value).every(item => {
    if (item === 'tagIds') {
      formState.value[item] = data?.tags?.map(item => item.id);
    } else {
      formState.value[item] = data[item];
    }
    return true;
  });
  // Create deep copy for change detection
  oldFormState.value = JSON.parse(JSON.stringify(formState.value));
};

/**
 * Two snapshots of form data:
 * - `formState`: current editable fields
 * - `oldFormState`: original snapshot used to detect changes on save
 */
const formState = ref<FormState>({ code: '', name: '', remark: '', tagIds: [] });
const oldFormState = ref<FormState>({ code: '', name: '', remark: '', tagIds: [] });

/** Global submit/loading guard */
const loading = ref(false);

/**
 * Create a new group by submitting current form state.
 * Redirects to group list page on success.
 */
const addGroup = async () => {
  if (loading.value) {
    return;
  }
  loading.value = true;
  const result = await addGroupUtil(formState.value, t);
  loading.value = false;
  if (result) {
    await router.push('/organization/group');
  }
};

/**
 * Update an existing group when form content actually changed compared to original.
 * Uses deep comparison to detect real changes and avoid unnecessary API calls.
 */
const editGroup = async () => {
  if (loading.value) {
    return;
  }

  // Check if form has actually changed before submitting
  const isEqual = utils.deepCompare(oldFormState.value, formState.value);
  if (isEqual) {
    // No changes, navigate back without API call
    source === 'home' ? await router.push('/organization/group') : await router.push(`/organization/group/${groupId}`);
    return;
  }

  loading.value = true;
  const result = await editGroupUtil(groupId, formState.value, oldFormState.value, t);
  loading.value = false;
  if (result) {
    // Navigate based on source page after successful edit
    source === 'home' ? await router.push('/organization/group') : await router.push(`/organization/group/${groupId}`);
  }
};

/**
 * Form submit entry. Dispatch to add or edit according to presence of `groupId`.
 */
const onFinish = () => {
  if (groupId) {
    editGroup();
  } else {
    addGroup();
  }
};

/** Navigate back according to `source`. */
const handleCancel = () => {
  source === 'home' ? router.push('/organization/group') : router.push(`/organization/group/${groupId}`);
};

/**
 * The number of tags already associated with the group (used in quota hints).
 */
const total = ref(0);

/**
 * Fetch tag summary for current group to show quota hints.
 * Used to display current tag usage in edit mode.
 */
const loadGroupTagList = async (): Promise<void> => {
  const params = { pageNo: 1, pageSize: 1 };
  total.value = await loadGroupTagListUtil(groupId, params);
};

/** Initialize page data on mount for edit mode. */
onMounted(() => {
  if (groupId) {
    loadGroupDetail();
    loadGroupTagList();
  }
});

</script>
<template>
  <!-- Main form container with centered layout -->
  <PureCard class="min-h-full p-3.5">
    <Form
      :model="formState"
      size="small"
      class="flex w-180 mx-auto mt-10"
      @finish="onFinish">
      <!-- Form labels column with required indicators -->
      <div class="text-3 leading-3 text-theme-content text-right font-semibold w-28">
        <div class="h-7 leading-7 mb-5 pr-1.5 text-right">
          <IconRequired class="mr-0.5" />
          {{ t('common.columns.name') }}
          <Colon />
        </div>
        <div class="h-7 leading-7 mb-5 pr-1.5 text-right">
          <IconRequired class="mr-0.5" />
          {{ t('common.columns.code') }}
          <Colon />
        </div>
        <div class="h-7 leading-7 mb-24 pr-1.5 text-right">
          {{ t('group.columns.remark') }}
          <Colon />
        </div>
        <div class="h-7 leading-7 pr-1.5 text-right">
          {{ t('common.columns.tags') }}
          <Colon />
        </div>
      </div>

      <!-- Form inputs column -->
      <div class="flex-1 ml-3.5">
        <!-- Group name input with validation -->
        <FormItem name="name" :rules="[{ required: true, message:t('group.placeholder.addNameTip') }]">
          <Input
            v-model:value="formState.name"
            size="small"
            :maxlength="100"
            :placeholder="t('group.placeholder.addNameTip')" />
        </FormItem>

        <!-- Group code input (disabled in edit mode) -->
        <FormItem name="code" :rules="[{ required: true, message:t('group.placeholder.addCodeTip') }]">
          <Input
            v-model:value="formState.code"
            size="small"
            dataType="mixin-en"
            includes=":_-."
            :maxlength="80"
            :placeholder="t('group.placeholder.addCodeTip')"
            :disabled="!!groupId" />
        </FormItem>

        <!-- Group remark textarea -->
        <FormItem name="remark">
          <Textarea
            v-model:value="formState.remark"
            size="small"
            :rows="4"
            :maxlength="200"
            :placeholder="t('group.placeholder.remark')" />
        </FormItem>

        <!-- Tag quota hint information -->
        <FormItem>
          <Hints :text="t('group.groupTagQuotaTip', { num: groupId ? total : 0 })" />
        </FormItem>

        <!-- Tag selection with multiple mode -->
        <FormItem name="tagIds" class="-mt-5">
          <Select
            v-model:value="formState.tagIds"
            size="small"
            showSearch
            internal
            :fieldNames="{ label: 'name', value: 'id' }"
            :maxTags="10"
            :action="`${GM}/org/tag`"
            :placeholder="t('tag.placeholder.name')"
            mode="multiple" />
        </FormItem>

        <!-- Form action buttons -->
        <FormItem>
          <Button
            :loading="loading"
            type="primary"
            size="small"
            htmlType="submit"
            class="px-3">
            {{ t('common.actions.submit') }}
          </Button>
          <Button
            size="small"
            class="ml-5 px-3"
            @click="handleCancel">
            {{ t('common.actions.cancel') }}
          </Button>
        </FormItem>
      </div>
    </Form>
  </PureCard>
</template>
