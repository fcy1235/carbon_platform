package cn.iocoder.power.module.carbon.controller.admin.basedata.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 文档管理 Response VO")
@Data
public class CarbonDocumentRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "文档编号", example = "DOC20260514001")
    private String docCode;

    @Schema(description = "文档标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "文档标题1")
    private String docTitle;

    @Schema(description = "文档名称", example = "文档1.pdf")
    private String docName;


    @Schema(description = "适用边界", example = "河北省农村区域")
    private String applicableBoundary;

    @Schema(description = "计算公式", example = "E=AD×EF")
    private String formula;

    @Schema(description = "上传人", example = "admin")
    private String uploader;

    @Schema(description = "上传时间")
    private LocalDateTime uploadTime;

    @Schema(description = "文件地址", example = "/uploads/doc/xxx.pdf")
    private String fileUrl;

    @Schema(description = "备注", example = "")
    private String remark;

    @Schema(description = "详细信息列表")
    private List<CarbonDocumentDetailRespVO> details;

    @Schema(description = "参数说明列表")
    private List<CarbonDocumentParamRespVO> params;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;
}
