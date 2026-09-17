import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';

import { DICT_TYPE } from '@vben/constants';
import { getDictOptions } from '@vben/hooks';

import LazyAreaCascader from '#/components/lazy-area-cascader/lazy-area-cascader.vue';
import AreaCascaderItem from '#/components/area-cascader-item/area-cascader-item.vue';

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
      fieldName: 'name',
      label: '姓名',
      component: 'Input',
      componentProps: {
        placeholder: '请输入姓名',
      },
      rules: 'required',
    },
    {
      fieldName: 'gender',
      label: '性别',
      component: 'Select',
      componentProps: {
        options: getDictOptions(DICT_TYPE.SYSTEM_USER_SEX, 'string'),
        placeholder: '请选择性别',
        allowClear: true,
      },
      rules: 'required',
    },
    {
      fieldName: 'idCardNo',
      label: '身份证号',
      component: 'Input',
      componentProps: {
        placeholder: '请输入身份证号',
      },
      rules: 'idCardRequired',
    },
    {
      fieldName: 'phone',
      label: '联系电话',
      component: 'Input',
      componentProps: {
        placeholder: '请输入联系电话',
      },
      rules: 'mobileRequired',
    },
    {
      fieldName: 'education',
      label: '学历',
      component: 'Select',
      componentProps: {
        options: getDictOptions(DICT_TYPE.SYSTEM_USER_EDUCATION, 'string'),
        placeholder: '请选择学历',
        allowClear: true,
      },
    },
    {
      fieldName: 'qualificationNo',
      label: '从业资格证编号',
      component: 'Input',
      componentProps: {
        placeholder: '请输入从业资格证编号',
      },
      rules: 'required',
    },
    {
      fieldName: 'idCardFrontImage',
      label: '身份证正面',
      component: 'Input',
      dependencies: {
        triggerFields: [''],
        show: () => false,
      },
    },
    {
      fieldName: 'idCardBackImage',
      label: '身份证反面',
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
      label: '所属燃气企业',
      component: 'Input',
      componentProps: {
        placeholder: '请选择所属燃气企业',
      },
      rules: 'required',
    },
    {
      fieldName: 'unifiedSocialCreditCode',
      label: '统一社会信用编码',
      component: 'Input',
      componentProps: {
        placeholder: '选择企业后自动带入',
        disabled: true,
      },
    },
    {
      fieldName: 'area1',
      label: '负责区域1',
      component: AreaCascaderItem,
      componentProps: {
        placeholder: '请选择负责区域1',
        allowClear: true,
        maxLevel: 5,
      },
      formItemClass: 'col-span-2',
    },
    {
      fieldName: 'area2',
      label: '负责区域2',
      component: AreaCascaderItem,
      componentProps: {
        placeholder: '请选择负责区域2',
        allowClear: true,
        maxLevel: 5,
      },
      formItemClass: 'col-span-2',
    },
    {
      fieldName: 'area3',
      label: '负责区域3',
      component: AreaCascaderItem,
      componentProps: {
        placeholder: '请选择负责区域3',
        allowClear: true,
        maxLevel: 5,
      },
      formItemClass: 'col-span-2',
    },
    {
      fieldName: 'area4',
      label: '负责区域4',
      component: AreaCascaderItem,
      componentProps: {
        placeholder: '请选择负责区域4',
        allowClear: true,
        maxLevel: 5,
      },
      formItemClass: 'col-span-2',
    },
    {
      fieldName: 'area5',
      label: '负责区域5',
      component: AreaCascaderItem,
      componentProps: {
        placeholder: '请选择负责区域5',
        allowClear: true,
        maxLevel: 5,
      },
      formItemClass: 'col-span-2',
    },
    {
      fieldName: 'staffStatus',
      label: '人员状态',
      component: 'Select',
      componentProps: {
        options: getDictOptions(
          DICT_TYPE.SYSTEM_USER_STATUS,
          'string',
        ),
        placeholder: '请选择人员状态',
        allowClear: true,
      },
      rules: 'required',
    },
    {
      fieldName: 'onDutyDate',
      label: '到岗日期',
      component: 'DatePicker',
      componentProps: {
        placeholder: '请选择到岗日期',
        valueFormat: 'YYYY-MM-DD',
        style: { width: '100%' },
      },
    },
    {
      fieldName: 'offDutyDate',
      label: '离岗日期',
      component: 'DatePicker',
      componentProps: {
        placeholder: '请选择离岗日期',
        valueFormat: 'YYYY-MM-DD',
        style: { width: '100%' },
      },
    },
  ];
}

