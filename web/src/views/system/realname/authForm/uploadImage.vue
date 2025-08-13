<script setup lang="ts">
import Viewer from 'viewerjs';
import 'viewerjs/dist/viewer.css';
import { cookieUtils, upload } from '@xcan-angus/infra';
import { Icon } from '@xcan-angus/vue-ui';

import { defineEmits, defineProps, ref, watch, withDefaults } from 'vue';

interface Props {
  mess: string, // Message or image name
  type: string, // Upload type identifier
  error: boolean // Whether there's an upload error
}

const props = withDefaults(defineProps<Props>(), {
  type: '1',
  mess: '',
  error: false
});

const emit = defineEmits(['change']);

// Image viewer component instance
let viewerObj: any;

// Image reference for viewer
const viewerImageRef = ref();

// Description message or image name
const _message = ref('');
watch(() => props.mess, (newVal) => {
  _message.value = newVal;
}, { immediate: true });

// Image URL for display
const imageUrl = ref('');

// File input reference
const upFile = ref();

/**
 * Trigger file selection dialog
 */
const selectFile = function () {
  if (upFile.value) {
    upFile.value.click();
  }
};

/**
 * Handle file selection and upload
 * @param el - File input event target
 */
const fileChange = async function (el: any) {
  const file: File = el.target.files?.[0];
  if (file) {
    const [error, res] = await upload(file, { bizKey: 'realnameCertification' });
    if (error) {
      return;
    }

    const data = (res as any).data;
    if (Array.isArray(data) && data[0]) {
      const item = data[0];
      emit('change', item.url);

      try {
        const _url = new URL(item.url);
        const token = cookieUtils.get('access_token');
        _url.searchParams.set('access_token', token as string);
        imageUrl.value = _url.toString();
      } catch {
        imageUrl.value = item.url;
      }
    }
  }
};

/**
 * Initialize and show image viewer
 */
const viewImage = function () {
  if (!viewerObj) {
    viewerObj = new Viewer(viewerImageRef.value, {
      navbar: false,
      toolbar: {
        zoomIn: true,
        zoomOut: true,
        reset: true,
        rotateLeft: true,
        rotateRight: true
      }
    });
  }
  viewerObj.show();
};

/**
 * Delete uploaded image
 */
const deleteImage = function () {
  imageUrl.value = '';
  _message.value = props.mess;
  emit('change', '');
};

</script>

<template>
  <div class="w-45">
    <!-- Upload container with different dimensions based on type -->
    <!-- Type 1: ID card, Type 2: Other certificates -->
    <div
      :class="[ type === '1' ? 'h-27.5 w-45' : 'h-40 w-30', {'border-danger': error}, 'overflow-hidden border flex items-center relative border-theme-text-box']">
      <!-- Upload icon when no image is present -->
      <Icon
        v-if="!imageUrl"
        icon="icon-shangchuanzhengjian"
        class="mx-auto text-3xl cursor-pointer text-theme-sub-content text-theme-text-hover"
        @click="selectFile" />

      <!-- Display uploaded image -->
      <img
        v-else
        ref="viewerImageRef"
        class="w-full"
        :src="imageUrl" />

      <!-- Image action overlay (view/delete) -->
      <div
        v-if="imageUrl"
        class="absolute top-0 left-0 w-full h-full bg-black-mask flex items-center justify-center opacity-0 hover:opacity-100">
        <Icon
          icon="icon-zhengyan"
          class="text-2xl text-white cursor-pointer"
          @click="viewImage" />
        <Icon
          icon="icon-lajitong"
          class="text-2xl text-white ml-5 cursor-pointer"
          @click="deleteImage" />
      </div>
    </div>

    <!-- Description message for ID card type -->
    <p v-show="type === '1'" class="mt-3 text-center text-3 leading-3 text-gray-forget">{{ _message }}</p>

    <!-- Hidden file input for upload -->
    <input
      v-if="imageUrl === ''"
      ref="upFile"
      type="file"
      accept=".jpg,.jpeg,.bmp,.png"
      style="display: none;"
      @change="fileChange">
  </div>
</template>
