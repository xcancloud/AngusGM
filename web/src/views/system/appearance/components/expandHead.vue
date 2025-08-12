<script setup lang="ts">
import { Arrow } from '@xcan-angus/vue-ui';

/**
 * Props interface for ExpandHead component
 */
interface Props {
  /** Icon identifier for the expandable section */
  icon: string;
  /** Title text to display */
  title: string;
  /** Controls the expanded/collapsed state */
  visible: boolean;
}

// Define component props with default values
const props = withDefaults(defineProps<Props>(), {
  icon: '',
  title: '',
  visible: false
});

// Define component emits for parent communication
const emit = defineEmits<{
  (e: 'update:visible', visible: boolean): void;
}>();

/**
 * Toggles the expanded/collapsed state
 * Emits the new state to parent component
 */
const toggleExpand = () => {
  emit('update:visible', !props.visible);
};

</script>

<template>
  <div class="flex justify-between items-center py-1.5 cursor-pointer min-w-full">
    <template v-if="!$slots.title">
      <div
        class="text-3 leading-3 font-medium text-theme-title flex items-center"
        @click.stop="toggleExpand">
        <Arrow
          class="arrow-icon"
          :open="props.visible"
          @click.stop="toggleExpand" />
        {{ props.title }}
      </div>
    </template>
  </div>
</template>
