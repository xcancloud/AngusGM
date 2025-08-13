<script setup lang='ts'>
import { useI18n } from 'vue-i18n';
import { Icon } from '@xcan-angus/vue-ui';
import { Badge } from 'ant-design-vue';

import type { PolicyDetailType } from '../types';

/**
 * Props interface for BaseInfo component
 * @interface Props
 * @property {PolicyDetailType} detail - Policy detail information to display
 */
interface Props {
  detail: PolicyDetailType
}

/**
 * Component props with default values
 * Provides fallback values for all detail properties to prevent rendering errors
 */
const props = withDefaults(defineProps<Props>(), {
  detail: () => ({
    id: undefined,
    name: undefined,
    code: undefined,
    appId: undefined,
    appName: undefined,
    createdByName: undefined,
    createdDate: undefined,
    type: { value: undefined, message: undefined },
    enabled: false,
    description: undefined,
    global: false,
    default0: false,
    grantStage: undefined
  })
});

const { t } = useI18n();

/**
 * Provide safe text for possibly empty values
 * Returns a dash when the value is undefined, null, or empty
 */
const textOrDash = (value?: any): string => (value === undefined || value === null || value === '' ? '--' : String(value));

/**
 * Resolve an i18n key with a fallback plain text when the key is missing
 * Ensures display text is always available even if translation keys are missing
 */
const tt = (key: string, fallback: string): string => {
  const msg = t(key);
  return msg === key ? fallback : msg;
};
</script>

<template>
  <div class="policy-detail">
    <!-- Basic Policy Information -->
    <div class="section">
      <div class="section-title">
        <Icon icon="icon-jibenxinxi" class="info-icon" />
        {{ t('permission.policy.detail.basicInfo') }}
      </div>
      <div class="info-grid">
        <div class="info-item">
          <div class="info-label">{{ t('common.columns.name') }}</div>
          <div class="info-value" :title="props.detail?.name">{{ textOrDash(props.detail?.name) }}</div>
        </div>
        <div class="info-item">
          <div class="info-label">{{ t('common.columns.type') }}</div>
          <div class="info-value">{{ props.detail?.type?.message || '--' }}</div>
        </div>
        <div class="info-item">
          <div class="info-label">{{ t('common.columns.code') }}</div>
          <div class="info-value" :title="props.detail?.code">{{ textOrDash(props.detail?.code) }}</div>
        </div>
        <div class="info-item">
          <div class="info-label">{{ t('common.status.validStatus') }}</div>
          <div class="info-value">
            <Badge
              :status="props.detail?.enabled ? 'success' : 'error'"
              :text="props.detail?.enabled ? t('common.status.enabled') : t('common.status.disabled')" />
          </div>
        </div>
        <div class="info-item">
          <div class="info-label">{{ t('common.columns.description') }}</div>
          <div class="info-value" :title="props.detail?.description">{{ textOrDash(props.detail?.description) }}</div>
        </div>
      </div>
    </div>

    <!-- Application and Authorization Information -->
    <div class="section">
      <div class="section-title">
        <Icon icon="icon-yingyongguanli" class="info-icon" />
        {{ t('permission.policy.detail.authInfo') }}
      </div>
      <div class="info-grid">
        <div class="info-item">
          <div class="info-label">{{ t('permission.policy.added.columns.appName') }}</div>
          <div class="info-value" :title="props.detail?.appName">{{ textOrDash(props.detail?.appName) }}</div>
        </div>
        <div class="info-item">
          <div class="info-label">{{ t('permission.policy.detail.info.globalAccount') }}</div>
          <div class="info-value">
            <Badge
              :status="props.detail?.global ? 'processing' : 'default'"
              :text="props.detail?.global ? t('common.labels.all') : t('common.labels.tenant')" />
          </div>
        </div>
        <div class="info-item">
          <div class="info-label">{{ t('permission.policy.detail.info.grantStage') }}</div>
          <div class="info-value">{{ props.detail?.grantStage?.message || '--' }}</div>
        </div>
        <div class="info-item">
          <div class="info-label">{{ t('permission.policy.detail.info.defaultPolicy') }}</div>
          <div class="info-value">
            <Badge
              :status="props.detail?.default0 ? 'warning' : 'default'"
              :text="props.detail?.default0 ? t('common.status.yes') : t('common.status.no')" />
          </div>
        </div>
        <div class="info-item">
          <div class="info-label">{{ t('common.columns.createdByName') }}</div>
          <div class="info-value">{{ textOrDash(props.detail?.createdByName) }}</div>
        </div>
        <div class="info-item">
          <div class="info-label">{{ t('common.columns.createdDate') }}</div>
          <div class="info-value muted">{{ textOrDash(props.detail?.createdDate) }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.policy-detail {
  padding: 8px 0;
}

.section + .section {
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
  grid-template-columns: 156px 1fr;
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
