package cn.iocoder.power.module.carbon.controller.admin.project.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "管理后台 - 项目关联用户 Response VO")
@Data
public class CarbonProjectUserRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "项目编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long projectId;

    @Schema(description = "用户信息编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long carbonUserInfoId;

    @Schema(description = "碳核算编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2048")
    private Long accountingId;

    @Schema(description = "用户姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    private String username;

    @Schema(description = "身份证号", example = "130102199001010001")
    private String idCard;

    @Schema(description = "联系电话", example = "13800138000")
    private String phone;

    @Schema(description = "行政区划", requiredMode = Schema.RequiredMode.REQUIRED, example = "河北省石家庄市裕华区裕强街道")
    private String division;

    @Schema(description = "详细地址", requiredMode = Schema.RequiredMode.REQUIRED, example = "测试地址1号")
    private String address;

    @Schema(description = "改造类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "气代煤")
    private String reformType;

    @Schema(description = "电力号", example = "E10000")
    private String electricityId;

    @Schema(description = "燃气号", example = "G20000")
    private String gasId;

    @Schema(description = "基准线排放量", requiredMode = Schema.RequiredMode.REQUIRED, example = "40.5")
    private BigDecimal baselineEmission;

    @Schema(description = "实际排放量", requiredMode = Schema.RequiredMode.REQUIRED, example = "220")
    private BigDecimal actualEmission;

    @Schema(description = "减排量", requiredMode = Schema.RequiredMode.REQUIRED, example = "-179.5")
    private BigDecimal reduction;

    @Schema(description = "核算周期开始日期", example = "2023-01-01")
    private LocalDate accountingPeriodStart;

    @Schema(description = "核算周期结束日期", example = "2023-12-31")
    private LocalDate accountingPeriodEnd;

}
