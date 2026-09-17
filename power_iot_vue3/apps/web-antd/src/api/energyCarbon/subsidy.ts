import type { PageParam } from '@vben/request';

import { requestClient } from '#/api/request';

// 补贴管理分页请求参数
export interface SubsidyPageParam extends PageParam {
  username?: string;
  provinceCode?: number;
  cityCode?: number;
  districtCode?: number;
  reformType?: string;
  status?: string;
  ids?: number[];
}

// 补贴管理响应
export interface SubsidyVO {
  id?: number;
  subsidyCode?: string;
  userInfoId?: number;
  subsidyAmount?: number;
  status?: string;
  grantTime?: string;
  createTime?: string;
  username?: string;
  address?: string;
  reformType?: string;
  idCard?: string;
  phone?: string;
}

// 获得补贴分页
export function getSubsidyPage(params: SubsidyPageParam) {
  return requestClient.get<any>('/carbon/subsidy/page', { params });
}

// 获得补贴
export function getSubsidy(id: number) {
  return requestClient.get<any>(`/carbon/subsidy/get?id=${id}`);
}

// 创建补贴
export function createSubsidy(data: any) {
  return requestClient.post<any>('/carbon/subsidy/create', data);
}

// 更新补贴
export function updateSubsidy(data: any) {
  return requestClient.put<any>('/carbon/subsidy/update', data);
}

// 删除补贴
export function deleteSubsidy(id: number) {
  return requestClient.delete<any>(`/carbon/subsidy/delete?id=${id}`);
}

// 导出补贴 Excel
export function exportSubsidy(params: SubsidyPageParam) {
  return requestClient.download('/carbon/subsidy/export-excel', { params });
}

// 下载导入模板
export function getSubsidyImportTemplate() {
  return requestClient.download('/carbon/subsidy/get-import-template');
}

// 导入补贴数据
export function importSubsidy(file: File) {
  const formData = new FormData();
  formData.append('file', file);
  return requestClient.post<any>('/carbon/subsidy/import', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
}
