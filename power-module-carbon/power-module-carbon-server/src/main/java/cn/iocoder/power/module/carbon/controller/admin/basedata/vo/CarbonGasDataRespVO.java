package cn.iocoder.power.module.carbon.controller.admin.basedata.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 燃气数据 Response VO")
@Data
public class CarbonGasDataRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "数据来源", example = "燃气公司接口")
    private String dataSource;

    @Schema(description = "用户姓名", example = "张三")
    private String username;

    @Schema(description = "身份证号", example = "11010519900301001X")
    private String idCard;

    @Schema(description = "用户信息编号", example = "1024")
    private Long carbonUserInfoId;

    @Schema(description = "行政区划", example = "河北省石家庄市裕华区裕强街道")
    private String division;

    @Schema(description = "详细地址", example = "测试地址1号")
    private String address;

    @Schema(description = "燃气户号", example = "G20000")
    private String gasId;

    @Schema(description = "当前累计气量", example = "800")
    private BigDecimal currentTotal;

    @Schema(description = "本次用气量", example = "12")
    private BigDecimal currentUsage;

    @Schema(description = "上次表读数", example = "788")
    private BigDecimal lastReading;

    @Schema(description = "读数时间")
    private LocalDateTime readingTime;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;
}
