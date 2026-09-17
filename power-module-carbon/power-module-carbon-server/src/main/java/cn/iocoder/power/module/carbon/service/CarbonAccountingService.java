package cn.iocoder.power.module.carbon.service;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.module.carbon.controller.admin.accounting.vo.*;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonAccountingActivityDO;
import jakarta.validation.Valid;

import java.util.List;

public interface CarbonAccountingService {

    Long createAccounting(@Valid CarbonAccountingSaveReqVO createReqVO);

    List<Long> createAccountingBatch(@Valid List<CarbonAccountingSaveReqVO> createReqVOList);

    void updateAccounting(@Valid CarbonAccountingSaveReqVO updateReqVO);

    void deleteAccounting(Long id);

    CarbonAccountingRespVO getAccounting(Long id);

    PageResult<CarbonAccountUserInfoRespVO> getAccountingPage(CarbonAccountingPageReqVO pageReqVO);

    List<CarbonAccountingRespVO> getAccountingList(CarbonAccountingPageReqVO pageReqVO);

    List<CarbonAccountingActivityDO> getAccountingActivities(Long accountingId);

    CarbonAccountingRespVO calculateReduction(@Valid CarbonAccountingCalculateReqVO reqVO);

    PageResult<CarbonAccountUserInfoRespVO> getAccountUserInfoPage(@Valid CarbonAccountingPageReqVO pageReqVO);

    /**
     * 分页查询指定核算周期内尚未核算的用户（用户表为主表，排除同周期已核算用户）
     * 供项目管理选择用户批量创建核算使用
     */
    PageResult<CarbonAccountUserInfoRespVO> getUnaccountedUserPage(@Valid CarbonAccountingPageReqVO pageReqVO);

    /**
     * 选择用户后一次性返回所有初始化数据：基准线、用量、排放因子、减排量、设备列表
     */
    List<CarbonAccountingInitRespVO> getInitData(@Valid CarbonAccountingInitReqVO reqVO);

    /**
     * 根据采暖季和核算周期批量计算用电量/用气量
     * 先按 reformType 和 dataSource 分组批量预加载数据，再统一计算填充，避免循环内逐条查库。
     * - reformType=1(煤改电) + dataSource=2(接口)：取 carbon_device_data 首末记录差值
     * - reformType=1(煤改电) + dataSource=1(导入)：取 carbon_electricity_usage_report 首末表底差或 totalUsage
     * - reformType=2(煤改气)：取 carbon_gas_usage_report 首末表底差或 totalUsage
     */
    void calculateAndFillHeatingSeasonUsage(List<CarbonAccountingRespVO> list);
}
