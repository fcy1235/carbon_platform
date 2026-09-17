package cn.iocoder.power.module.carbon.dal.dataobject;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 台账导入任务联表查询 DO（包含关联的台账文件信息）
 */
@Data
public class CarbonLedgerImportTaskWithFileDO {

    /**
     * 导入任务ID
     */
    private Long id;

    /**
     * 关联台账文件ID
     */
    private Long ledgerFileId;

    /**
     * 正常数量
     */
    private Integer successCount;

    /**
     * 失败数量
     */
    private Integer failCount;

    /**
     * 导入状态：1-待检测 2-检测通过 3-检测失败 4-导入中 5-导入成功 6-部分失败 7-导入失败
     */
    private String importStatus;

    /**
     * 上传人
     */
    private String uploader;

    /**
     * 上传时间
     */
    private LocalDateTime uploadTime;

    /**
     * 导入结果文件地址
     */
    private String resultFileUrl;

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
     * 所属市名称（来自台账文件表）
     */
    private String cityName;

    /**
     * 所属区县编码（来自台账文件表）
     */
    private String districtCode;

    /**
     * 所属区县名称（来自台账文件表）
     */
    private String districtName;

    /**
     * 上传类型（来自台账文件表）
     */
    private String uploadType;

    /**
     * 文件名称（来自台账文件表）
     */
    private String fileName;

    /**
     * 文件地址（来自台账文件表）
     */
    private String fileUrl;

    /**
     * 数据量（来自台账文件表）
     */
    private Integer dataCount;

    /**
     * 上传状态（来自台账文件表）
     */
    private String uploadStatus;

    /**
     * 审核状态（来自台账文件表）
     */
    private String auditStatus;

    /**
     * 数据层级（来自台账文件表）
     */
    private String dataLevel;


    private Long deptId;
}