package cn.iocoder.power.module.carbon.controller.admin.device.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ExcelIgnoreUnannotated
public class CarbonDeviceDataExcelVO {

    @ExcelProperty("用户编码")
    private String userCode;

    @ExcelProperty("用户名")
    private String username;

    @ExcelProperty("设备编码")
    private String deviceCode;

    @ExcelProperty("设备类型")
    private String deviceType;

    @ExcelProperty("设备名称")
    private String deviceName;

    @ExcelProperty("设备状态")
    private String deviceStatus;

    @ExcelProperty("同步时间")
    private String reportTime;

    @ExcelProperty("总用电量")
    private BigDecimal totalElectricity;

    @ExcelProperty("电压")
    private BigDecimal voltage;

    @ExcelProperty("电流")
    private BigDecimal electricCurrent;

    @ExcelProperty("设定温度")
    private BigDecimal setTemperature;

    @ExcelProperty("液管温度")
    private BigDecimal liquidPipeTemperature;

    @ExcelProperty("模块温度")
    private BigDecimal moduleTemperature;

    @ExcelProperty("经济器进温度")
    private BigDecimal economizerInTemperature;

    @ExcelProperty("经济器出温度")
    private BigDecimal economizerOutTemperature;

    @ExcelProperty("回水温度")
    private BigDecimal returnWaterTemperature;

    @ExcelProperty("出水温度")
    private BigDecimal outletWaterTemperature;

    @ExcelProperty("室外温度")
    private BigDecimal outdoorTemperature;

    @ExcelProperty("盘管温度")
    private BigDecimal coilTemperature;

    @ExcelProperty("排气温度")
    private BigDecimal exhaustTemperature;

    @ExcelProperty("吸水温度")
    private BigDecimal suctionWaterTemperature;

    @ExcelProperty("线控器室内温度")
    private BigDecimal wireControllerIndoorTemperature;

    @ExcelProperty("内机环温度")
    private BigDecimal indoorAmbientTemperature;

    @ExcelProperty("内盘管温度")
    private BigDecimal indoorCoilTemperature;
}
