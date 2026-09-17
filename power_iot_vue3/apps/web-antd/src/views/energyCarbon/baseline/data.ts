import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';

import { DICT_TYPE } from '@vben/constants';
import { getDictOptions } from '@vben/hooks';

import { getAreaTree } from '#/api/system/area';

const AREA_TREE_CACHE_KEY = 'local_storage_data-areaTree';

async function getAreaTreeWithCache() {
  const cachedAreaTree = localStorage.getItem(AREA_TREE_CACHE_KEY);
  if (cachedAreaTree) {
    try {
      return JSON.parse(cachedAreaTree);
    } catch {}
  }
  const result = await getAreaTree({ levels: [0, 1, 2] });
  if (result?.length) {
    localStorage.setItem(AREA_TREE_CACHE_KEY, JSON.stringify(result));
  }
  return result;
}

/** 列表的搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'reformType',
      label: '气候子区',
      component: 'Select',
      componentProps: {
        options: getDictOptions(DICT_TYPE.ENERGY_CBON_CLIMATE_ZONE, 'string'),
        placeholder: '请选择气候子区',
        allowClear: true,
      },
    },
    {
      fieldName: 'division',
      label: '行政区划',
      component: 'ApiCascader',
      componentProps: {
        placeholder: '请选择行政区划',
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
  ];
}

/** 列表的字段 */
export function useGridColumns(): VxeTableGridOptions['columns'] {
  return [
    { type: 'checkbox', width: 40 },
    { type: 'seq', width: 60 },
    {
      field: 'division',
      title: '行政区划名称',
      minWidth: 150,
    },
    {
      field: 'reformType',
      title: '所属气候子区',
      minWidth: 150,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.ENERGY_CBON_CLIMATE_ZONE },
      },
    },
    {
      field: 'intensity',
      title: '基准线碳排放强度',
      minWidth: 180,
    },
    {
      field: 'unit',
      title: '计量单位',
      minWidth: 120,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.ENERGY_CBON_UNIT_MEASURE },
      },
    },
    {
      title: '操作',
      width: 180,
      fixed: 'right',
      slots: { default: 'actions' },
    },
  ];
}

/** 表单的字段 */
export function useFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'division',
      label: '行政区划名称',
      component: 'ApiCascader',
      componentProps: {
        placeholder: '请选择行政区划',
        allowClear: true,
        api: () => getAreaTreeWithCache(),
        fieldNames: {
          label: 'name',
          value: 'id',
          children: 'children',
        },
        style: { width: '100%' },
      },
      rules: 'required',
    },
    {
      fieldName: 'reformType',
      label: '所属气候子区',
      component: 'Select',
      componentProps: {
        options: getDictOptions(DICT_TYPE.ENERGY_CBON_CLIMATE_ZONE, 'string'),
        placeholder: '请选择气候子区',
        allowClear: true,
      },
      rules: 'required',
    },
    {
      fieldName: 'intensity',
      label: '基准线碳排放强度',
      component: 'InputNumber',
      componentProps: {
        placeholder: '请输入基准线碳排放强度',
        min: 0,
        precision: 2,
        style: { width: '100%' },
      },
      rules: 'required',
    },
    {
      fieldName: 'unit',
      label: '计量单位',
      component: 'Select',
      componentProps: {
        options: getDictOptions(DICT_TYPE.ENERGY_CBON_UNIT_MEASURE, 'string'),
        placeholder: '请选择计量单位',
        allowClear: true,
      },
      rules: 'required',
    },
  ];
}
