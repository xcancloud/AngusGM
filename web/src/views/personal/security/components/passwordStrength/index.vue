<script setup lang="ts">
import { computed } from 'vue';
import { useI18n } from 'vue-i18n';

type Strength = 'weak' | 'medium' | 'strong'

interface StrengthDesc {
  slider: string,
  textColor: string,
  text: Strength,
  name?: string
}

interface Props {
  strength: Strength
}

const props = withDefaults(defineProps<Props>(), {
  strength: 'weak'
});

const { t } = useI18n();

const configs: StrengthDesc[] = [{
  slider: 'w-1/3 bg-danger',
  textColor: 'text-danger',
  text: 'weak'
},
{
  slider: 'w-2/3 bg-warn',
  textColor: 'text-warn',
  text: 'medium'
},
{
  slider: 'w-full bg-success',
  textColor: 'text-success',
  text: 'strong'
}];

const strengthConfig = computed(() => {
  const temp = {} as Record<Strength, StrengthDesc>;
  configs.every((item: StrengthDesc) => {
    temp[item.text] = {
      ...item,
      name: t('personalCenter.security.' + item.text)
    };

    return true;
  });

  return temp;
});

const config = computed(() => {
  const key = props.strength.toLowerCase() as Strength;
  return strengthConfig.value[key];
});

</script>
<template>
  <div class="flex items-center">
    <span :class="config?.textColor" class="text-3 leading-3 mr-3">{{ t('personalCenter.security.passwordStrength') + config?.name }}</span>
    <span class="relative block w-30 h-1 bg-gray-slider rounded">
      <span :class="config?.slider" class="block absolute left-0 rounded h-1 bg-danger"></span>
    </span>
  </div>
</template>
