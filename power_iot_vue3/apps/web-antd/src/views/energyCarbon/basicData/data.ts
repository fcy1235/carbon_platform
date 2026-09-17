import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';
import { DICT_TYPE } from '@vben/constants';
import { getDictOptions } from '@vben/hooks';

import LazyAreaCascader from '#/components/lazy-area-cascader/lazy-area-cascader.vue';
import SectionTitle from './components/section-title.vue';

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
    // === 用户基本信息 ===
    {
      fieldName: 'basicTitle',
      label: '',
      component: SectionTitle,
      componentProps: {
        title: '用户基本信息',
      },
      formItemClass: 'col-span-2',
      hideLabel: true,
    },
    {
      fieldName: 'username',
      label: '用户名',
      component: 'Input',
      componentProps: {
        placeholder: '请输入用户名',
      },
      rules: 'required',
    },
    {
      fieldName: 'idCard',
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
      fieldName: 'useStatus',
      label: '使用状态',
      component: 'Select',
      componentProps: {
        options: getDictOptions(
          DICT_TYPE.ENERGY_CBON_USE_STATUS,
          'string',
        ),
        placeholder: '请选择使用状态',
        allowClear: true,
      },
      rules: 'required',
    },
    {
      fieldName: 'division',
      label: '所属行政区划',
      component: LazyAreaCascader,
      componentProps: {
        placeholder: '请选择所属行政区划',
        allowClear: true,
      },
      rules: 'required',
      formItemClass: 'col-span-2',
    },
    {
      fieldName: 'address',
      label: '地址',
      component: 'Textarea',
      componentProps: {
        placeholder: '请输入地址',
        rows: 2,
      },
      formItemClass: 'col-span-2',
    },
    {
      fieldName: 'remark',
      label: '备注',
      component: 'Textarea',
      componentProps: {
        placeholder: '请输入备注',
        rows: 2,
      },
      formItemClass: 'col-span-2',
    },
    // === 改造信息 ===
    {
      fieldName: 'reformTitle',
      label: '',
      component: SectionTitle,
      componentProps: {
        title: '改造信息',
      },
      formItemClass: 'col-span-2',
      hideLabel: true,
    },
    {
      fieldName: 'heatingArea',
      label: '建筑面积',
      component: 'Input',
      componentProps: {
        min: 0,
        placeholder: '请输入建筑面积',
        addonAfter: '㎡',
      },
      rules: 'required',
    },
    {
      fieldName: 'dataSource',
      label: '数据来源',
      component: 'Select',
      componentProps: {
        options: getDictOptions(
          DICT_TYPE.ENERGY_CBON_DATA_SOURCE,
          'string',
        ),
        placeholder: '请选择数据来源',
        allowClear: true,
      },
      rules: 'required',
    },
    {
      fieldName: 'reformType',
      label: '改造类别',
      component: 'Select',
      componentProps: {
        options: getDictOptions(
          DICT_TYPE.ENERGY_CBON_TRANSFORMATION_TYPE,
          'string',
        ),
        placeholder: '请选择改造类别',
        allowClear: true,
      },
      rules: 'required',
    },
    {
      fieldName: 'reformMode',
      label: '改造类型',
      component: 'Select',
      componentProps: {
        options: getDictOptions(
          DICT_TYPE.ENERGY_CBON_REFORM_MODE,
          'string',
        ),
        placeholder: '请选择改造类型',
        allowClear: true,
      },
    },
    {
      fieldName: 'reformYear',
      label: '改造年份',
      component: 'DatePicker',
      componentProps: {
        picker: 'year',
        placeholder: '请选择改造年份',
        style: { width: '100%' },
      },
    },
    {
      fieldName: 'reformBatch',
      label: '改造批次',
      component: 'Select',
      componentProps: {
        options: getDictOptions(
          DICT_TYPE.ENERGY_CBON_REFORM_BATCH,
          'string',
        ),
        placeholder: '请选择改造批次',
        allowClear: true,
      },
    },
    {
      fieldName: 'subsidyMethod',
      label: '发放补贴方式',
      component: 'Select',
      componentProps: {
        options: getDictOptions(
          DICT_TYPE.ENERGY_CBON_SUBSIDY_METHOD,
          'string',
        ),
        placeholder: '请选择发放补贴方式',
        allowClear: true,
      },
    },
    {
      fieldName: 'houseUsage',
      label: '房屋用途',
      component: 'Select',
      componentProps: {
        options: getDictOptions(
          DICT_TYPE.ENERGY_CBON_HOUSE_USAGE,
          'string',
        ),
        placeholder: '请选择房屋用途',
        allowClear: true,
      },
    },

    {
      fieldName: 'userCategory',
      label: '用户分类',
      component: 'Select',
      componentProps: {
        options: getDictOptions(
          DICT_TYPE.ENERGY_CBON_USER_CATEGORY,
          'string',
        ),
        placeholder: '请选择用户分类',
        allowClear: true,
      },
    },
    {
      fieldName: 'gasUserCode',
      label: '燃气用户编码',
      component: 'Input',
      componentProps: {
        placeholder: '请输入燃气用户编码',
      },
      dependencies: {
        triggerFields: ['reformType'],
        show: (values: any) => values.reformType === '2',
      },
    },

    {
      fieldName: 'electricityId',
      label: '电表ID(户号)',
      component: 'Input',
      componentProps: {
        placeholder: '请输入电表ID(户号)',
      },
      dependencies: {
        triggerFields: ['reformType'],
        show: (values: any) => values.reformType === '1',
      },
    },
    {
      fieldName: 'gasId',
      label: '燃气表ID(户号)',
      component: 'Input',
      componentProps: {
        placeholder: '请输入燃气表ID(户号)',
      },
      dependencies: {
        triggerFields: ['reformType'],
        show: (values: any) => values.reformType === '2',
      },
    },
  ];
}

