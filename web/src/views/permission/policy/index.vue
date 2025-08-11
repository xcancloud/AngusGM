<script setup lang='ts'>
import { ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { Hints } from '@xcan-angus/vue-ui';

// Import policy management components
import AddPolicyForm from '@/views/permission/policy/components/addForm/index.vue';
import AddedPolicyList from '@/views/permission/policy/components/addList/index.vue';
import DefaultPolicyList from '@/views/permission/policy/components/defaultList/index.vue';

const { t } = useI18n();

// Component references for inter-component communication
const formRef = ref();
const defaultRef = ref();
const addedRef = ref();

// Store default policy IDs for reference
const defaultPolicies = ref<string[]>([]);

/**
 * Reload data after successful operations
 * Refreshes both default and added policy lists
 */
const load = () => {
  defaultRef.value.load();
  addedRef.value.load();
};

/**
 * Edit policy handler
 * @param item - Object containing appId and policyId
 * Scrolls to the form component after editing
 */
const editPolicy = (item: { appId: string | number | undefined, policyId: string | number | undefined }) => {
  formRef.value.editPolicy(item);
  formRef.value.$el.scrollIntoView();
};
</script>

<template>
  <!-- Permission strategy description hint -->
  <Hints :text="t('permission.policyTip')" class="mb-1" />

  <!-- Add/Edit permission policy form -->
  <AddPolicyForm ref="formRef" @reload="load" />

  <!-- Default permission policies section -->
  <Hints :text="t('permission.defaultPolicyTip')" class="mt-3 mb-1" />
  <DefaultPolicyList ref="defaultRef" />

  <!-- Added permission policies list -->
  <AddedPolicyList
    ref="addedRef"
    class="mt-3"
    :defaultPolicies="defaultPolicies"
    @edit="editPolicy" />
</template>
