<script setup lang='ts'>
import { useI18n } from 'vue-i18n';
import { Grid } from '@xcan-angus/vue-ui';

import type { DetailType } from './PropTypes';

interface Props {
  detail: DetailType
}

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
const columns = [
  [
    {
      label: t('permissionsStrategy.detail.info.name'),
      dataIndex: 'name'
    },
    {
      label: t('type'),
      dataIndex: 'type',
      customRender: ({ text }) => text?.message
    },
    {
      label: t('permissionsStrategy.detail.info.code'),
      dataIndex: 'code'
    },
    {
      label: t('permissionsStrategy.detail.info.enabled'),
      dataIndex: 'enabled',
      customRender: ({ text }) => text ? t('enable') : t('disable')
    },
    {
      label: t('permissionsStrategy.detail.info.fullName'),
      dataIndex: 'createdByName'
    },
    {
      label: t('permissionsStrategy.detail.info.desc'),
      dataIndex: 'description'
    }
  ],
  [
    {
      label: t('permissionsStrategy.detail.info.appName'),
      dataIndex: 'appName'
    },
    {
      label: t('授权账号'),
      dataIndex: 'global',
      customRender: ({ text }) => text ? t('permissionsStrategy.form.all') : props.detail.tenantName
    },
    {
      label: t('授权初始化阶段'),
      dataIndex: 'grantStage',
      customRender: ({ text }) => text?.message
    },
    {
      label: t('默认授权策略'),
      dataIndex: 'default0',
      customRender: ({ text }) => text ? t('yes') : t('no')
    },
    {
      label: t('permissionsStrategy.detail.info.createdDate'),
      dataIndex: 'createdDate'
    }
  ]
];
</script>

<template>
  <Grid
    class="w-full"
    :columns="columns"
    :dataSource="detail" />
</template>
