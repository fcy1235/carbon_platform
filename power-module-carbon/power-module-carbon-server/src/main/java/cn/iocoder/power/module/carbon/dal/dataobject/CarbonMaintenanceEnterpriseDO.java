package cn.iocoder.power.module.carbon.dal.dataobject;

import cn.iocoder.power.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("carbon_maintenance_enterprise")
@Data
@EqualsAndHashCode(callSuper = true)
public class CarbonMaintenanceEnterpriseDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 省编码
     */
    private Long provinceCode;

    /**
     * 市编码
     */
    private Long cityCode;

    /**
     * 县(市、区)编码
     */
    private Long countyCode;

    /**
     * 企业名称
     */
    private String enterpriseName;

    /**
     * 统一社会信用代码
     */
    private String unifiedSocialCreditCode;

    /**
     * 企业服务类型 1气代煤 2电代煤
     */
    private String serviceType;

    /**
     * 服务状态 1服务中 2已停止
     */
    private String serviceStatus;

    /**
     * 区域负责人姓名
     */
    private String regionalManagerName;

    /**
     * 区域负责人电话
     */
    private String regionalManagerPhone;
}
