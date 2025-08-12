<script setup lang="ts">
import Logo from '@/components/Logo/index.vue';
import OfficialLink from '@/components/OfficialLink/index.vue';
// import Language from '@/components/Language/index.vue';
import Records from '@/components/CopyRight/index.vue';

import { ref } from 'vue';
import { useI18n } from 'vue-i18n';
import Tab from '@/components/SignTab/index.vue';
import Form from './form.vue';

/**
 * Prevent default browser behavior for image interactions
 * Disables context menu, drag, and touch events on images
 */
const preventDefaultListener = (e: Event) => {
  e.preventDefault();
};

const { t } = useI18n();

// Active tab selection for registration type
const activeValue = ref<'mobile' | 'email'>('mobile');

/**
 * Tab list for registration methods
 * Supports both mobile and email registration
 */
const tabList = [
  { label: t('sign.tabs.registerMobile'), value: 'mobile' },
  { label: t('sign.tabs.registerEmail'), value: 'email' }
];
</script>

<template>
  <div class="layout-container">
    <!-- Header with logo and official links -->
    <div class="layout-header">
      <Logo />
      <div class="layout-header-right">
        <OfficialLink />
        <!-- Language selector commented out for now -->
        <!-- <Language /> -->
      </div>
    </div>

    <!-- Main content area with decorative images -->
    <div class="layout-inner-container">
      <img
        class="img-front"
        src="./assets/signup.svg"
        @contextmenu="preventDefaultListener"
        @dragstart="preventDefaultListener"
        @touchstart="preventDefaultListener">

      <!-- Registration form container -->
      <div class="main-content-container">
        <Tab v-model:activeKey="activeValue" :tabList="tabList" />
        <Form :type="activeValue" />
      </div>

      <img
        class="img-end"
        src="./assets/inner.png"
        @contextmenu="preventDefaultListener"
        @dragstart="preventDefaultListener"
        @touchstart="preventDefaultListener">
    </div>

    <!-- Footer copyright -->
    <Records class="layout-footer" />
  </div>
</template>

<style scoped>
.layout-container {
  display: flex;
  flex-direction: column;
  min-width: 1200px;
  min-height: 100%;
  padding-top: 28px;
  padding-bottom: 12px;
  background-image: url("./assets/bg.svg");
  background-repeat: no-repeat;
  background-size: cover;
}

.layout-header {
  display: flex;
  position: relative;
  z-index: 10;
  flex-wrap: nowrap;
  align-items: center;
  justify-content: space-between;
  padding-right: 40px;
  padding-left: 40px;
  white-space: nowrap;
}

.layout-header-right {
  display: flex;
  flex-wrap: nowrap;
  align-items: center;
  white-space: nowrap;
}

.layout-header-right > * + * {
  margin-left: 40px;
}

.main-content-container {
  position: relative;
  z-index: 10;
  width: 460px;
  margin-top: 60px;
  margin-right: 12.5%;
  margin-bottom: 60px;
  padding: 40px;
  border-radius: 24px;
  background-color: rgba(255, 255, 255, 100%);
  box-shadow: 0 6px 20px 0 rgba(0, 0, 0, 5%);
}

.layout-inner-container {
  display: flex;
  position: relative;
  flex: 1 1 0;
  justify-content: space-between;
}

.img-front {
  z-index: 10;
  flex-grow: 0;
  flex-shrink: 0;
  justify-content: space-between;
  margin-left: 12.5%;
}

.img-end {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
}

.layout-footer {
  flex: 1 1 0;
}

/* Responsive design for different screen sizes */
@media (width <= 1350px) {
  .img-front {
    margin-left: 8%;
  }
}

@media (width <= 1200px) {
  .img-front {
    margin-left: 5%;
  }
}
</style>
