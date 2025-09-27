<script setup lang='ts'>
import { computed, defineAsyncComponent, ref } from 'vue';
import { Hints, PureCard } from '@xcan-angus/vue-ui';
import { useI18n } from 'vue-i18n';
import { app, GM, enumUtils } from '@xcan-angus/infra';

import { OrgTag } from './types';
import { ChartType, DateRangeType } from '@/components/Dashboard/enums';
import { OrgTargetType } from '@/enums/enums';

// Async components
const Statistics = defineAsyncComponent(() => import('@/components/Statistics/index.vue'));

const List = defineAsyncComponent(() => import('@/views/organization/tag/list/index.vue'));
const Table = defineAsyncComponent(() => import('@/views/organization/tag/table/index.vue'));
const Info = defineAsyncComponent(() => import('@/views/organization/tag/info/index.vue'));
const Dashboard = defineAsyncComponent(() => import('@/components/Dashboard/Dashboard.vue'));

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

const dashboardConfig = {
  charts: [
    {
      type: ChartType.LINE,
      title: t('statistics.metrics.newTags'),
      field: 'created_date'
    },
    {
      type: ChartType.PIE,
      title: [t('common.labels.association')],
      field: ['target_type'],
      enumKey: [
        enumUtils.enumToMessages(OrgTargetType)
      ],
      legendPosition: ['right']
    }
  ],
  layout: {
    cols: 2,
    gap: 16
  }
};

</script>
<template>
  <PureCard class="p-3.5 flex flex-col h-full">
    <Dashboard
      class="py-3"
      :config="dashboardConfig"
      :apiRouter="GM"
      resource="OrgTagTarget"
      :dateType="DateRangeType.YEAR"
      :showChartParam="true" />

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
