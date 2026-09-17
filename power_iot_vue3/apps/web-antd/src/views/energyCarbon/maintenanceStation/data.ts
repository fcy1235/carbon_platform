import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';

import { DICT_TYPE } from '@vben/constants';
import { getDictOptions } from '@vben/hooks';

import ServiceScopeSelect from '#/components/service-scope-select/service-scope-select.vue';

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
      fieldName: 'enterpriseId',
      component: 'Input',
      dependencies: {
        triggerFields: [''],
        show: () => false,
      },
    },
    {
      fieldName: 'enterpriseName',
      label: '所属企业',
      component: 'Input',
      componentProps: {
        placeholder: '请选择所属企业',
      },
      rules: 'required',
    },
    {
      fieldName: 'stationName',
      label: '网点名称',
      component: 'Input',
      componentProps: {
        placeholder: '请输入网点名称',
      },
      rules: 'required',
    },
    {
      fieldName: 'serviceType',
      label: '网点服务类型',
      component: 'Select',
      componentProps: {
        options: getDictOptions(
          DICT_TYPE.ENERGY_CBON_TRANSFORMATION_TYPE,
          'string',
        ),
        placeholder: '请选择网点服务类型',
        allowClear: true,
      },
      rules: 'required',
    },
    {
      fieldName: 'businessStatus',
      label: '网点经营状态',
      component: 'Select',
      componentProps: {
        options: getDictOptions(
          DICT_TYPE.ENERGY_CBON_NETWORK_BUSINESS_STATUS,
          'string',
        ),
        placeholder: '请选择网点经营状态',
        allowClear: true,
      },
      rules: 'required',
    },
    {
      fieldName: 'maintenanceStaffCount',
      label: '维保人员数量',
      component: 'InputNumber',
      componentProps: {
        min: 0,
        placeholder: '请输入维保人员数量',
        style: { width: '100%' },
      },
      rules: 'required',
    },
    {
      fieldName: 'coveredVillages',
      label: '覆盖村数',
      component: 'InputNumber',
      componentProps: {
        min: 0,
        placeholder: '请输入覆盖村数',
        style: { width: '100%' },
      },
      rules: 'required',
    },
    {
      fieldName: 'coveredHouseholds',
      label: '覆盖户数',
      component: 'InputNumber',
      componentProps: {
        min: 0,
        placeholder: '请输入覆盖户数',
        style: { width: '100%' },
      },
      rules: 'required',
    },
    {
      fieldName: 'serviceScope',
      label: '服务范围',
      component: ServiceScopeSelect,
      componentProps: {
        maxLevel: 5,
      },
      formItemClass: 'col-span-2',
    },
    {
      fieldName: 'managerName',
      label: '网点负责人',
      component: 'Input',
      componentProps: {
        placeholder: '请输入网点负责人',
      },
      rules: 'required',
    },
    {
      fieldName: 'managerPhone',
      label: '网点负责人电话',
      component: 'Input',
      componentProps: {
        placeholder: '请输入网点负责人电话',
      },
      rules: 'mobileRequired',
    },
  ];
}

/** 列表的搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'stationName',
      label: '网点名称',
      component: 'Input',
      componentProps: {
        placeholder: '请输入网点名称',
        allowClear: true,
        style: { width: '200px' },
      },
    },
    {
      fieldName: 'serviceType',
      label: '网点服务类型',
      component: 'Select',
      componentProps: {
        options: getDictOptions(
          DICT_TYPE.ENERGY_CBON_TRANSFORMATION_TYPE,
          'string',
        ),
        placeholder: '请选择网点服务类型',
        allowClear: true,
        style: { width: '200px' },
      },
    },
    {
      fieldName: 'businessStatus',
      label: '网点经营状态',
      component: 'Select',
      componentProps: {
        options: getDictOptions(
          DICT_TYPE.ENERGY_CBON_NETWORK_BUSINESS_STATUS,
          'string',
        ),
        placeholder: '请选择网点经营状态',
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
      field: 'enterpriseName',
      title: '所属企业',
      minWidth: 150,
    },
    {
      field: 'stationName',
      title: '网点名称',
      minWidth: 150,
    },
    {
      field: 'serviceType',
      title: '网点服务类型',
      minWidth: 120,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.ENERGY_CBON_TRANSFORMATION_TYPE },
      },
    },
    {
      field: 'businessStatus',
      title: '网点经营状态',
      minWidth: 120,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.ENERGY_CBON_NETWORK_BUSINESS_STATUS },
      },
    },
    {
      field: 'maintenanceStaffCount',
      title: '维保人员数量',
      minWidth: 100,
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
      field: 'serviceScope',
      title: '服务范围',
      minWidth: 200,
      formatter: ({ cellValue }: any) => {
        if (!cellValue) return '';
        try {
          const areas = JSON.parse(cellValue);
          if (Array.isArray(areas)) {
            return areas.map((a: any) => a.label).join('、');
          }
        } catch {
          return cellValue;
        }
        return cellValue;
      },
    },
    {
      field: 'managerName',
      title: '网点负责人',
      minWidth: 120,
    },
    {
      field: 'managerPhone',
      title: '网点负责人电话',
      minWidth: 130,
    },
    {
      title: '操作',
      width: 180,
      fixed: 'right',
      slots: { default: 'actions' },
    },
  ];
}
