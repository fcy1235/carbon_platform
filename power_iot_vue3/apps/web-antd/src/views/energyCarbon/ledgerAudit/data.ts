import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';

import { DICT_TYPE } from '@vben/constants';
import { getDictOptions } from '@vben/hooks';

import { getAreaListSimple } from '#/api/system/area';
import { getRangePickerDefaultProps } from '#/utils';

/** 河北省ID */
const HEBEI_PROVINCE_ID = 13;

/** 缓存河北省下辖市列表 */
let cachedCities: any[] | null = null;

async function getCitiesOfHebei() {
  if (cachedCities) return cachedCities;
  cachedCities = await getAreaListSimple({ parentId: HEBEI_PROVINCE_ID });
  // 初始化城市ID映射
  cityIdMap.clear();
  (cachedCities || []).forEach((c) => {
    const val = String(c.code ?? c.id ?? '');
    if (c.id != null) cityIdMap.set(val, c.id);
  });
  return cachedCities;
}

/** 城市ID映射表 */
const cityIdMap = new Map<string, number>();

/** 根据选中市的code加载对应区县列表 */
async function loadDistrictsByCity(params: Record<string, any>) {
  if (!params?.cityCode) return [];
  // 确保城市ID映射已初始化
  if (cityIdMap.size === 0) {
    await getCitiesOfHebei();
  }
  const cityId = cityIdMap.get(String(params.cityCode));
  if (cityId == null) return [];
  return getAreaListSimple({ parentId: cityId });
}

/** 列表的搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'cityCode',
      label: '所属市',
      component: 'ApiSelect',
      componentProps: {
        api: () => getCitiesOfHebei(),
        labelField: 'name',
        valueField: 'code',
        numberToString: true,
        afterFetch: (data: any[]) => {
          return (data || []).map((item) => ({
            ...item,
            name: item.name || '',
            code: String(item.code ?? item.id ?? ''),
          }));
        },
        placeholder: '请选择所属市',
        allowClear: true,
      },
    },
    {
      fieldName: 'districtCode',
      label: '所属区县',
      component: 'ApiSelect',
      componentProps: (values: Record<string, any>, formApi: any) => {
        return {
          api: loadDistrictsByCity,
          labelField: 'name',
          valueField: 'code',
          numberToString: true,
          afterFetch: (data: any[]) => {
            return (data || []).map((item) => ({
              ...item,
              name: item.name || '',
              code: String(item.code ?? item.id ?? ''),
            }));
          },
          placeholder: values.cityCode ? '请选择所属区县' : '请先选择所属市',
          allowClear: true,
          disabled: !values.cityCode,
          params: { cityCode: values.cityCode },
        };
      },
    },
    {
      fieldName: 'uploadType',
      label: '上传类型',
      component: 'Select',
      componentProps: {
        options: getDictOptions(DICT_TYPE.ENERGY_CBON_LEDGER_TYPE, 'string'),
        placeholder: '请选择上传类型',
        allowClear: true,
      },
    },
    {
      fieldName: 'uploadStatus',
      label: '上传状态',
      component: 'Select',
      componentProps: {
        options: getDictOptions(
          DICT_TYPE.ENERGY_CBON_SUCCESS_STATUS,
          'string',
        ),
        placeholder: '请选择上传状态',
        allowClear: true,
      },
    },
    {
      fieldName: 'uploadTime',
      label: '上传时间',
      component: 'RangePicker',
      componentProps: {
        ...getRangePickerDefaultProps(),
        allowClear: true,
      },
    },
  ];
}

/** 列表的字段 */
export function useGridColumns(): VxeTableGridOptions['columns'] {
  return [
    { type: 'seq', width: 60, title: '序号' },
    {
      field: 'cityName',
      title: '所属市',
      minWidth: 120,
    },
    {
      field: 'districtName',
      title: '所属区县',
      minWidth: 120,
    },
    {
      field: 'uploadType',
      title: '上传类型',
      minWidth: 120,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.ENERGY_CBON_LEDGER_TYPE },
      },
    },
    {
      field: 'fileName',
      title: '文件名称',
      minWidth: 200,
      showOverflow: 'tooltip',
    },
    {
      field: 'dataCount',
      title: '数据量',
      minWidth: 100,
    },
    {
      field: 'uploadStatus',
      title: '上传状态',
      minWidth: 100,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.ENERGY_CBON_SUCCESS_STATUS },
      },
    },
    {
      field: 'auditStatus',
      title: '流程状态',
      minWidth: 100,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.ENERGY_CBON_LEDGER_REPORT_STATUS },
      },
    },
    {
      field: 'uploadTime',
      title: '上传时间',
      minWidth: 160,
      formatter: 'formatDateTime',
    },
    {
      field: 'uploader',
      title: '上传人',
      minWidth: 160
    },
    {
      title: '操作',
      width: 245,
      fixed: 'right',
      slots: { default: 'actions' },
    },
  ];
}
