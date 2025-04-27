<script setup lang='ts'>
import { useI18n } from 'vue-i18n';
import { Grid } from '@xcan/design';
import { Badge } from 'ant-design-vue';

import { Detail } from './PropsType';

interface Props {
  dataSource: Detail

}

const props = withDefaults(defineProps<Props>(), {
  dataSource: undefined

});

const { t } = useI18n();
const gridColumns = [
  [
    {
      label: 'ID',
      dataIndex: 'id'
    },
    {
      label: t('mobileNumber'),
      dataIndex: 'mobile',
      customRender: ({ text }): string => text || '--'
    },
    {
      label: t('email'),
      dataIndex: 'email',
      customRender: ({ text }): string => text || '--'
    },
    {
      label: t('landline'),
      dataIndex: 'landline'
    },
    {
      label: t('gender'),
      dataIndex: 'gender',
      customRender: ({ text }): string => text?.message
    },
    {
      label: t('source'),
      dataIndex: 'source',
      customRender: ({ text }): string => text?.message
    }
  ],
  [
    {
      label: t('position'),
      dataIndex: 'title'
    },
    {
      label: t('systemIdentity'),
      dataIndex: 'sysAdmin',
      customRender: ({ text }): string => text ? t('systemAdministrator') : t('generalUsers')
    },
    {
      label: t('status'),
      dataIndex: 'enabled'
    },
    {
      label: t('lockedStatus'),
      dataIndex: 'locked',
      customRender: ({ text }): string => text ? t('locked') : t('unlocked')
    },
    { label: t('lockTime'), dataIndex: 'lockStartDate' },
    {
      label: t('address'),
      dataIndex: 'address'
    }
  ]
];
</script>
<template>
  <Grid :columns="gridColumns" :dataSource="props.dataSource">
    <template #enabled="{text}">
      <Badge :status=" text ? 'success' : 'error' " :text=" text ? t('enable') : t('disable') " />
    </template>
    <template #lockStartDate="{text}">
      {{ text }} - {{ props.dataSource?.lockEndDate }}
    </template>
  </Grid>
</template>
