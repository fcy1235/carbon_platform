package cn.iocoder.power.module.carbon.controller.admin.subsidy.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 补贴管理 Response VO")
@Data
public class CarbonSubsidyRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "补贴编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "SUB20260101001")
    private String subsidyCode;

    @Schema(description = "用户信息ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long userInfoId;

    @Schema(description = "补贴金额", requiredMode = Schema.RequiredMode.REQUIRED, example = "500")
    private BigDecimal subsidyAmount;



    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

    // ========== 用户信息（来自用户信息表） ==========

    @Schema(description = "用户姓名", example = "张三")
    private String username;

    @Schema(description = "详细地址", example = "测试地址1号")
    private String address;

    @Schema(description = "改造类型", example = "气代煤")
    private String reformType;

    @Schema(description = "身份证号", example = "130102199001011234")
    private String idCard;

    @Schema(description = "联系电话", example = "13800138000")
    private String phone;

    @Schema(description = "省编码", example = "130000")
    private Long provinceCode;

    @Schema(description = "市编码", example = "130100")
    private Long cityCode;

    @Schema(description = "区编码", example = "130102")
    private Long districtCode;
}
