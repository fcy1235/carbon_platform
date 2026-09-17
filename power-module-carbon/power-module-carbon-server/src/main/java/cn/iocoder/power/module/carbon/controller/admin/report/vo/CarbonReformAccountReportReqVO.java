package cn.iocoder.power.module.carbon.controller.admin.report.vo;

import cn.iocoder.power.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 清洁取暖改造确户台账 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class CarbonReformAccountReportReqVO extends PageParam {

    @Schema(description = "省")
    private Long provinceCode;

    @Schema(description = "市")
    private Long cityCode;

    @Schema(description = "区/县")
    private Long districtCode;

    @Schema(description = "街道/镇")
    private Long townCode;

    @Schema(description = "社区/村")
    private Long villageCode;

    @Schema(description = "身份证号/姓名（模糊查询）")
    private String keyword;

    @Schema(description = "联系方式")
    private String phone;

    @Schema(description = "改造类型")
    private String reformType;

    @Schema(description = "使用状态")
    private String useStatus;

    @Schema(description = "改造年限")
    private String reformYear;
}
