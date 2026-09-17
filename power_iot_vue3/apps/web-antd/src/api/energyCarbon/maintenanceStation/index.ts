import { requestClient } from '#/api/request';

// 维保网点分页请求参数
export interface MaintenanceStationPageParam {
  pageNo?: number;
  pageSize?: number;
  ids?: number[];
  enterpriseId?: number;
  stationName?: string;
  serviceType?: string;
  businessStatus?: string;
}

// 维保网点
export interface MaintenanceStation {
  id?: number;
  enterpriseId?: number;
  enterpriseName?: string;
  stationName?: string;
  serviceType?: string;
  businessStatus?: string;
  maintenanceStaffCount?: number;
  coveredVillages?: number;
  coveredHouseholds?: number;
  serviceScope?: string;
  serviceScopeNames?: string[];
  managerName?: string;
  managerPhone?: string;
  createTime?: string;
}

// 创建维保网点
export function createMaintenanceStation(data: MaintenanceStation) {
  return requestClient.post<any>('/carbon/maintenance-station/create', data);
}

// 更新维保网点
export function updateMaintenanceStation(data: MaintenanceStation) {
  return requestClient.put<any>('/carbon/maintenance-station/update', data);
}

// 删除维保网点
export function deleteMaintenanceStation(id: number) {
  return requestClient.delete<any>(`/carbon/maintenance-station/delete?id=${id}`);
}

// 获得维保网点分页
export function getMaintenanceStationPage(
  params: MaintenanceStationPageParam,
) {
  return requestClient.get<any>('/carbon/maintenance-station/page', {
    params,
  });
}

// 获得维保网点详情
export function getMaintenanceStation(id: number) {
  return requestClient.get<any>(
    `/carbon/maintenance-station/get?id=${id}`,
  );
}

// 获得指定企业的网点列表
export function getMaintenanceStationListByEnterprise(enterpriseId: number) {
  return requestClient.get<any>(
    `/carbon/maintenance-station/list-by-enterprise?enterpriseId=${enterpriseId}`,
  );
}

// 导出维保网点
export function exportMaintenanceStation(params: MaintenanceStationPageParam) {
  return requestClient.download('/carbon/maintenance-station/export-excel', { params });
}