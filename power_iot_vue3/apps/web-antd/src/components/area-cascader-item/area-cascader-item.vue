<script setup lang="ts">
import { onMounted, ref, watch } from 'vue';

import { Cascader } from 'ant-design-vue';

import { getAreaListSimple } from '#/api/system/area';

interface AreaValue {
  codes: (number | string)[];
  label: string;
}

const props = defineProps<{
  allowClear?: boolean;
  disabled?: boolean;
  /** 最大加载层级，默认5级(省/市/区/乡镇/村) */
  maxLevel?: number;
  /** JSON 字符串: {"codes":[13,1301],"label":"xx省xx市"} */
  modelValue?: string;
  placeholder?: string;
}>();

const emit = defineEmits<{
  (e: 'update:modelValue', val?: string): void;
}>();

const maxLevelVal = props.maxLevel ?? 5;
const options = ref<any[]>([]);
const loading = ref(false);
const innerCodes = ref<(number | string)[] | undefined>();
const plainLabel = ref<string | undefined>();

// 解析 modelValue（JSON 字符串）→ 内部 codes 数组
watch(
  () => props.modelValue,
  (val) => {
    if (val) {
      try {
        const parsed = JSON.parse(val) as AreaValue;
        if (parsed.codes && Array.isArray(parsed.codes)) {
          innerCodes.value = parsed.codes;
          plainLabel.value = undefined;
          return;
        }
      } catch {
        // 兼容旧格式：纯逗号分隔字符串
        const codes = val.split(',').map((c: string) => Number(c));
        if (codes.length > 0 && !codes.some(isNaN)) {
          innerCodes.value = codes;
          plainLabel.value = undefined;
          return;
        }
      }
    }
    // JSON解析失败且disabled时，视为纯文本
    if (props.disabled && val) {
      plainLabel.value = val;
      innerCodes.value = undefined;
      return;
    }
    innerCodes.value = undefined;
    plainLabel.value = undefined;
  },
  { immediate: true },
);

// 级联选择变化时，提取 label 并以 JSON 格式 emit
function handleChange(
  value: (number | string)[] | undefined,
  selectedOptions: any[],
) {
  innerCodes.value = value;
  if (value && value.length > 0 && selectedOptions?.length > 0) {
    const label = selectedOptions.map((opt) => opt.name || '').join('');
    emit('update:modelValue', JSON.stringify({ codes: value, label }));
  } else {
    emit('update:modelValue', undefined);
  }
}

// 级联懒加载
async function loadData(selectedOptions: any[]) {
  const targetOption = selectedOptions[selectedOptions.length - 1];
  if (!targetOption) return;
  targetOption.loading = true;
  try {
    const children = await getAreaListSimple({ parentId: targetOption.id });
    targetOption.children = (children || []).map((item: any) => ({
      ...item,
      isLeaf: (item.level ?? 0) >= maxLevelVal - 1,
    }));
  } finally {
    targetOption.loading = false;
  }
}

async function expandPath(path: (number | string)[]) {
  let currentOptions = options.value;
  for (const id of path) {
    const option = currentOptions.find(
      (o: any) => String(o.id) === String(id),
    );
    if (!option) break;
    if (!option.children || option.children.length === 0) {
      const children = await getAreaListSimple({ parentId: option.id });
      option.children = (children || []).map((item: any) => ({
        ...item,
        isLeaf: (item.level ?? 0) >= maxLevelVal - 1,
      }));
    }
    currentOptions = option.children;
  }
}

async function initOptions() {
  loading.value = true;
  try {
    const provinces = await getAreaListSimple({ parentId: 0 });
    options.value = (provinces || []).map((item: any) => ({
      ...item,
      isLeaf: (item.level ?? 0) >= maxLevelVal - 1,
    }));
    // 预加载初始值路径
    if (innerCodes.value && innerCodes.value.length > 0) {
      await expandPath(innerCodes.value);
    }
  } finally {
    loading.value = false;
  }
}

onMounted(initOptions);

watch(
  () => props.modelValue,
  async (val, oldVal) => {
    if (val && val !== oldVal && options.value.length > 0) {
      try {
        const parsed = JSON.parse(val) as AreaValue;
        if (parsed.codes && Array.isArray(parsed.codes)) {
          await expandPath(parsed.codes);
        }
      } catch {
        // ignore
      }
    }
  },
);
</script>

<template>
  <div v-if="disabled && plainLabel" class="area-plain-text">{{ plainLabel }}</div>
  <Cascader
    v-else
    v-model:value="innerCodes"
    :options="options"
    :load-data="loadData"
    :field-names="{ label: 'name', value: 'id', children: 'children' }"
    :placeholder="placeholder"
    :allow-clear="allowClear"
    :disabled="disabled"
    change-on-select
    style="width: 100%"
    @change="handleChange"
  />
</template>

<style scoped>
.area-plain-text {
  padding: 4px 11px;
  min-height: 32px;
  line-height: 22px;
  color: rgba(0, 0, 0, 0.88);
}
</style>
