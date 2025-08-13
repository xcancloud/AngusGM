<script setup lang='ts'>
import { onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { Button, Form, FormItem, Textarea } from 'ant-design-vue';
import { Colon, Hints, IconRequired, Input, notification, PureCard, Select } from '@xcan-angus/vue-ui';
import { GM, utils } from '@xcan-angus/infra';

import { Detail, FormState } from '../types';
import { group } from '@/api';

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
  const [error, { data }] = await group.getGroupDetail(groupId);
  if (error) {
    return;
  }

  groupDetail.value = data;
  Object.keys(formState.value).every(item => {
    if (item === 'tagIds') {
      formState.value[item] = data?.tags?.map(item => item.id);
    } else {
      formState.value[item] = data[item];
    }
    return true;
  });
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
 */
const addGroup = async () => {
  if (loading.value) {
    return;
  }
  loading.value = true;
  const [error] = await group.addGroup([formState.value]);
  loading.value = false;
  if (error) {
    return;
  }
  notification.success(t('common.messages.addSuccess'));
  router.push('/organization/group');
};

/**
 * Update an existing group when form content actually changed compared to original.
 */
const editGroup = async () => {
  if (loading.value) {
    return;
  }

  const isEqual = utils.deepCompare(oldFormState.value, formState.value);
  if (isEqual) {
    source === 'home' ? router.push('/organization/group') : router.push(`/organization/group/${groupId}`);
    return;
  }

  loading.value = true;
  const [error] = await group.replaceGroup([{ id: groupId, ...formState.value }]);
  loading.value = false;
  if (error) {
    return;
  }
  notification.success(t('common.messages.editSuccess'));
  source === 'home' ? router.push('/organization/group') : router.push(`/organization/group/${groupId}`);
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
 */
const loadGroupTagList = async (): Promise<void> => {
  const params = { pageNo: 1, pageSize: 1 };
  const [error, { data }] = await group.getGroupTag(groupId, params);
  if (error) {
    return;
  }

  total.value = +data?.total;
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
  <PureCard class="min-h-full p-3.5">
    <Form
      :model="formState"
      size="small"
      class="flex w-180 mx-auto mt-10"
      @finish="onFinish">
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
      <div class="flex-1 ml-3.5">
        <FormItem name="name" :rules="[{ required: true, message:t('group.placeholder.addNameTip') }]">
          <Input
            v-model:value="formState.name"
            size="small"
            :maxlength="100"
            :placeholder="t('group.placeholder.addNameTip')" />
        </FormItem>
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
        <FormItem name="remark">
          <Textarea
            v-model:value="formState.remark"
            size="small"
            :rows="4"
            :maxlength="200"
            :placeholder="t('group.placeholder.remark')" />
        </FormItem>
        <FormItem>
          <Hints :text="t('group.groupTagQuotaTip', { num: groupId ? total : 0 })" />
        </FormItem>
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
