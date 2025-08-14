<script setup lang="ts">
import { ref } from 'vue';
import { downloadEditionTypes, isCloudGoods, isPriGoods, multipleEditionTypes } from './types';
import { Button, Popconfirm, RadioButton, RadioGroup } from 'ant-design-vue';
import { AppOrServiceRoute, DomainManager, download, routerUtils } from '@xcan-angus/infra';
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

/**
 * <p>Preferred download edition type bound to the popconfirm radio group.</p>
 * <p>Default to the first available type from goods meta.</p>
 */
const downloadEdition = ref(downloadEditionTypes(props.goods)?.[0]?.value);

/**
 * <p>Local loading flag to avoid double clicks and reflect UI loading.</p>
 */
const downLoading = ref(false);

/**
 * <p>Open official deployment page in a new tab.</p>
 * <p>Fix: await domain resolving to avoid opening url with a Promise.</p>
 */
const toDownloadApply = () => {
  const host = DomainManager.getInstance().getAppDomain(AppOrServiceRoute.www);
  window.open(host + '/deployment', '_blank');
};

/**
 * <p>Download install edition package.</p>
 * <p>Guard: if goods type is APPLICATION, redirect to deployment page instead.</p>
 */
const downloadInstallEdition = async (installType: string = downloadEdition.value) => {
  if (props.goods.goodsType.value === 'APPLICATION') {
    await toDownloadApply();
    return;
  }
  downLoading.value = true;
  emit('update:downLoading', true);
  const url = routerUtils.getESSApiUrl(`/package/plugin/${props.goods.goodsId}/${installType}/download`);
  const [error] = await download(url);
  if (error) {
    notification.error(error.message);
  }
  downLoading.value = false;
  emit('update:downLoading', false);
};

/**
 * <p>Go to deployment page on the official website.</p>
 */
const toDeployUrl = () => {
  const host = DomainManager.getInstance().getAppDomain(AppOrServiceRoute.www);
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
      {{ t('cloud.messages.alreadyActivated') }}
    </Button>
    <Button
      v-if="isPriGoods(props.goods.applyEditionTypes)"
      size="small"
      class="ml-3"
      type="primary"
      ghost
      @click="toDeployUrl">
      {{ t('cloud.messages.download') }}
    </Button>
  </template>
  <template v-else>
    <template v-if="isPriGoods(props.goods.applyEditionTypes)">
      <Popconfirm
        v-if="multipleEditionTypes(props.goods)"
        placement="leftTop"
        @confirm="downloadInstallEdition()">
        <template #title>
          <p>{{ t('cloud.messages.selectInstallVersion') }}</p>
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
          {{ t('cloud.messages.download') }}
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
        {{ t('cloud.messages.download') }}
      </Button>
    </template>
  </template>
</template>
