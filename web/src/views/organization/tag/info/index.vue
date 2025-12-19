<script setup lang="ts">
import { defineProps, withDefaults, defineEmits } from 'vue';
import { Card, Icon } from '@xcan-angus/vue-ui';
import { useI18n } from 'vue-i18n';
import type { TagInfoProps } from '@/views/organization/tag/types';

// Component props with default values
const props = withDefaults(defineProps<TagInfoProps>(), {
  tag: undefined,
  canModify: false
});

// Emit events for parent component communication
const emit = defineEmits<{(e: 'editName'): void }>();

// Handle edit name action
const onEdit = () => emit('editName');

const { t } = useI18n();
</script>

<template>
  <Card v-show="props.tag?.id" class="mb-2">
    <template #title>
      <span class="text-3">{{ t('tag.basicInfo') }}</span>
    </template>

    <!-- Tag information display with consistent layout -->
    <div class="tag-info-display">
      <!-- Row 1: Name, ID, Created By -->
      <div class="info-row">
        <div class="info-item">
          <div class="info-label">
            <Icon icon="icon-biaoqian2" class="info-icon" />
            {{ t('tag.columns.name') }}
          </div>
          <div class="info-value">
            {{ props.tag?.name || '' }}
            <!-- Edit icon for name modification -->
            <Icon
              v-if="props.canModify"
              icon="icon-shuxie"
              class="text-text-link cursor-pointer hover:text-text-link-hover ml-3"
              @click="onEdit" />
          </div>
        </div>
        <div class="info-item">
          <div class="info-label">
            <Icon icon="icon-a-ID1" class="info-icon" />
            {{ t('ID') }}
          </div>
          <div class="info-value">{{ props.tag?.id || '--' }}</div>
        </div>
        <div class="info-item">
          <div class="info-label">
            <Icon icon="icon-yonghu" class="info-icon" />
            {{ t('common.columns.creator') }}
          </div>
          <div class="info-value">{{ props.tag?.creator || '--' }}</div>
        </div>
      </div>

      <!-- Row 2: Created Date, Last Modified By, Last Modified Date -->
      <div class="info-row">
        <div class="info-item">
          <div class="info-label">
            <Icon icon="icon-shijianriqi" class="info-icon" />
            {{ t('common.columns.createdDate') }}
          </div>
          <div class="info-value">{{ props.tag?.createdDate || '' }}</div>
        </div>
        <div class="info-item">
          <div class="info-label">
            <Icon icon="icon-yonghu" class="info-icon" />
            {{ t('department.columns.modifier') }}
          </div>
          <div class="info-value">{{ props.tag?.modifier || '--' }}</div>
        </div>
        <div class="info-item">
          <div class="info-label">
            <Icon icon="icon-shijianriqi" class="info-icon" />
            {{ t('department.columns.modifiedDate') }}
          </div>
          <div class="info-value">{{ props.tag?.modifiedDate || '--' }}</div>
        </div>
      </div>
    </div>
  </Card>
</template>

<style scoped>
/* Tag information display layout styles */
.tag-info-display {
  padding: 4px 0;
}

.info-row {
  display: flex;
  gap: 4px;
  margin-bottom: 4px;
  padding: 4px 0;
}

.info-row:last-child {
  border-bottom: none;
  margin-bottom: 0;
}

.info-item {
  flex: 1;
  min-width: 0;
}

.info-label {
  display: flex;
  align-items: center;
  font-size: 12px;
  color: #666;
  margin-bottom: 4px;
  font-weight: 500;
  padding-left: 20px;
}

.info-icon {
  margin-right: 6px;
  font-size: 12px;
  color: #1890ff;
}

.info-value {
  font-size: 12px;
  color: #262626;
  font-weight: 500;
  word-break: break-word;
  line-height: 1.4;
  padding-left: 40px;
}

/* Responsive design for mobile devices */
@media (max-width: 768px) {
  .info-row {
    flex-direction: column;
    gap: 8px;
  }
  .info-item {
    flex: none;
  }
}
</style>
