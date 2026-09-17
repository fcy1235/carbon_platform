package cn.iocoder.power.module.carbon.dal.mysql;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.power.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.power.module.carbon.controller.admin.maintenance.vo.CarbonMaintenanceStationPageReqVO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonMaintenanceStationDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper
public interface CarbonMaintenanceStationMapper extends BaseMapperX<CarbonMaintenanceStationDO> {

    default List<CarbonMaintenanceStationDO> selectByEnterpriseId(Long enterpriseId) {
        return selectList(new LambdaQueryWrapperX<CarbonMaintenanceStationDO>()
                .eq(CarbonMaintenanceStationDO::getEnterpriseId, enterpriseId));
    }

    default PageResult<CarbonMaintenanceStationDO> selectPage(CarbonMaintenanceStationPageReqVO pageReqVO) {
        return selectPage(pageReqVO, new LambdaQueryWrapperX<CarbonMaintenanceStationDO>()
                .eqIfPresent(CarbonMaintenanceStationDO::getEnterpriseId, pageReqVO.getEnterpriseId())
                .likeIfPresent(CarbonMaintenanceStationDO::getStationName, pageReqVO.getStationName())
                .eqIfPresent(CarbonMaintenanceStationDO::getServiceType, pageReqVO.getServiceType())
                .eqIfPresent(CarbonMaintenanceStationDO::getBusinessStatus, pageReqVO.getBusinessStatus())
                .orderByDesc(CarbonMaintenanceStationDO::getId));
    }

    default List<CarbonMaintenanceStationDO> selectByEnterpriseIds(Collection<Long> enterpriseIds) {
        return selectList(new LambdaQueryWrapperX<CarbonMaintenanceStationDO>()
                .in(CarbonMaintenanceStationDO::getEnterpriseId, enterpriseIds));
    }

    default List<CarbonMaintenanceStationDO> selectList(CarbonMaintenanceStationPageReqVO pageReqVO) {
        return selectList(new LambdaQueryWrapperX<CarbonMaintenanceStationDO>()
                .inIfPresent(CarbonMaintenanceStationDO::getId, pageReqVO.getIds())
                .eqIfPresent(CarbonMaintenanceStationDO::getEnterpriseId, pageReqVO.getEnterpriseId())
                .likeIfPresent(CarbonMaintenanceStationDO::getStationName, pageReqVO.getStationName())
                .eqIfPresent(CarbonMaintenanceStationDO::getServiceType, pageReqVO.getServiceType())
                .eqIfPresent(CarbonMaintenanceStationDO::getBusinessStatus, pageReqVO.getBusinessStatus())
                .orderByDesc(CarbonMaintenanceStationDO::getId));
    }
}
