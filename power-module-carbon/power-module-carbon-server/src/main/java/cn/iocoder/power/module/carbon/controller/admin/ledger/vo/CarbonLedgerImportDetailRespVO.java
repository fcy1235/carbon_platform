package cn.iocoder.power.module.carbon.controller.admin.ledger.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 导入明细 Response VO")
@Data
public class CarbonLedgerImportDetailRespVO {

    @Schema(description = "编号", example = "1024")
    private Long id;

    @Schema(description = "任务ID", example = "1024")
    private Long taskId;

    // ==================== 用户基本信息字段 ====================

    @Schema(description = "用户姓名", example = "张三")
    private String username;

    @Schema(description = "身份证号", example = "130102199001011234")
    private String idCard;

    @Schema(description = "联系方式", example = "13800138000")
    private String phone;

    @Schema(description = "省名称", example = "河北省")
    private String provinceName;

    @Schema(description = "市名称", example = "石家庄市")
    private String cityName;

    @Schema(description = "区县名称", example = "长安区")
    private String districtName;

    @Schema(description = "乡镇名称", example = "某某镇")
    private String townName;

    @Schema(description = "村名称", example = "某某村")
    private String villageName;

    @Schema(description = "地址", example = "某村1号")
    private String address;

    @Schema(description = "采暖面积(m²)", example = "120.50")
    private BigDecimal heatingArea;

    @Schema(description = "改造类型", example = "1")
    private String reformType;

    @Schema(description = "数据来源", example = "1")
    private String dataSource;

    @Schema(description = "电力用户编号", example = "E001")
    private String electricityId;

    @Schema(description = "燃气用户编号", example = "G001")
    private String gasId;

    @Schema(description = "改造年份", example = "2024")
    private String reformYear;

    @Schema(description = "改造批次", example = "1")
    private String reformBatch;

    @Schema(description = "补贴方式", example = "1")
    private String subsidyMethod;

    @Schema(description = "房屋用途", example = "1")
    private String houseUsage;

    @Schema(description = "用户类别", example = "1")
    private String userCategory;

    @Schema(description = "燃气用户编码", example = "GU001")
    private String gasUserCode;

    @Schema(description = "使用状态", example = "1")
    private String useStatus;

    @Schema(description = "备注", example = "")
    private String remark;

    // ==================== 导入相关字段 ====================

    @Schema(description = "导入状态", example = "1")
    private String importStatus;

    @Schema(description = "错误信息", example = "身份证号格式不正确")
    private String errorMsg;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
