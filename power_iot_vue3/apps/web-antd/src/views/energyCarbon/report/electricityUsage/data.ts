import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';

/** 列表的搜索表单（查询新表 carbon_electricity_usage_report） */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'heatingSeason',
      label: '采暖季',
      component: 'Input',
      componentProps: {
        placeholder: '请输入采暖季，如 2025年',
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
      fieldName: 'electricityId',
      label: '电表号',
      component: 'Input',
      componentProps: {
        placeholder: '请输入电表号',
        allowClear: true,
      },
    },
  ];
}

/** 列表的字段（查询新表 carbon_electricity_usage_report） */
export function useGridColumns(): VxeTableGridOptions['columns'] {
  return [
    { type: 'seq', width: 60 },
    {
      field: 'username',
      title: '用户名',
      minWidth: 100,
    },
    {
      field: 'idCard',
      title: '身份证号',
      minWidth: 170,
    },
    {
      field: 'electricityId',
      title: '电表号',
      minWidth: 130,
    },
    {
      field: 'heatingSeason',
      title: '采暖季',
      minWidth: 100,
    },
    {
      field: 'startReading',
      title: '采暖季起始表底数',
      minWidth: 160,
    },
    {
      field: 'endReading',
      title: '采暖季终止表底数',
      minWidth: 160,
    },
    {
      field: 'totalUsage',
      title: '采暖季合计用电量（kWh）',
      minWidth: 200,
    },
    {
      field: 'remark',
      title: '备注',
      minWidth: 150,
    },
  ];
}
