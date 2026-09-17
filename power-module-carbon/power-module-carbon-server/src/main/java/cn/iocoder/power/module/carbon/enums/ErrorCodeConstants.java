package cn.iocoder.power.module.carbon.enums;

import cn.iocoder.power.framework.common.exception.ErrorCode;

public interface ErrorCodeConstants {

    // ========== 用户基本信息 1-020-001-000 ==========
    ErrorCode USER_INFO_NOT_EXISTS = new ErrorCode(1_020_001_000, "用户基本信息不存在");
    ErrorCode USER_INFO_CODE_EXISTS = new ErrorCode(1_020_001_001, "用户编码已存在");
    ErrorCode USER_INFO_ID_CARD_EXISTS = new ErrorCode(1_020_001_002, "身份证号已存在");

    // ========== 行政区划管理 1-020-002-000 ==========
    ErrorCode DIVISION_NOT_EXISTS = new ErrorCode(1_020_002_000, "行政区划不存在");
    ErrorCode DIVISION_CODE_EXISTS = new ErrorCode(1_020_002_001, "区划编码已存在");
    ErrorCode DIVISION_EXITS_CHILDREN = new ErrorCode(1_020_002_002, "存在子区划，无法删除");

    // ========== 电力数据 1-020-003-000 ==========
    ErrorCode ELECTRICITY_NOT_EXISTS = new ErrorCode(1_020_003_000, "电力数据不存在");
    ErrorCode ELECTRICITY_ID_EXISTS = new ErrorCode(1_020_003_001, "电力户号已存在");
    ErrorCode ELECTRICITY_IMPORT_USER_NOT_FOUND = new ErrorCode(1_020_003_002, "根据身份证号和电力表户号未找到对应用户");

    // ========== 电力用电量报表 1-020-023-000 ==========
    ErrorCode ELECTRICITY_USAGE_REPORT_IMPORT_USER_NOT_FOUND = new ErrorCode(1_020_023_000, "根据身份证号和电力表户号未找到对应用户：%s");

    // ========== 燃气数据 1-020-004-000 ==========
    ErrorCode GAS_DATA_NOT_EXISTS = new ErrorCode(1_020_004_000, "燃气数据不存在");
    ErrorCode GAS_ID_EXISTS = new ErrorCode(1_020_004_001, "燃气户号已存在");
    ErrorCode GAS_DATA_IMPORT_USER_NOT_FOUND = new ErrorCode(1_020_004_002, "根据身份证号和燃气表户号未找到对应用户");

    // ========== 基准线碳排放强度 1-020-005-000 ==========
    ErrorCode BASELINE_NOT_EXISTS = new ErrorCode(1_020_005_000, "基准线数据不存在");

    // ========== 排放源 1-020-006-000 ==========
    ErrorCode EMISSION_SOURCE_NOT_EXISTS = new ErrorCode(1_020_006_000, "排放源不存在");
    ErrorCode EMISSION_SOURCE_CODE_EXISTS = new ErrorCode(1_020_006_001, "排放源编码已存在");

    // ========== 气体 1-020-007-000 ==========
    ErrorCode GAS_INFO_NOT_EXISTS = new ErrorCode(1_020_007_000, "气体信息不存在");
    ErrorCode GAS_INFO_CODE_EXISTS = new ErrorCode(1_020_007_001, "气体编码已存在");

    // ========== 参数库 1-020-008-000 ==========
    ErrorCode PARAM_LIB_NOT_EXISTS = new ErrorCode(1_020_008_000, "参数不存在");
    ErrorCode PARAM_LIB_CODE_EXISTS = new ErrorCode(1_020_008_001, "参数编码已存在");

    // ========== 排放因子库 1-020-009-000 ==========
    ErrorCode FACTOR_LIB_NOT_EXISTS = new ErrorCode(1_020_009_000, "排放因子不存在");
    ErrorCode FACTOR_LIB_CODE_EXISTS = new ErrorCode(1_020_009_001, "排放因子编码已存在");

    // ========== 文档管理 1-020-010-000 ==========
    ErrorCode DOCUMENT_NOT_EXISTS = new ErrorCode(1_020_010_000, "文档不存在");

    // ========== 联系人 1-020-011-000 ==========
    ErrorCode CONTACT_NOT_EXISTS = new ErrorCode(1_020_011_000, "联系人不存在");
    ErrorCode CONTACT_CODE_EXISTS = new ErrorCode(1_020_011_001, "联系人编码已存在");

    // ========== 碳排放核算 1-020-012-000 ==========
    ErrorCode ACCOUNTING_NOT_EXISTS = new ErrorCode(1_020_012_000, "碳排放核算不存在");
    ErrorCode ACCOUNTING_CODE_EXISTS = new ErrorCode(1_020_012_001, "核算编码已存在");
    ErrorCode ACCOUNTING_PERIOD_EXISTS = new ErrorCode(1_020_012_002, "该用户已存在相同核算周期的碳排放核算数据");

    // ========== 设备管理 1-020-013-000 ==========
    ErrorCode DEVICE_NOT_EXISTS = new ErrorCode(1_020_013_000, "设备不存在");
    ErrorCode DEVICE_CODE_EXISTS = new ErrorCode(1_020_013_001, "设备编码已存在");

    // ========== 项目管理 1-020-014-000 ==========
    ErrorCode PROJECT_NOT_EXISTS = new ErrorCode(1_020_014_000, "项目不存在");
    ErrorCode PROJECT_CODE_EXISTS = new ErrorCode(1_020_014_001, "项目编码已存在");

