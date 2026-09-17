import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';

import { DICT_TYPE } from '@vben/constants';
import { getDictOptions } from '@vben/hooks';

import LazyAreaCascader from '#/components/lazy-area-cascader/lazy-area-cascader.vue';

/** 搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'username',
      label: '用户名',
      component: 'Input',
      componentProps: {
        placeholder: '请输入用户名',
      },
    },
    {
      fieldName: 'division',
      label: '所属行政区划',
      component: LazyAreaCascader,
      componentProps: {
        placeholder: '请选择所属行政区划',
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
  ];
}

/** 列表字段 */
export function useGridColumns(): VxeTableGridOptions['columns'] {
  return [
    {
      type: 'checkbox',
      width: 60,
    },
    {
      type: 'seq',
      width: 60,
      title: '序号',
    },
    {
      field: 'subsidyCode',
      title: '补贴编码',
      minWidth: 150,
      showOverflow: 'tooltip',
    },
    {
      field: 'username',
      title: '用户姓名',
      minWidth: 100,
    },
    {
      field: 'idCard',
      title: '身份证号',
      minWidth: 170,
      showOverflow: 'tooltip',
    },
    {
      field: 'phone',
      title: '联系电话',
      minWidth: 120,
    },
    {
      field: 'address',
      title: '地址',
      minWidth: 150,
      showOverflow: 'tooltip',
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
      field: 'subsidyAmount',
      title: '补贴金额(元)',
      minWidth: 120,
      align: 'right',
    },

    {
      field: 'createTime',
      title: '创建时间',
      minWidth: 160,
      formatter: 'formatDateTime',
    },
    {
      title: '操作',
      width: 180,
      fixed: 'right',
      slots: { default: 'actions' },
    },
  ];
}
