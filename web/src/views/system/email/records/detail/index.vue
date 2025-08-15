<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute } from 'vue-router';
import { Badge, Skeleton } from 'ant-design-vue';
import { Card, Grid } from '@xcan-angus/vue-ui';
import { cookieUtils } from '@xcan-angus/infra';

import { email } from '@/api';
import { DetailState } from '../types';
import {
  createGridColumns, createStatusColorMapping, createInitialDetailState, getSendStatusColor,
  hasAttachments, getAttachmentDisplayName, getAttachmentDownloadUrl, formatTemplateParams
} from '../utils';

const { t } = useI18n();
const route = useRoute();

// Component state management
const state = reactive<DetailState>(createInitialDetailState());

// Email record ID from route
const id = route.params.id as string;

// Status color mapping
const statusColors = ref(createStatusColorMapping());

// Grid columns configuration
const gridColumns = ref(createGridColumns(t));

/**
 * Initialize component data
 */
const init = (): void => {
  getEmailRecordInfo();
};

/**
 * Fetch email record details from API with error handling
 */
const getEmailRecordInfo = async (): Promise<void> => {
  try {
    const [error, response] = await email.getEmailDetail(id);

    if (error) {
      console.error('Failed to load email record details:', error);
      return;
    }

    state.emailRecordInfo = response.data;
  } catch (err) {
    console.error('Unexpected error loading email record details:', err);
  } finally {
    state.firstLoad = false;
  }
};

// Initialize component on mount
onMounted(() => {
  init();
});
</script>

<template>
  <!-- Email record information card -->
  <Card :title="t('email.titles.emailInfo')" bodyClass="px-8 py-5">
    <Skeleton
      :loading="state.firstLoad"
      :title="false"
      :paragraph="{ rows: 11 }">
      <Grid
        :columns="gridColumns"
        :dataSource="state.emailRecordInfo">
        <!-- Send status with colored badge -->
        <template #sendStatus="{ text }">
          <Badge
            :color="getSendStatusColor(text?.value, statusColors)"
            :text="text?.message" />
        </template>

        <!-- File attachments with download links -->
        <template #attachments="{ text }">
          <template v-if="hasAttachments(text)">
            <a
              v-for="(item, index) in text"
              :key="index"
              class="mr-4 text-theme-special"
              :href="getAttachmentDownloadUrl(item, cookieUtils.get('access_token'))">
              {{ getAttachmentDisplayName(item) }}
            </a>
          </template>
          <template v-else>
            --
          </template>
        </template>
      </Grid>
    </Skeleton>
  </Card>

  <!-- Template parameters card -->
  <Card
    :title="t('email.titles.sendParams')"
    bodyClass="px-8 py-5"
    class="flex-1">
    <Skeleton
      :loading="state.firstLoad"
      :title="false"
      :paragraph="{ rows: 5 }">
      <pre v-if="state.emailRecordInfo?.templateParams">
        {{ formatTemplateParams(state.emailRecordInfo.templateParams) }}
      </pre>
    </Skeleton>
  </Card>
</template>
