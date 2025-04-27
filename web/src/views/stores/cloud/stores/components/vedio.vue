<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import { cookie } from '@xcan/utils';
import { Icon, Modal } from '@xcan/design';
import 'vue3-video-play/dist/style.css';
import { videoPlay } from 'vue3-video-play';

interface Props {
  src: string;
  width: number;
  name: string;
}

const props = withDefaults(defineProps<Props>(), {
  src: ''
});
const videoRef = ref();
const visible = ref(false);

const videoSrc = computed(() => {
  if (/\/pubapi\//gi.test(props.src)) {
    return props.src;
  }

  try {
    const url = new URL(props.src);
    url.searchParams.set('access_token', cookie.get('access_token') as string);
    return url.href;
  } catch (error) {
    return props.src;
  }
});

const playVideo = () => {
  visible.value = true;
  setTimeout(() => {
    videoRef.value.play();
  });
};

watch(() => visible.value, newValue => {
  if (!newValue) {
    videoRef.value.pause();
  }
});
</script>
<template>
  <div class="w-full relative overflow-hidden">
    <video
      :width="props.width"
      class="h-full">
      <source :src="videoSrc">
    </video>
    <div class="absolute w-full h-full top-0 left-0 z-9 bg-black-mask">
      <Icon
        icon="icon-bofangcishu"
        class="text-white my-0 mx-auto text-10 mt-8 cursor-pointer"
        @click="playVideo" />
    </div>
    <Modal
      v-model:visible="visible"
      :title="props.name"
      width="850px"
      :footer="false">
      <videoPlay
        ref="videoRef"
        :src="videoSrc"
        :speed="true" />
    </Modal>
  </div>
</template>
