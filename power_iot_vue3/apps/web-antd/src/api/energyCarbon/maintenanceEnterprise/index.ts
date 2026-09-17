import { requestClient } from '#/api/request';

// 维保企业分页请求参数
export interface MaintenanceEnterprisePageParam {
  pageNo?: number;
  pageSize?: number;
  ids?: number[];
  cityCode?: number;
  countyCode?: number;
  enterpriseName?: string;
  unifiedSocialCreditCode?: string;
  serviceType?: string;
  serviceStatus?: string;
}

// 维保企业
export interface MaintenanceEnterprise {
  id?: number;
  provinceCode?: number;
  cityCode?: number;
  countyCode?: number;
  provinceName?: string;
  cityName?: string;
  countyName?: string;
  enterpriseName?: string;
  unifiedSocialCreditCode?: string;
  serviceType?: string;
  serviceStatus?: string;
  regionalManagerName?: string;
  regionalManagerPhone?: string;
  maintenanceStaffCount?: number;
  coveredVillages?: number;
  coveredHouseholds?: number;
  stationCount?: number;
  createTime?: string;
}

// 创建维保企业
export function createMaintenanceEnterprise(data: MaintenanceEnterprise) {
  return requestClient.post<any>('/carbon/maintenance-enterprise/create', data);
}

// 更新维保企业
export function updateMaintenanceEnterprise(data: MaintenanceEnterprise) {
  return requestClient.put<any>('/carbon/maintenance-enterprise/update', data);
}

// 删除维保企业
export function deleteMaintenanceEnterprise(id: number) {
  return requestClient.delete<any>(`/carbon/maintenance-enterprise/delete?id=${id}`);
}

// 获得维保企业分页
export function getMaintenanceEnterprisePage(
  params: MaintenanceEnterprisePageParam,
) {
  return requestClient.get<any>('/carbon/maintenance-enterprise/page', {
    params,
  });
}

// 获得维保企业详情
export function getMaintenanceEnterprise(id: number) {
  return requestClient.get<any>(
    `/carbon/maintenance-enterprise/get?id=${id}`,
  );
}

// 导出维保企业
export function exportMaintenanceEnterprise(params: MaintenanceEnterprisePageParam) {
  return requestClient.download('/carbon/maintenance-enterprise/export-excel', { params });
}
