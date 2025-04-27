<script setup lang="ts">
import { Arrow } from '@xcan-angus/vue-ui';

interface Props {
  icon: string,
  title: string,
  visible: boolean,
}

const props = withDefaults(defineProps<Props>(), {
  icon: '',
  title: '',
  visible: false
});

const emit = defineEmits<{(e: 'update:visible', visible: boolean): void, }>();

const expand = () => {
  emit('update:visible', !props.visible);
};

</script>

<template>
  <div class="flex justify-between items-center py-1.5  cursor-pointer min-w-full">
    <template v-if="!$slots.title">
      <div class="text-3 leading-3 font-medium text-theme-title flex items-center" @click.stop="expand">
        <Arrow
          class="arrow-icon"
          :open="props.visible"
          @click.stop="expand" />
        {{ props.title }}
      </div>
    </template>
  </div>
</template>
