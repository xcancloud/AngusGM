<script setup lang="ts">
import { ref } from 'vue';
import { downloadEditionTypes, isCloudGoods, isPriGoods, multipleEditionTypes } from '../PropsType';
import { Button, Popconfirm, RadioButton, RadioGroup } from 'ant-design-vue';
import { http, site, download, STORE } from '@xcan-angus/tools';
import { notification } from '@xcan-angus/vue-ui';

interface Props {
  goods: any,
  downLoading: boolean;
  disabled: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  goods: {},
  disabled: false
});

const emit = defineEmits<{(e: 'reload'): void, (e: 'update:downLoading', value: boolean): void }>();

const downloadEdition = ref(downloadEditionTypes(props.goods)?.[0]?.value);

const downLoading = ref(false);

const topay = () => {
  window.open(props.goods.pricingUrl, '_blank');
  // window.parent.postMessage({ e: 'purchase', value: props.goods.pricingUrl }, '*');
  // window.parent.location.href = props.goods.pricingUrl;
};

const toDownloadApply = async () => {
  const host = await site.getUrl('www');
  window.open(host + '/deployment', '_blank');
};

const toDownload = async () => {
  if (props.goods.type.value === 'PLUGIN' || props.goods.type.value === 'PLUGIN_APPLICATION') {
    const installType = props.goods.applyEditionTypes[0].value;
    downloadInstallEdition(installType);
    return;
  }
  if (props.goods.type.value === 'APPLICATION') {
    await toDownloadApply();
  }
};

const downloadInstallEdition = async (installType: string = downloadEdition.value) => {
  if (props.goods.type.value === 'APPLICATION') {
    await toDownloadApply();
    return;
  }
  downLoading.value = true;
  emit('update:downLoading', true);
  const host = await site.getUrl('apis');
  const [error] = await download(`${host}${STORE}/package/plugin/${props.goods.goodsId}/${installType}/download`);
  if (error) {
    notification.error(error.message);
  }
  downLoading.value = false;
  emit('update:downLoading', false);
};

const open = async () => {
  await http.post(`${STORE}/store/purchase/free/open`, {
    goodsId: props.goods.goodsId
  });
  emit('reload');
};

</script>
<template>
  <div class="space-x-2.5">
    <!-- 收费 -->
    <template v-if="goods.charge">
      <!-- 已购买 -->
      <template v-if="goods.purchased && !goods.expired">
        <!--  私有化商品 -->
        <template v-if="isPriGoods(goods)">
          <Popconfirm
            v-if="multipleEditionTypes(goods)"
            :disabled="props.disabled"
            placement="leftTop"
            @confirm="downloadInstallEdition()">
            <template #title>
              <p>选择安装版本类型</p>
              <RadioGroup
                v-model:value="downloadEdition"
                size="small"
                :disabled="props.disabled"
                buttonStyle="solid">
                <RadioButton
                  v-for="types in downloadEditionTypes(goods)"
                  :key="types.value"
                  :value="types.value">
                  {{ types.message }}
                </RadioButton>
              </RadioGroup>
            </template>
            <Button
              type="primary"
              ghost
              size="small"
              :disabled="props.disabled"
              :loading="downLoading">
              立即下载
            </Button>
          </Popconfirm>
          <Button
            v-else
            type="primary"
            ghost
            size="small"
            :disabled="props.disabled"
            :loading="downLoading"
            @click="toDownload">
            立即下载
          </Button>
        </template>
        <!-- 云服务商品 -->
        <Button
          v-if="isCloudGoods(goods)"
          disabled
          size="small">
          已开通
        </Button>
      </template>

      <!-- 未购买 -->
      <template v-else>
        <Button
          type="primary"
          ghost
          :disabled="props.disabled"
          size="small"
          @click="topay">
          去购买
        </Button>
      </template>
    </template>

    <!-- 免费 -->
    <template v-else>
      <template v-if="isPriGoods(goods)">
        <Popconfirm
          v-if="multipleEditionTypes(goods)"
          :disabled="props.disabled"
          placement="leftTop"
          @confirm="downloadInstallEdition()">
          <template #title>
            <p>选择安装版本类型</p>
            <RadioGroup
              v-model:value="downloadEdition"
              :disabled="props.disabled"
              size="small"
              buttonStyle="solid">
              <RadioButton
                v-for="types in downloadEditionTypes(goods)"
                :key="types.value"
                :value="types.value">
                {{ types.message }}
              </RadioButton>
            </RadioGroup>
          </template>
          <Button
            type="primary"
            ghost
            :disabled="props.disabled"
            size="small"
            :loading="downLoading">
            立即下载
          </Button>
        </Popconfirm>
        <Button
          v-else
          type="primary"
          ghost
          :disabled="props.disabled"
          size="small"
          :loading="downLoading"
          @click="toDownload">
          立即下载
        </Button>
      </template>
      <template v-if="isCloudGoods(goods)">
        <template v-if="!goods.purchased">
          <Button
            type="primary"
            ghost
            :disabled="props.disabled"
            size="small"
            @click="open">
            立即开通
          </Button>
        </template>
        <template v-else>
          <Button
            disabled
            size="small">
            已开通
          </Button>
        </template>
      </template>
    </template>
  </div>
</template>
