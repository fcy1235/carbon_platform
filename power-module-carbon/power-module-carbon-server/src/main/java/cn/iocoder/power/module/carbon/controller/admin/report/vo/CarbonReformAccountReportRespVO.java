package cn.iocoder.power.module.carbon.controller.admin.report.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - 清洁取暖改造确户台账 Response VO")
@Data
public class CarbonReformAccountReportRespVO {

    private Long provinceCode;
    private Long cityCode;
    private Long districtCode;
    private Long townCode;
    private Long villageCode;

    @Schema(description = "所属区县")
    private String districtName;

    @Schema(description = "所属乡镇")
    private String townName;

    @Schema(description = "所属村名称")
    private String villageName;

    @Schema(description = "地址")
    private String address;

    @Schema(description = "户主姓名")
    private String username;

    @Schema(description = "身份证号")
    private String idCard;

    @Schema(description = "联系方式")
    private String phone;

    @Schema(description = "采暖面积（㎡）")
    private BigDecimal heatingArea;

    @Schema(description = "改造年限")
    private String reformYear;

    @Schema(description = "使用状态")
    private String useStatus;

    @Schema(description = "改造类型")
    private String reformType;

    @Schema(description = "备注")
    private String remark;
}
