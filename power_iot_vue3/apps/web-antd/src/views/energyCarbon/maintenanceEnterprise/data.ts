import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';

import { DICT_TYPE } from '@vben/constants';
import { getDictOptions } from '@vben/hooks';

import LazyAreaCascader from '#/components/lazy-area-cascader/lazy-area-cascader.vue';

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
      fieldName: 'division',
      label: '行政区',
      component: LazyAreaCascader,
      componentProps: {
        placeholder: '请选择行政区',
        allowClear: true,
        maxLevel: 3,
      },
      rules: 'required',
      formItemClass: 'col-span-2',
    },
    {
      fieldName: 'enterpriseName',
      label: '企业名称',
      component: 'Input',
      componentProps: {
        placeholder: '请输入企业名称',
      },
      rules: 'required',
    },
    {
      fieldName: 'unifiedSocialCreditCode',
      label: '统一社会信用代码',
      component: 'Input',
      componentProps: {
        placeholder: '请输入统一社会信用代码',
      },
      rules: 'required',
    },
    {
      fieldName: 'serviceType',
      label: '企业服务类型',
      component: 'Select',
      componentProps: {
        options: getDictOptions(
          DICT_TYPE.ENERGY_CBON_TRANSFORMATION_TYPE,
          'string',
        ),
        placeholder: '请选择企业服务类型',
        allowClear: true,
      },
      rules: 'required',
    },
    {
      fieldName: 'serviceStatus',
      label: '服务状态',
      component: 'Select',
      componentProps: {
        options: getDictOptions(
          DICT_TYPE.ENERGY_CBON_SERVICE_STATUS,
          'string',
        ),
        placeholder: '请选择服务状态',
        allowClear: true,
      },
      rules: 'required',
    },
    {
      fieldName: 'regionalManagerName',
      label: '区域负责人姓名',
      component: 'Input',
      componentProps: {
        placeholder: '请输入区域负责人姓名',
      },
      rules: 'required',
    },
    {
      fieldName: 'regionalManagerPhone',
      label: '区域负责人电话',
      component: 'Input',
      componentProps: {
        placeholder: '请输入区域负责人电话',
      },
      rules: 'mobileRequired',
    },
  ];
}

/** 列表的搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'division',
      label: '行政区',
      component: LazyAreaCascader,
      componentProps: {
        placeholder: '请选择行政区',
        allowClear: true,
        maxLevel: 3,
        style: { width: '260px' },
      },
    },
    {
      fieldName: 'enterpriseName',
      label: '企业名称',
      component: 'Input',
      componentProps: {
        placeholder: '请输入企业名称',
        allowClear: true,
        style: { width: '200px' },
      },
    },
    {
      fieldName: 'unifiedSocialCreditCode',
      label: '统一社会信用代码',
      component: 'Input',
      componentProps: {
        placeholder: '请输入统一社会信用代码',
        allowClear: true,
        style: { width: '200px' },
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
      field: 'cityName',
      title: '行政区',
      minWidth: 150,
      formatter: ({ row }: any) => {
        const parts = [row.provinceName, row.cityName, row.countyName].filter(Boolean);
        return parts.join('') || '-';
      },
    },
    {
      field: 'enterpriseName',
      title: '企业名称',
      minWidth: 150,
    },
    {
      field: 'unifiedSocialCreditCode',
      title: '统一社会信用代码',
      minWidth: 180,
    },
    {
      field: 'serviceType',
      title: '企业服务类型',
      minWidth: 120,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.ENERGY_CBON_TRANSFORMATION_TYPE },
      },
    },
    {
      field: 'serviceStatus',
      title: '服务状态',
      minWidth: 100,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.ENERGY_CBON_SERVICE_STATUS },
      },
    },
    {
      field: 'regionalManagerName',
      title: '区域负责人姓名',
      minWidth: 120,
    },
    {
      field: 'regionalManagerPhone',
      title: '区域负责人电话',
      minWidth: 130,
    },
    {
      field: 'maintenanceStaffCount',
      title: '维保人员总数量',
      minWidth: 120,
    },
    {
      field: 'coveredVillages',
      title: '覆盖村数',
      minWidth: 100,
    },
    {
      field: 'coveredHouseholds',
      title: '覆盖户数',
      minWidth: 100,
    },
    {
      field: 'stationCount',
      title: '网点数量',
      minWidth: 100,
    },
    {
      title: '操作',
      width: 180,
      fixed: 'right',
      slots: { default: 'actions' },
    },
  ];
}