/** 列表的搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'areaCode',
      label: '行政区',
      component: LazyAreaCascader,
      componentProps: {
        placeholder: '请选择',
        allowClear: true,
        maxLevel: 3,
        style: { width: '100%' },
      },
    },
    {
      fieldName: 'name',
      label: '姓名',
      component: 'Input',
      componentProps: {
        placeholder: '请输入',
        allowClear: true,
        style: { width: '180px' },
      },
    },
    {
      fieldName: 'qualificationNo',
      label: '资格证编号',
      component: 'Input',
      componentProps: {
        placeholder: '请输入',
        allowClear: true,
        style: { width: '220px' },
      },
    },
    {
      fieldName: 'staffStatus',
      label: '人员状态',
      component: 'Select',
      componentProps: {
        options: getDictOptions(
          DICT_TYPE.SYSTEM_USER_STATUS,
          'string',
        ),
        placeholder: '请选择',
        allowClear: true,
        style: { width: '150px' },
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
      field: 'name',
      title: '姓名',
      minWidth: 100,
    },
    {
      field: 'gender',
      title: '性别',
      minWidth: 80,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.SYSTEM_USER_SEX },
      },
    },
    {
      field: 'phone',
      title: '联系电话',
      minWidth: 130,
    },
    {
      field: 'qualificationNo',
      title: '从业资格证编号',
      minWidth: 150,
    },
    {
      field: 'enterpriseName',
      title: '所属燃气企业',
      minWidth: 150,
    },
    {
      field: 'area1',
      title: '负责区域1',
      minWidth: 140,
      formatter: ({ cellValue }: any) => {
        if (!cellValue) return '-';
        try {
          const parsed = JSON.parse(cellValue);
          return parsed.label || '-';
        } catch {
          return cellValue;
        }
      },
    },
    {
      field: 'area2',
      title: '负责区域2',
      minWidth: 140,
      formatter: ({ cellValue }: any) => {
        if (!cellValue) return '-';
        try {
          const parsed = JSON.parse(cellValue);
          return parsed.label || '-';
        } catch {
          return cellValue;
        }
      },
    },
    {
      field: 'area3',
      title: '负责区域3',
      minWidth: 140,
      formatter: ({ cellValue }: any) => {
        if (!cellValue) return '-';
        try {
          const parsed = JSON.parse(cellValue);
          return parsed.label || '-';
        } catch {
          return cellValue;
        }
      },
    },
    {
      field: 'area4',
      title: '负责区域4',
      minWidth: 140,
      formatter: ({ cellValue }: any) => {
        if (!cellValue) return '-';
        try {
          const parsed = JSON.parse(cellValue);
          return parsed.label || '-';
        } catch {
          return cellValue;
        }
      },
    },
    {
      field: 'area5',
      title: '负责区域5',
      minWidth: 140,
      formatter: ({ cellValue }: any) => {
        if (!cellValue) return '-';
        try {
          const parsed = JSON.parse(cellValue);
          return parsed.label || '-';
        } catch {
          return cellValue;
        }
      },
    },
    {
      field: 'staffStatus',
      title: '人员状态',
      minWidth: 100,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.SYSTEM_USER_STATUS },
      },
    },
    {
      field: 'onDutyDate',
      title: '到岗日期',
      minWidth: 110,
    },
    {
      field: 'offDutyDate',
      title: '离岗日期',
      minWidth: 110,
    },
    {
      field: 'createTime',
      title: '创建时间',
      minWidth: 170,
    },
    {
      title: '操作',
      width: 180,
      fixed: 'right',
      slots: { default: 'actions' },
    },
  ];
}
