import type { PageParam } from '@vben/request';

import { requestClient } from '#/api/request';

// 台账上报分页请求参数
export interface LedgerReportPageParam extends PageParam {
  cityCode?: string;
  districtCode?: string;
  uploadType?: string;
  auditStatus?: string;
  auditStatusCollection?: string[];
  uploadTime?: string[];
}

// 台账上报文件
export interface LedgerReportFile {
  id?: number;
  reportId?: number;
  fileId?: number;
  fileName?: string;
  fileUrl?: string;
  uploadType?: string;
  dataCount?: number;
}

// 审核轨迹
export interface LedgerAuditLog {
  id?: number;
  reportId?: number;
  status?: string;
  reason?: string;
  operator?: string;
  changeTime?: string;
}

// 台账上报 - 保存请求参数（对应后端 CarbonLedgerReportSaveReqVO）
export interface LedgerReportSaveData {
  id?: number;
  ledgerFileId?: number;
  reportDesc?: string;
  stampedReportUrl?: string;
  stampedReportName?: string;
  remark?: string;
}

// 台账上报详情
export interface LedgerReport {
  id?: number;
  cityCode?: string;
  cityName?: string;
  districtCode?: string;
  districtName?: string;
  uploadType?: string;
  reportDesc?: string;
  dataCount?: number;
  auditStatus?: string;
  uploadTime?: string;
  submitTime?: string;
  ledgerFileId?: number;
  ledgerFileName?: string;
  ledgerFileUrl?: string;
  stampedReportUrl?: string;
  stampedReportName?: string;
  reviewer?: string;
  reviewTime?: string;
  rejectReason?: string;
  remark?: string;
  files?: LedgerReportFile[];
  auditLogs?: LedgerAuditLog[];
}

// 获得台账上报分页
export function getLedgerReportPage(params: LedgerReportPageParam) {
  return requestClient.get<any>('/carbon/ledger-report/page', { params });
}

// 获得台账上报详情
export function getLedgerReport(id: number) {
  return requestClient.get<any>(`/carbon/ledger-report/get?id=${id}`);
}

// 新增台账上报
export function createLedgerReport(data: LedgerReportSaveData, stampedReportFile?: File) {
  const formData = new FormData();
  formData.append('reportData', new Blob([JSON.stringify(data)], { type: 'application/json' }));
  if (stampedReportFile) {
    formData.append('stampedReportFile', stampedReportFile);
  }
  return requestClient.post<any>('/carbon/ledger-report/create', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
}

// 更新台账上报
export function updateLedgerReport(data: LedgerReportSaveData, stampedReportFile?: File) {
  const formData = new FormData();
  formData.append('reportData', new Blob([JSON.stringify(data)], { type: 'application/json' }));
  if (stampedReportFile) {
    formData.append('stampedReportFile', stampedReportFile);
  }
  return requestClient.put<any>('/carbon/ledger-report/update', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
}

// 删除台账上报
export function deleteLedgerReport(id: number) {
  return requestClient.delete<any>(`/carbon/ledger-report/delete?id=${id}`);
}

// 提交台账上报
export function submitLedgerReport(id: number) {
  return requestClient.put<any>(`/carbon/ledger-report/submit?id=${id}`);
}

// 撤回台账上报
export function withdrawLedgerReport(id: number) {
  return requestClient.put<any>(`/carbon/ledger-report/withdraw?id=${id}`);
}
