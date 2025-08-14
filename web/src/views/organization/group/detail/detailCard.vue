<script setup lang='ts'>
import { useI18n } from 'vue-i18n';
import { ButtonAuth, Icon, PureCard } from '@xcan-angus/vue-ui';
import { useRouter } from 'vue-router';
import { Badge, Tag, Tooltip } from 'ant-design-vue';

import { DetailCardProps } from '../types';

/**
 * Component props with default values
 * dataSource: Group detail data to display
 */
const props = withDefaults(defineProps<DetailCardProps>(), {
  dataSource: undefined
});

/**
 * Component emits for parent communication
 * update: Triggered when group status needs to be updated
 * success: Triggered when an operation completes successfully
 */
const emit = defineEmits<{(e: 'update'): void, (e: 'success'): void }>();

const router = useRouter();
const { t } = useI18n();

/**
 * Emit update event to parent component for status change
 */
const updateStatus = () => {
  emit('update');
};

/**
 * Navigate to group edit page with source parameter
 */
const handleEdit = () => {
  router.push(`/organization/group/edit/${props.dataSource.id}?source=detail`);
};
</script>
<template>
  <!-- Main detail card container -->
  <PureCard class="w-100 px-5 py-5">
    <!-- Header actions section with edit and status toggle buttons -->
    <div class="flex items-center justify-end header-actions">
      <ButtonAuth
        code="GroupModify"
        type="primary"
        icon="icon-shuxie"
        class="mr-2"
        @click="handleEdit" />
      <ButtonAuth
        code="GroupEnable"
        type="primary"
        :icon="props.dataSource?.enabled ? 'icon-jinyong1' : 'icon-qiyong'"
        :showTextIndex="props.dataSource?.enabled ? 1 : 0"
        @click="updateStatus" />
    </div>

    <!-- Group detail information sections -->
    <div class="detail-info mt-4">
      <!-- Basic information section -->
      <div class="section">
        <div class="section-title">
          <Icon icon="icon-jibenxinxi1" class="info-icon" />
          {{ t('group.basicInfo') }}
        </div>
        <div class="info-grid one-col">
          <!-- Group ID -->
          <div class="info-item">
            <div class="info-label">ID</div>
            <div class="info-value">{{ props.dataSource?.id }}</div>
          </div>
          <!-- Group name with tooltip for long names -->
          <div class="info-item">
            <div class="info-label">{{ t('common.columns.name') }}</div>
            <div class="info-value">
              <Tooltip :title="props.dataSource?.name" placement="bottomLeft">
                <span class="value-strong">{{ props.dataSource?.name || '--' }}</span>
              </Tooltip>
            </div>
          </div>
          <!-- Group code -->
          <div class="info-item">
            <div class="info-label">{{ t('common.columns.code') }}</div>
            <div class="info-value">{{ props.dataSource?.code || '--' }}</div>
          </div>
          <!-- User count with tag display -->
          <div class="info-item">
            <div class="info-label">{{ t('group.columns.userNum') }}</div>
            <div class="info-value">
              <Tag class="count-tag">{{ props.dataSource?.userNum ?? 0 }}</Tag>
            </div>
          </div>
          <!-- Group status with badge indicator -->
          <div class="info-item">
            <div class="info-label">{{ t('common.status.validStatus') }}</div>
            <div class="info-value">
              <Badge
                v-if="props.dataSource?.enabled"
                status="success"
                :text="t('common.status.enabled')" />
              <Badge
                v-else
                status="error"
                :text="t('common.status.disabled')" />
            </div>
          </div>
          <!-- Group remark with tooltip for long text -->
          <div class="info-item remark">
            <div class="info-label">{{ t('group.columns.remark') }}</div>
            <div class="info-value">
              <Tooltip :title="props.dataSource?.remark" placement="bottomLeft">
                <span class="value-remark">{{ props.dataSource?.remark || '--' }}</span>
              </Tooltip>
            </div>
          </div>
        </div>
      </div>

      <!-- Audit information section -->
      <div class="section mt-4">
        <div class="section-title">
          <Icon icon="icon-shenjirizhi" class="info-icon" />
          {{ t('group.auditInfo') }}
        </div>
        <div class="info-grid one-col">
          <!-- Creator information -->
          <div class="info-item">
            <div class="info-label">{{ t('common.columns.createdByName') }}</div>
            <div class="info-value">{{ props.dataSource?.createdByName || '--' }}</div>
          </div>
          <!-- Creation date -->
          <div class="info-item">
            <div class="info-label">{{ t('common.columns.createdDate') }}</div>
            <div class="info-value muted">{{ props.dataSource?.createdDate || '--' }}</div>
          </div>
          <!-- Last modifier information -->
          <div class="info-item">
            <div class="info-label">{{ t('common.columns.lastModifiedByName') }}</div>
            <div class="info-value">{{ props.dataSource?.lastModifiedByName || '--' }}</div>
          </div>
          <!-- Last modification date -->
          <div class="info-item">
            <div class="info-label">{{ t('common.columns.lastModifiedDate') }}</div>
            <div class="info-value muted">{{ props.dataSource?.lastModifiedDate || '--' }}</div>
          </div>
        </div>
      </div>

      <!-- Tags section -->
      <div class="section mt-4">
        <div class="section-title">
          <Icon icon="icon-biaoqian" class="info-icon" />
          {{ t('common.columns.tags') }}
        </div>
        <div class="info-item">
          <div class="info-value tags-container">
            <!-- Display tags if available, otherwise show placeholder -->
            <template v-if="props.dataSource?.tags?.length">
              <Tag
                v-for="(tag, tagIndex) in props.dataSource?.tags"
                :key="tagIndex"
                class="tag">
                {{ tag.name }}
              </Tag>
            </template>
            <span v-else>--</span>
          </div>
        </div>
      </div>
    </div>
  </PureCard>
</template>
<style scoped>
.header-actions {
  border-bottom: 1px solid var(--border-divider);
  padding-bottom: 8px;
}

.section-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 10px;
}
.section-title {
  display: inline-flex;
  align-items: center;
}

.detail-info {
  width: 100%;
}

.info-grid {
  display: grid;
  gap: 10px;
}

.info-grid.one-col {
  grid-template-columns: 1fr;
}

.info-item {
  min-width: 0;
  display: grid;
  grid-template-columns: 120px 1fr;
  align-items: start;
  column-gap: 5px;
  margin-left: 15px;
}

@media (max-width: 720px) {
  .info-item {
    grid-template-columns: 80px 1fr;
  }
}

.info-label {
  font-size: 12px;
  line-height: 12px;
  color: var(--text-secondary);
  margin-top: 2px;
}

.info-value {
  color: var(--text-primary);
  font-size: 12px;
}

.info-icon {
  margin-right: 6px;
  font-size: 12px;
  color: #1890ff;
}

.value-strong {
  font-weight: 600;
}

.muted {
  color: var(--text-secondary);
}

.count-tag {
  background-color: var(--theme-hover);
  border: none;
}

.value-remark {
  display: -webkit-box;
  line-clamp: 2;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
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

</style>
