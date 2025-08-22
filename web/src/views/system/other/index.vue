<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { Card, Colon, Input, notification, Spin } from '@xcan-angus/vue-ui';
import { debounce } from 'throttle-debounce';
import { duration } from '@xcan-angus/infra';
import { useI18n } from 'vue-i18n';

import { setting } from '@/api';

const { t } = useI18n();

// Loading state for all operations
const loading = ref(false);

// Monitoring data settings
const controlSetting = ref();
const maxMetricsDays = ref('15');

/**
 * Fetch monitoring data retention settings from API
 * Retrieves MAX_METRICS_DAYS configuration
 */
const getControlSetting = async () => {
  loading.value = true;
  const [error, { data }] = await setting.getSettingDetail('MAX_METRICS_DAYS');
  loading.value = false;

  if (error) {
    return;
  }

  controlSetting.value = data;
  maxMetricsDays.value = data.maxMetricsDays;
};

/**
 * Update monitoring data retention setting with debounced API call
 */
const handleSetControl = debounce(duration.search, async (value) => {
  loading.value = true;
  const [error] = await setting.setSetting({
    ...controlSetting.value,
    maxMetricsDays: value,
    key: 'MAX_METRICS_DAYS'
  });
  loading.value = false;

  if (error) {
    return;
  }

  notification.success(t('other.messages.modifyConfigSuccess'));
});

// Activity data settings
const activitySetting = ref();
const maxResourceActivities = ref('200');

/**
 * Fetch activity data retention settings from API
 * Retrieves MAX_RESOURCE_ACTIVITIES configuration
 */
const getActivitySetting = async () => {
  loading.value = true;
  const [error, { data }] = await setting.getSettingDetail('MAX_RESOURCE_ACTIVITIES');
  loading.value = false;

  if (error) {
    return;
  }

  activitySetting.value = data;
  maxResourceActivities.value = data.maxResourceActivities;
};

/**
 * Update activity data retention setting with debounced API call
 */
const handleSetActivity = debounce(duration.search, async (value) => {
  loading.value = true;
  const [error] = await setting.setSetting({
    ...activitySetting.value,
    maxResourceActivities: value,
    key: 'MAX_RESOURCE_ACTIVITIES'
  });
  loading.value = false;

  if (error) {
    return;
  }

  notification.success(t('other.messages.modifyConfigSuccess'));
});

// Initialize component data on mount
onMounted(() => {
  getControlSetting();
  getActivitySetting();
});
</script>

<template>
  <Spin :spinning="loading">
    <!-- Monitoring Data Retention Configuration -->
    <Card :title="t('other.titles.monitoringData')" bodyClass="flex items-center text-3 p-3.5">
      {{ t('other.descriptions.monitoringDataDesc') }}
      <Input
        v-model:value="maxMetricsDays"
        class="w-25 flex-none mx-1"
        dataType="number"
        :min="1"
        :max="3650"
        @change="handleSetControl($event.target.value)" />
      {{ t('common.time.day') }}{{ t('other.descriptions.monitoringDataRange') }}
    </Card>

    <!-- Activity Data Retention Configuration -->
    <Card
      :title="t('other.titles.activityData')"
      class="mt-2"
      bodyClass="flex items-center text-3 p-3.5">
      {{ t('other.descriptions.activityDataDesc') }}
      <Colon />
      <Input
        v-model:value="maxResourceActivities"
        class="w-25 flex-none mx-1"
        dataType="number"
        :min="1"
        :max="10000"
        @change="handleSetActivity($event.target.value)" />
      {{ t('other.descriptions.activityDataRange') }}
    </Card>
  </Spin>
</template>
