<script setup lang="ts">
import { onMounted, ref, watch } from 'vue';

import { Button, Cascader, Tag } from 'ant-design-vue';

import { getAreaListSimple } from '#/api/system/area';

interface AreaItem {
  codes: (number | string)[];
  label: string;
}

const props = defineProps<{
  disabled?: boolean;
  maxLevel?: number;
  modelValue?: string;
}>();

const emit = defineEmits<{
  (e: 'update:modelValue', val?: string): void;
}>();

const maxLevelVal = props.maxLevel ?? 5;
const selectedAreas = ref<AreaItem[]>([]);
const cascaderValue = ref<(number | string)[] | undefined>();
const options = ref<any[]>([]);
const loading = ref(false);

// 解析 modelValue（JSON 字符串）
watch(
  () => props.modelValue,
  (val) => {
    if (val) {
      try {
        const parsed = JSON.parse(val);
        if (Array.isArray(parsed)) {
          selectedAreas.value = parsed;
        }
      } catch {
        selectedAreas.value = [];
      }
    } else {
      selectedAreas.value = [];
    }
  },
  { immediate: true },
);

// 通知外部值变更
function emitValue() {
  if (selectedAreas.value.length > 0) {
    emit('update:modelValue', JSON.stringify(selectedAreas.value));
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
    const children = await getAreaListSimple({
      parentId: targetOption.id,
    });
    targetOption.children = (children || []).map((item: any) => ({
      ...item,
      isLeaf: (item.level ?? 0) >= maxLevelVal - 1,
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
      isLeaf: (item.level ?? 0) >= maxLevelVal - 1,
    }));
  } finally {
    loading.value = false;
  }
}

onMounted(initOptions);

// 从已加载的 options 树中提取完整路径名称
function getLabelFromOptions(codes: (number | string)[]) {
  const names: string[] = [];
  let currentOptions = options.value;
  for (const code of codes) {
    const option = currentOptions.find(
      (o: any) => String(o.id) === String(code),
    );
    if (option) {
      names.push(option.name || '');
      currentOptions = option.children || [];
    }
  }
  return names.join('');
}

// 点击"添加"按钮
function handleAddArea() {
  if (!cascaderValue.value || cascaderValue.value.length === 0) return;

  const codes = [...cascaderValue.value];
  const label = getLabelFromOptions(codes);

  // 去重
  const exists = selectedAreas.value.some(
    (a) => JSON.stringify(a.codes) === JSON.stringify(codes),
  );
  if (!exists) {
    selectedAreas.value.push({ codes, label: label || codes.join('-') });
    emitValue();
  }

  cascaderValue.value = undefined;
}

// 移除单个行政区
function handleRemove(index: number) {
  selectedAreas.value.splice(index, 1);
  emitValue();
}
</script>

<template>
  <div class="service-scope-select">
    <!-- 已选行政区标签 -->
    <div v-if="selectedAreas.length > 0" class="area-tags">
      <Tag
        v-for="(area, index) in selectedAreas"
        :key="index"
        closable
        @close="handleRemove(index)"
      >
        {{ area.label }}
      </Tag>
    </div>
    <div v-else class="area-placeholder">暂未选择服务范围</div>

    <!-- 级联选择器 + 添加按钮 -->
    <div class="area-cascader-row">
      <Cascader
        v-model:value="cascaderValue"
        :options="options"
        :load-data="loadData"
        :field-names="{ label: 'name', value: 'id', children: 'children' }"
        placeholder="请选择行政区"
        allow-clear
        :disabled="disabled"
        change-on-select
        expand-trigger="click"
        style="flex: 1"
      />
      <Button
        type="primary"
        size="small"
        :disabled="
          !cascaderValue || cascaderValue.length === 0 || disabled
        "
        @click="handleAddArea"
      >
        添加
      </Button>
    </div>
  </div>
</template>

<style scoped>
.service-scope-select {
  width: 100%;
}

.area-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  padding: 8px;
  margin-bottom: 8px;
  border: 1px solid #d9d9d9;
  border-radius: 6px;
  min-height: 40px;
  background-color: #fafafa;
}

.area-placeholder {
  padding: 8px;
  margin-bottom: 8px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  color: #bfbfbf;
  text-align: center;
  min-height: 40px;
  line-height: 24px;
}

.area-cascader-row {
  display: flex;
  gap: 8px;
  align-items: center;
}
</style>