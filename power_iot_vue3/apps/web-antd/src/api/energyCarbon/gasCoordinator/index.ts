import { requestClient } from '#/api/request';

// 农村气代煤协管员分页请求参数
export interface GasCoordinatorPageParam {
  pageNo?: number;
  pageSize?: number;
  phone?: string;
  enterpriseId?: number;
  staffStatus?: string;
  isVillageCommitteeMember?: string;
  areaCode?: string;
}

// 农村气代煤协管员
export interface GasCoordinator {
  id?: number;
  name?: string;
  gender?: string;
  idCardNo?: string;
  education?: string;
  phone?: string;
  isVillageCommitteeMember?: string;
  area?: string;
  areaName?: string;
  trainingEnterpriseType?: string;
  enterpriseId?: number;
  enterpriseName?: string;
  unifiedSocialCreditCode?: string;
  trainingScore?: number;
  staffStatus?: string;
  onDutyDate?: string;
  offDutyDate?: string;
  createTime?: string;
}

// 创建农村气代煤协管员
export function createGasCoordinator(data: Record<string, any>) {
  return requestClient.post<any>('/carbon/gas-coordinator/create', data);
}

// 更新农村气代煤协管员
export function updateGasCoordinator(data: Record<string, any>) {
  return requestClient.put<any>('/carbon/gas-coordinator/update', data);
}

// 删除农村气代煤协管员
export function deleteGasCoordinator(id: number) {
  return requestClient.delete<any>(
    `/carbon/gas-coordinator/delete?id=${id}`,
  );
}

// 获得农村气代煤协管员分页
export function getGasCoordinatorPage(params: GasCoordinatorPageParam) {
  return requestClient.get<any>('/carbon/gas-coordinator/page', {
    params,
  });
}

// 获得农村气代煤协管员详情
export function getGasCoordinator(id: number) {
  return requestClient.get<any>(`/carbon/gas-coordinator/get?id=${id}`);
}

// 获得指定企业的协管员列表
export function getGasCoordinatorListByEnterpriseId(enterpriseId: number) {
  return requestClient.get<any>(
    `/carbon/gas-coordinator/list-by-enterprise?enterpriseId=${enterpriseId}`,
  );
}

// 导出气代煤协管员
export function exportGasCoordinator(params: GasCoordinatorPageParam) {
  return requestClient.download('/carbon/gas-coordinator/export-excel', { params });
}
