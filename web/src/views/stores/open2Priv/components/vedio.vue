<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import { cookieUtils } from '@xcan-angus/infra';
import { Icon, Modal } from '@xcan-angus/vue-ui';
import 'vue3-video-play/dist/style.css';
import { videoPlay } from 'vue3-video-play';

/**
 * Props interface for Video component
 * - src: Video source URL
 * - width: Video width in pixels
 * - name: Video name/title for modal display
 */
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

/**
 * Computed video source with access token
 * Adds access token to video URL for authentication if needed
 * Handles both public API URLs and custom URLs
 */
const videoSrc = computed(() => {
  // Public API URLs don't need access token
  if (/\/pubapi\//gi.test(props.src)) {
    return props.src;
  }

  try {
    const url = new URL(props.src);
    url.searchParams.set('access_token', cookieUtils.get('access_token') as string);
    return url.href;
  } catch (error) {
    // Return original source if URL parsing fails
    return props.src;
  }
});

/**
 * Play video in modal
 * Shows modal and starts video playback after a short delay
 */
const playVideo = () => {
  visible.value = true;
  setTimeout(() => {
    videoRef.value.play();
  });
};

/**
 * Watch modal visibility changes
 * Pauses video when modal is closed
 */
watch(() => visible.value, newValue => {
  if (!newValue) {
    videoRef.value.pause();
  }
});
</script>

<template>
  <div class="w-full relative overflow-hidden">
    <!-- Video preview with play button overlay -->
    <video
      :width="props.width"
      class="h-full">
      <source :src="videoSrc">
    </video>
    
    <!-- Play button overlay -->
    <div class="absolute w-full h-full top-0 left-0 z-9 bg-black-mask">
      <Icon
        icon="icon-bofangcishu"
        class="text-white my-0 mx-auto text-10 mt-8 cursor-pointer"
        @click="playVideo" />
    </div>
    
    <!-- Video modal for full-screen playback -->
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
