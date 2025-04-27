<script setup lang="ts">
import { computed, inject } from 'vue';
import { useI18n } from 'vue-i18n';
import { Skeleton } from 'ant-design-vue';
import { Card, Table } from '@xcan-angus/vue-ui';

interface Props {
  serviceData: any
}

const props = withDefaults(defineProps<Props>(), {
  serviceData: []
});

const { t } = useI18n();

const _columns = [
  {
    title: 'serviceName',
    dataIndex: 'name'
  },
  {
    title: 'instanceAddress',
    dataIndex: 'instances',
    key: 'instances',
    width: '30%'
  },
  {
    title: 'Swagger',
    dataIndex: 'swagger',
    width: '35%'
  },
  {
    title: 'status',
    dataIndex: 'status',
    width: '10%'
  }
];

const columns = computed(() => {
  return _columns.map((item) => {
    return {
      ...item,
      title: t(item.title)
    };
  });
});

const loading = inject('loading');
</script>
<template>
  <Card class="flex-1" :title="t('实例')">
    <Skeleton
      active
      :loading="loading"
      :title="false"
      :paragraph="{rows:6}">
      <Table
        :dataSource="props.serviceData"
        :columns="columns"
        :pagination="false"
        size="small">
        <template #bodyCell="{ column,text,record}">
          <template v-if="column.dataIndex === 'instances'">
            <span
              v-for="item,index in text"
              :key="item.id">
              <a
                :href="item.url"
                target="_blank"
                class="text-theme-special text-theme-text-hover">{{ item.id }}</a>
              <template v-if="index < text.length-1">
                ,
              </template>
            </span>
          </template>
          <template v-if="column.dataIndex === 'swagger'">
            <span
              v-for="item,index in record.instances"
              :key="item.id">
              <a
                :href="`http://${item.id}/swagger-ui/`"
                target="_blank"
                class="text-theme-special text-theme-text-hover">{{ `http://${item.id}/swagger-ui/` }}</a>
              <template v-if="index < record.instances.length-1">
                ,
              </template>
            </span>
          </template>
          <template v-if="column.dataIndex === 'status'">
            <div class="flex">
              <div class="w-1.5 h-1.5 rounded-xl mt-1.5 mr-1.5" :class="text=='UP'?'bg-success':'bg-danger'"></div>
              {{ text }}
            </div>
          </template>
        </template>
      </Table>
    </Skeleton>
  </Card>
</template>
