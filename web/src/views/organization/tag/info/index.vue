<script setup lang="ts">
import { defineProps, withDefaults, defineEmits } from 'vue';
import { Card, Icon } from '@xcan-angus/vue-ui';
import { useI18n } from 'vue-i18n';
import type { OrgTag } from '@/views/organization/tag/types';

interface Props {
  tag?: OrgTag;
  canModify?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  tag: undefined,
  canModify: false
});

const emit = defineEmits<{(e: 'editName'): void }>();
const onEdit = () => emit('editName');

const { t } = useI18n();
</script>

<template>
  <Card v-show="props.tag?.id" class="mb-2">
    <template #title>
      <span class="text-3">{{ t('tag.basicInfo') }}</span>
    </template>

    <!-- Tag information display (aligned with Department info layout) -->
    <div class="dept-info-display">
      <!-- Row 1: Name, ID, Created By -->
      <div class="info-row">
        <div class="info-item">
          <div class="info-label">
            <Icon icon="icon-biaoqian2" class="info-icon" />
            {{ t('tag.columns.name') }}
          </div>
          <div class="info-value">
            {{ props.tag?.name || '' }}
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
            {{ t('common.columns.createdByName') }}
          </div>
          <div class="info-value">{{ props.tag?.createdByName || '--' }}</div>
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
            {{ t('department.columns.lastModifiedByName') }}
          </div>
          <div class="info-value">{{ props.tag?.lastModifiedByName || '--' }}</div>
        </div>
        <div class="info-item">
          <div class="info-label">
            <Icon icon="icon-shijianriqi" class="info-icon" />
            {{ t('department.columns.lastModifiedDate') }}
          </div>
          <div class="info-value">{{ props.tag?.lastModifiedDate || '--' }}</div>
        </div>
      </div>
    </div>
  </Card>
</template>

<style scoped>
/* Reuse department info layout styles for consistency */
.dept-info-display {
  padding: 4px 0;
}

.info-row {
  display: flex;
  gap: 4px;
  margin-bottom: 4px;
  padding: 4px 0
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

/* Responsive design */
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
