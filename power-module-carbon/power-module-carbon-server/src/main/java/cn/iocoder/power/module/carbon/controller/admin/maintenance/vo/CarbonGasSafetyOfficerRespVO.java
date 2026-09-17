package cn.iocoder.power.module.carbon.controller.admin.maintenance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 燃气安全员 Response VO")
@Data
public class CarbonGasSafetyOfficerRespVO {

    @Schema(description = "安全员编号", example = "1024")
    private Long id;

    @Schema(description = "姓名", example = "张三")
    private String name;

    @Schema(description = "性别", example = "男")
    private String gender;

    @Schema(description = "身份证号", example = "130102199001011234")
    private String idCardNo;

    @Schema(description = "身份证正面图片地址")
    private String idCardFrontImage;

    @Schema(description = "身份证反面图片地址")
    private String idCardBackImage;

    @Schema(description = "学历", example = "本科")
    private String education;

    @Schema(description = "联系电话", example = "13800138000")
    private String phone;

    @Schema(description = "从业资格证编号", example = "ZY2024001")
    private String qualificationNo;

    @Schema(description = "负责区域1（省,市,区,街道,村 逗号分隔的区划代码）", example = "13,1301,130102,130102001,130102001001")
    private String area1;

    @Schema(description = "负责区域2（省,市,区,街道,村 逗号分隔的区划代码）", example = "13,1301,130102,130102002,130102002001")
    private String area2;

    @Schema(description = "负责区域3（省,市,区,街道,村 逗号分隔的区划代码）", example = "13,1301,130103,130103001,130103001001")
    private String area3;

    @Schema(description = "负责区域4（省,市,区,街道,村 逗号分隔的区划代码）")
    private String area4;

    @Schema(description = "负责区域5（省,市,区,街道,村 逗号分隔的区划代码）")
    private String area5;

    @Schema(description = "负责区域1名称（省市区街道村拼接）", example = "河北省/石家庄市/长安区/XX街道/XX村")
    private String area1Name;

    @Schema(description = "负责区域2名称（省市区街道村拼接）")
    private String area2Name;

    @Schema(description = "负责区域3名称（省市区街道村拼接）")
    private String area3Name;

    @Schema(description = "负责区域4名称（省市区街道村拼接）")
    private String area4Name;

    @Schema(description = "负责区域5名称（省市区街道村拼接）")
    private String area5Name;

    @Schema(description = "所属燃气企业（维保企业ID）", example = "1")
    private Long enterpriseId;

    @Schema(description = "所属燃气企业名称", example = "XX燃气公司")
    private String enterpriseName;

    @Schema(description = "统一社会信用代码", example = "91130100MA0XXXXXXX")
    private String unifiedSocialCreditCode;

    @Schema(description = "人员状态 1在岗 2离岗", example = "1")
    private String staffStatus;

    @Schema(description = "到岗日期", example = "2024-01-01")
    private LocalDate onDutyDate;

    @Schema(description = "离岗日期", example = "2024-12-31")
    private LocalDate offDutyDate;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
