<script setup lang="ts">
import { onMounted, reactive } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute } from 'vue-router';
import { Badge, Skeleton } from 'ant-design-vue';
import { Card, Grid } from '@xcan-angus/vue-ui';
import { cookieUtils } from '@xcan-angus/infra';

import { sms } from '@/api';
import { DetailState } from '../types';
import {
  createSmsRecordDetailColumns, getSendStatusColor, parseThirdPartyOutputParams, formatJsonForDisplay
} from '../utils';

const { t } = useI18n();
const route = useRoute();

// Reactive state management
const state = reactive<DetailState>({
  firstLoad: true,
  recordInfo: undefined
});

const id = route.params.id as string;

/**
 * Initialize component data
 */
const init = (): void => {
  getSmsRecordInfo();
};

/**
 * Load SMS record detail information from API
 */
const getSmsRecordInfo = async (): Promise<void> => {
  const [error, { data }] = await sms.getSmsDetail(id);
  state.firstLoad = false;
  if (error) {
    return;
  }
  state.recordInfo = data;
};

// Create grid columns using utility function
const gridColumns = createSmsRecordDetailColumns(t);

// Lifecycle hook - initialize component on mount
onMounted(() => {
  init();
});
</script>

<template>
  <Card :title="t('common.labels.basicInformation')" bodyClass="px-8 py-5">
    <Skeleton
      :loading="state.firstLoad"
      :title="false"
      :paragraph="{ rows: 7 }">
      <Grid
        :columns="gridColumns"
        :dataSource="state.recordInfo">
        <template #sendStatus="{text}">
          <Badge :color="getSendStatusColor(text?.value)" :text="text?.message" />
        </template>
        <template #attachments="{text}">
          <a
            v-for="(item,index) in text"
            :key="index"
            class="mr-4 text-theme-special"
            :href="`${item.url}&access_token=${cookieUtils.getTokenInfo().access_token}`">{{ item.name }}</a>
        </template>
      </Grid>
    </Skeleton>
  </Card>

  <Card
    :title="t('sms.titles.sendParams')"
    class="my-2"
    bodyClass="px-8 py-5">
    <pre v-if="state.recordInfo?.inputParam">{{ formatJsonForDisplay(state.recordInfo?.inputParam) }}</pre>
  </Card>

  <Card
    :title="t('sms.titles.returnParams')"
    bodyClass="px-8 py-5">
    <pre v-if="state.recordInfo?.thirdOutputParam">{{
        formatJsonForDisplay(parseThirdPartyOutputParams(state.recordInfo?.thirdOutputParam))
    }}</pre>
  </Card>
</template>
