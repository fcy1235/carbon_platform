import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';

import { DICT_TYPE } from '@vben/constants';
import { getDictOptions } from '@vben/hooks';

/** 新增/修改的表单 */
export function useFormSchema(): VbenFormSchema[] {
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
      fieldName: 'sourceCode',
      label: '排放源编码',
      component: 'Input',
      componentProps: {
        placeholder: '系统自动生成',
        disabled: true,
      },
    },
    {
      fieldName: 'sourceName',
      label: '排放源名称',
      component: 'Input',
      componentProps: {
        placeholder: '请输入排放源名称',
      },
      rules: 'required',
    },
    {
      fieldName: 'scope',
      label: '排放范围',
      component: 'Select',
      componentProps: {
        options: getDictOptions(DICT_TYPE.ENERGY_CBON_EMISSION_SCOPE, 'string'),
        placeholder: '请选择能碳排放源范围',
        allowClear: true,
      },
      rules: 'required',
    },
  ];
}

/** 列表的搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'sourceName',
      label: '排放源名称',
      component: 'Input',
      componentProps: {
        placeholder: '请输入排放源名称',
        allowClear: true,
      },
    },
    {
      fieldName: 'scope',
      label: '排放范围',
      component: 'Select',
      componentProps: {
        options: getDictOptions(DICT_TYPE.ENERGY_CBON_EMISSION_SCOPE, 'string'),
        placeholder: '请选择能碳排放源范围',
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
      field: 'sourceCode',
      title: '排放源编码',
      minWidth: 160,
    },
    {
      field: 'sourceName',
      title: '排放源名称',
      minWidth: 150,
    },
    {
      field: 'scope',
      title: '排放范围',
      minWidth: 140,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.ENERGY_CBON_EMISSION_SCOPE },
      },
    },
    {
      field: 'createTime',
      title: '创建时间',
      minWidth: 160,
    },
    {
      title: '操作',
      width: 180,
      fixed: 'right',
      slots: { default: 'actions' },
    },
  ];
}
