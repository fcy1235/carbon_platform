import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';

import { DICT_TYPE } from '@vben/constants';
import { getDictLabel, getDictOptions } from '@vben/hooks';

import { z } from '#/adapter/form';
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

export const PROJECT_STATUS_OPTIONS = getDictOptions(DICT_TYPE.ENERGY_CBON_PROJECT_STATUS, 'string');

/** 获取状态标签 */
export function getProjectStatusLabel(status?: number | string): string {
  if (status === undefined || status === null) return '-';
  return getDictLabel(DICT_TYPE.ENERGY_CBON_PROJECT_STATUS, String(status)) || '-';
}

/** 项目基本信息表单 */
export function useBasicFormSchema(): VbenFormSchema[] {
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
      fieldName: 'projectName',
      label: '项目名称',
      component: 'Input',
      componentProps: {
        placeholder: '请输入项目名称',
      },
      rules: 'required',
    },
    {
      fieldName: 'projectLeader',
      label: '项目负责人',
      component: 'Input',
      componentProps: {
        placeholder: '请选择项目负责人',
      },
      rules: 'required',
    },
    {
      fieldName: 'division',
      label: '行政区',
      component: 'ApiCascader',
      componentProps: {
        placeholder: '请选择行政区',
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
      fieldName: 'projectDesc',
      label: '项目描述',
      component: 'Textarea',
      componentProps: {
        placeholder: '请输入项目描述',
      },
    },
    {
      fieldName: 'planStartDate',
      label: '计划开始时间',
      component: 'DatePicker',
      componentProps: {
        placeholder: '请选择计划开始时间',
        valueFormat: 'YYYY-MM-DD',
      },
      rules: 'required',
    },
    {
      fieldName: 'planEndDate',
      label: '计划结束时间',
      component: 'DatePicker',
      componentProps: {
        placeholder: '请选择计划结束时间',
        valueFormat: 'YYYY-MM-DD',
      },
      rules: 'required',
    },
    {
      fieldName: 'projectCycle',
      label: '项目周期',
      component: 'Input',
      componentProps: {
        placeholder: '自动计算',
        disabled: true,
      },
      rules: 'required',
    },
    {
      fieldName: 'totalReduction',
      label: '项目减排量',
      component: 'InputNumber',
      componentProps: {
        min: 0,
        placeholder: '自动计算',
        addonAfter: 'kgCO2e',
        disabled: true,
      },
      rules: z.number().default(0),
    },
    {
      fieldName: 'projectStatus',
      label: '项目状态',
      component: 'Select',
      componentProps: {
        options: PROJECT_STATUS_OPTIONS,
      },
      rules: 'required',
    },
  ];
}

/** 搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'projectName',
      label: '项目名称',
      component: 'Input',
      componentProps: {
        placeholder: '请输入项目名称',
        allowClear: true,
      },
    },
    {
      fieldName: 'projectLeader',
      label: '项目负责人',
      component: 'Input',
      componentProps: {
        placeholder: '请输入项目负责人',
        allowClear: true,
      },
    },
    {
      fieldName: 'planStartDateStart',
      label: '计划开始时间',
      component: 'DatePicker',
      componentProps: {
        placeholder: '请选择计划开始时间',
        valueFormat: 'YYYY-MM-DD',
      },
    },
    {
      fieldName: 'planEndDateEnd',
      label: '计划结束时间',
      component: 'DatePicker',
      componentProps: {
        placeholder: '请选择计划结束时间',
        valueFormat: 'YYYY-MM-DD',
      },
    },
    {
      fieldName: 'projectStatus',
      label: '项目状态',
      component: 'Select',
      componentProps: {
        placeholder: '请选择项目状态',
        allowClear: true,
        options: PROJECT_STATUS_OPTIONS,
      },
    },
  ];
}

/** 列表字段 */
export function useGridColumns(): VxeTableGridOptions['columns'] {
  return [
    { type: 'checkbox', width: 60 },
    { type: 'seq', width: 60 },
    {
      field: 'projectName',
      title: '项目名称',
      minWidth: 150,
    },
    {
      field: 'projectLeader',
      title: '项目负责人',
      minWidth: 120,
      formatter: ({ row }: any) => row.contactName || row.projectLeader || '-',
    },
    {
      field: 'planStartDate',
      title: '计划开始时间',
      minWidth: 150,
      slots: { default: 'planStartDate' },
    },
    {
      field: 'planEndDate',
      title: '计划结束时间',
      minWidth: 150,
      slots: { default: 'planEndDate' },
    },
    {
      field: 'projectCycle',
      title: '项目周期',
      minWidth: 120,
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
      field: 'totalReduction',
      title: '项目减排量(kgCO2e)',
      minWidth: 150,
    },
    {
      field: 'projectStatus',
      title: '项目状态',
      minWidth: 120,
      slots: { default: 'status' },
    },
    {
      title: '操作',
      width: 180,
      fixed: 'right',
      slots: { default: 'actions' },
    },
  ];
}
