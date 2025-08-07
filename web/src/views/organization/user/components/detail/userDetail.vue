<script setup lang='ts'>
import { useI18n } from 'vue-i18n';
import { Grid } from '@xcan-angus/vue-ui';
import { Badge } from 'ant-design-vue';

import { Detail } from './PropsType';

/**
 * Component props interface
 * Defines the properties passed to the user detail component
 */
interface Props {
  dataSource: Detail // User detail data to display
}

const props = withDefaults(defineProps<Props>(), {
  dataSource: undefined
});

// Internationalization setup
const { t } = useI18n();

/**
 * Grid columns configuration for user information display
 * Defines the layout and fields for user info grid
 */
const gridColumns = [
  [
    {
      label: 'ID',
      dataIndex: 'id'
    },
    {
      label: t('user.columns.mobile'),
      dataIndex: 'mobile',
      customRender: ({ text }): string => text || '--'
    },
    {
      label: t('user.columns.email'),
      dataIndex: 'email',
      customRender: ({ text }): string => text || '--'
    },
    {
      label: t('user.columns.landline'),
      dataIndex: 'landline'
    },
    {
      label: t('user.columns.gender'),
      dataIndex: 'gender',
      customRender: ({ text }): string => text?.message
    },
    {
      label: t('user.columns.source'),
      dataIndex: 'source',
      customRender: ({ text }): string => text?.message
    }
  ],
  [
    {
      label: t('user.columns.title'),
      dataIndex: 'title'
    },
    {
      label: t('user.columns.identity'),
      dataIndex: 'sysAdmin',
      customRender: ({ text }): string =>
        text ? t('user.profile.systemAdmin') : t('user.profile.generalUser')
    },
    {
      label: t('user.columns.status'),
      dataIndex: 'enabled'
    },
    {
      label: t('user.columns.lockedStatus'),
      dataIndex: 'locked',
      customRender: ({ text }): string =>
        text ? t('common.status.locked') : t('common.status.unlocked')
    },
    {
      label: t('user.columns.lockStartDate'),
      dataIndex: 'lockStartDate'
    },
    {
      label: t('user.columns.address'),
      dataIndex: 'address'
    }
  ]
];
</script>
<template>
  <!-- User detail information grid -->
  <Grid :columns="gridColumns" :dataSource="props.dataSource">
    <!-- Enabled status badge -->
    <template #enabled="{text}">
      <Badge
        :status=" text ? 'success' : 'error' "
        :text=" text? t('common.status.enabled') : t('common.status.disabled') " />
    </template>

    <!-- Locked status badge -->
    <template #locked="{text}">
      <Badge
        :status=" text ? 'error' : 'success' "
        :text=" text ? t('common.status.locked') : t('common.status.unlocked') " />
    </template>

    <!-- Lock date range display -->
    <template v-if="props.dataSource?.locked" #lockStartDate="{text}">
      <div v-if="props.dataSource?.lockEndDate">
        {{ text }} - {{ props.dataSource?.lockEndDate }}
      </div>
      <div v-else>
        {{ t('user.permanentLock') }}
      </div>
    </template>
  </Grid>
</template>
