package cn.iocoder.power.module.carbon.dal.mysql;

import cn.iocoder.power.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.power.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonGasUsageReportDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

/**
 * 燃气数据报表（采暖季用气量统计）Mapper
 */
@Mapper
public interface CarbonGasUsageReportMapper extends BaseMapperX<CarbonGasUsageReportDO> {

    /**
     * 报表查询：采暖季精确匹配；userIds 非空时按用户编号过滤（用户侧条件由 Service 预解析）
     */
    default List<CarbonGasUsageReportDO> selectReportList(String heatingSeason, Collection<Long> userIds) {
        return selectList(new LambdaQueryWrapperX<CarbonGasUsageReportDO>()
                .eqIfPresent(CarbonGasUsageReportDO::getHeatingSeason, heatingSeason)
                .in(userIds != null && !userIds.isEmpty(), CarbonGasUsageReportDO::getCarbonUserInfoId, userIds)
                .orderByDesc(CarbonGasUsageReportDO::getId));
    }

    /**
     * 按用户ID和采暖季查询用气量报表记录，用于采暖季用气量计算
     */
    default List<CarbonGasUsageReportDO> selectListByUserInfoIdAndHeatingSeason(
            Long carbonUserInfoId, String heatingSeason) {
        return selectList(new LambdaQueryWrapperX<CarbonGasUsageReportDO>()
                .eq(CarbonGasUsageReportDO::getCarbonUserInfoId, carbonUserInfoId)
                .eq(CarbonGasUsageReportDO::getHeatingSeason, heatingSeason)
                .orderByAsc(CarbonGasUsageReportDO::getId));
    }

    /**
     * 批量按用户ID集合和采暖季查询用气量报表记录（ID升序），用于采暖季用气量批量计算
     */
    default List<CarbonGasUsageReportDO> selectListByUserInfoIdsAndHeatingSeason(
            Collection<Long> carbonUserInfoIds, String heatingSeason) {
        return selectList(new LambdaQueryWrapperX<CarbonGasUsageReportDO>()
                .in(CarbonGasUsageReportDO::getCarbonUserInfoId, carbonUserInfoIds)
                .eq(CarbonGasUsageReportDO::getHeatingSeason, heatingSeason)
                .orderByAsc(CarbonGasUsageReportDO::getId));
    }

    /**
     * 查询指定取暖季有数据的用户ID集合（去重）
     */
    default List<Long> selectDistinctUserIdsByHeatingSeason(String heatingSeason) {
        return selectObjs(new LambdaQueryWrapperX<CarbonGasUsageReportDO>()
                .select(CarbonGasUsageReportDO::getCarbonUserInfoId)
                .eq(CarbonGasUsageReportDO::getHeatingSeason, heatingSeason)
                .groupBy(CarbonGasUsageReportDO::getCarbonUserInfoId));
    }

}
