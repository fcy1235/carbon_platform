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
      fieldName: 'gasCode',
      label: '气体编码',
      component: 'Input',
      componentProps: {
        placeholder: '系统自动生成',
        disabled: true,
      },
    },
    {
      fieldName: 'gasName',
      label: '气体名称',
      component: 'Input',
      componentProps: {
        placeholder: '请输入气体名称',
      },
      rules: 'required',
    },
    {
      fieldName: 'gwp',
      label: '全球变暖潜能值(GWP)',
      component: 'InputNumber',
      componentProps: {
        min: 0,
        placeholder: '请输入GWP值',
        style: { width: '100%' },
      },
      rules: 'required',
    },
    {
      fieldName: 'category',
      label: '所属类别',
      component: 'Select',
      componentProps: {
        options: getDictOptions(DICT_TYPE.ENERGY_CBON_GAS_CATEGORY, 'number'),
        placeholder: '请选择能碳气体类别',
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
      fieldName: 'gasName',
      label: '气体名称',
      component: 'Input',
      componentProps: {
        placeholder: '请输入气体名称',
        allowClear: true,
      },
    },
    {
      fieldName: 'category',
      label: '所属类别',
      component: 'Select',
      componentProps: {
        options: getDictOptions(DICT_TYPE.ENERGY_CBON_GAS_CATEGORY, 'string'),
        placeholder: '请选择能碳气体类别',
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
      field: 'gasCode',
      title: '气体编码',
      minWidth: 160,
    },
    {
      field: 'gasName',
      title: '气体名称',
      minWidth: 150,
    },
    {
      field: 'gwp',
      title: '全球变暖潜能值(GWP)',
      minWidth: 160,
    },
    {
      field: 'category',
      title: '所属类别',
      minWidth: 160,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.ENERGY_CBON_GAS_CATEGORY },
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
