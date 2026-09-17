<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue';

import { Cascader } from 'ant-design-vue';

import { getAreaListSimple } from '#/api/system/area';

const props = defineProps<{
  allowClear?: boolean;
  disabled?: boolean;
  /** 最大加载层级，默认5级(省/市/区/乡镇/村) */
  maxLevel?: number;
  modelValue?: (number | string)[] | (number | string)[][];
  /** 是否多选 */
  multiple?: boolean;
  placeholder?: string;
}>();

const emit = defineEmits<{
  (e: 'update:modelValue', val?: (number | string)[] | (number | string)[][]): void;
}>();

const maxLevel = props.maxLevel ?? 5;

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
    const children = await getAreaListSimple({
      parentId: targetOption.id,
    });
    targetOption.children = (children || []).map((item: any) => ({
      ...item,
      isLeaf: (item.level ?? 0) >= maxLevel - 1,
    }));
  } finally {
    targetOption.loading = false;
  }
}

async function initOptions() {
  loading.value = true;
  try {
    const provinces = await getAreaListSimple({ parentId: 0 });
    options.value = (provinces || []).map((item: any) => ({
      ...item,
      isLeaf: (item.level ?? 0) >= maxLevel - 1,
    }));

    // 如果有初始值，预加载选中路径
    if (innerValue.value && innerValue.value.length > 0) {
      if (props.multiple && Array.isArray(innerValue.value[0])) {
        // 多选模式：值为二维数组 [[path1], [path2], ...]
        for (const path of innerValue.value as (number | string)[][]) {
          await expandPath(path);
        }
      } else {
        await expandPath(innerValue.value as (number | string)[]);
      }
    }
  } finally {
    loading.value = false;
  }
}

async function expandPath(path: (number | string)[]) {
  let currentOptions = options.value;

  for (const [i, id] of path.entries()) {
    const option = currentOptions.find(
      (o) => String(o.id) === String(id),
    );
    if (!option) break;

    const children = await getAreaListSimple({
      parentId: option.id,
    });

    option.children = (children || []).map((item: any) => ({
      ...item,
      isLeaf: (item.level ?? 0) >= maxLevel - 1,
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
      if (props.multiple && Array.isArray(val[0])) {
        for (const path of val as (number | string)[][]) {
          await expandPath(path);
        }
      } else {
        await expandPath(val as (number | string)[]);
      }
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
    :multiple="multiple"
    change-on-select
    style="width: 100%"
  />
</template>
