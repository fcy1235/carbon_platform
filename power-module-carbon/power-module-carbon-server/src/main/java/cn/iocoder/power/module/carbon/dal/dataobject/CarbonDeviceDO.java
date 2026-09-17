package cn.iocoder.power.module.carbon.dal.dataobject;

import cn.iocoder.power.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@TableName("carbon_device")
@Data
@EqualsAndHashCode(callSuper = true)
public class CarbonDeviceDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String deviceCode;

    private String deviceName;

    private String deviceType;

    private String manufacturer;

    private LocalDate productionDate;

    private Integer serviceLife;

    private LocalDate installDate;

    private LocalDate operationDate;

    private LocalDate stopDate;

    private String status;

    private Long carbonUserInfoId;

    private Long gasEnterpriseId;

    private String wallMountedStoveInstallYear;

    private String gasMeterType;

    private String safetyDeviceStatus;

    private String deviceBrandModel;

    private String remark;

    private Integer isCarbon;
}
