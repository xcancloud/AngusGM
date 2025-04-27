<script setup lang="ts">
import Logo from '@/components/Logo/index.vue';
import OfficialLink from '@/components/OfficialLink/index.vue';
// import Language from '@/components/Language/index.vue';
import Records from '@/components/CopyRight/index.vue';

import { ref } from 'vue';
import { useI18n } from 'vue-i18n';
import Tab from '@/components/SignTab/index.vue';
import Form from './form.vue';

const preventDefaultListener = (e) => {
  e.preventDefault();
};

const { t } = useI18n();
const activeValue = ref('mobile');
const tabList = [
  { label: t('register-mobile'), value: 'mobile' },
  { label: t('register-email'), value: 'email' }
];
</script>

<template>
  <div class="layout-container">
    <div class="layout-header">
      <Logo />
      <div class="layout-header-right">
        <OfficialLink />
        <!-- <Language /> -->
      </div>
    </div>
    <div class="layout-inner-container">
      <img
        class="img-front"
        src="./assets/signup.svg"
        @contextmenu="preventDefaultListener"
        @dragstart="preventDefaultListener"
        @touchstart="preventDefaultListener">
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
