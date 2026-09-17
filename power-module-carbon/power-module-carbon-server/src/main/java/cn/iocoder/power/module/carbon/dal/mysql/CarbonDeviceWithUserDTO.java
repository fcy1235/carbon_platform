package cn.iocoder.power.module.carbon.dal.mysql;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 设备 + 用户信息 联表查询结果 DTO
 */
@Data
public class CarbonDeviceWithUserDTO {

    // 设备字段
    private Long id;
    private String deviceCode;
    private Long carbonUserInfoId;
    private String deviceName;
    private String deviceType;
    private String manufacturer;
    private LocalDate productionDate;
    private Integer serviceLife;
    private LocalDate installDate;
    private LocalDate operationDate;
    private LocalDate stopDate;
    private String status;
    private Long gasEnterpriseId;
    private String wallMountedStoveInstallYear;
    private String gasMeterType;
    private String safetyDeviceStatus;
    private String deviceBrandModel;
    private String remark;
    private LocalDateTime createTime;

    // 用户信息字段
    private String username;
    private String idCard;
    private String phone;
    private Long provinceCode;
    private Long cityCode;
    private Long districtCode;
    private Long townCode;
    private Long villageCode;
    private String address;
    private String reformType;
    private String reformMode;
    private String gasUserCode;
    private String gasId;
    private String electricityId;
}
