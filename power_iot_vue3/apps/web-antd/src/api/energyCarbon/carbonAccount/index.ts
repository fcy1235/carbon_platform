import type { PageParam } from '@vben/request';

import { requestClient } from '#/api/request';

// 碳排放核算列表请求参数
export interface AccountingListParam extends PageParam {
  username?: string; // 用户姓名
  activityNameCode?: string; // 活动名称编码
  carbonUserInfoId?: number; // 用户信息编号
  carbonUserInfoIds?: number[]; // 用户信息编号列表
  provinceCode?: string; // 省份编码
  cityCode?: string; // 城市编码
  districtCode?: string; // 区县编码
  ids?: number[]; // 选中的ID列表，用于选中导出
}

// 活动数据列表项
export interface Activities {
  id?: number; // 主键
  activityName?: string; // 活动名称
  activityNameCode?: string; // 活动名称编码
  activityLevel?: number; // 活动等级
  emissionFactor?: number; // 排放因子
  emission?: number; // 排放量
  accountingId?: number; // 核算编号
  carbonDeviceId?: number; // 关联设备编号
  carbonDeviceName?: string; // 关联设备名称
}

// 创建、更新碳排放核算
export interface Accounting {
  id?: number; // 编号
  carbonUserInfoId?: number; // 用户信息编号
  username?: string; // 用户姓名
  division?: string; // 行政区划-区
  address?: string; // 地址
  reformType?: string; // 改造类型
  accountingPeriodStart?: string; // 核算周期开始时间
  accountingPeriodEnd?: string; // 核算周期结束时间
  heatingArea?: number; // 加热面积
  baselineIntensity?: number; // 基准线排放强度
  baselineEmission?: number; // 基准线排放量
  activities?: Activities[]; // 活动列表
}

// 碳排放核算列表响应参数
export interface AccountingListResponse {
  heatingArea?: number; // 供暖面积
  baselineIntensity?: number; // 基准线排放强度
  activities?: Activities[]; // 活动列表
}

// 试算减排量
export function calculateAccounting(params: AccountingListResponse) {
  return requestClient.get<any>(`/carbon/accounting/calculate`, { params });
}

// 获得碳排放核算分页
export function getAccountingList(params: AccountingListParam) {
  return requestClient.get<any>(`/carbon/accounting/page`, { params });
}

// 获得碳排放核算
export function getAccountingDetail(id: number) {
  return requestClient.get<any>(`/carbon/accounting/get?id=${id}`);
}

// 新增碳排放核算
export function addAccounting(data: Accounting) {
  return requestClient.post<any>(`/carbon/accounting/create`, data);
}

// 批量新增碳排放核算
export function addAccountingBatch(data: Accounting[]) {
  return requestClient.post<any>(`/carbon/accounting/create-batch`, data);
}

// 更新碳排放核算
export function updateAccounting(data: Accounting) {
  return requestClient.put<any>(`/carbon/accounting/update`, data);
}

// 删除碳排放核算
export function deleteAccounting(id: number) {
  return requestClient.delete<any>(`/carbon/accounting/delete?id=${id}`);
}

// ============= 核算初始化数据（批量） =============

// 核算初始化请求参数（支持批量用户）
export interface AccountingInitParams {
  carbonUserInfoIds: number[];
  accountingPeriodStart: string;
  accountingPeriodEnd: string;
}

// 核算初始化响应（单个用户）
export interface AccountingInitResponse {
  carbonUserInfoId: number;
  username: string;
  idCard: string;
  phone: string;
  division: string;
  address: string;
  reformType: string;
  dataSource: string;
  heatingArea: number;
  provinceCode: number;
  cityCode: number;
  districtCode: number;
  baselineIntensity: number;
  baselineEmission: number;
  electricityUsage: number;
  gasUsage: number;
  activityNameCode: string;
  activityLevel: number;
  emissionFactor: number;
  emissionFactorName: string;
  actualEmission: number;
  reduction: number;
  deviceList: Array<{ id: number; deviceName: string; deviceCode: string }>;
}

// 获取核算初始化数据（批量）
export function getAccountingInitData(params: AccountingInitParams) {
  return requestClient.get<AccountingInitResponse[]>(
    `/carbon/accounting/init-data`,
    { params },
  );
}

// 未核算用户分页查询参数（对应后端 CarbonAccountingPageReqVO）
export interface UnaccountedUserPageParam extends PageParam {
  username?: string;
  idCard?: string;
  reformType?: string;
  provinceCode?: string;
  cityCode?: string;
  districtCode?: string;
  accountingPeriodStart: string;
  accountingPeriodEnd: string;
}

// 查询未核算用户分页
// GET /carbon/accounting/getUnaccountedUserPage
export function getUnaccountedUserPage(params: UnaccountedUserPageParam) {
  return requestClient.get<any>(`/carbon/accounting/getUnaccountedUserPage`, {
    params,
  });
}

// 导出碳排放核算Excel
export function exportAccounting(params: AccountingListParam) {
  return requestClient.download(`/carbon/accounting/export-excel`, { params });
}
