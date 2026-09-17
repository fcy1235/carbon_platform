package cn.iocoder.power.module.carbon.dal.dataobject;

import cn.iocoder.power.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 台账上报 DO
 */
@TableName("carbon_ledger_report")
@Data
@EqualsAndHashCode(callSuper = true)
public class CarbonLedgerReportDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联台账文件ID
     */
    private Long ledgerFileId;

    /**
     * 上报说明 调整后备注
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

    @TableField(fill = FieldFill.INSERT)
    private Long deptId;
}
