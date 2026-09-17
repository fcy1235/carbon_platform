package cn.iocoder.power.module.carbon.dal.mysql;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.power.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.power.module.carbon.controller.admin.maintenance.vo.CarbonMaintenanceEnterprisePageReqVO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonMaintenanceEnterpriseDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CarbonMaintenanceEnterpriseMapper extends BaseMapperX<CarbonMaintenanceEnterpriseDO> {

    default PageResult<CarbonMaintenanceEnterpriseDO> selectPage(CarbonMaintenanceEnterprisePageReqVO pageReqVO) {


        return selectPage(pageReqVO, new LambdaQueryWrapperX<CarbonMaintenanceEnterpriseDO>()
                .likeIfPresent(CarbonMaintenanceEnterpriseDO::getEnterpriseName, pageReqVO.getEnterpriseName())
                .eqIfPresent(CarbonMaintenanceEnterpriseDO::getProvinceCode, pageReqVO.getProvinceCode())
                .eqIfPresent(CarbonMaintenanceEnterpriseDO::getCityCode, pageReqVO.getCityCode())
                .eqIfPresent(CarbonMaintenanceEnterpriseDO::getCountyCode, pageReqVO.getCountyCode())
                .eqIfPresent(CarbonMaintenanceEnterpriseDO::getServiceType, pageReqVO.getServiceType())
                .eqIfPresent(CarbonMaintenanceEnterpriseDO::getServiceStatus, pageReqVO.getServiceStatus())
                .eqIfPresent(CarbonMaintenanceEnterpriseDO::getUnifiedSocialCreditCode, pageReqVO.getUnifiedSocialCreditCode())
                .orderByDesc(CarbonMaintenanceEnterpriseDO::getId));
    }

    default List<CarbonMaintenanceEnterpriseDO> selectList(CarbonMaintenanceEnterprisePageReqVO pageReqVO) {
        return selectList(new LambdaQueryWrapperX<CarbonMaintenanceEnterpriseDO>()
                .inIfPresent(CarbonMaintenanceEnterpriseDO::getId, pageReqVO.getIds())
                .likeIfPresent(CarbonMaintenanceEnterpriseDO::getEnterpriseName, pageReqVO.getEnterpriseName())
                .eqIfPresent(CarbonMaintenanceEnterpriseDO::getProvinceCode, pageReqVO.getProvinceCode())
                .eqIfPresent(CarbonMaintenanceEnterpriseDO::getCityCode, pageReqVO.getCityCode())
                .eqIfPresent(CarbonMaintenanceEnterpriseDO::getCountyCode, pageReqVO.getCountyCode())
                .eqIfPresent(CarbonMaintenanceEnterpriseDO::getServiceType, pageReqVO.getServiceType())
                .eqIfPresent(CarbonMaintenanceEnterpriseDO::getServiceStatus, pageReqVO.getServiceStatus())
                .eqIfPresent(CarbonMaintenanceEnterpriseDO::getUnifiedSocialCreditCode, pageReqVO.getUnifiedSocialCreditCode())
                .orderByDesc(CarbonMaintenanceEnterpriseDO::getId));
    }

    default CarbonMaintenanceEnterpriseDO selectByEnterpriseName(String enterpriseName) {
        return selectOne(new LambdaQueryWrapperX<CarbonMaintenanceEnterpriseDO>()
                .eq(CarbonMaintenanceEnterpriseDO::getEnterpriseName, enterpriseName));
    }

    default CarbonMaintenanceEnterpriseDO selectByUnifiedSocialCreditCode(String unifiedSocialCreditCode) {
        return selectOne(new LambdaQueryWrapperX<CarbonMaintenanceEnterpriseDO>()
                .eq(CarbonMaintenanceEnterpriseDO::getUnifiedSocialCreditCode, unifiedSocialCreditCode));
    }
}
