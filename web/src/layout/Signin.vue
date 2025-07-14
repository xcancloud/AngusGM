<script setup lang="ts">
import Logo from '@/components/Logo/index.vue';
import OfficialLink from '@/components/OfficialLink/index.vue';
import Records from '@/components/CopyRight/index.vue';

const preventDefaultListener = (e) => {
  e.preventDefault();
};
</script>
<template>
  <div class="layout-container">
    <div class="layout-header">
      <Logo />
      <div class="layout-header-right">
        <OfficialLink />
      </div>
    </div>
    <div class="layout-inner-container">
      <div class="img-front">
        <div class="title">AngusGM</div>
        <div class="content">
          统一资源认证与授权中心 ▪ 开启安全和资源共享 ▪ 基础资源统一化管理！
        </div>
        <img
          src="./assets/sign-in.svg"
          @contextmenu="preventDefaultListener"
          @dragstart="preventDefaultListener"
          @touchstart="preventDefaultListener">
      </div>
      <div class="main-content-container">
        <RouterView />
      </div>
      <img
        class="img-end"
        src="./assets/sign-background.png"
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
  background-image: url("./assets/background.svg");
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

.layout-inner-container {
  display: flex;
  position: relative;
  flex: 1 1 0;
  justify-content: space-between;
  margin-top: 60px;
}

.img-front {
  display: flex;
  position: relative;
  z-index: 10;
  flex-direction: column;
  flex-grow: 0;
  flex-shrink: 0;
  align-items: center;
  justify-content: center;
  width: 451px;
  margin-left: 12.5%;
}

.layout-container .title,
.layout-container .content {
  color: #536185;
  pointer-events: none;
  user-select: none;
}

.layout-container .title {
  width: 364px;
  margin-bottom: 12px;
  font-size: 30px;
  line-height: 30px;
}

.layout-container .content {
  width: 364px;
  margin-bottom: 8px;
  font-size: 19px;
  line-height: 30px;
}

.img-end {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: calc(100% + 28px);
}

.main-content-container {
  position: relative;
  z-index: 10;
  width: 460px;
  margin-top: 60px;
  margin-right: 12.5%;
  margin-bottom: 180px;
  padding: 40px;
  border-radius: 24px;
  background-color: rgb(255, 255, 255, 100%);
  box-shadow: 0 6px 20px 0 rgba(0, 0, 0, 5%);
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
