package cn.iocoder.power.module.carbon.dal.dataobject;

import cn.iocoder.power.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 台账文件 DO
 */
@TableName("carbon_ledger_file")
@Data
@EqualsAndHashCode(callSuper = true)
public class CarbonLedgerFileDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属市编码
     */
    private String cityCode;



    /**
     * 所属区县编码
     */
    private String districtCode;



    /**
     * 数据层级：1-县级 2-市级
     */
    private String dataLevel;

    /**
     * 上传类型：1-新增户 2-变更户 3-撤销户 4-改造类型变更
     */
    private String uploadType;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件地址
     */
    private String fileUrl;

    /**
     * 数据量
     */
    private Integer dataCount;

    /**
     * 上传状态：1-上传成功 2-上传失败
     */
    private String uploadStatus;

    /**
     * 审核状态：1-待提交 2-审核中 3-已通过 4-已驳回
     */
    private String auditStatus;

    /**
     * 上传时间
     */
    private LocalDateTime uploadTime;

    /**
     * 上传人
     */
    private String uploader;

    /**
     * 审核人
     */
    private String reviewer;

    /**
     * 审核时间
     */
    private LocalDateTime reviewTime;

    /**
     * 驳回原因
     */
    private String rejectReason;

    /**
     * 备注
     */
    private String remark;


    private LocalDateTime submitTime;

    /**
     * 部门ID
     */
    @TableField(fill = FieldFill.INSERT)
    private Long deptId;
}