/** 列表的搜索表单 */
export function useGridFormSchema(): VbenFormSchema[] {
  return [
    {
      fieldName: 'username',
      label: '用户名',
      component: 'Input',
      componentProps: {
        placeholder: '请输入用户名',
        allowClear: true,
      },
    },
    {
      fieldName: 'idCard',
      label: '身份证号',
      component: 'Input',
      componentProps: {
        placeholder: '请输入身份证号',
        allowClear: true,
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
        options: getDictOptions(
          DICT_TYPE.ENERGY_CBON_TRANSFORMATION_TYPE,
          'string',
        ),
        placeholder: '请选择改造类别',
        allowClear: true,
      },
    },
    {
      fieldName: 'reformMode',
      label: '改造类型',
      component: 'Select',
      componentProps: {
        options: getDictOptions(
          DICT_TYPE.ENERGY_CBON_REFORM_MODE,
          'string',
        ),
        placeholder: '请选择改造类型',
        allowClear: true,
      },
    },
    {
      fieldName: 'useStatus',
      label: '使用状态',
      component: 'Select',
      componentProps: {
        options: getDictOptions(
          DICT_TYPE.ENERGY_CBON_USE_STATUS,
          'string',
        ),
        placeholder: '请选择使用状态',
        allowClear: true,
      },
    },
  ];
}

/** 列表的字段 */
export function useGridColumns(): VxeTableGridOptions['columns'] {
  return [
    { type: 'checkbox', width: 40 },
    {
      field: 'userCode',
      title: '用户编码',
      minWidth: 120,
    },
    {
      field: 'username',
      title: '用户名',
      minWidth: 80,
    },
    {
      field: 'idCard',
      title: '身份证号',
      minWidth: 150,
    },
    {
      field: 'phone',
      title: '联系电话',
      minWidth: 100,
    },
    {
      field: 'division',
      title: '所属行政区划',
      minWidth: 200,
    },
    {
      field: 'address',
      title: '地址',
      minWidth: 200,
    },
    {
      field: 'heatingArea',
      title: '清洁建筑取暖面积(㎡)',
      minWidth: 140,
    },
    {
      field: 'reformType',
      title: '改造类别',
      minWidth: 90,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.ENERGY_CBON_TRANSFORMATION_TYPE },
      },
    },
    {
      field: 'reformMode',
      title: '改造类型',
      minWidth: 90,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.ENERGY_CBON_REFORM_MODE },
      },
    },
    {
      field: 'reformYear',
      title: '改造年份',
      minWidth: 90,
    },
    {
      field: 'reformBatch',
      title: '改造批次',
      minWidth: 90,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.ENERGY_CBON_REFORM_BATCH },
      },
    },
    {
      field: 'useStatus',
      title: '使用状态',
      minWidth: 90,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.ENERGY_CBON_USE_STATUS },
      },
    },
    {
      field: 'dataSource',
      title: '数据来源',
      minWidth: 90,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.ENERGY_CBON_DATA_SOURCE },
      },
    },
    // {
    //   field: 'electricityId',
    //   title: '电表ID(户号)',
    //   minWidth: 130,
    // },
    // {
    //   field: 'gasId',
    //   title: '燃气表ID(户号)',
    //   minWidth: 130,
    // },
    {
      field: 'updateTime',
      title: '更新时间',
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
