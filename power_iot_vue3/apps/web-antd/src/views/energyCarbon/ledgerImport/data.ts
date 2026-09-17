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
      label: '上传类型',
      component: 'Select',
      componentProps: {
        options: getDictOptions(DICT_TYPE.ENERGY_CBON_LEDGER_TYPE, 'string'),
        placeholder: '请选择上传类型',
        allowClear: true,
      },
    },
    {
      fieldName: 'importStatus',
      label: '导入状态',
      component: 'Select',
      componentProps: {
        options: getDictOptions(DICT_TYPE.ENERGY_CBON_LEDGER_IMPORT_STATUS, 'string'),
        placeholder: '请选择导入状态',
        allowClear: true,
      },
    },
    {
      fieldName: 'uploadTime',
      label: '上传时间',
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
      field: 'cityName',
      title: '所属市',
      minWidth: 120,
    },
    {
      field: 'districtName',
      title: '所属区县',
      minWidth: 120,
    },
    {
      field: 'uploadType',
      title: '上传类型',
      minWidth: 120,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.ENERGY_CBON_LEDGER_TYPE },
      },
    },
    {
      field: 'fileName',
      title: '文件名称',
      minWidth: 200,
      showOverflow: 'tooltip',
    },
    {
      field: 'dataCount',
      title: '数据量',
      minWidth: 100,
    },
    {
      field: 'uploadStatus',
      title: '上传状态',
      minWidth: 100,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.ENERGY_CBON_SUCCESS_STATUS },
      },
    },
    {
      field: 'successCount',
      title: '正常数量',
      minWidth: 100,
    },
    {
      field: 'failCount',
      title: '失败数量',
      minWidth: 100,
    },
    {
      field: 'importStatus',
      title: '导入状态',
      minWidth: 100,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.ENERGY_CBON_LEDGER_IMPORT_STATUS },
      },
    },
    {
      field: 'uploader',
      title: '上传人',
      minWidth: 120,
    },
    {
      field: 'uploadTime',
      title: '上传时间',
      minWidth: 160,
      formatter: 'formatDateTime',
    },
    {
      title: '操作',
      width: 320,
      fixed: 'right',
      slots: { default: 'actions' },
    },
  ];
}

/** 导入明细表格列 */
export function useDetailColumns(): VxeTableGridOptions['columns'] {
  return [
    { type: 'seq', width: 60, title: '序号' },
    {
      field: 'username',
      title: '户主姓名',
      minWidth: 120,
    },
    {
      field: 'cityName',
      title: '所属市',
      minWidth: 120,
    },
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
      minWidth: 140,
    },
    {
      field: 'address',
      title: '地址',
      minWidth: 200,
      showOverflow: 'tooltip',
    },
    {
      field: 'idCard',
      title: '身份证号',
      minWidth: 180,
    },
    {
      field: 'phone',
      title: '联系方式',
      minWidth: 140,
    },
    {
      field: 'heatingArea',
      title: '采暖面积 (m²)',
      minWidth: 140,
    },
    {
      field: 'extFields',
      title: '模板扩展字段',
      minWidth: 200,
      showOverflow: 'tooltip',
    },
    {
      field: 'importStatus',
      title: '导入状态',
      minWidth: 100,
      slots: { default: 'detailStatus' },
    },
    {
      field: 'errorMsg',
      title: '失败原因',
      minWidth: 200,
      slots: { default: 'errorMsg' },
    },
  ];
}
