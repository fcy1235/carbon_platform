import type { PageParam } from '@vben/request';

import { requestClient } from '#/api/request';

// 用户信息列表请求参数
export interface UserInfoListParam extends PageParam {
  username?: string; // 用户姓名
  idCard?: string; // 身份证号
  phone?: string; // 联系电话
  provinceCode?: string; // 行政区划-省
  cityCode?: string; // 行政区划-市
  districtCode?: string; // 行政区划-区
  townCode?: string; // 行政区划-乡镇/街道
  villageCode?: string; // 行政区划-村/社区
  reformType?: string; // 改造类别
  reformMode?: string; // 改造类型
  dataSource?: string; // 数据来源
  useStatus?: string; // 使用状态
  accountingPeriodStart?: string; // 核算周期开始时间
  accountingPeriodEnd?: string; // 核算周期结束时间
}

// 获取用户信息列表
export function getUserInfoList(params: UserInfoListParam) {
  return requestClient.get<any>(`/carbon/user-info/page`, { params });
}
export function getAccountingUserInfoList(params: UserInfoListParam) {
  return requestClient.get<any>(`/carbon/accounting/getAccountUserInfoPage`, {
    params,
  });
}

// 获取未核算用户分页（用户表为主表，排除核算周期与查询周期重叠的已核算用户）
export function getUnaccountedUserList(params: UserInfoListParam) {
  return requestClient.get<any>(`/carbon/accounting/getUnaccountedUserPage`, {
    params,
  });
}

// 获取碳核算用户分页（项目管理选择用户）
export function getAccountingPage(params: UserInfoListParam) {
  return requestClient.get<any>(`/carbon/accounting/page`, { params });
}

// 新增用户信息
export function addUser(data: any) {
  return requestClient.post<any>(`/carbon/user-info/create`, data);
}

// 更新用户信息
export function updateUserInfo(data: any) {
  return requestClient.put<any>(`/carbon/user-info/update`, data);
}

// 删除用户信息
export function deleteUserInfo(id: number) {
  return requestClient.delete<any>(`/carbon/user-info/delete?id=${id}`);
}

// 批量删除用户信息
export function batchDeleteUserInfo(ids: number[]) {
  return requestClient.delete<any>(
    `/carbon/user-info/delete-list?ids=${ids.join(',')}`,
  );
}

// 导出用户信息
export function exportUserInfo(params: UserInfoListParam) {
  return requestClient.download('/carbon/user-info/export-excel', { params });
}

// 下载导入模板
export function getUserImportTemplate() {
  return requestClient.download('/carbon/user-info/get-import-template');
}

// 导入用户信息
export function importUserInfo(file: File) {
  return requestClient.upload('/carbon/user-info/import', { file });
}

// 获取用户列表（不分页，用于下拉选择）
export function getUserInfoListForSelect(params?: { username?: string }) {
  return requestClient.get<any>(`/carbon/user-info/list`, { params });
}
