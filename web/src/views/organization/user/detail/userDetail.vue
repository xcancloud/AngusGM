<script setup lang='ts'>
import { useI18n } from 'vue-i18n';
import { Icon } from '@xcan-angus/vue-ui';
import { Badge } from 'ant-design-vue';

import { Detail } from '../types';

/**
 * Component props interface
 * Defines the properties passed to the user detail component
 */
interface Props {
  dataSource: Detail // User detail data to display
}

const props = withDefaults(defineProps<Props>(), {
  dataSource: undefined
});

// Internationalization setup
const { t } = useI18n();

/**
 * Provide safe text for possibly empty values
 */
const textOrDash = (value?: any): string => (value === undefined || value === null || value === '' ? '--' : String(value));

/**
 * Resolve an i18n key with a fallback plain text when the key is missing
 */
const tt = (key: string, fallback: string): string => {
  const msg = t(key);
  return msg === key ? fallback : msg;
};

</script>
<template>
  <div class="user-detail">
    <!-- Basic Information -->
    <div class="section">
      <div class="section-title">
        <Icon icon="icon-jibenxinxi" class="info-icon" />
        {{ t('user.profile.basicInfo') }}
      </div>
      <div class="info-grid">
        <div class="info-item">
          <div class="info-label">ID</div>
          <div class="info-value" :title="props.dataSource?.id">{{ textOrDash(props.dataSource?.id) }}</div>
        </div>
        <div class="info-item">
          <div class="info-label">{{ t('user.columns.title') }}</div>
          <div class="info-value" :title="props.dataSource?.title">{{ textOrDash(props.dataSource?.title) }}</div>
        </div>
        <div class="info-item">
          <div class="info-label">{{ t('user.columns.gender') }}</div>
          <div class="info-value">{{ props.dataSource?.gender?.message || '--' }}</div>
        </div>
        <div class="info-item">
          <div class="info-label">{{ t('user.columns.identity') }}</div>
          <div class="info-value">
            {{ props.dataSource?.sysAdmin ? t('user.profile.systemAdmin') :
              t('user.profile.generalUser') }}
          </div>
        </div>
        <div class="info-item">
          <div class="info-label">{{ t('user.columns.status') }}</div>
          <div class="info-value">
            <Badge
              :status="props.dataSource?.enabled ? 'success' : 'error'"
              :text="props.dataSource?.enabled ? t('common.status.enabled') : t('common.status.disabled')" />
          </div>
        </div>
        <div class="info-item">
          <div class="info-label">{{ t('user.columns.lockedStatus') }}</div>
          <div class="info-value">
            <Badge
              :status="props.dataSource?.locked ? 'error' : 'success'"
              :text="props.dataSource?.locked ? t('common.status.locked') : t('common.status.unlocked')" />
          </div>
        </div>

        <div class="info-item">
          <div class="info-label">{{ t('user.columns.mobile') }}</div>
          <div class="info-value" :title="props.dataSource?.mobile">{{ textOrDash(props.dataSource?.mobile) }}</div>
        </div>
        <div class="info-item">
          <div class="info-label">{{ t('user.columns.landline') }}</div>
          <div class="info-value" :title="props.dataSource?.landline">{{ textOrDash(props.dataSource?.landline) }}</div>
        </div>
        <div class="info-item">
          <div class="info-label">{{ t('user.columns.email') }}</div>
          <div class="info-value" :title="props.dataSource?.email">{{ textOrDash(props.dataSource?.email) }}</div>
        </div>
        <div class="info-item">
          <div class="info-label">{{ t('user.columns.address') }}</div>
          <div class="info-value" :title="props.dataSource?.contactAddress">
            {{
              textOrDash(props.dataSource?.contactAddress) }}
          </div>
        </div>
        <div class="info-item">
          <div class="info-label">{{ t('user.columns.source') }}</div>
          <div class="info-value">{{ props.dataSource?.source?.message || '--' }}</div>
        </div>
        <div v-if="props.dataSource?.locked" class="info-item">
          <div class="info-label">{{ t('user.columns.lockStartDate') }}</div>
          <div class="info-value">
            <template v-if="props.dataSource?.lockEndDate">
              {{ props.dataSource?.lockStartDate }} - {{ props.dataSource?.lockEndDate }}
            </template>
            <template v-else>
              {{ t('user.permanentLock') }}
            </template>
          </div>
        </div>
      </div>
    </div>

    <!-- Audit Information -->
    <div class="section">
      <div class="section-title">
        <Icon icon="icon-shenjirizhi" class="info-icon" />
        {{ tt('user.profile.auditInfo', 'Audit Information') }}
      </div>
      <div class="info-grid">
        <div class="info-item">
          <div class="info-label">{{ t('common.columns.creator') }}</div>
          <div class="info-value">{{ textOrDash(props.dataSource?.creator) }}</div>
        </div>
        <div class="info-item">
          <div class="info-label">{{ t('common.columns.createdDate') }}</div>
          <div class="info-value muted">{{ textOrDash(props.dataSource?.createdDate) }}</div>
        </div>
        <div class="info-item">
          <div class="info-label">{{ t('common.columns.modifier') }}</div>
          <div class="info-value">{{ textOrDash(props.dataSource?.modifier) }}</div>
        </div>
        <div class="info-item">
          <div class="info-label">{{ t('common.columns.modifiedDate') }}</div>
          <div class="info-value muted">{{ textOrDash(props.dataSource?.modifiedDate) }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.user-detail {
  padding: 8px 0;
}

.section+.section {
  margin-top: 16px;
}

.section-title {
  display: flex;
  align-items: center;
  font-size: 14px;
  font-weight: 600;
  color: var(--theme-text);
  margin-bottom: 12px;
  padding-bottom: 6px;
  border-bottom: 1px solid var(--border-divider);
}

.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  row-gap: 12px;
  column-gap: 5px;
}

.info-icon {
  margin-right: 6px;
  font-size: 12px;
  color: #1890ff;
}

@media (max-width: 1200px) {
  .info-grid {
    grid-template-columns: 1fr 1fr;
  }
}

@media (max-width: 720px) {
  .info-grid {
    grid-template-columns: 1fr;
  }
}

.info-item {
  display: grid;
  grid-template-columns: 130px 1fr;
  column-gap: 16px;
  align-items: center;
}

.info-label {
  text-align: right;
  font-weight: 500;
  color: var(--theme-content);
}

.info-value {
  color: var(--theme-text);
  font-size: 13px;
  line-height: 1.65;
  word-break: break-word;
}

.info-value :deep(.ant-badge-status-text) {
  font-size: 13px;
}

.info-value :deep(.ant-badge) {
  line-height: 20px;
}

.muted {
  color: var(--theme-content);
}
</style>
