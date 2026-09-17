package cn.iocoder.power.module.carbon.dal.dataobject;

import cn.iocoder.power.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 设备数据 DO（设备上报数据）
 *
 * 对应表 carbon_device_data；user_code / device_code 直接存接口原始编码
 */
@TableName("carbon_device_data")
@Data
@EqualsAndHashCode(callSuper = true)
public class CarbonDeviceDataDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户编码（carbon_user_info.user_code，接口原始值）
     */
    private String userCode;

    /**
     * 设备编码（接口原始值，如 EM-000001 / ASHP-000001）
     */
    private String deviceCode;

    /**
     * 设备类型（电表/空气源热泵，接口原文）
     */
    private String deviceType;

    /**
     * 设备名称（接口原文）
     */
    private String deviceName;

    /**
     * 设备状态（接口原文）
     */
    private String deviceStatus;

    /**
     * 同步时间（接口 reportDate）
     */
    private LocalDateTime reportTime;

    /**
     * 总用电量
     */
    private BigDecimal totalElectricity;

    /**
     * 电压
     */
    private BigDecimal voltage;

    /**
     * 电流
     */
    private BigDecimal electricCurrent;

    /**
     * 设定温度
     */
    private BigDecimal setTemperature;

    /**
     * 液管温度
     */
    private BigDecimal liquidPipeTemperature;

    /**
     * 模块温度
     */
    private BigDecimal moduleTemperature;

    /**
     * 经济器进温度
     */
    private BigDecimal economizerInTemperature;

    /**
     * 经济器出温度
     */
    private BigDecimal economizerOutTemperature;

    /**
     * 回水温度
     */
    private BigDecimal returnWaterTemperature;

    /**
     * 出水温度
     */
    private BigDecimal outletWaterTemperature;

    /**
     * 室外温度
     */
    private BigDecimal outdoorTemperature;

    /**
     * 盘管温度
     */
    private BigDecimal coilTemperature;

    /**
     * 排气温度
     */
    private BigDecimal exhaustTemperature;

    /**
     * 吸水温度
     */
    private BigDecimal suctionWaterTemperature;

    /**
     * 线控器室内温度
     */
    private BigDecimal wireControllerIndoorTemperature;

    /**
     * 内机环温度
     */
    private BigDecimal indoorAmbientTemperature;

    /**
     * 内盘管温度
     */
    private BigDecimal indoorCoilTemperature;

}