    // ========== 补贴管理 1-020-015-000 ==========
    ErrorCode SUBSIDY_NOT_EXISTS = new ErrorCode(1_020_015_000, "补贴不存在");
    ErrorCode SUBSIDY_CODE_EXISTS = new ErrorCode(1_020_015_001, "补贴编码已存在");
    ErrorCode SUBSIDY_IMPORT_PARTIAL_FAILED = new ErrorCode(1_020_015_002, "补贴导入部分失败");

    // ========== 维保企业 1-020-016-000 ==========
    ErrorCode MAINTENANCE_ENTERPRISE_NOT_EXISTS = new ErrorCode(1_020_016_000, "维保企业不存在");
    ErrorCode MAINTENANCE_ENTERPRISE_HAS_STATIONS = new ErrorCode(1_020_016_001, "该企业下存在网点，无法删除");
    ErrorCode MAINTENANCE_ENTERPRISE_NAME_EXISTS = new ErrorCode(1_020_016_002, "企业名称已存在");
    ErrorCode MAINTENANCE_ENTERPRISE_CREDIT_CODE_EXISTS = new ErrorCode(1_020_016_003, "统一社会信用代码已存在");

    // ========== 维保网点 1-020-017-000 ==========
    ErrorCode MAINTENANCE_STATION_NOT_EXISTS = new ErrorCode(1_020_017_000, "维保网点不存在");

    // ========== 燃气安全员 1-020-018-000 ==========
    ErrorCode GAS_SAFETY_OFFICER_NOT_EXISTS = new ErrorCode(1_020_018_000, "燃气安全员不存在");
    ErrorCode MAINTENANCE_ENTERPRISE_HAS_OFFICERS = new ErrorCode(1_020_018_001, "该企业下存在安全员，无法删除");

    // ========== 农村气代煤协管员 1-020-019-000 ==========
    ErrorCode GAS_COORDINATOR_NOT_EXISTS = new ErrorCode(1_020_019_000, "农村气代煤协管员不存在");
    ErrorCode MAINTENANCE_ENTERPRISE_HAS_COORDINATORS = new ErrorCode(1_020_019_001, "该企业下存在协管员，无法删除");

    // ========== 台账文件（台账审定） 1-020-020-000 ==========
    ErrorCode LEDGER_FILE_NOT_EXISTS = new ErrorCode(1_020_020_000, "台账文件不存在");
    ErrorCode LEDGER_FILE_REFERENCED = new ErrorCode(1_020_020_001, "该台账已被上报引用，禁止操作");
    ErrorCode LEDGER_FILE_FORMAT_ERROR = new ErrorCode(1_020_020_002, "仅支持上传 .xls/.xlsx 格式的台账文件");
    ErrorCode LEDGER_FILE_NOT_DRAFT = new ErrorCode(1_020_020_003, "台账文件非待提交状态，无法操作");
    ErrorCode LEDGER_FILE_CANNOT_WITHDRAW = new ErrorCode(1_020_020_004, "台账文件非审核中状态，无法撤回");
    ErrorCode LEDGER_FILE_CANNOT_REVIEW = new ErrorCode(1_020_020_005, "台账文件非审核中状态，无法审核");
    ErrorCode LEDGER_FILE_REJECT_REASON_REQUIRED = new ErrorCode(1_020_020_006, "驳回原因不能为空");
    ErrorCode LEDGER_FILE_NOT_APPROVED = new ErrorCode(1_020_020_007, "台账文件未审核通过，无法上报");

    // ========== 台账上报 1-020-021-000 ==========
    ErrorCode LEDGER_REPORT_NOT_EXISTS = new ErrorCode(1_020_021_000, "台账上报不存在");
    ErrorCode LEDGER_REPORT_NOT_DRAFT = new ErrorCode(1_020_021_001, "台账上报非待提交状态，无法操作");
    ErrorCode LEDGER_REPORT_CANNOT_WITHDRAW = new ErrorCode(1_020_021_002, "台账上报非审核中状态，无法撤回");
    ErrorCode LEDGER_REPORT_CANNOT_REVIEW = new ErrorCode(1_020_021_003, "台账上报非审核中状态，无法审核");
    ErrorCode LEDGER_REPORT_REJECT_REASON_REQUIRED = new ErrorCode(1_020_021_004, "驳回原因不能为空");
    ErrorCode LEDGER_STAMPED_REPORT_FORMAT_ERROR = new ErrorCode(1_020_021_005, "盖章报告仅支持上传 PDF 格式文件");

    // ========== 导入任务 1-020-022-000 ==========
    ErrorCode LEDGER_IMPORT_TASK_NOT_EXISTS = new ErrorCode(1_020_022_000, "导入任务不存在");
    ErrorCode LEDGER_IMPORT_TASK_EXECUTING = new ErrorCode(1_020_022_001, "导入任务正在执行中，请勿重复操作");
    ErrorCode LEDGER_IMPORT_TASK_NOT_CHECK_SUCCESS = new ErrorCode(1_020_022_002, "导入任务未检测通过，无法执行导入");
    ErrorCode LEDGER_IMPORT_FILE_FORMAT_ERROR = new ErrorCode(1_020_022_003, "仅支持上传 .xls/.xlsx 格式的导入文件");

    // ========== 设备数据 1-020-024-000 ==========
    ErrorCode DEVICE_DATA_NOT_EXISTS = new ErrorCode(1_020_024_000, "设备数据不存在");
}
