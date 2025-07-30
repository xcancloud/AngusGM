<script setup lang='ts'>
import { ref, onMounted, computed } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { Button, Form, FormItem, Textarea } from 'ant-design-vue';
import { Hints, PureCard, Input, IconRequired, Select, notification, Colon } from '@xcan-angus/vue-ui';
import { utils, GM } from '@xcan-angus/infra';

import { FormState, Detail } from '../../PropsType';
import { group } from '@/api';

const { t } = useI18n();
const route = useRoute();
const router = useRouter();

const groupId = route.params.id as string;
const source = route.query.source as string;
const groupDetail = ref<Detail>();
const loadGroupDetail = async () => {
  const [error, { data }] = await group.getGroupDetail(groupId, {});
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

const formState = ref<FormState>({ code: '', name: '', remark: '', tagIds: [] });
const oldFormState = ref<FormState>({ code: '', name: '', remark: '', tagIds: [] });

const loading = ref(false);
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
  notification.success('添加成功');
  router.push('/organization/group');
};

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
  notification.success('修改成功');
  source === 'home' ? router.push('/organization/group') : router.push(`/organization/group/${groupId}`);
};

const onFinish = () => {
  if (groupId) {
    editGroup();
  } else {
    addGroup();
  }
};

const handleCancel = () => {
  source === 'home' ? router.push('/organization/group') : router.push(`/organization/group/${groupId}`);
};

const total = ref(0);
const loadGroupTagList = async (): Promise<void> => {
  const params = { pageNo: 1, pageSize: 1 };
  const [error, { data }] = await group.getGroupTag(groupId, params);
  if (error) {
    return;
  }

  total.value = +data?.total;
};

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
      class="flex w-150 mx-auto mt-10"
      @finish="onFinish">
      <div class="text-3 leading-3 text-theme-content">
        <div class="h-7 leading-7 mb-5">
          <IconRequired class="mr-0.5" />
          {{ t('name') }}
          <Colon />
        </div>
        <div class="h-7 leading-7 mb-5">
          <IconRequired class="mr-0.5" />
          {{ t('code') }}
          <Colon />
        </div>
        <div class="h-7 leading-7 mb-24 pl-1.5">
          {{ t('description') }}
          <Colon />
        </div>
        <div class="h-7 leading-7 pl-1.5">
          {{ t('label') }}
          <Colon />
        </div>
      </div>
      <div class="flex-1 ml-2">
        <FormItem name="name" :rules="[{ required: true, message:t('addNameTip') }]">
          <Input
            v-model:value="formState.name"
            size="small"
            :maxlength="100"
            :placeholder="t('pubPlaceholder',{name:t('name'),num:100})" />
        </FormItem>
        <FormItem name="code" :rules="[{ required: true, message:t('addCodeTip') }]">
          <Input
            v-model:value="formState.code"
            size="small"
            dataType="mixin-en"
            includes=":_-."
            :maxlength="80"
            :placeholder="t('pubPlaceholder',{name:t('code'),num:80})"
            :disabled="groupId" />
        </FormItem>
        <FormItem name="remark">
          <Textarea
            v-model:value="formState.remark"
            size="small"
            :rows="4"
            :maxlength="200"
            :placeholder="t('pubPlaceholder',{name:t('description'),num:200})" />
        </FormItem>
        <FormItem>
          <Hints
            :text="groupId?t(`每个组最多允许关联10个标签，当前组已关联${total}个标签。`):t(`每个组最多允许关联10个标签。`)" />
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
            :placeholder="t('tagPlaceholder')"
            mode="multiple" />
        </FormItem>
        <FormItem>
          <Button
            :loading="loading"
            type="primary"
            size="small"
            htmlType="submit"
            class="px-3">
            {{ t('submit') }}
          </Button>
          <Button
            size="small"
            class="ml-5 px-3"
            @click="handleCancel">
            {{ t('cancel') }}
          </Button>
        </FormItem>
      </div>
    </Form>
  </PureCard>
</template>
