<script setup lang="ts">
import { Card } from 'ant-design-vue';
import { IconifyIcon } from '@vben/icons';

interface Props {
  title: string;
  value: number;
  unit?: string;
  icon?: string;
  subItems?: {
    label: string;
    value: number;
    unit?: string;
  }[];
}

withDefaults(defineProps<Props>(), {
  unit: '',
  icon: 'home',
  subItems: () => [],
});

const iconMap: Record<string, string> = {
  home: 'lucide:home',
  leaf: 'lucide:leaf',
  users: 'lucide:users',
  building: 'lucide:building',
};

// 安全的数字格式化，null/undefined/NaN 显示为 0
const formatNumber = (val: number | undefined | null): string => {
  if (val === undefined || val === null || Number.isNaN(val)) {
    return '0';
  }
  return val.toLocaleString();
};
</script>

<template>
  <Card class="h-full">
    <div class="text-gray-600 text-sm font-medium mb-3">{{ title }}</div>
    <div class="flex items-end justify-between mb-4">
      <div>
        <span class="text-3xl font-bold text-gray-800">{{ formatNumber(value) }}</span>
        <span v-if="unit" class="ml-1 text-base text-gray-500">{{ unit }}</span>
      </div>
      <IconifyIcon :icon="iconMap[icon]" class="w-10 h-10 text-blue-500" />
    </div>
    <div v-if="subItems.length > 0" class="grid grid-cols-2 gap-4 pt-3 border-t border-gray-100">
      <div v-for="(item, index) in subItems" :key="index" class="text-sm">
        <div class="text-gray-500">{{ item.label }}</div>
        <div class="font-medium text-gray-700">
          {{ formatNumber(item.value) }}
          <span v-if="item.unit" class="ml-0.5 text-gray-400">{{ item.unit }}</span>
        </div>
      </div>
    </div>
  </Card>
</template>
