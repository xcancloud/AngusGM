<script setup lang='ts'>
import { ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { Hints } from '@xcan/design';

import AddPolicyForm from '@/views/permission/policy/components/addForm/index.vue';
import AddedPolicyList from '@/views/permission/policy/components/addList/index.vue';
import DefaultPolicyList from '@/views/permission/policy/components/defaultList/index.vue';

const { t } = useI18n();
const formRef = ref();
const defaultRef = ref();
const addedRef = ref();
const defaultPolicies = ref<string[]>([]);

// 保存成功后的回调
const load = () => {
  defaultRef.value.load();
  addedRef.value.load();
};

const editPolicy = (item: { appId: string | number | undefined, policyId: string | number | undefined }) => {
  formRef.value.editPolicy(item);
  formRef.value.$el.scrollIntoView();
};
</script>
<template>
  <Hints :text="t('permissionsStrategy.tip')" class="mb-1" />
  <!-- 添加权限策略 -->
  <add-policy-form ref="formRef" @reload="load" />
  <!-- 默认权限策略 -->
  <Hints :text="t('permissionsStrategy.default.tip')" class="mt-3 mb-1" />
  <DefaultPolicyList ref="defaultRef" />
  <!-- 已添加的权限策略 -->
  <AddedPolicyList
    ref="addedRef"
    class="mt-3"
    :defaultPolicies="defaultPolicies"
    @edit="editPolicy" />
</template>
