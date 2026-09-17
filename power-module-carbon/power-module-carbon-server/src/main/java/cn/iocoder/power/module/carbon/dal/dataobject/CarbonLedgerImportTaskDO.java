package cn.iocoder.power.module.carbon.dal.dataobject;

import cn.iocoder.power.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 台账导入任务 DO
 * 注意：公共字段（城市、区县、上传类型、数据量等）通过 ledger_file_id 关联 carbon_ledger_file 获取
 */
@TableName("carbon_ledger_import_task")
@Data
@EqualsAndHashCode(callSuper = true)
public class CarbonLedgerImportTaskDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
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
     * 部门ID
     */
    @TableField(fill = FieldFill.INSERT)
    private Long deptId;
}
