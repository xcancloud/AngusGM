<script setup lang="ts">
import { ref } from 'vue';
import { AppOrServiceRoute, DomainManager } from '@xcan-angus/infra';
import { Button } from 'ant-design-vue';
import { useI18n } from 'vue-i18n';
import { store } from '@/api';

import type { Goods } from '../types';

/**
 * Props interface for ShowButton component
 * - goods: The goods item to display action buttons for
 */
interface Props {
  goods: Goods;
}

const props = withDefaults(defineProps<Props>(), {
  goods: (): Goods => ({}) as Goods
});

/**
 * Emits
 * - reload: Triggered when goods state changes (install/upgrade)
 */
const emit = defineEmits<{(e: 'reload'): void }>();

const { t } = useI18n();
const loading = ref(false);

/**
 * Handle purchase action
 * Sends purchase message to parent window with pricing URL
 */
const handleToPrice = () => {
  if (props.goods.pricingUrl) {
    window.parent.postMessage({ e: 'purchase', value: props.goods.pricingUrl }, '*');
  }
};

/**
 * Navigate to private cloud deployment page
 * Sends message to parent window to redirect to deployment page
 */
const toPriCloud = async () => {
  const host = await DomainManager.getInstance().getAppDomain(AppOrServiceRoute.www);
  window.parent.postMessage({ e: 'purchase', value: host + '/deployment' }, '*');
};

/**
 * Handle goods upgrade
 * Calls upgrade API and emits reload event on success
 */
const toUpgrade = async () => {
  loading.value = true;

  try {
    const [error] = await store.onlineUpgradeInPriv(
      { upgradeToGoodsId: props.goods.goodsId },
      window.location.ancestorOrigins[0]
    );

    if (error) {
      return;
    }

    emit('reload');
  } finally {
    loading.value = false;
  }
};

/**
 * Handle goods installation
 * Calls install API and emits reload event on success
 */
const toInstall = async () => {
  loading.value = true;

  try {
    const [error] = await store.onlineInstallInPriv(
      props.goods.goodsId,
      window.location.ancestorOrigins[0]
    );

    if (error) {
      return;
    }

    emit('reload');
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <div>
    <!-- Paid goods that haven't been purchased -->
    <template v-if="goods.charge && !goods.purchased">
      <Button
        type="primary"
        ghost
        size="small"
        @click="handleToPrice">
        {{ t('open2p.messages.goToPurchase') }}
      </Button>
    </template>

    <!-- Free goods or already purchased -->
    <template v-else>
      <!-- Application type goods -->
      <template v-if="goods.editionType.value === 'APPLICATION'">
        <!-- Upgrade available -->
        <template v-if="goods.allowUpgrade">
          <Button
            type="primary"
            size="small"
            ghost
            @click="toPriCloud">
            {{ t('open2p.messages.goToUpgrade') }}
          </Button>
        </template>

        <!-- Installation available -->
        <template v-if="goods.allowInstall">
          <Button
            type="primary"
            size="small"
            ghost
            @click="toPriCloud">
            {{ t('open2p.messages.goToInstall') }}
          </Button>
        </template>

        <!-- Already installed -->
        <template v-if="goods.installed && !goods.allowUpgrade">
          <Button type="text" size="small">
            {{ t('open2p.messages.installed') }}
          </Button>
        </template>
      </template>

      <!-- Plugin or plugin application type goods -->
      <template v-else>
        <!-- Installation available -->
        <template v-if="goods.allowInstall">
          <Button
            type="primary"
            size="small"
            ghost
            :loading="loading"
            @click="toInstall">
            {{ t('open2p.messages.installNow') }}
          </Button>
        </template>

        <!-- Upgrade available -->
        <template v-if="goods.allowUpgrade">
          <Button
            type="primary"
            size="small"
            ghost
            :loading="loading"
            @click="toUpgrade">
            {{ t('open2p.messages.upgradeNow') }}
          </Button>
        </template>

        <!-- Already installed -->
        <template v-if="goods.installed && !goods.allowUpgrade">
          <Button type="text" size="small">
            {{ t('open2p.messages.installed') }}
          </Button>
        </template>
      </template>
    </template>
  </div>
</template>
