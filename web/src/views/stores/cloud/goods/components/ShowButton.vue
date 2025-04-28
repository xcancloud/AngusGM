<script setup lang="ts">
import { ref } from 'vue';
import { downloadEditionTypes, isCloudGoods, isPriGoods, multipleEditionTypes } from '../PropsType';
import { Button, Popconfirm, RadioButton, RadioGroup } from 'ant-design-vue';
import { site, download, ESS } from '@xcan-angus/tools';
import { useI18n } from 'vue-i18n';
import { notification } from '@xcan-angus/vue-ui';

const { t } = useI18n();

interface Props {
  goods: any;
  downLoading: boolean
}

const props = withDefaults(defineProps<Props>(), {
  goods: {}
});

const emit = defineEmits<{(e: 'update:downLoading', value: boolean): void }>();

const downloadEdition = ref(downloadEditionTypes(props.goods)?.[0]?.value);

const downLoading = ref(false);

const toDownloadApply = async () => {
  const host = await site.getUrl('www');
  window.open(host + '/deployment', '_blank');
};

const downloadInstallEdition = async (installType: string = downloadEdition.value) => {
  if (props.goods.goodsType.value === 'APPLICATION') {
    await toDownloadApply();
    return;
  }
  downLoading.value = true;
  emit('update:downLoading', true);
  const host = await site.getUrl('apis');
  const [error] = await download(`${host}${ESS}/package/plugin/${props.goods.goodsId}/${installType}/download`);
  if (error) {
    notification.error(error.message);
  }
  downLoading.value = false;
  emit('update:downLoading', false);
};

// 跳到官网
const toDeployUrl = async () => {
  const host = await site.getUrl('www');
  window.open(`${host}/deployment`, '_blank');
};
</script>
<template>
  <template v-if="props.goods.goodsType.value === 'APPLICATION'">
    <Button
      v-if="isCloudGoods(props.goods.applyEditionTypes)"
      size="small"
      class="ml-3"
      type="primary"
      ghost>
      {{ t('已开通') }}
    </Button>
    <Button
      v-if="isPriGoods(props.goods.applyEditionTypes)"
      size="small"
      class="ml-3"
      type="primary"
      ghost
      @click="toDeployUrl">
      {{ t('去下载') }}
    </Button>
  </template>
  <template v-else>
    <template v-if="isPriGoods(props.goods.applyEditionTypes)">
      <Popconfirm
        v-if="multipleEditionTypes(props.goods)"
        placement="leftTop"
        @confirm="downloadInstallEdition()">
        <template #title>
          <p>选择安装版本类型</p>
          <RadioGroup
            v-model:value="downloadEdition"
            size="small"
            buttonStyle="solid">
            <RadioButton
              v-for="types in downloadEditionTypes(props.goods)"
              :key="types.value"
              :value="types.value">
              {{ types.message }}
            </RadioButton>
          </RadioGroup>
        </template>
        <Button
          type="primary"
          size="small"
          ghost
          :loading="downLoading">
          {{ t('下载') }}
        </Button>
      </Popconfirm>
      <Button
        v-else
        size="small"
        class="ml-3"
        type="primary"
        ghost
        :loading="downLoading"
        @click="downloadInstallEdition()">
        {{ t('下载') }}
      </Button>
    </template>
  </template>
</template>
