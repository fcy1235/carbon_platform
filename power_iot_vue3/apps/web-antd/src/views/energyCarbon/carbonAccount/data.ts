import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';

import { DICT_TYPE } from '@vben/constants';
import { getDictOptions } from '@vben/hooks';

import { getAreaTree } from '#/api/system/area';
const AREA_TREE_CACHE_KEY = 'local_storage_data-areaTree';

async function getAreaTreeWithCache() {
  // 先从缓存获取
  const cachedAreaTree = localStorage.getItem(AREA_TREE_CACHE_KEY);
  if (cachedAreaTree) {
    try {
      return JSON.parse(cachedAreaTree);
    } catch {
      // 缓存数据格式错误，继续请求 API
    }
  }
  // 没有缓存或缓存错误，请求 API
  const result = await getAreaTree({ levels: [0, 1, 2] });
  if (result?.length) {
    localStorage.setItem(AREA_TREE_CACHE_KEY, JSON.stringify(result));
  }
  return result;
}

/** 新增/修改的表单 - 核算信息 */
export function useAccountingFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'id',
      component: 'Input',
      dependencies: {
        triggerFields: [''],
        show: () => false,
      },
    },
    {
      fieldName: 'carbonUserInfoId',
      component: 'Input',
      dependencies: {
        triggerFields: [''],
        show: () => false,
      },
    },
    {
      fieldName: 'username',
      label: '用户名',
      component: 'Input',
      componentProps: {
        placeholder: '请选择用户',
        disabled: true,
      },
      rules: 'required',
    },
    {
      fieldName: 'division',
      label: '所属行政区划',
      component: 'ApiCascader',
      componentProps: {
        placeholder: '请选择所属行政区划',
        disabled: true,
        api: () => getAreaTreeWithCache(),
        fieldNames: {
          label: 'name',
          value: 'id',
          children: 'children',
        },
        style: { width: '100%' },
      },
    },
    {
      fieldName: 'address',
      label: '地址',
      component: 'Input',
      componentProps: {
        placeholder: '请输入地址',
        disabled: true,
      },
    },
    {
      fieldName: 'reformType',
      label: '改造类别',
      component: 'Select',
      componentProps: {
        options: getDictOptions(
          DICT_TYPE.ENERGY_CBON_TRANSFORMATION_TYPE,
          'string',
        ),
        placeholder: '请选择改造类别',
        allowClear: true,
        disabled: true,
      },
      rules: 'required',
    },
    {
      fieldName: 'accountingPeriodYear',
      label: '核算周期',
      component: 'DatePicker',
      componentProps: {
        picker: 'year',
        placeholder: '请选择核算年份',
        valueFormat: 'YYYY',
      },
      rules: 'required',
    },
    {
      fieldName: 'heatingArea',
      label: '清洁建筑取暖面积',
      component: 'InputNumber',
      componentProps: {
        min: 0,
        placeholder: '请输入清洁建筑取暖面积',
        addonAfter: '㎡',
        disabled: true,
      },
    },
    {
      fieldName: 'reduction',
      label: '减排量',
      component: 'InputNumber',
      componentProps: {
        min: 0,
        placeholder: '请输入减排量',
        addonAfter: 'kgCO2e',
        disabled: true,
      },
    },
  ];
}

/** 基准线排放量表单 */
export function useBaselineFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'baselineIntensity',
      label: '区域基准碳排放强度',
      component: 'InputNumber',
      componentProps: {
        min: 0,
        placeholder: '请输入区域基准碳排放强度',
        style: { width: '100%' },
        disabled: true,
      },
    },
    {
      fieldName: 'baselineEmission',
      label: '基准线排放量',
      component: 'InputNumber',
      componentProps: {
        min: 0,
        placeholder: '基准线排放量',
        addonAfter: 'kgCO2e',
        disabled: true,
      },
    },
  ];
}

