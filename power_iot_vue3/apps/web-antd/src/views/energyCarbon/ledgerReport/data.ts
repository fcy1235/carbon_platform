import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';

import { DICT_TYPE } from '@vben/constants';
import { getDictOptions } from '@vben/hooks';

import { getRangePickerDefaultProps } from '#/utils';

/** 列表的搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'uploadType',
      label: '上报类型',
      component: 'Select',
      componentProps: {
        options: getDictOptions(DICT_TYPE.ENERGY_CBON_LEDGER_TYPE, 'string'),
        placeholder: '请选择上报类型',
        allowClear: true,
      },
    },
    {
      fieldName: 'auditStatus',
      label: '流程状态',
      component: 'Select',
      componentProps: {
        options: getDictOptions(DICT_TYPE.ENERGY_CBON_LEDGER_REPORT_STATUS, 'string'),
        placeholder: '请选择流程状态',
        allowClear: true,
      },
    },
    {
      fieldName: 'uploadTime',
      label: '上报时间',
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
      field: 'districtName',
      title: '所属区县',
      minWidth: 120,
    },
    {
      field: 'uploadType',
      title: '上报类型',
      minWidth: 120,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.ENERGY_CBON_LEDGER_TYPE },
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
      field: 'dataCount',
      title: '数据量',
      minWidth: 100,
    },
    {
      field: 'uploadTime',
      title: '上报时间',
      minWidth: 160,
      formatter: 'formatDateTime',
    },
    {
      field: 'uploader',
      title: '上传人',
      minWidth: 120,
    },
    {
      field: 'reviewer',
      title: '审核人',
      minWidth: 120,
    },
    {
      title: '操作',
      width: 280,
      fixed: 'right',
      slots: { default: 'actions' },
    },
  ];
}

/** 审核轨迹表格列 */
export function useAuditLogColumns(): VxeTableGridOptions['columns'] {
  return [
    { type: 'seq', width: 60, title: '序号' },
    {
      field: 'status',
      title: '流程状态',
      minWidth: 120,
      slots: { default: 'logStatus' },
    },
    {
      field: 'reason',
      title: '状态变更原因',
      minWidth: 200,
      showOverflow: 'tooltip',
    },
    {
      field: 'changeTime',
      title: '状态变更时间',
      minWidth: 160,
      formatter: 'formatDateTime',
    },
  ];
}

