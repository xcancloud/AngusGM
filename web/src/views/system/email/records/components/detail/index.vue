<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute } from 'vue-router';
import { Badge, Skeleton } from 'ant-design-vue';
import { Card, Grid } from '@xcan-angus/vue-ui';
import { cookieUtils } from '@xcan-angus/infra';

import { email } from '@/api';
import { EmailRecord, EmailSendStatus } from '../../PropsType';

const { t } = useI18n();
const route = useRoute();

// Reactive state management
const firstLoad = ref<boolean>(true);
const emailRecordInfo = ref<EmailRecord>();
const id = route.params.id as string;

// Status color mapping for better maintainability
const STATUS_COLORS: Record<EmailSendStatus, string> = {
  SUCCESS: 'rgba(82,196,26,1)', // Green for success
  PENDING: 'rgba(255,165,43,1)', // Orange for pending
  FAILURE: 'rgba(245,34,45,1)' // Red for failure
};

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
    const [error, { data }] = await email.getEmailDetail(id);

    if (error) {
      console.error('Failed to load email record details:', error);
      return;
    }

    emailRecordInfo.value = data;
  } catch (err) {
    console.error('Unexpected error loading email record details:', err);
  } finally {
    firstLoad.value = false;
  }
};

/**
 * Get status color based on send status value
 */
const getSendStatusColor = (value: EmailSendStatus): string => {
  return STATUS_COLORS[value] || STATUS_COLORS.FAILURE;
};

/**
 * Grid column configuration for email record details
 * Organized in two columns for better layout
 */
const gridColumns = [
  // Left column
  [
    {
      label: t('email.columns.id'),
      dataIndex: 'id'
    },
    {
      label: t('email.columns.emailType'),
      dataIndex: 'emailType'
    },
    {
      label: t('email.columns.sendStatus'),
      dataIndex: 'sendStatus'
    },
    {
      label: t('email.columns.sendTenantId'),
      dataIndex: 'sendTenantId'
    },
    {
      label: t('email.columns.sendUserId'),
      dataIndex: 'sendId'
    },
    {
      label: t('email.columns.templateCode'),
      dataIndex: 'templateCode'
    },
    {
      label: t('email.columns.urgent'),
      dataIndex: 'urgent',
      customRender: ({ text }): string => text ? t('common.status.yes') : t('common.status.no')
    },
    {
      label: t('email.columns.verificationCode'),
      dataIndex: 'verificationCode',
      customRender: ({ text }): string => text ? t('common.status.yes') : t('common.status.no')
    },
    {
      label: t('email.columns.batch'),
      dataIndex: 'batch',
      customRender: ({ text }): string => text ? t('common.status.yes') : t('common.status.no')
    },
    {
      label: t('email.columns.subject'),
      dataIndex: 'subject'
    },
    {
      label: t('email.columns.content'),
      dataIndex: 'content'
    }
  ],
  // Right column
  [
    {
      label: t('email.columns.sendTime'),
      dataIndex: 'actualSendDate'
    },
    {
      label: t('email.columns.expectedTime'),
      dataIndex: 'expectedSendDate'
    },
    {
      label: t('email.columns.outId'),
      dataIndex: 'outId'
    },
    {
      label: t('email.columns.bizKey'),
      dataIndex: 'bizKey'
    },
    {
      label: t('email.columns.language'),
      dataIndex: 'language',
      customRender: ({ text }): string => text?.message || '--'
    },
    {
      label: t('email.columns.fromAddr'),
      dataIndex: 'fromAddr'
    },
    {
      label: t('email.columns.toAddress'),
      dataIndex: 'toAddress',
      customRender: ({ text }): string => text?.join(',') || '--'
    },
    {
      label: t('email.columns.html'),
      dataIndex: 'html',
      customRender: ({ text }): string => text ? t('common.status.yes') : t('common.status.no')
    },
    {
      label: t('email.columns.failureReason'),
      dataIndex: 'failureReason'
    },
    {
      label: t('email.columns.attachments'),
      dataIndex: 'attachments'
    }
  ]
];

// Initialize component on mount
onMounted(() => {
  init();
});
</script>

<template>
  <!-- Email record information card -->
  <Card :title="t('email.titles.emailInfo')" bodyClass="px-8 py-5">
    <Skeleton
      :loading="firstLoad"
      :title="false"
      :paragraph="{ rows: 11 }">
      <Grid
        :columns="gridColumns"
        :dataSource="emailRecordInfo">
        <!-- Send status with colored badge -->
        <template #sendStatus="{ text }">
          <Badge
            :color="getSendStatusColor(text?.value)"
            :text="text?.message" />
        </template>

        <!-- File attachments with download links -->
        <template #attachments="{ text }">
          <a
            v-for="(item, index) in text"
            :key="index"
            class="mr-4 text-theme-special"
            :href="`${item.url}&access_token=${cookieUtils.get('access_token')}`">
            {{ item.name }}
          </a>
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
      :loading="firstLoad"
      :title="false"
      :paragraph="{ rows: 5 }">
      <pre v-if="emailRecordInfo?.templateParams">
        {{ JSON.stringify(emailRecordInfo?.templateParams, null, 2) }}
      </pre>
    </Skeleton>
  </Card>
</template>
