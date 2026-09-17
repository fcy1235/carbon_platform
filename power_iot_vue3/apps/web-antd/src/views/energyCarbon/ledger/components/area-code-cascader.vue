<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue';

import { Cascader } from 'ant-design-vue';

import { getAreaListSimple } from '#/api/system/area';

export interface AreaCodeValue {
  provinceCode?: string;
  provinceName?: string;
  cityCode?: string;
  cityName?: string;
  districtCode?: string;
  districtName?: string;
}

const props = defineProps<{
  value?: AreaCodeValue;
  placeholder?: string;
  allowClear?: boolean;
  disabled?: boolean;
}>();

const emit = defineEmits<{
  (e: 'update:value', val?: AreaCodeValue): void;
  (e: 'change', val?: AreaCodeValue): void;
}>();

const options = ref<any[]>([]);
const loading = ref(false);

const innerValue = computed({
  get: () => props.value,
  set: (val) => emit('update:value', val),
});

function buildAreaOption(item: any, level: number) {
  return {
    ...item,
    isLeaf: level >= 2,
  };
}

async function loadData(selectedOptions: any[]) {
  const target = selectedOptions[selectedOptions.length - 1];
  if (!target) return;
  target.loading = true;
  try {
    const level = target.level ?? 0;
    const children = await getAreaListSimple({ parentId: target.id });
    target.children = (children || []).map((item: any) =>
      buildAreaOption(item, level + 1),
    );
  } finally {
    target.loading = false;
  }
}

async function initOptions() {
  loading.value = true;
  try {
    const provinces = await getAreaListSimple({ parentId: 0 });
    options.value = (provinces || []).map((item: any) =>
      buildAreaOption(item, 0),
    );
    await syncExpand();
  } finally {
    loading.value = false;
  }
}

async function syncExpand() {
  const val = innerValue.value;
  if (!val) return;
  if (val.provinceCode) {
    await expandPath([
      val.provinceCode,
      val.cityCode,
      val.districtCode,
    ].filter(Boolean) as string[]);
  } else if (val.cityCode && val.districtCode) {
    await expandByCityAndDistrict(val.cityCode, val.districtCode);
  }
}

async function expandPath(path: string[]) {
  let current = options.value;
  for (const code of path) {
    const option = current.find(
      (o) => String(o.code) === String(code),
    );
    if (!option) break;
    const level = option.level ?? 0;
    const children = await getAreaListSimple({ parentId: option.id });
    option.children = (children || []).map((item: any) =>
      buildAreaOption(item, level + 1),
    );
    current = option.children;
  }
}

async function expandByCityAndDistrict(cityCode?: string, districtCode?: string) {
  if (!cityCode || !districtCode) return;
  const provinces = await getAreaListSimple({ parentId: 0 });
  for (const province of provinces || []) {
    const cities = await getAreaListSimple({ parentId: province.id });
    const city = cities.find(
      (item: any) => String(item.code) === String(cityCode),
    );
    if (city) {
      province.children = (cities || []).map((item: any) =>
        buildAreaOption(item, 1),
      );
      const districts = await getAreaListSimple({ parentId: city.id });
      city.children = (districts || []).map((item: any) =>
        buildAreaOption(item, 2),
      );
      const district = districts.find(
        (item: any) => String(item.code) === String(districtCode),
      );
      options.value = (provinces || []).map((item: any) =>
        buildAreaOption(item, 0),
      );
      innerValue.value = {
        provinceCode: province.code,
        provinceName: province.name,
        cityCode: city.code,
        cityName: city.name,
        districtCode: district?.code,
        districtName: district?.name,
      };
      return;
    }
  }
}

function handleChange(_value: (string | number)[], selectedOptions: any[]) {
  if (!selectedOptions || selectedOptions.length === 0) {
    innerValue.value = undefined;
    emit('change', undefined);
    return;
  }
  const [province, city, district] = selectedOptions;
  const newVal: AreaCodeValue = {
    provinceCode: province?.code,
    provinceName: province?.name,
    cityCode: city?.code,
    cityName: city?.name,
    districtCode: district?.code,
    districtName: district?.name,
  };
  innerValue.value = newVal;
  emit('change', newVal);
}

onMounted(initOptions);

watch(
  () => props.value,
  async (val, oldVal) => {
    if (
      val &&
      JSON.stringify(val) !== JSON.stringify(oldVal) &&
      options.value.length > 0
    ) {
      await syncExpand();
    }
  },
);
</script>

<template>
  <Cascader
    :value="
      innerValue
        ? ([innerValue.provinceCode, innerValue.cityCode, innerValue.districtCode].filter(Boolean) as (string | number)[])
        : undefined
    "
    :options="options"
    :load-data="loadData"
    :field-names="{ label: 'name', value: 'code', children: 'children' }"
    :placeholder="placeholder || '请选择行政区划'"
    :allow-clear="allowClear"
    :disabled="disabled"
    change-on-select
    style="width: 100%"
    @change="handleChange as any"
  />
</template>
