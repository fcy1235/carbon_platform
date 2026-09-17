import type { PageParam } from '@vben/request';

import { requestClient } from '#/api/request';

// 台账文件分页请求参数
export interface LedgerFilePageParam extends PageParam {
  cityCode?: string;
  districtCode?: string;
  uploadType?: string;
  uploadStatus?: string;
  uploadStatusCollection?: string[];
  uploadTime?: string[];
  fileName?: string;
}

// 台账文件
export interface LedgerFile {
  id?: number;
  cityCode?: string;
  cityName?: string;
  districtCode?: string;
  districtName?: string;
  uploadType?: string;
  fileName?: string;
  fileUrl?: string;
  dataCount?: number;
  uploadStatus?: string;
  uploadTime?: string;
  uploader?: string;
  remark?: string;
}

// 获得台账文件分页
export function getLedgerFilePage(params: LedgerFilePageParam) {
  return requestClient.get<any>('/carbon/ledger-file/page', { params });
}

// 获得台账文件
export function getLedgerFile(id: number) {
  return requestClient.get<any>(`/carbon/ledger-file/get?id=${id}`);
}

// 新增台账文件（multipart/form-data）
export function createLedgerFile(
  uploadType: string,
  file: File,
  cityCode?: string,
  districtCode?: string,
  dataLevel?: number,
) {
  const formData = new FormData();
  formData.append('uploadType', uploadType);
  formData.append('file', file);
  if (cityCode) formData.append('cityCode', cityCode);
  if (districtCode) formData.append('districtCode', districtCode);
  if (dataLevel !== undefined) formData.append('dataLevel', String(dataLevel));
  return requestClient.post<any>('/carbon/ledger-file/create', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
}

// 更新台账文件（multipart/form-data）
export function updateLedgerFile(
  id: number,
  uploadType: string,
  file?: File,
  cityCode?: string,
  districtCode?: string,
  dataLevel?: number,
) {
  const formData = new FormData();
  formData.append('id', String(id));
  formData.append('uploadType', uploadType);
  if (file) {
    formData.append('file', file);
  }
  if (cityCode) formData.append('cityCode', cityCode);
  if (districtCode) formData.append('districtCode', districtCode);
  if (dataLevel !== undefined) formData.append('dataLevel', String(dataLevel));
  return requestClient.put<any>('/carbon/ledger-file/update', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
}

// 删除台账文件
export function deleteLedgerFile(id: number) {
  return requestClient.delete<any>(`/carbon/ledger-file/delete?id=${id}`);
}

// 导出台账文件 Excel
export function exportLedgerFile(params: LedgerFilePageParam) {
  return requestClient.download('/carbon/ledger-file/export-excel', { params });
}

// 下载导入模板
export function downloadLedgerTemplate(type: string) {
  return requestClient.download('/carbon/ledger-file/download-template', {
    params: { type },
  });
}

// 下载台账原文件
export function downloadLedgerFile(id: number) {
  return requestClient.download('/carbon/ledger-file/download', {
    params: { id },
  });
}

// 获得可用于上报的台账文件
export function getAvailableFilesForReport(
  uploadType: string,
  districtCode?: string,
) {
  return requestClient.get<any>('/carbon/ledger-file/available-for-report', {
    params: { uploadType, districtCode },
  });
}

// 获取当前登录用户行政区地址信息
export function getLoginUserAddress() {
  return requestClient.get<any>('/carbon/ledger-file/current-address');
}

// 修改台账文件审核状态
export function updateAuditStatus(
  id: number,
  auditStatus: string,
  rejectReason?: string,
) {
  return requestClient.put<any>('/carbon/ledger-file/audit-status', {
    id,
    auditStatus,
    rejectReason,
  });
}
