package cn.iocoder.power.module.carbon.dal.mysql;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.power.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.power.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.power.module.carbon.controller.admin.accounting.vo.CarbonAccountingPageReqVO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonAccountingDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonAccountingActivityDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonUserInfoDO;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface CarbonAccountingMapper extends BaseMapperX<CarbonAccountingDO> {

    /**
     * 连表列表查询：碳核算 + 用户信息 + 活动信息
     */
    default List<CarbonAccountingWithUserDTO> selectListWithUser(CarbonAccountingPageReqVO reqVO) {
        return selectJoinList(CarbonAccountingWithUserDTO.class, buildJoinWrapper(reqVO));
    }

    /**
     * 查询同一用户相同核算周期的碳排放核算数量（用于唯一性校验）
     *
     * @param carbonUserInfoId      用户信息编号
     * @param accountingPeriodStart 核算周期开始日期
     * @param accountingPeriodEnd   核算周期结束日期
     * @param excludeId             排除的碳核算编号（更新时排除自身，新增时传 null）
     */
    default Long selectCountByUserInfoIdAndPeriod(Long carbonUserInfoId, LocalDate accountingPeriodStart,
                                                  LocalDate accountingPeriodEnd, Long excludeId) {
        return selectCount(new LambdaQueryWrapperX<CarbonAccountingDO>()
                .eq(CarbonAccountingDO::getCarbonUserInfoId, carbonUserInfoId)
                .eq(CarbonAccountingDO::getAccountingPeriodStart, accountingPeriodStart)
                .eq(CarbonAccountingDO::getAccountingPeriodEnd, accountingPeriodEnd)
                .ne(excludeId != null, CarbonAccountingDO::getId, excludeId));
    }

    /**
     * 简单列表查询（不关联用户表，用于内部调用）
     */
    default List<CarbonAccountingDO> selectSimpleList(CarbonAccountingPageReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<CarbonAccountingDO>()
                .inIfPresent(CarbonAccountingDO::getCarbonUserInfoId, reqVO.getCarbonUserInfoIds())
                .orderByDesc(CarbonAccountingDO::getCreateTime));
    }

    /**
     * 构建关联查询的 Wrapper（碳核算为主表）
     */
    private MPJLambdaWrapper<CarbonAccountingDO> buildJoinWrapper(CarbonAccountingPageReqVO reqVO) {
        return new MPJLambdaWrapper<CarbonAccountingDO>()
                // 碳核算字段
                .select(CarbonAccountingDO::getId, CarbonAccountingDO::getCarbonUserInfoId,
                        CarbonAccountingDO::getAccountingPeriodStart, CarbonAccountingDO::getAccountingPeriodEnd,
                        CarbonAccountingDO::getActualEmission, CarbonAccountingDO::getReduction,
                        CarbonAccountingDO::getCreateTime)
                // 用户信息字段
                .select(CarbonUserInfoDO::getUsername, CarbonUserInfoDO::getAddress,
                        CarbonUserInfoDO::getProvinceCode, CarbonUserInfoDO::getCityCode,
                        CarbonUserInfoDO::getDistrictCode, CarbonUserInfoDO::getHeatingArea,
                        CarbonUserInfoDO::getReformType, CarbonUserInfoDO::getUserCode,
                        CarbonUserInfoDO::getDataSource)
                // 活动信息字段
                .select(CarbonAccountingActivityDO::getActivityNameCode,CarbonAccountingActivityDO::getEmissionFactor)
                // LEFT JOIN
                .leftJoin(CarbonUserInfoDO.class, CarbonUserInfoDO::getId, CarbonAccountingDO::getCarbonUserInfoId)
                .leftJoin(CarbonAccountingActivityDO.class, CarbonAccountingActivityDO::getAccountingId, CarbonAccountingDO::getId)
                // 用户信息筛选条件
                .likeIfExists(CarbonUserInfoDO::getUsername, reqVO.getUsername())
                .eqIfExists( CarbonUserInfoDO::getProvinceCode, reqVO.getProvinceCode())
                .eqIfExists( CarbonUserInfoDO::getCityCode, reqVO.getCityCode())
                .eqIfExists( CarbonUserInfoDO::getDistrictCode, reqVO.getDistrictCode())
                // 碳核算筛选条件
                .eqIfExists(CarbonAccountingActivityDO::getActivityNameCode, reqVO.getActivityNameCode())
                .eqIfExists(CarbonAccountingDO::getCarbonUserInfoId, reqVO.getCarbonUserInfoId())
                .in(reqVO.getCarbonUserInfoIds() != null && !reqVO.getCarbonUserInfoIds().isEmpty(),
                        CarbonAccountingDO::getCarbonUserInfoId, reqVO.getCarbonUserInfoIds())
                .in(CollUtil.isNotEmpty(reqVO.getIds()), CarbonAccountingDO::getId, reqVO.getIds())
                .geIfExists(CarbonAccountingDO::getAccountingPeriodStart, reqVO.getAccountingPeriodStart())
                .leIfExists(CarbonAccountingDO::getAccountingPeriodEnd, reqVO.getAccountingPeriodEnd())
                .orderByDesc(CarbonAccountingDO::getCreateTime);
    }
}
