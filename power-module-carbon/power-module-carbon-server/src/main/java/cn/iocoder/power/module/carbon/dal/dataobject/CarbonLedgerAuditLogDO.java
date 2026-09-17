package cn.iocoder.power.module.carbon.dal.dataobject;

import cn.iocoder.power.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 台账审核轨迹 DO
 */
@TableName("carbon_ledger_audit_log")
@Data
@EqualsAndHashCode(callSuper = true)
public class CarbonLedgerAuditLogDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 上报ID
     */
    private Long reportId;

    /**
     * 状态：1-待提交 2-已提交 3-审核中 4-审核通过 5-审核驳回 6-已撤回
     */
    private String status;

    /**
     * 状态变更原因
     */
    private String reason;

    /**
     * 操作人
     */
    private String operator;

    /**
     * 状态变更时间
     */
    private LocalDateTime changeTime;
}
