<script setup lang="ts">
interface Props {
  value:string;
}

const props = withDefaults(defineProps<Props>(), {
  value: ''
});

</script>
<template>
  <div v-html="props.value"></div>
</template>
