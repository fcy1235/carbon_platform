import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';

import { DICT_TYPE } from '@vben/constants';

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
      fieldName: 'electricityId',
      label: '电表ID(户号)',
      component: 'Input',
      componentProps: {
        placeholder: '请输入电表ID(户号)',
        allowClear: true,
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
      field: 'username',
      title: '用户名',
      minWidth: 100,
    },
    {
      field: 'idCard',
      title: '身份证号',
      minWidth: 120,
    },
    {
      field: 'division',
      title: '所属行政区划',
      minWidth: 150,
    },
    {
      field: 'address',
      title: '地址',
      minWidth: 200,
    },
    {
      field: 'electricityId',
      title: '电表ID(户号)',
      minWidth: 130,
    },
    {
      field: 'dataSource',
      title: '数据来源',
      minWidth: 90,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.ENERGY_CBON_ELECTRIC_SOURCE },
      },
    },
    {
      field: 'currentTotal',
      title: '最新累计电量(kWh)',
      minWidth: 160,
      formatter: ({ row }: any) => row.currentTotal ?? row.latestTotal ?? '-',
    },

    {
      field: 'currentUsage',
      title: '本次用电量(kWh)',
      minWidth: 160,
      formatter: ({ row }: any) => row.currentUsage ?? row.latestUsage ?? '-',
    },
    {
      field: 'readingTime',
      title: '读数时间',
      minWidth: 150,
      formatter: ({ row }: any) => row.readingTime ?? row.latestTime ?? '-',
    },
    {
      title: '操作',
      width: 180,
      fixed: 'right',
      slots: { default: 'actions' },
    },
  ];
}

/** 历史数据表格列 */
export function useHistoryGridColumns(): VxeTableGridOptions['columns'] {
  return [
    { type: 'seq', width: 60 },
    {
      field: 'readingTime',
      title: '读数时间',
      minWidth: 180,
    },
    {
      field: 'currentTotal',
      title: '当前累计电量(kWh)',
      minWidth: 160,
      formatter: ({ row }: any) => row.currentTotal ?? row.latestTotal ?? '-',
    },
    {
      field: 'currentUsage',
      title: '本次用电量(kWh)',
      minWidth: 160,
      formatter: ({ row }: any) => row.currentUsage ?? row.latestUsage ?? '-',
    },
    {
      field: 'dataSource',
      title: '数据来源',
      minWidth: 90,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.ENERGY_CBON_ELECTRIC_SOURCE },
      },
    },
  ];
}
