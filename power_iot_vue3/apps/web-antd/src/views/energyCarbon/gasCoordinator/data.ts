import type { VbenFormSchema } from '#/adapter/form';
import type { VxeTableGridOptions } from '#/adapter/vxe-table';

import { DICT_TYPE } from '@vben/constants';
import { getDictOptions } from '@vben/hooks';

import AreaCascaderItem from '#/components/area-cascader-item/area-cascader-item.vue';
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
      fieldName: 'phone',
      label: '联系电话',
      component: 'Input',
      componentProps: {
        placeholder: '请输入联系电话',
      },
      rules: 'mobileRequired',
    },
    {
      fieldName: 'isVillageCommitteeMember',
      label: '是否为村两委干部成员',
      component: 'Select',
      componentProps: {
        options: getDictOptions(DICT_TYPE.INFRA_BOOLEAN_STRING, 'string'),
        placeholder: '请选择',
        allowClear: true,
      },
    },
    {
      fieldName: 'area',
      label: '负责村（社区）',
      component: AreaCascaderItem,
      componentProps: {
        placeholder: '请选择负责村（社区）',
        allowClear: true,
        maxLevel: 5,
      },
      formItemClass: 'col-span-2',
      rules: 'required',
    },
    {
      fieldName: 'trainingEnterpriseType',
      label: '入职培训企业类型',
      component: 'Select',
      componentProps: {
        options: getDictOptions(
          DICT_TYPE.ENERGY_CBON_BON_ENTERPRISE_TYPE,
          'string',
        ),
        placeholder: '请选择入职培训企业类型',
        allowClear: true,
      },
      rules: 'required',
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
      label: '负责培训燃气企业/第三方机构',
      component: 'Input',
      componentProps: {
        placeholder: '请选择所属企业',
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
      fieldName: 'trainingScore',
      label: '入职专业操作技能培训成绩',
      component: 'InputNumber',
      componentProps: {
        placeholder: '请输入培训成绩',
        min: 0,
        max: 100,
        precision: 1,
        style: { width: '100%' },
      },
    },
    {
      fieldName: 'staffStatus',
      label: '人员状态',
      component: 'Select',
      componentProps: {
        options: [
          { label: '在岗', value: '1' },
          { label: '离岗', value: '2' },
        ],
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
      fieldName: 'phone',
      label: '联系电话',
      component: 'Input',
      componentProps: {
        placeholder: '请输入',
        allowClear: true,
        style: { width: '180px' },
      },
    },
    {
      fieldName: 'staffStatus',
      label: '人员状态',
      component: 'Select',
      componentProps: {
        options: [
          { label: '在岗', value: '1' },
          { label: '离岗', value: '2' },
        ],
        placeholder: '请选择',
        allowClear: true,
        style: { width: '120px' },
      },
    },
    {
      fieldName: 'isVillageCommitteeMember',
      label: '村两委干部',
      component: 'Select',
      componentProps: {
        options: getDictOptions(DICT_TYPE.INFRA_BOOLEAN_STRING, 'string'),
        placeholder: '请选择',
        allowClear: true,
        style: { width: '120px' },
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
      field: 'education',
      title: '学历',
      minWidth: 80,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.SYSTEM_USER_EDUCATION },
      },
    },
    {
      field: 'isVillageCommitteeMember',
      title: '村两委干部',
      minWidth: 100,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.INFRA_BOOLEAN_STRING },
      },
    },
    {
      field: 'areaName',
      title: '负责区域',
      minWidth: 200,
    },
    {
      field: 'trainingEnterpriseType',
      title: '入职培训企业类型',
      minWidth: 140,
      cellRender: {
        name: 'CellDict',
        props: { type: DICT_TYPE.ENERGY_CBON_BON_ENTERPRISE_TYPE },
      },
    },
    {
      field: 'enterpriseName',
      title: '负责培训燃气企业/第三方机构',
      minWidth: 150,
    },
    {
      field: 'trainingScore',
      title: '培训成绩',
      minWidth: 100,
    },
    {
      field: 'staffStatus',
      title: '人员状态',
      minWidth: 100,
      formatter: ({ cellValue }: any) => {
        if (cellValue === '1') return '在岗';
        if (cellValue === '2') return '离岗';
        return '-';
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
