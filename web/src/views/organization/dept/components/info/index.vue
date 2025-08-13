<script setup lang="ts">
import { withDefaults, defineProps, defineEmits } from 'vue';
import { useI18n } from 'vue-i18n';
import { ButtonAuth, Card, Icon } from '@xcan-angus/vue-ui';
import { Tag } from 'ant-design-vue';
import type { DeptInfo, TreeRecordType } from '../../types';

interface Props {
  node: TreeRecordType;
  deptInfo: DeptInfo;
}

const props = withDefaults(defineProps<Props>(), {
  node: () => ({} as TreeRecordType),
  deptInfo: () => ({}) as DeptInfo
});

const emit = defineEmits<{
  (e: 'add', node: NodeLike): void;
  (e: 'editName', node: NodeLike): void;
  (e: 'delete', node: NodeLike): void;
  (e: 'editTag', node: NodeLike): void;
  (e: 'move', node: NodeLike): void;
}>();

const { t } = useI18n();

const onAdd = () => emit('add', props.node);
const onEditName = () => emit('editName', props.node);
const onDelete = () => emit('delete', props.node);
const onEditTag = () => emit('editTag', props.node);
const onMove = () => emit('move', props.node);
</script>

<template>
  <Card v-show="props.node?.id" class="mb-2">
    <template #title>
      <span class="text-3">{{ t('department.basicInfo') }}</span>
    </template>
    <template #rightExtra>
      <div class="flex items-center space-x-2.5">
        <ButtonAuth
          code="DeptAdd"
          type="text"
          icon="icon-tianjia"
          @click="onAdd" />
        <ButtonAuth
          code="DeptModify"
          type="text"
          icon="icon-shuxie"
          @click="onEditName" />
        <ButtonAuth
          code="DeptDelete"
          type="text"
          icon="icon-lajitong"
          @click="onDelete" />
        <ButtonAuth
          code="DeptTagsAdd"
          type="text"
          icon="icon-biaoqian2"
          @click="onEditTag" />
        <ButtonAuth
          v-if="false"
          code="Move"
          type="text"
          icon="icon-riqiyou"
          @click="onMove" />
      </div>
    </template>

    <!-- Department information display -->
    <div v-show="props.node?.id" class="dept-info-display">
      <!-- Basic Information Row -->
      <div class="info-row">
        <div class="info-item">
          <div class="info-label">
            <Icon icon="icon-bumen" class="info-icon" />
            {{ t('common.columns.name') }}
          </div>
          <div class="info-value">{{ props.deptInfo.name || '--' }}</div>
        </div>
        <div class="info-item">
          <div class="info-label">
            <Icon icon="icon-a-bianhao1" class="info-icon" />
            {{ t('common.columns.code') }}
          </div>
          <div class="info-value">{{ props.deptInfo.code || '--' }}</div>
        </div>
        <div class="info-item">
          <div class="info-label">
            <Icon icon="icon-a-ID1" class="info-icon" />
            {{ t('ID') }}
          </div>
          <div class="info-value">{{ props.deptInfo.id || '--' }}</div>
        </div>
      </div>

      <!-- Creation Information Row -->
      <div class="info-row">
        <div class="info-item">
          <div class="info-label">
            <Icon icon="icon-yonghu" class="info-icon" />
            {{ t('common.columns.createdByName') }}
          </div>
          <div class="info-value">{{ props.deptInfo.createdByName || '--' }}</div>
        </div>
        <div class="info-item">
          <div class="info-label">
            <Icon icon="icon-shijianriqi" class="info-icon" />
            {{ t('common.columns.createdDate') }}
          </div>
          <div class="info-value">{{ props.deptInfo.createdDate || '--' }}</div>
        </div>
        <div class="info-item">
          <div class="info-label">
            <Icon icon="icon-a-cengji1" class="info-icon" />
            {{ t('department.columns.level') }}
          </div>
          <div class="info-value">{{ props.deptInfo.level || '--' }}</div>
        </div>
      </div>

      <!-- Modification Information Row -->
      <div class="info-row">
        <div class="info-item">
          <div class="info-label">
            <Icon icon="icon-yonghu" class="info-icon" />
            {{ t('department.columns.lastModifiedByName') }}
          </div>
          <div class="info-value">{{ props.deptInfo.lastModifiedByName || '--' }}</div>
        </div>
        <div class="info-item">
          <div class="info-label">
            <Icon icon="icon-shijianriqi" class="info-icon" />
            {{ t('department.columns.lastModifiedDate') }}
          </div>
          <div class="info-value">{{ props.deptInfo.lastModifiedDate || '--' }}</div>
        </div>
        <div class="info-item">
          <div class="info-label">
            <Icon icon="icon-biaoqian2" class="info-icon" />
            {{ t('common.columns.tags') }}
          </div>
          <div class="info-value">
            <div v-if="props.deptInfo.tags && props.deptInfo.tags.length > 0" class="tags-container">
              <Tag
                v-for="tag in props.deptInfo.tags"
                :key="tag.id"
                class="tag">
                {{ tag.name }}
              </Tag>
            </div>
            <span v-else class="no-tags">--</span>
          </div>
        </div>
      </div>
    </div>
  </Card>
</template>

<style scoped>

/* Department information display styling */
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

.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.tag {
  background: #f6ffed;
  border: 1px solid #b7eb8f;
  color: #52c41a;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 4px;
  margin: 0;
}

.no-tags {
  color: #bfbfbf;
  font-style: italic;
}

/* Responsive design for department info */
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
