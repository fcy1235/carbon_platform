package cn.iocoder.power.module.carbon.dal.mysql;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.power.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.power.module.carbon.controller.admin.maintenance.vo.CarbonGasSafetyOfficerPageReqVO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonGasSafetyOfficerDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper
public interface CarbonGasSafetyOfficerMapper extends BaseMapperX<CarbonGasSafetyOfficerDO> {

    default PageResult<CarbonGasSafetyOfficerDO> selectPage(CarbonGasSafetyOfficerPageReqVO pageReqVO) {
        return selectPage(pageReqVO, buildQueryWrapper(pageReqVO));
    }

    default List<CarbonGasSafetyOfficerDO> selectList(CarbonGasSafetyOfficerPageReqVO pageReqVO) {
        return selectList(buildQueryWrapper(pageReqVO));
    }

    private LambdaQueryWrapperX<CarbonGasSafetyOfficerDO> buildQueryWrapper(CarbonGasSafetyOfficerPageReqVO pageReqVO) {
        LambdaQueryWrapperX<CarbonGasSafetyOfficerDO> wrapper = new LambdaQueryWrapperX<CarbonGasSafetyOfficerDO>()
                .inIfPresent(CarbonGasSafetyOfficerDO::getId, pageReqVO.getIds())
                .likeIfPresent(CarbonGasSafetyOfficerDO::getName, pageReqVO.getName())
                .likeIfPresent(CarbonGasSafetyOfficerDO::getQualificationNo, pageReqVO.getQualificationNo())
                .eqIfPresent(CarbonGasSafetyOfficerDO::getEnterpriseId, pageReqVO.getEnterpriseId())
                .eqIfPresent(CarbonGasSafetyOfficerDO::getStaffStatus, pageReqVO.getStaffStatus());
        // 区域代码匹配：取路径最后一级编码，LIKE 前缀匹配 area JSON 字符串
        if (StrUtil.isNotBlank(pageReqVO.getAreaCode())) {
            String lastCode = pageReqVO.getAreaCode().split(",")[pageReqVO.getAreaCode().split(",").length - 1];
//            String lastCode = areaCode.contains(",")
//                    ? StrUtil.subAfter(areaCode, ",", true)
//                    : areaCode;
            wrapper.and(w -> w
                    .like(CarbonGasSafetyOfficerDO::getArea1, lastCode)
                    .or().like(CarbonGasSafetyOfficerDO::getArea2, lastCode)
                    .or().like(CarbonGasSafetyOfficerDO::getArea3, lastCode)
                    .or().like(CarbonGasSafetyOfficerDO::getArea4, lastCode)
                    .or().like(CarbonGasSafetyOfficerDO::getArea5, lastCode));
        }
        wrapper.orderByDesc(CarbonGasSafetyOfficerDO::getId);
        return wrapper;
    }

    default List<CarbonGasSafetyOfficerDO> selectByEnterpriseId(Long enterpriseId) {
        return selectList(new LambdaQueryWrapperX<CarbonGasSafetyOfficerDO>()
                .eq(CarbonGasSafetyOfficerDO::getEnterpriseId, enterpriseId));
    }

    default List<CarbonGasSafetyOfficerDO> selectByEnterpriseIds(Collection<Long> enterpriseIds) {
        return selectList(new LambdaQueryWrapperX<CarbonGasSafetyOfficerDO>()
                .in(CarbonGasSafetyOfficerDO::getEnterpriseId, enterpriseIds));
    }
}
