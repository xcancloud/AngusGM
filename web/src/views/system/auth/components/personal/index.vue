<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { Card } from '@xcan/design';
import certFrontPicUrl from '../../images/idCardFront.png';
import certBackPicUrl from '../../images/idCardBack.png';

import PageCard from '@/views/system/auth/components/cardPage/index.vue';

interface Form {
  certBackPicUrl: string
  certFrontPicUrl: string
  certNo: string
  name: string,
  status: string,
  reason: string
}

const { t } = useI18n();
const params = ref();

interface Props {
  data: Record<string, any>
}

const props = withDefaults(defineProps<Props>(), {
  data: () => ({})
});

const auditType = ['AUDITING', 'FAILED_AUDIT'];

const form = ref<Form>({
  certBackPicUrl: '',
  certFrontPicUrl: '',
  certNo: '',
  name: '',
  status: '',
  reason: ''
});

// 状态组件展示
const statusOption = reactive({
  visible: false,
  status: 'error',
  reason: ''
});

// 得到数据后的执行方法(确保获取到数据)
const start = function () {
  form.value = {
    ...params.value.personalCert,
    status: params.value.status.value,
    reason: params.value?.auditRecord?.reason
  };
  statusOption.visible = auditType.includes(form.value.status);
  statusOption.reason = form.value.reason;
};

onMounted(() => {
  params.value = props.data;
  start();
});

const contents = [
  t('适用于个人用户，账号归属于个人；'),
  t('不支持开增值税专用发票；'),
  t('认证人员需年满18周岁；'),
  t('一个证件只允许认证一个账号。')
];

</script>
<template>
  <div class="flex space-x-2">
    <PageCard
      :contents="contents"
      :pageTitle="t('个人认证')"
      icon="icon-gerenrenzheng"
      :success="form.status === 'AUDITED'" />
    <Card
      :title="t('个人认证')"
      class="flex-1"
      bodyClass="px-8 py-5">
      <div class="flex py-3.75">
        <div class="text-theme-content w-28 mr-7.5">{{ t('name2') }}</div>
        <div>{{ form.name }}</div>
      </div>
      <div class="flex py-3.75">
        <div class="text-theme-content w-28 mr-7.5">{{ t('idCardLabel') }}</div>
        <div>{{ form.certNo }}</div>
      </div>
      <div class="flex mt-8.75">
        <div class="mr-15">
          <div class="w-67.5 h-42 rounded flex items-center justify-center overflow-hidden border-theme-divider border">
            <img :src="certFrontPicUrl" class="w-full" />
          </div>
          <p class="text-center text-theme-content mt-3">{{ t('cardFront') }}</p>
        </div>
        <div>
          <div class="w-67.5 h-42 rounded flex items-center justify-center overflow-hidden border-theme-divider border">
            <img :src="certBackPicUrl" class="w-full" />
          </div>
          <p class="text-center text-theme-content mt-3">{{ t('cardBack') }}</p>
        </div>
      </div>
    </Card>
  </div>
</template>
