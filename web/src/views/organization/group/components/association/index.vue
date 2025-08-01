<script setup lang='ts'>
import {defineAsyncComponent} from 'vue';
import {useI18n} from 'vue-i18n';
import {TabPane, Tabs} from 'ant-design-vue';
import {PureCard} from '@xcan-angus/vue-ui';

const PolicyTable = defineAsyncComponent(() => import('./policy.vue'));
const UserTable = defineAsyncComponent(() => import('./user.vue'));

interface Props {
  groupId: string;
  enabled: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  groupId: undefined,
  enabled: false
});

const { t } = useI18n();

</script>
<template>
  <PureCard class="flex-1 p-3.5">
    <Tabs style="flex: 0 0 auto;" size="small">
      <TabPane key="1" :tab="t('关联用户')">
        <UserTable :groupId="groupId" :enabled="props.enabled" />
      </TabPane>
      <TabPane key="2" :tab="t('授权策略')">
        <PolicyTable :groupId="groupId" :enabled="props.enabled" />
      </TabPane>
    </Tabs>
  </PureCard>
</template>
