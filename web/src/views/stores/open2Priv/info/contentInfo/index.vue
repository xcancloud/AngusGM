<script setup lang="ts">
import { PureCard } from '@xcan-angus/vue-ui';
import { Divider, Skeleton } from 'ant-design-vue';
import { useI18n } from 'vue-i18n';
// import RichBrowser from '@xcan/browser';
import RichEditor from '@/components/RichEditor/index.vue';
import { Goods } from '../../types';

interface Props {
  loading: boolean;
  goods: Goods;
}

const props = withDefaults(defineProps<Props>(), {
  loading: false
});

const { t } = useI18n();

</script>
<template>
  <PureCard class="p-6 text-3">
    <Skeleton
      active
      :loading="loading"
      :title="true"
      :paragraph="{ rows: 10}">
      <div>
        <h4 class="text-theme-special text-3.5">{{ t('open2p.labels.productIntro') }}</h4>
        <div class="text-black-title mt-5">{{ props.goods?.introduction }}</div>
      </div>
      <Divider class="my-6 divider" />
      <template v-if="!!props.goods?.features">
        <div>
          <h4 class="text-theme-special text-3.5">{{ t('open2p.labels.productFeatures') }}</h4>
          <ul class="mt-5 text-black-title text-3">
            <li v-for="item in props.goods?.features" :key="item">
              {{ item }}
            </li>
          </ul>
        </div>
        <Divider class="my-6 divider" />
      </template>
      <div>
        <h4 class="text-theme-special text-3.5">{{ t('open2p.labels.productInfo') }}</h4>
        <RichEditor :value="props.goods?.information || ''" mode="view" />
        <!-- <RichBrowser
          :value="props.goods?.information || ''"
          contentStyle="pre{white-space: wrap}"
          style="scrollbar-gutter: stable;" /> -->
      </div>
    </Skeleton>
  </PureCard>
</template>
<style scoped>
.divider {
  background-color: rgba(235, 237, 248, 100%);
}

.title-icon-bg {
  background-color: #008fff;
}

.title-text {
  color: #008fff;
}

h4 {
  position: relative;
  padding-left: 12px;
}

h4::before {
  content: "";
  display: inline-block;
  position: absolute;
  top: 7px;
  left: 0;
  width: 0;
  height: 0;
  margin-right: 4px;
  border: 3px solid;
}
</style>
