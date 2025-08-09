<script setup lang='ts'>
import { computed, defineAsyncComponent, ref } from 'vue';
import { Hints, PureCard } from '@xcan-angus/vue-ui';
import { useI18n } from 'vue-i18n';
import { app, GM } from '@xcan-angus/infra';

import { OrgTag } from './PropsType';

// Async components
const List = defineAsyncComponent(() => import('@/views/organization/tag/components/list/index.vue'));
const Table = defineAsyncComponent(() => import('@/views/organization/tag/components/table/index.vue'));
const Info = defineAsyncComponent(() => import('@/views/organization/tag/components/info/index.vue'));
const Statistics = defineAsyncComponent(() => import('@/components/Statistics/index.vue'));

const { t } = useI18n();

/**
 * Currently selected tag details from the left list.
 */
const tag = ref<OrgTag>();

/**
 * Tenant name echoed from the left list; passed down to the table area.
 */
const tenantName = ref<string>('');

/**
 * Visibility flag for the right-side content panel (statistics/table).
 */
const visible = ref<boolean>(true);

/**
 * Reference to List component for invoking its public methods (e.g., openEditName).
 */
const listRef = ref<any>();

/**
 * Whether current user has permission to modify tag name.
 */
const canModify = computed<boolean>(() => app.has('TagModify'));

/**
 * Open the edit-name flow in child List component, using current selected tag.
 */
const editTagName = (): void => {
  listRef.value?.openEditName(tag.value);
};
</script>
<template>
  <PureCard class="p-3.5 flex flex-col h-full">
    <Statistics
      resource="OrgTagTarget"
      :barTitle="t('statistics.metrics.newTags')"
      :router="GM"
      dateType="YEAR"
      :visible="visible" />
    <Hints
      class="my-1"
      :text="t('tag.description')" />
    <div class="flex space-x-2 flex-1 min-h-0">
      <List
        ref="listRef"
        v-model:tag="tag"
        v-model:tenantName="tenantName"
        :visible="visible" />
      <div class="flex-1 flex flex-col overflow-y-auto">
        <Info
          :tag="tag"
          :canModify="canModify"
          @editName="editTagName" />
        <Table
          v-model:visible="visible"
          :tagId="tag?.id"
          :tenantName="tenantName" />
      </div>
    </div>
  </PureCard>
</template>
