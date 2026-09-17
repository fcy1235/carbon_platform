package cn.iocoder.power.module.infra.enums;

import cn.iocoder.power.framework.common.exception.ErrorCode;

public interface ErrorCodeConstants {

    // ========== CARBON 模块 1-003-000-000 ==========
    ErrorCode CARBON_EMISSION_CODE_USED = new ErrorCode(1_003_000_001, "该排放源正在被使用，暂时不能删除");

}
