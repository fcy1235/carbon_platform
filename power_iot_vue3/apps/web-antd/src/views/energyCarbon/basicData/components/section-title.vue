<script setup lang="ts">
defineProps<{
  modelValue?: any;
  title?: string;
}>();
</script>

<template>
  <div class="py-1 text-base font-bold text-gray-800">{{ title }}</div>
</template>
