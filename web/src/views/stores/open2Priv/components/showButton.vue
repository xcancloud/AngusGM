<script setup lang="ts">
import { ref } from 'vue';
import { site } from '@xcan-angus/tools';
import { Button } from 'ant-design-vue';
import { store } from '@/api';

interface Props {
  goods: any,
}

const props = withDefaults(defineProps<Props>(), {
  goods: {}
});
const emit = defineEmits<{(e: 'reload'): void }>();

const loading = ref(false);

// 去购买
const handleToPrice = () => {
  if (props.goods.pricingUrl) {
    window.parent.postMessage({ e: 'purchase', value: props.goods.pricingUrl }, '*');
  }
};

// 跳到官网安装、下载
const toPriCloud = async () => {
  const host = await site.getUrl('www');
  window.parent.postMessage({ e: 'purchase', value: host + '/deployment' }, '*');
};

const toUpgrade = async () => {
  loading.value = true;
  const [error] = await store.onlineUpgradeInPriv({ upgradeToGoodsId: props.goods.goodsId }, window.location.ancestorOrigins[0]);
  loading.value = false;
  if (error) {
    return;
  }
  emit('reload');
};

const toInstall = async () => {
  loading.value = true;
  const [error] = await store.onlineInstallInPriv(props.goods.goodsId, window.location.ancestorOrigins[0]);
  loading.value = false;
  if (error) {
    return;
  }
  emit('reload');
};

</script>
<template>
  <div>
    <!-- 收费 且未购买-->
    <template v-if="goods.charge && !goods.purchased">
      <Button
        type="primary"
        ghost
        size="small"
        @click="handleToPrice">
        去购买
      </Button>
    </template>
    <!-- 免费/或已购买 -->
    <template v-else>
      <!-- 应用 -->
      <template v-if="goods.editionType.value === 'APPLICATION'">
        <template v-if="goods.allowUpgrade">
          <Button
            type="primary"
            size="small"
            ghost
            @click="toPriCloud">
            去升级
          </Button>
        </template>
        <!-- 未安装 -->
        <template v-if="goods.allowInstall">
          <Button
            type="primary"
            size="small"
            ghost
            @click="toPriCloud">
            去安装
          </Button>
        </template>
        <!-- 已安装 -->
        <template v-if="goods.installed && !goods.allowUpgrade">
          <Button type="text" size="small">
            已安装
          </Button>
        </template>
      </template>
      <!-- 插件， 插件应用 -->
      <template v-else>
        <!-- 可安装 -->
        <template v-if="goods.allowInstall">
          <Button
            type="primary"
            size="small"
            ghost
            :loading="loading"
            @click="toInstall">
            立即安装
          </Button>
        </template>
        <!-- 可升级 -->
        <template v-if="goods.allowUpgrade">
          <Button
            type="primary"
            size="small"
            ghost
            :loading="loading"
            @click="toUpgrade">
            立即升级
          </Button>
        </template>
        <!-- 已安装 -->
        <template v-if="goods.installed && !goods.allowUpgrade">
          <Button type="text" size="small">
            已安装
          </Button>
        </template>
      </template>
    </template>
  </div>
</template>