/** 活动表单 */
export function useActivityFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'activityName',
      label: '活动名称',
      component: 'Select',
      componentProps: {
        options: getDictOptions(DICT_TYPE.ENERGY_CBON_ACTIVITY_TYPE, 'string'),
        placeholder: '请选择活动名称',
        allowClear: true,
      },
      rules: 'required',
    },
    {
      fieldName: 'activityLevel',
      label: '活动水平数据',
      component: 'InputNumber',
      componentProps: {
        min: 0,
        placeholder: '请输入活动水平数据',
        style: { width: '100%' },
      },
    },
    {
      fieldName: 'emissionFactor',
      label: '排放因子',
      component: 'InputNumber',
      componentProps: {
        min: 0,
        placeholder: '请输入排放因子',
        style: { width: '100%' },
      },
    },
    {
      fieldName: 'emission',
      label: '排放量',
      component: 'InputNumber',
      componentProps: {
        min: 0,
        placeholder: '排放量',
        addonAfter: 'kgCO2e',
      },
    },
  ];
}

/** 列表的搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'username',
      label: '用户名',
      component: 'Input',
      componentProps: {
        placeholder: '请输入用户名',
        allowClear: true,
      },
    },
    {
      fieldName: 'division',
      label: '所属行政区划',
      component: 'ApiCascader',
      componentProps: {
        placeholder: '请选择所属行政区划',
        allowClear: true,
        api: () => getAreaTreeWithCache(),
        fieldNames: {
          label: 'name',
          value: 'id',
          children: 'children',
        },
        style: { width: '100%' },
      },
    },
    {
      fieldName: 'activityNameCode',
      label: '活动名称',
      component: 'Select',
      componentProps: {
        options: getDictOptions(DICT_TYPE.ENERGY_CBON_ACTIVITY_TYPE, 'string'),
        placeholder: '请选择活动名称',
        allowClear: true,
      },
    },
  ];
}

/** 列表的字段 */
export function useGridColumns(): VxeTableGridOptions['columns'] {
  return [
    { type: 'checkbox', width: 60 },
    { type: 'seq', width: 60 },
    {
      field: 'username',
      title: '用户名',
      minWidth: 100,
    },
    {
      field: 'division',
      title: '所属行政区划',
      minWidth: 200,
    },
    {
      field: 'address',
      title: '地址',
      minWidth: 200,
    },
    {
      field: 'reformType',
      title: '改造类别',
      minWidth: 120,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.ENERGY_CBON_TRANSFORMATION_TYPE },
      },
    },

    {
      field: 'accountingPeriodStart',
      title: '核算周期',
      minWidth: 220,
      formatter: ({ row }: any) => {
        const start = row.accountingPeriodStart || '';
        const end = row.accountingPeriodEnd || '';
        return start ? `${start} ~ ${end}` : '-';
      },
    },
    {
      field: 'electricityUsage',
      title: '用电量(kWh)/用气量(m³)',
      minWidth: 200,
      formatter: ({ row }: any) => {
        const electricity = row.electricityUsage ?? '-';
        const gas = row.gasUsage ?? '-';
        return `${electricity} / ${gas}`;
      },
    },
    {
      field: 'reduction',
      title: '减排量(kgCO2e)',
      minWidth: 150,
    },
    {
      title: '操作',
      width: 180,
      fixed: 'right',
      slots: { default: 'actions' },
    },
  ];
}

/** 活动列表表格列配置 */
export function useActivityGridColumns() {
  return [
    {
      title: '活动名称',
      field: 'activityName',
      minWidth: 150,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.ENERGY_CBON_ACTIVITY_TYPE },
      },
    },
    { title: '活动水平数据', field: 'activityLevel', minWidth: 120 },
    { title: '排放因子', field: 'emissionFactor', minWidth: 120 },
    { title: '排放量(kgCO2e)', field: 'emission', minWidth: 150 },
    {
      title: '操作',
      minWidth: 150,
      fixed: 'right',
      slots: { default: 'actions' },
    },
  ];
}
