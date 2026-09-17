import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';

import { DICT_TYPE } from '@vben/constants';
import { getDictOptions } from '@vben/hooks';
import { getEmissionSourceList } from '#/api/energyCarbon/emissionSource';
import { getDocumentSimpleList } from '#/api/energyCarbon/document';

/** 气体列表表格列 */
export function useGasListColumns(): VxeTableGridOptions['columns'] {
  return [
    {
      field: 'gasName',
      title: '气体名称',
      minWidth: 180,
    },
    {
      field: 'gwp',
      title: '全球变暖潜能值(GWP)',
      minWidth: 180,
    },
    {
      field: 'category',
      title: '所属类别',
      minWidth: 120,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.ENERGY_CBON_GAS_CATEGORY },
      },
    },
    {
      title: '操作',
      width: 80,
      slots: { default: 'gasActions' },
    },
  ];
}

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
      fieldName: 'factorCode',
      label: '排放因子编码',
      component: 'Input',
      componentProps: {
        placeholder: '系统自动生成',
        disabled: true,
      },
    },
    {
      fieldName: 'factorName',
      label: '排放因子名称',
      component: 'Input',
      componentProps: {
        placeholder: '请输入排放因子名称',
      },
      rules: 'required',
    },

    {
      fieldName: 'emissionSource',
      label: '排放源',
      component: 'ApiSelect',
      componentProps: {
        placeholder: '请选择排放源',
        allowClear: true,
        api: () => getEmissionSourceList(),
        labelField: 'sourceName',
        valueField: 'sourceCode',
        style: { width: '100%' },
      },
      rules: 'required',
    },
    {
      fieldName: 'unit',
      label: '计量单位',
      component: 'Select',
      componentProps: {
        options: getDictOptions(
          DICT_TYPE.ENERGY_CBON_UNIT_MEASURE,
          'string',
        ),
        placeholder: '请选择计量单位',
        allowClear: true,
      },
      rules: 'required',
    },
    {
      fieldName: 'factorValue',
      label: '排放因子值',
      component: 'InputNumber',
      componentProps: {
        min: 0,
        placeholder: '请输入排放因子值',
        style: { width: '100%' },
      },
      rules: 'required',
    },
    {
      fieldName: 'relatedDoc',
      label: '关联核算标准文档',
      component: 'ApiSelect',
      componentProps: {
        placeholder: '请选择关联核算标准文档',
        allowClear: true,
        api: () => getDocumentSimpleList(),
        labelField: 'docTitle',
        valueField: 'id',
        style: { width: '100%' },
      },
    },
  ];
}

/** 列表的搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'factorName',
      label: '排放因子名称',
      component: 'Input',
      componentProps: {
        placeholder: '请输入排放因子名称',
        allowClear: true,
      },
    },
    {
      fieldName: 'emissionSource',
      label: '排放源',
      component: 'ApiSelect',
      componentProps: {
        placeholder: '请选择排放源',
        allowClear: true,
        api: () => getEmissionSourceList(),
        labelField: 'sourceName',
        valueField: 'sourceCode',
        style: { width: '100%' },
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
      field: 'factorCode',
      title: '排放因子编码',
      minWidth: 160,
    },
    {
      field: 'factorName',
      title: '排放因子名称',
      minWidth: 150,
    },
    {
      field: 'emissionSource',
      title: '排放源',
      minWidth: 120,
    },
    {
      field: 'factorValue',
      title: '排放因子值',
      minWidth: 120,
    },
    {
      field: 'unit',
      title: '计量单位',
      minWidth: 100,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.ENERGY_CBON_UNIT_MEASURE },
      },
    },
    {
      field: 'relatedDoc',
      title: '关联核算标准文档',
      minWidth: 150,
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
