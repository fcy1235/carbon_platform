package cn.iocoder.power.module.carbon.dal.dataobject;

import cn.iocoder.power.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@TableName("carbon_user_info")
@Data
@EqualsAndHashCode(callSuper = true)
public class CarbonUserInfoDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String userCode;

    private String idCard;

    private String phone;

    private Long provinceCode;

    private Long cityCode;

    private Long districtCode;

    private Long townCode;

    private Long villageCode;

    private String address;

    private BigDecimal heatingArea;

    private String reformType;

    private String reformMode;

    private String dataSource;

    private String electricityId;

    private String gasId;

    private String reformYear;

    private String reformBatch;

    private String subsidyMethod;

    private String houseUsage;

    private String userCategory;

    private String gasUserCode;

    private String useStatus;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private Long deptId;
}
