<script setup lang='ts'>
import { useI18n } from 'vue-i18n';
import { Grid } from '@xcan-angus/vue-ui';

import type { PolicyDetailType } from '../PropsType';

/**
 * Props interface for BaseInfo component
 * @interface Props
 * @property {PolicyDetailType} detail - Policy detail information to display
 */
interface Props {
  detail: PolicyDetailType
}

/**
 * Component props with default values
 * Provides fallback values for all detail properties to prevent rendering errors
 */
const props = withDefaults(defineProps<Props>(), {
  detail: () => ({
    id: undefined,
    name: undefined,
    code: undefined,
    appId: undefined,
    appName: undefined,
    createdByName: undefined,
    createdDate: undefined,
    type: { value: undefined, message: undefined },
    enabled: false,
    description: undefined
  })
});

const { t } = useI18n();

/**
 * Grid column configuration for displaying policy information
 * Organized in two rows for better layout and readability
 * First row: Basic policy information (name, type, code, enabled status, creator, description)
 * Second row: Application and authorization details (app name, global scope, grant stage, default policy, creation date)
 */
const columns = [
  [
    {
      label: t('common.columns.name'),
      dataIndex: 'name'
    },
    {
      label: t('common.columns.type'),
      dataIndex: 'type',
      customRender: ({ text }) => text?.message
    },
    {
      label: t('common.columns.code'),
      dataIndex: 'code'
    },
    {
      label: t('common.status.validStatus'),
      dataIndex: 'enabled',
      customRender: ({ text }) => text ? t('common.status.enabled') : t('common.status.disabled')
    },
    {
      label: t('common.columns.createdByName'),
      dataIndex: 'createdByName'
    },
    {
      label: t('common.columns.description'),
      dataIndex: 'description'
    }
  ],
  [
    {
      label: t('permission.policy.added.columns.appName'),
      dataIndex: 'appName'
    },
    {
      label: t('permission.policy.detail.info.globalAccount'),
      dataIndex: 'global',
      customRender: ({ text }) => text ? t('common.labels.all') : t('common.labels.tenant')
    },
    {
      label: t('permission.policy.detail.info.grantStage'),
      dataIndex: 'grantStage',
      customRender: ({ text }) => text?.message
    },
    {
      label: t('permission.policy.detail.info.defaultPolicy'),
      dataIndex: 'default0',
      customRender: ({ text }) => text ? t('common.status.yes') : t('common.status.no')
    },
    {
      label: t('common.columns.createdDate'),
      dataIndex: 'createdDate'
    }
  ]
];
</script>

<template>
  <!-- Policy basic information display grid -->
  <Grid
    class="w-full"
    :columns="columns"
    :dataSource="props.detail" />
</template>
