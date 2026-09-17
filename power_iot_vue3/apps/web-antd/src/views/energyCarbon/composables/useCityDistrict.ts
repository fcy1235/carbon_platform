import { onMounted, ref } from 'vue';

import { getAreaListSimple } from '#/api/system/area';

/** 河北省ID */
const HEBEI_PROVINCE_ID = 13;

export interface CityDistrictValue {
  cityCode?: string;
  cityName?: string;
  districtCode?: string;
  districtName?: string;
}

export interface UseCityDistrictOptions {
  /** 是否自动加载市列表，默认 true */
  autoLoad?: boolean;
  /** 省份ID，默认 13（河北省） */
  provinceId?: number;
}

/**
 * 市/区县选择 composable
 * 封装台账模块中"所属市"和"所属区县"的级联选择逻辑
 */
export function useCityDistrict(options: UseCityDistrictOptions = {}) {
  const {
    autoLoad = true,
    provinceId = HEBEI_PROVINCE_ID,
  } = options;

  // 市列表
  const cityCode = ref<string>();
  const cityOptions = ref<{ label: string; value: string }[]>([]);
  const cityLoading = ref(false);

  // 区县列表
  const districtCode = ref<string>();
  const districtOptions = ref<{ label: string; value: string }[]>([]);
  const districtLoading = ref(false);

  // 防止回填时触发 onChange 清空 districtCode
  const isRestoring = ref(false);

  // cityCode → cityId 映射表（用于级联加载区县）
  const cityIdMap = new Map<string, number>();

  /** 加载市列表 */
  async function loadCityOptions() {
    if (cityOptions.value.length > 0) return;
    cityLoading.value = true;
    try {
      const cities = await getAreaListSimple({ parentId: provinceId });
      cityIdMap.clear();
      cityOptions.value = (cities || []).map((c) => {
        const val = String(c.code ?? c.id ?? '');
        if (c.id != null) cityIdMap.set(val, c.id);
        return { label: c.name || '', value: val };
      });
    } catch {
      cityIdMap.clear();
      cityOptions.value = [];
    } finally {
      cityLoading.value = false;
    }
  }

  /** 根据选中市加载对应区县列表 */
  async function loadDistrictOptions(code: string) {
    if (!code) {
      districtOptions.value = [];
      return;
    }
    // 确保市列表已加载
    if (cityOptions.value.length === 0) {
      await loadCityOptions();
    }
    const cityId = cityIdMap.get(code);
    if (cityId == null) {
      districtOptions.value = [];
      return;
    }
    districtLoading.value = true;
    try {
      const districts = await getAreaListSimple({ parentId: cityId });
      districtOptions.value = (districts || []).map((d) => ({
        label: d.name || '',
        value: String(d.code ?? d.id ?? ''),
      }));
    } catch {
      districtOptions.value = [];
    } finally {
      districtLoading.value = false;
    }
  }

  /** 所属市变化时处理 */
  async function handleCityChange(value: any) {
    if (isRestoring.value) return;
    districtCode.value = undefined;
    if (value) {
      await loadDistrictOptions(value);
    } else {
      districtOptions.value = [];
    }
  }

  /** 回填市/区县数据 */
  async function restoreCityDistrict(data: CityDistrictValue) {
    if (!data.cityCode) return;
    isRestoring.value = true;
    try {
      // 确保市列表已加载
      await loadCityOptions();
      cityCode.value = String(data.cityCode);
      await loadDistrictOptions(String(data.cityCode));
      districtCode.value = data.districtCode
        ? String(data.districtCode)
        : undefined;
    } finally {
      isRestoring.value = false;
    }
  }

  /** 获取市/区县的编码和名称 */
  function getCityDistrictData(): CityDistrictValue {
    const city = cityOptions.value.find((c) => c.value === cityCode.value);
    const district = districtOptions.value.find(
      (d) => d.value === districtCode.value,
    );
    return {
      cityCode: cityCode.value,
      cityName: city?.label,
      districtCode: districtCode.value,
      districtName: district?.label,
    };
  }

  /** 重置状态 */
  function resetCityDistrict() {
    cityCode.value = undefined;
    districtCode.value = undefined;
    districtOptions.value = [];
    isRestoring.value = false;
  }

  // 自动加载市列表
  if (autoLoad) {
    onMounted(loadCityOptions);
  }

  return {
    // 市相关
    cityCode,
    cityOptions,
    cityLoading,
    loadCityOptions,

    // 区县相关
    districtCode,
    districtOptions,
    districtLoading,
    loadDistrictOptions,

    // 事件处理
    handleCityChange,

    // 数据操作
    restoreCityDistrict,
    getCityDistrictData,
    resetCityDistrict,

    // 状态
    isRestoring,
  };
}
