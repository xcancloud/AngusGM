<script setup lang='ts'>
import { ref, onMounted, defineAsyncComponent } from 'vue';
import { useRoute } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { modal, notification } from '@xcan-angus/vue-ui';

import { Detail } from '../../PropsType';
import { group } from '@/api';

const DetailCard = defineAsyncComponent(() => import('./card.vue'));
const Associations = defineAsyncComponent(() => import('@/views/organization/group/components/association/index.vue'));

const { t } = useI18n();
const groupDetail = ref<Detail>();
const route = useRoute();
const loading = ref(false);

const groupId = route.params.id as string;

const loadGroupDetail = async () => {
  if (loading.value) {
    return;
  }
  loading.value = true;
  const [error, { data }] = await group.getGroupDetail(groupId);
  loading.value = false;
  if (error) {
    return;
  }

  groupDetail.value = data;
};

// 禁用启用弹框
const updateStatusConfirm = () => {
  modal.confirm({
    centered: true,
    title: groupDetail.value?.enabled ? t('disable') : t('enable'),
    content: groupDetail.value?.enabled ? t('userTip6', { name: groupDetail.value?.name }) : t('userTip5', { name: groupDetail.value?.name }),
    async onOk () {
      await updateStatus();
    }
  });
};

// 禁用启用请求
const updateStatus = async () => {
  if (!groupDetail.value) {
    return;
  }
  const params = [{ id: groupDetail.value.id, enabled: !groupDetail.value.enabled }];
  const [error] = await group.toggleGroupEnabled(params);
  if (error) {
    return;
  }
  notification.success(groupDetail.value.enabled ? '禁用成功' : '启用成功');
  loadGroupDetail();
};

const editSuccess = () => {
  loadGroupDetail();
};

onMounted(() => {
  loadGroupDetail();
});
</script>
<template>
  <div class="flex space-x-2 min-h-full">
    <DetailCard
      :dataSource="groupDetail"
      @update="updateStatusConfirm"
      @success="editSuccess" />
    <Associations :groupId="groupId" :enabled="groupDetail?.enabled" />
  </div>
</template>
