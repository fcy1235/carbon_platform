package cn.iocoder.power.module.iot.dal.dataobject.device;

import cn.iocoder.power.framework.mybatis.core.type.LongSetTypeHandler;
import cn.iocoder.power.framework.tenant.core.db.TenantBaseDO;
import cn.iocoder.power.module.iot.core.enums.device.IotDeviceStateEnum;
import cn.iocoder.power.module.iot.dal.dataobject.ota.IotOtaFirmwareDO;
import cn.iocoder.power.module.iot.dal.dataobject.product.IotProductDO;
import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * IoT 设备 DO
 *
 * @author haohao
 */
@TableName(value = "iot_device", autoResultMap = true)
@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IotDeviceDO extends TenantBaseDO {

    /**
     * 设备编号 - 全部设备
     */
    public static final Long DEVICE_ID_ALL = 0L;

    /**
     * 设备 ID，主键，自增
     */
     @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 设备名称，在产品内唯一，用于标识设备
     */
    private String deviceName;
    /**
     * 设备备注名称
     */
    private String nickname;
    /**
     * 设备序列号
     */
    private String serialNumber;
    /**
     * 设备图片
     */
    private String picUrl;
    /**
     * 设备分组编号集合
     *
     * 关联 {@link IotDeviceGroupDO#getId()}
     */
    @TableField(typeHandler = LongSetTypeHandler.class)
    private Set<Long> groupIds;

    /**
     * 产品编号
     * <p>
     * 关联 {@link IotProductDO#getId()}
     */
    private Long productId;
    /**
     * 产品标识
     * <p>
     * 冗余 {@link IotProductDO#getProductKey()}
     */
    private String productKey;
    /**
     * 设备类型
     * <p>
     * 冗余 {@link IotProductDO#getDeviceType()}
     */
    private Integer deviceType;
    /**
     * 网关设备编号
     * <p>
     * 子设备需要关联的网关设备 ID
     * <p>
     * 关联 {@link IotDeviceDO#getId()}
     */
    private Long gatewayId;

    /**
     * 设备状态
     * <p>
     * 枚举 {@link IotDeviceStateEnum}
     */
    private Integer state;
    /**
     * 最后上线时间
     */
    private LocalDateTime onlineTime;
    /**
     * 最后离线时间
     */
    private LocalDateTime offlineTime;
    /**
     * 设备激活时间
     */
    private LocalDateTime activeTime;

    /**
     * 固件编号
     *
     * 关联 {@link IotOtaFirmwareDO#getId()}
     */
    private Long firmwareId;

    /**
     * 设备密钥，用于设备认证
     */
    private String deviceSecret;

    /**
     * 设备位置的纬度
     */
    private BigDecimal latitude;
    /**
     * 设备位置的经度
     */
    private BigDecimal longitude;

    /**
     * 设备配置
     *
     * JSON 格式，可下发给 device 进行自定义配置
     */
    private String config;

}