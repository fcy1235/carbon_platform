<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue';

import { Cascader } from 'ant-design-vue';

import { getAreaListSimple } from '#/api/system/area';

const props = defineProps<{
  modelValue?: (number | string)[];
  placeholder?: string;
  allowClear?: boolean;
  disabled?: boolean;
}>();

const emit = defineEmits<{
  (e: 'update:modelValue', val?: (number | string)[]): void;
}>();

const options = ref<any[]>([]);
const loading = ref(false);

const innerValue = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val),
});

async function loadData(selectedOptions: any[]) {
  const targetOption = selectedOptions[selectedOptions.length - 1];
  if (!targetOption) return;

  targetOption.loading = true;
  try {
    const level = targetOption.level ?? 0;
    const children = await getAreaListSimple({
      parentId: targetOption.id,
      levels: [level + 1],
    });
    targetOption.children = (children || []).map((item: any) => ({
      ...item,
      isLeaf: (item.level ?? level + 1) >= 4,
    }));
  } finally {
    targetOption.loading = false;
  }
}

async function initOptions() {
  loading.value = true;
  try {
    const provinces = await getAreaListSimple({ parentId: 0, levels: [0] });
    options.value = (provinces || []).map((item: any) => ({
      ...item,
      isLeaf: (item.level ?? 0) >= 4,
    }));

    // 如果有初始值，预加载选中路径
    if (innerValue.value && innerValue.value.length > 0) {
      await expandPath(innerValue.value);
    }
  } finally {
    loading.value = false;
  }
}

async function expandPath(path: (number | string)[]) {
  let currentOptions = options.value;

  for (let i = 0; i < path.length; i++) {
    const id = path[i];
    const option = currentOptions.find(
      (o) => String(o.id) === String(id),
    );
    if (!option) break;

    const level = option.level ?? i;
    const children = await getAreaListSimple({
      parentId: option.id,
      levels: [level + 1],
    });

    option.children = (children || []).map((item: any) => ({
      ...item,
      isLeaf: (item.level ?? level + 1) >= 4,
    }));

    currentOptions = option.children;
  }
}

onMounted(initOptions);

watch(
  () => props.modelValue,
  async (val, oldVal) => {
    if (
      val &&
      val.length > 0 &&
      options.value.length > 0 &&
      JSON.stringify(val) !== JSON.stringify(oldVal)
    ) {
      await expandPath(val);
    }
  },
);
</script>

<template>
  <Cascader
    v-model:value="innerValue"
    :options="options"
    :load-data="loadData"
    :field-names="{ label: 'name', value: 'id', children: 'children' }"
    :placeholder="placeholder"
    :allow-clear="allowClear"
    :disabled="disabled"
    change-on-select
    style="width: 100%"
  />
</template>
