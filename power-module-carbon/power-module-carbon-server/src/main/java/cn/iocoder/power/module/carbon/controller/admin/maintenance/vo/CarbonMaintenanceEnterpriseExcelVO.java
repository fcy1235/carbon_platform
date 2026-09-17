package cn.iocoder.power.module.carbon.controller.admin.maintenance.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 维保企业 Excel 导出 VO")
@Data
@ExcelIgnoreUnannotated
public class CarbonMaintenanceEnterpriseExcelVO {

    @ExcelProperty("省")
    private String provinceName;

    @ExcelProperty("市")
    private String cityName;

    @ExcelProperty("县(区)")
    private String countyName;

    @ExcelProperty("企业名称")
    private String enterpriseName;

    @ExcelProperty("统一社会信用代码")
    private String unifiedSocialCreditCode;

    @ExcelProperty("服务类型")
    private String serviceType;

    @ExcelProperty("服务状态")
    private String serviceStatus;

    @ExcelProperty("区域负责人")
    private String regionalManagerName;

    @ExcelProperty("负责人电话")
    private String regionalManagerPhone;

    @ExcelProperty("维保人员数")
    private Integer maintenanceStaffCount;

    @ExcelProperty("覆盖村数")
    private Integer coveredVillages;

    @ExcelProperty("覆盖户数")
    private Integer coveredHouseholds;

    @ExcelProperty("网点数量")
    private Integer stationCount;
}
