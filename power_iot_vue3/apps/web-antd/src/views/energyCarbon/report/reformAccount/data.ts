import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';

import { DICT_TYPE } from '@vben/constants';
import { getDictOptions } from '@vben/hooks';

import LazyAreaCascader from '#/components/lazy-area-cascader/lazy-area-cascader.vue';

/** 列表的搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'division',
      label: '行政区划',
      component: LazyAreaCascader,
      componentProps: {
        placeholder: '请选择行政区划',
        allowClear: true,
      },
    },
    {
      fieldName: 'keyword',
      label: '用户信息',
      component: 'Input',
      componentProps: {
        placeholder: '请输入身份证号或姓名',
        allowClear: true,
      },
    },
    {
      fieldName: 'phone',
      label: '联系方式',
      component: 'Input',
      componentProps: {
        placeholder: '请输入联系方式',
        allowClear: true,
      },
    },
    {
      fieldName: 'reformType',
      label: '改造类别',
      component: 'Select',
      componentProps: {
        options: getDictOptions(DICT_TYPE.ENERGY_CBON_TRANSFORMATION_TYPE, 'string'),
        placeholder: '请选择改造类别',
        allowClear: true,
      },
    },
    {
      fieldName: 'useStatus',
      label: '使用状态',
      component: 'Select',
      componentProps: {
        options: getDictOptions(DICT_TYPE.ENERGY_CBON_USE_STATUS, 'string'),
        placeholder: '请选择使用状态',
        allowClear: true,
      },
    },
    {
      fieldName: 'reformYear',
      label: '改造年限',
      component: 'DatePicker',
      componentProps: {
        picker: 'year',
        placeholder: '请选择改造年限',
        allowClear: true,
        style: { width: '100%' },
      },
    },
  ];
}

/** 列表的字段 */
export function useGridColumns(): VxeTableGridOptions['columns'] {
  return [
    { type: 'seq', width: 60 },
    {
      field: 'districtName',
      title: '所属区县',
      minWidth: 120,
    },
    {
      field: 'townName',
      title: '所属乡镇',
      minWidth: 120,
    },
    {
      field: 'villageName',
      title: '所属村名称',
      minWidth: 120,
    },
    {
      field: 'address',
      title: '地址',
      minWidth: 200,
    },
    {
      field: 'username',
      title: '户主姓名',
      minWidth: 100,
    },
    {
      field: 'idCard',
      title: '身份证号',
      minWidth: 170,
    },
    {
      field: 'phone',
      title: '联系方式',
      minWidth: 120,
    },
    {
      field: 'heatingArea',
      title: '采暖面积（㎡）',
      minWidth: 130,
    },
    {
      field: 'reformYear',
      title: '改造年限',
      minWidth: 100,
    },
    {
      field: 'useStatus',
      title: '使用状态',
      minWidth: 100,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.ENERGY_CBON_USE_STATUS },
      },
    },
    {
      field: 'reformType',
      title: '改造类别',
      minWidth: 100,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.ENERGY_CBON_TRANSFORMATION_TYPE },
      },
    },
    {
      field: 'remark',
      title: '备注',
      minWidth: 150,
    },
  ];
}
