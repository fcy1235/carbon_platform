package cn.iocoder.power.module.carbon.dal.mysql;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.power.module.carbon.controller.admin.report.vo.CarbonElectricityUsageReportDataPageReqVO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonElectricityUsageReportDataDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonUserInfoDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CarbonElectricityUsageReportDataMapper extends BaseMapperX<CarbonElectricityUsageReportDataDO> {

    default PageResult<CarbonElectricityUsageReportDataRespDTO> selectPageWithUser(CarbonElectricityUsageReportDataPageReqVO reqVO) {
        MPJLambdaWrapper<CarbonElectricityUsageReportDataDO> wrapper = buildWrapper(reqVO);
        Page<CarbonElectricityUsageReportDataRespDTO> page = new Page<>(reqVO.getPageNo(), reqVO.getPageSize());
        page = selectJoinPage(page, CarbonElectricityUsageReportDataRespDTO.class, wrapper);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    default List<CarbonElectricityUsageReportDataRespDTO> selectListWithUser(CarbonElectricityUsageReportDataPageReqVO reqVO) {
        MPJLambdaWrapper<CarbonElectricityUsageReportDataDO> wrapper = buildWrapper(reqVO);
        return selectJoinList(CarbonElectricityUsageReportDataRespDTO.class, wrapper);
    }

    /**
     * 按用户ID和采暖季查询用电量报表记录，用于采暖季用电量计算
     */
    default List<CarbonElectricityUsageReportDataDO> selectListByUserInfoIdAndHeatingSeason(
            Long carbonUserInfoId, String heatingSeason) {
        return selectList(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CarbonElectricityUsageReportDataDO>()
                .eq(CarbonElectricityUsageReportDataDO::getCarbonUserInfoId, carbonUserInfoId)
                .eq(CarbonElectricityUsageReportDataDO::getHeatingSeason, heatingSeason)
                .orderByAsc(CarbonElectricityUsageReportDataDO::getId));
    }

    /**
     * 批量按用户ID集合和采暖季查询用电量报表记录，用于采暖季用电量批量计算
     */
    default List<CarbonElectricityUsageReportDataDO> selectListByUserInfoIdsAndHeatingSeason(
            java.util.Collection<Long> carbonUserInfoIds, String heatingSeason) {
        return selectList(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CarbonElectricityUsageReportDataDO>()
                .in(CarbonElectricityUsageReportDataDO::getCarbonUserInfoId, carbonUserInfoIds)
                .eq(CarbonElectricityUsageReportDataDO::getHeatingSeason, heatingSeason)
                .orderByAsc(CarbonElectricityUsageReportDataDO::getId));
    }

    /**
     * 查询指定取暖季有数据的用户ID集合（去重）
     */
    default List<Long> selectDistinctUserIdsByHeatingSeason(String heatingSeason) {
        return selectObjs(new LambdaQueryWrapper<CarbonElectricityUsageReportDataDO>()
                .select(CarbonElectricityUsageReportDataDO::getCarbonUserInfoId)
                .eq(CarbonElectricityUsageReportDataDO::getHeatingSeason, heatingSeason)
                .groupBy(CarbonElectricityUsageReportDataDO::getCarbonUserInfoId));
    }

    private static MPJLambdaWrapper<CarbonElectricityUsageReportDataDO> buildWrapper(CarbonElectricityUsageReportDataPageReqVO reqVO) {
        MPJLambdaWrapper<CarbonElectricityUsageReportDataDO> wrapper = new MPJLambdaWrapper<CarbonElectricityUsageReportDataDO>()
                // 电力数据字段
                .select(CarbonElectricityUsageReportDataDO::getId,
                        CarbonElectricityUsageReportDataDO::getHeatingSeason,
                        CarbonElectricityUsageReportDataDO::getStartReading,
                        CarbonElectricityUsageReportDataDO::getEndReading,
                        CarbonElectricityUsageReportDataDO::getTotalUsage,
                        CarbonElectricityUsageReportDataDO::getRemark)
                // 用户字段
                .select(CarbonUserInfoDO::getUsername,
                        CarbonUserInfoDO::getIdCard,
                        CarbonUserInfoDO::getElectricityId)
                // LEFT JOIN carbon_user_info
                .leftJoin(CarbonUserInfoDO.class, CarbonUserInfoDO::getId,
                        CarbonElectricityUsageReportDataDO::getCarbonUserInfoId)
                // 采暖季精确匹配
                .eq(StrUtil.isNotBlank(reqVO.getHeatingSeason()),
                        CarbonElectricityUsageReportDataDO::getHeatingSeason, reqVO.getHeatingSeason())
                // 电表号模糊（用户信息表字段）
                .like(StrUtil.isNotBlank(reqVO.getElectricityId()),
                        CarbonUserInfoDO::getElectricityId, reqVO.getElectricityId())
                .orderByDesc(CarbonElectricityUsageReportDataDO::getId);

        // 用户信息（身份证/姓名）模糊查询
        if (StrUtil.isNotBlank(reqVO.getKeyword())) {
            wrapper.and(w -> w.like(CarbonUserInfoDO::getUsername, reqVO.getKeyword())
                    .or()
                    .like(CarbonUserInfoDO::getIdCard, reqVO.getKeyword()));
        }

        return wrapper;
    }

    @lombok.Data
    class CarbonElectricityUsageReportDataRespDTO {
        private Long id;
        private String heatingSeason;
        private java.math.BigDecimal startReading;
        private java.math.BigDecimal endReading;
        private java.math.BigDecimal totalUsage;
        private String remark;
        // 用户信息
        private String username;
        private String idCard;
        private String electricityId;
    }

}
