package cn.iocoder.power.module.carbon.dal.dataobject;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 台账上报联表查询 DO（包含关联的台账文件信息）
 */
@Data
public class CarbonLedgerReportWithFileDO {

    /**
     * 台账上报ID
     */
    private Long id;

    /**
     * 关联台账文件ID
     */
    private Long ledgerFileId;

    /**
     * 上报说明
     */
    private String reportDesc;


    /**
     * 盖章报告PDF地址
     */
    private String stampedReportUrl;

    /**
     * 盖章报告PDF文件名
     */
    private String stampedReportName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 所属市编码（来自台账文件表）
     */
    private String cityCode;

    /**
     * 所属区县编码（来自台账文件表）
     */
    private String districtCode;

    /**
     * 上传类型（来自台账文件表）
     */
    private String uploadType;

    /**
     * 数据量（来自台账文件表）
     */
    private Integer dataCount;

    /**
     * 审核状态（来自台账文件表）
     */
    private String auditStatus;

    /**
     * 上传时间（来自台账文件表）
     */
    private LocalDateTime uploadTime;

    /**
     * 文件名称（来自台账文件表）
     */
    private String fileName;

    /**
     * 文件地址（来自台账文件表）
     */
    private String fileUrl;

    /**
     * 审核人（来自台账文件表）
     */
    private String reviewer;

    /**
     * 审核时间（来自台账文件表）
     */
    private LocalDateTime reviewTime;

    /**
     * 驳回原因（来自台账文件表）
     */
    private String rejectReason;

    /**
     * 上传者
     */
    private String uploader;
}