<script setup lang='ts'>
import { defineAsyncComponent, ref } from 'vue';
import { Hints, PureCard, Card, Icon } from '@xcan-angus/vue-ui';
import { useI18n } from 'vue-i18n';
import { app, GM } from '@xcan-angus/tools';

import { OrgTag } from './PropsType';

const List = defineAsyncComponent(() => import('@/views/organization/tag/components/list/index.vue'));
const Table = defineAsyncComponent(() => import('@/views/organization/tag/components/table/index.vue'));
const Statistics = defineAsyncComponent(() => import('@/components/Statistics/index.vue'));

const { t } = useI18n();
const tag = ref<OrgTag>();
const tenantName = ref('');
const visible = ref(true);

const listRef = ref();
const editTagName = () => {
  listRef.value?.openEditName(tag.value);
};
</script>
<template>
  <PureCard class="p-3.5 flex flex-col h-full">
    <Statistics
      resource="OrgTagTarget"
      :geteway="GM"
      dateType="YEAR"
      :barTitle="t('label')"
      :visible="visible" />
    <Hints
      class="my-1"
      text="“标签”可以帮助您标识有共同点的组织和人员，添加标签并关联组织和人员后，可以方便您在业务使用上更快地查找和选择。例如：“开发人员、性能测试专家、功能测试人员、头牌销售、商城成员”等。" />
    <div class="flex space-x-2 flex-1 min-h-0">
      <List
        ref="listRef"
        v-model:tag="tag"
        v-model:tenantName="tenantName"
        :visible="visible" />
      <div class="flex-1 flex flex-col overflow-y-auto">
        <Card v-show="tag?.id" class="mb-2">
          <template #title>
            <span class="text-3">基本信息</span>
          </template>
          <div class="text-3 flex w-full space-x-5">
            <div class="w-1/3">
              <div class="flex items-center space-x-2">
                <div class="truncate">{{ t('name') + `: ${tag?.name || ''}` }}</div>
                <Icon
                  v-if="app.has('TagModify')"
                  icon="icon-shuxie"
                  class="flex-none text-text-link cursor-pointer hover:text-text-link-hover"
                  @click="editTagName" />
              </div>
              <div class="mt-2">{{ t('createdDate') + `: ${tag?.createdDate || ''}` }}</div>
            </div>
            <div class="w-1/3">
              <div>{{ t('ID') + `: ${tag?.id}` }}</div>
              <div class=" truncate mt-2">{{ t('最后修改人') + `: ${tag?.lastModifiedByName || '--'}` }}</div>
            </div>
            <div class="w-1/3">
              <div class=" truncate">{{ t('founder') + `: ${tag?.createdByName || '--'}` }}</div>
              <div class="mt-2">{{ t('最后修改时间') + `: ${tag?.lastModifiedDate || '--'}` }}</div>
            </div>
          </div>
        </Card>
        <Table
          v-model:visible="visible"
          :tagId="tag?.id"
          :tenantName="tenantName" />
      </div>
    </div>
  </PureCard>
</template>
<style scoped>
.count-title {
  background-color: rgba(82, 196, 26, 30%);
}

.count-content {
  background-image: linear-gradient(to top, rgba(103, 215, 255, 60%), #fff);
}
</style>
