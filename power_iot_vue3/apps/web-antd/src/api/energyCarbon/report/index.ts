import { requestClient } from '#/api/request';

// ==================== 首页仪表盘统计 ====================

/** 取暖改造户数统计 */
export interface ReformHouseholdStats {
  gasCoalCount?: number;
  electricCoalCount?: number;
  totalCount?: number;
}

/** 减碳排量统计 */
export interface CarbonReductionStats {
  baselineEmission?: number;
  actualEmission?: number;
  totalReduction?: number;
}

/** 两员人数统计 */
export interface StaffStats {
  safetyOfficerCount?: number;
  coordinatorCount?: number;
  totalCount?: number;
}

/** 维保企业总数统计 */
export interface MaintenanceStats {
  stationCount?: number;
  coveredVillageCount?: number;
  enterpriseCount?: number;
}

/** 改造类别统计项 */
export interface ReformCategoryItem {
  name?: string;
  count?: number;
  percentage?: number;
}

/** 区县碳排放量 */
export interface DistrictCarbonEmission {
  districtName?: string;
  districtCode?: number;
  carbonEmission?: number;
}

/** 区域分布项 */
export interface RegionDistributionItem {
  regionName?: string;
  regionCode?: number;
  value?: number;
}

/** 设备状态统计项 */
export interface DeviceStatusItem {
  name?: string;
  status?: string;
  count?: number;
  percentage?: number;
}

/** 区县双代实施情况 */
export interface DistrictReformStats {
  districtName?: string;
  districtCode?: number;
  gasCoalCount?: number;
  electricCoalCount?: number;
}

/** 首页仪表盘统计 Response */
export interface CarbonDashboardRespVO {
  reformHouseholdStats?: ReformHouseholdStats;
  carbonReductionStats?: CarbonReductionStats;
  staffStats?: StaffStats;
  maintenanceStats?: MaintenanceStats;
  reformCategoryStats?: ReformCategoryItem[];
  districtCarbonEmissions?: DistrictCarbonEmission[];
  regionDistribution?: RegionDistributionItem[];
  deviceStatusStats?: DeviceStatusItem[];
  districtReformStats?: DistrictReformStats[];
}

/** 获得首页仪表盘统计数据 */
export function getDashboard() {
  return requestClient.get<CarbonDashboardRespVO>('/carbon/report/dashboard');
}

// ==================== 报表一：农村气代煤用户用气量统计 ====================

export interface GasUsageReportReqVO {
  startTime?: string;
  endTime?: string;
  keyword?: string;
  gasId?: string;
}

/** 获得农村气代煤用户用气量统计 */
export function getGasUsageReport(params: GasUsageReportReqVO) {
  return requestClient.get<any>('/carbon/report/gas-usage', { params });
}

/** 导出农村气代煤用户用气量统计 */
export function exportGasUsageReport(params: GasUsageReportReqVO) {
  return requestClient.download('/carbon/report/gas-usage/export', { params });
}

// ==================== 燃气数据报表（采暖季，新表 carbon_gas_usage_report） ====================

export interface GasUsageReportDataReqVO {
  heatingSeason?: string; // 采暖季，如 2025年
  keyword?: string; // 用户信息（户主姓名/身份证号）
  gasId?: string; // 燃气表具号
}

/** 查询燃气数据报表列表（新表） */
export function getGasUsageDataList(params: GasUsageReportDataReqVO) {
  return requestClient.get<any>('/carbon/gas-usage-report/list', { params });
}

/** 导出燃气数据报表（新表） */
export function exportGasUsageData(params: GasUsageReportDataReqVO) {
  return requestClient.download('/carbon/gas-usage-report/export-excel', {
    params,
  });
}

/** 下载燃气数据报表导入模板 */
export function getGasUsageImportTemplate() {
  return requestClient.download('/carbon/gas-usage-report/get-import-template');
}

