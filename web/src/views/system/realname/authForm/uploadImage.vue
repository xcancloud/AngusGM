<script setup lang="ts">
import Viewer from 'viewerjs';
import 'viewerjs/dist/viewer.css';
import { cookieUtils, upload } from '@xcan-angus/infra';
import { Icon } from '@xcan-angus/vue-ui';

import { useI18n } from 'vue-i18n';

const { t } = useI18n();

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
        const token = cookieUtils.getTokenInfo().access_token;
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
  <div class="upload-container">
    <!-- Upload container with different dimensions based on type -->
    <!-- Type 1: ID card, Type 2: Other certificates -->
    <div
      :class="[
        'upload-area',
        type === '1' ? 'h-28 w-48' : 'h-40 w-32',
        { 'upload-error': error }
      ]">

      <!-- Upload icon when no image is present -->
      <div v-if="!imageUrl" class="upload-placeholder" @click="selectFile">
        <Icon
          icon="icon-shangchuanzhengjian"
          class="upload-icon" />
        <span class="upload-text">{{ t('common.actions.upload')}}</span>
      </div>

      <!-- Display uploaded image -->
      <img
        v-else
        ref="viewerImageRef"
        class="uploaded-image"
        :src="imageUrl" />

      <!-- Image action overlay (view/delete) -->
      <div
        v-if="imageUrl"
        class="image-actions">
        <Icon
          icon="icon-zhengyan"
          class="action-icon view-icon"
          @click="viewImage" />
        <Icon
          icon="icon-lajitong"
          class="action-icon delete-icon"
          @click="deleteImage" />
      </div>
    </div>

    <!-- Description message for ID card type -->
    <p v-show="type === '1'" class="upload-description">{{ _message }}</p>

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

<style scoped>
.upload-container {
  display: inline-block;
}

.upload-area {
  position: relative;
  border-radius: 1rem;
  overflow: hidden;
  border: 2px solid #E5E7EB;
  background: linear-gradient(135deg, #F9FAFB 0%, #F3F4F6 100%);
  transition: all 0.3s ease;
  cursor: pointer;
}

.upload-area:hover {
  border-color: #3B82F6;
  transform: translateY(-2px);
  box-shadow: 0 8px 25px 0 rgba(59, 130, 246, 0.15);
}

.upload-area.upload-error {
  border-color: #EF4444;
  background: linear-gradient(135deg, #FEF2F2 0%, #FEE2E2 100%);
  animation: shake 0.5s ease-in-out;
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  padding: 1rem;
  text-align: center;
}

.upload-icon {
  font-size: 2rem;
  color: #9CA3AF;
  margin-bottom: 0.5rem;
  transition: color 0.3s ease;
}

.upload-area:hover .upload-icon {
  color: #3B82F6;
}

.upload-text {
  font-size: 0.875rem;
  color: #6B7280;
  font-weight: 500;
}

.uploaded-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-actions {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1rem;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.upload-area:hover .image-actions {
  opacity: 1;
}

.action-icon {
  font-size: 1.5rem;
  color: white;
  cursor: pointer;
  padding: 0.5rem;
  border-radius: 0.5rem;
  transition: all 0.3s ease;
}

.view-icon:hover {
  background: rgba(59, 130, 246, 0.8);
  transform: scale(1.1);
}

.delete-icon:hover {
  background: rgba(239, 68, 68, 0.8);
  transform: scale(1.1);
}

.upload-description {
  margin-top: 0.75rem;
  text-align: center;
  font-size: 0.875rem;
  color: #6B7280;
  font-weight: 500;
}

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-5px); }
  75% { transform: translateX(5px); }
}
</style>
