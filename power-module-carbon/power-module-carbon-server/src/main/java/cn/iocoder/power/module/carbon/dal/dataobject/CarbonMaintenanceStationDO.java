package cn.iocoder.power.module.carbon.dal.dataobject;

import cn.iocoder.power.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName("carbon_maintenance_station")
@Data
@EqualsAndHashCode(callSuper = true)
public class CarbonMaintenanceStationDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属企业ID
     */
    private Long enterpriseId;

    /**
     * 网点名称
     */
    private String stationName;

    /**
     * 网点服务类型 1气代煤 2电代煤
     */
    private String serviceType;

    /**
     * 网点经营状态 1未经营 2经营中 3已关闭
     */
    private String businessStatus;

    /**
     * 维保人员数量
     */
    private Integer maintenanceStaffCount;

    /**
     * 覆盖村数
     */
    private Integer coveredVillages;

    /**
     * 覆盖户数
     */
    private Integer coveredHouseholds;

    /**
     * 服务范围（行政区划编码，逗号分隔）
     */
    private String serviceScope;

    /**
     * 网点负责人
     */
    private String managerName;

    /**
     * 网点负责人联系电话
     */
    private String managerPhone;
}
