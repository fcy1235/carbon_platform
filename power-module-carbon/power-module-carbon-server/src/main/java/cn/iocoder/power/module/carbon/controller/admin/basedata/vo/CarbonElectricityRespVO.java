package cn.iocoder.power.module.carbon.controller.admin.basedata.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 电力数据 Response VO")
@Data
public class CarbonElectricityRespVO implements Serializable {

    @Serial
    private static final long serialVersionUID = -1L;

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "数据来源", example = "国网接口")
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

    @Schema(description = "电力户号", example = "E10000")
    private String electricityId;

    @Schema(description = "当前累计电量", example = "1200")
    private BigDecimal currentTotal;

    @Schema(description = "本次用电量", example = "45")
    private BigDecimal currentUsage;

    @Schema(description = "上次表读数", example = "1155")
    private BigDecimal lastReading;

    @Schema(description = "读数时间")
    private LocalDateTime readingTime;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;
}
