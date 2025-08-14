<script setup lang="ts">
import { computed } from 'vue';
import { useI18n } from 'vue-i18n';

// Password strength levels
type Strength = 'weak' | 'medium' | 'strong';

// Interface for strength configuration
interface StrengthDesc {
  slider: string; // CSS classes for slider width and color
  textColor: string; // CSS classes for text color
  text: Strength; // Strength level identifier
  name?: string; // Localized display name
}

// Component props interface
interface Props {
  strength: Strength; // Current password strength level
}

const props = withDefaults(defineProps<Props>(), {
  strength: 'weak'
});

// Internationalization setup
const { t } = useI18n();

// Configuration for different strength levels
// Each level has specific styling for slider and text
const configs: StrengthDesc[] = [
  {
    slider: 'w-1/3 bg-danger', // Weak: 1/3 width with danger color
    textColor: 'text-danger', // Red text color
    text: 'weak'
  },
  {
    slider: 'w-2/3 bg-warn', // Medium: 2/3 width with warning color
    textColor: 'text-warn', // Orange text color
    text: 'medium'
  },
  {
    slider: 'w-full bg-success', // Strong: full width with success color
    textColor: 'text-success', // Green text color
    text: 'strong'
  }
];

// Computed property to create strength configuration with localized names
// Maps each strength level to its complete configuration
const strengthConfig = computed(() => {
  const temp = {} as Record<Strength, StrengthDesc>;

  // Build configuration object with localized names
  configs.forEach((item: StrengthDesc) => {
    temp[item.text] = {
      ...item,
      name: t('securities.messages.' + item.text)
    };
  });

  return temp;
});

// Computed property to get current strength configuration
// Returns the configuration for the current strength level
const config = computed(() => {
  const key = props.strength.toLowerCase() as Strength;
  return strengthConfig.value[key];
});

</script>
<template>
  <!-- Password Strength Display Container -->
  <div class="flex items-center">
    <!-- Strength Level Text with Color Coding -->
    <span
      :class="config?.textColor"
      class="text-3 leading-3 mr-3">
      {{ t('securities.columns.passwordStrength') + " " + config?.name }}
    </span>

    <!-- Strength Progress Bar -->
    <span class="relative block w-30 h-1 bg-gray-slider rounded">
      <!-- Dynamic Slider Bar - Width and Color Based on Strength Level -->
      <span
        :class="config?.slider"
        class="block absolute left-0 rounded h-1 bg-danger"></span>
    </span>
  </div>
</template>
