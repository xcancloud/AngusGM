<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { Card, Grid } from '@xcan-angus/vue-ui';
import { useRoute } from 'vue-router';
import { Skeleton } from 'ant-design-vue';

import { notice } from '@/api';
import { getDetailColumns } from '../utils';

const route = useRoute();
const { t } = useI18n();

// Component state management
const dataSource = ref(); // Notice detail data
const id = ref(''); // Notice ID from route params
const firstLoad = ref(true); // First loading state for skeleton display

/**
 * Fetch notice detail data from API
 * Updates loading state and stores response data
 */
const getDetailData = async () => {
  const [error, { data = {} }] = await notice.getNoticeDetail(id.value);
  firstLoad.value = false; // Hide skeleton after data loads
  if (error) {
    return;
  }
  dataSource.value = data || {};
};

// Get detail columns configuration from utility function
const columns = getDetailColumns(t);

// Initialize component on mount
onMounted(() => {
  id.value = route.params.id as string || '';
  getDetailData();
});
</script>
<template>
  <div class="flex flex-col min-h-full">
    <!-- Basic information card -->
    <Card
      class="text-3 mb-2"
      bodyClass="px-8 py-5"
      :title="t('notification.basicInfo')">
      <Skeleton
        active
        :loading="firstLoad"
        :title="false"
        :paragraph="{ rows: 4 }">
        <!-- Grid layout for notice basic information -->
        <Grid :columns="columns" :dataSource="dataSource">
          <!-- Send type display with enum message -->
          <template #sendType="{text}">{{ text?.message }}</template>

          <!-- Scope display with enum message -->
          <template #scope="{text}">{{ text?.message }}</template>

          <!-- Timing date display (only shown for scheduled sends) -->
          <template #timingDate="{text}">
            {{ dataSource?.sendType?.value === 'TIMING_SEND' ? text : '--' }}
          </template>
        </Grid>
      </Skeleton>
    </Card>

    <!-- Notice content card -->
    <Card
      class="text-3 flex-1"
      bodyClass="px-8 py-5"
      :title="t('notification.columns.content')">
      <Skeleton
        active
        :loading="firstLoad"
        :title="false"
        :paragraph="{ rows: 12 }">
        <!-- Display notice content text -->
        <div>{{ dataSource?.content }}</div>
      </Skeleton>
    </Card>
  </div>
</template>