export interface GasUsageImportResult {
  totalCount?: number;
  successCount?: number;
  failCount?: number;
  errors?: { rowNum?: number; message?: string }[];
}

/** 导入燃气数据报表（新表） */
export function importGasUsageReport(file: File) {
  return requestClient.upload<GasUsageImportResult>(
    '/carbon/gas-usage-report/import',
    { file },
  );
}

// ==================== 报表二：农村电代煤用户用电量统计 ====================

export interface ElectricityUsageReportReqVO {
  startTime?: string;
  endTime?: string;
  keyword?: string;
  electricityId?: string;
}

/** 获得农村电代煤用户用电量统计 */
export function getElectricityUsageReport(params: ElectricityUsageReportReqVO) {
  return requestClient.get<any>('/carbon/report/electricity-usage', { params });
}

/** 导出农村电代煤用户用电量统计 */
export function exportElectricityUsageReport(params: ElectricityUsageReportReqVO) {
  return requestClient.download('/carbon/report/electricity-usage/export', { params });
}

// ==================== 电力用电量报表（新表 carbon_electricity_usage_report） ====================

export interface ElectricityUsageDataReqVO {
  heatingSeason?: string; // 采暖季，如 2025年
  keyword?: string; // 用户信息（户主姓名/身份证号）
  electricityId?: string; // 电表号
}

export interface ElectricityUsageDataRespVO {
  id?: number;
  username?: string;
  idCard?: string;
  electricityId?: string;
  heatingSeason?: string;
  startReading?: number;
  endReading?: number;
  totalUsage?: number;
  remark?: string;
}

/** 获得电力用电量报表分页 */
export function getElectricityUsageDataList(params: ElectricityUsageDataReqVO) {
  return requestClient.get<{
    list: ElectricityUsageDataRespVO[];
    total: number;
  }>('/carbon/electricity-usage-report/list', { params });
}

/** 导出电力用电量报表 */
export function exportElectricityUsageData(params: ElectricityUsageDataReqVO) {
  return requestClient.download('/carbon/electricity-usage-report/export-excel', { params });
}

/** 下载电力用电量报表导入模板 */
export function getElectricityUsageDataImportTemplate() {
  return requestClient.download('/carbon/electricity-usage-report/get-import-template');
}

/** 导入电力用电量报表 */
export function importElectricityUsageData(file: File) {
  return requestClient.upload('/carbon/electricity-usage-report/import', { file });
}

// ==================== 报表三：清洁取暖改造确户台账 ====================

export interface ReformAccountReportReqVO {
  provinceCode?: number;
  cityCode?: number;
  districtCode?: number;
  townCode?: number;
  villageCode?: number;
  keyword?: string;
  phone?: string;
  reformType?: string;
  useStatus?: string;
  reformYear?: string;
}

/** 获得清洁取暖改造确户台账 */
export function getReformAccountReport(params: ReformAccountReportReqVO) {
  return requestClient.get<any>('/carbon/report/reform-account', { params });
}

/** 导出清洁取暖改造确户台账 */
export function exportReformAccountReport(params: ReformAccountReportReqVO) {
  return requestClient.download('/carbon/report/reform-account/export', { params });
}

// ==================== 报表四：项目碳减排报表 ====================

/** 项目碳减排报表 Response */
export interface CarbonProjectReportRespVO {
  projectId?: number;
  projectName?: string;
  contactName?: string;
  projectDesc?: string;
  division?: string;
  planStartDate?: string;
  planEndDate?: string;
  reformHouseholds?: number;
  baselineEmission?: number;
  actualEmission?: number;
  buildingArea?: number;
  baselineIntensity?: number;
  electricityUsage?: number;
  electricityFactor?: number;
  gasUsage?: number;
  gasFactor?: number;
  reduction?: number;
}

/** 获得项目碳减排报表 */
export function getProjectReport(projectId: number) {
  return requestClient.get<CarbonProjectReportRespVO>('/carbon/report/project-report', {
    params: { projectId },
  });
}
