<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { Card, Input, Colon, Spin, notification } from '@xcan/design';
import { debounce } from 'throttle-debounce';
import { duration } from '@xcan/configs';

import { setting } from '@/api';

const loading = ref(false);
const controlSetting = ref();
const maxMetricsDays = ref('15');
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

const handleSetControl = debounce(duration.search, async (value) => {
  loading.value = true;
  const [error] = await setting.setSetting({ ...controlSetting.value,
    maxMetricsDays: value, key: 'MAX_METRICS_DAYS' });
  loading.value = false;
  if (error) {
    return;
  }
  notification.success('修改配置成功');
});

const activitySetting = ref();
const maxResourceActivities = ref('200');
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

const handleSetActivity = debounce(duration.search, async (value) => {
  loading.value = true;
  const [error] = await setting.setSetting({...activitySetting.value,
    maxResourceActivities: value, key: 'MAX_RESOURCE_ACTIVITIES'});
  loading.value = false;
  if (error) {
    return;
  }
  notification.success('修改配置成功');
});

onMounted(() => {
  getControlSetting();
  getActivitySetting();
});
</script>
<template>
  <Spin :spinning="loading">
    <Card title="监控数据" bodyClass="flex items-center text-3 p-3.5">
      配置监控数据最大保留最近<Input
        v-model:value="maxMetricsDays"
        class="w-25 flex-none mx-1"
        dataType="number"
        :min="1"
        :max="3650"
        @change="handleSetControl($event.target.value)" />天，允许范围：1-3650，默认15天。
    </Card>
    <Card
      title="活动数据"
      class="mt-2"
      bodyClass="flex items-center text-3 p-3.5">
      配置最大保留资源最新活动记录数
      <Colon />
      <Input
        v-model:value="maxResourceActivities"
        class="w-25 flex-none mx-1"
        dataType="number"
        :min="1"
        :max="10000"
        @change="handleSetActivity($event.target.value)" />，允许设置范围：1-10000，默认200。
    </Card>
  </Spin>
</template>
