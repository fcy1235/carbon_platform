package cn.iocoder.power.module.carbon.dal.mysql;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.power.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.power.module.carbon.controller.admin.maintenance.vo.CarbonGasCoordinatorPageReqVO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonGasCoordinatorDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper
public interface CarbonGasCoordinatorMapper extends BaseMapperX<CarbonGasCoordinatorDO> {

    default PageResult<CarbonGasCoordinatorDO> selectPage(CarbonGasCoordinatorPageReqVO pageReqVO) {
        return selectPage(pageReqVO, buildQueryWrapper(pageReqVO));
    }

    default List<CarbonGasCoordinatorDO> selectList(CarbonGasCoordinatorPageReqVO pageReqVO) {
        return selectList(buildQueryWrapper(pageReqVO));
    }

    private LambdaQueryWrapperX<CarbonGasCoordinatorDO> buildQueryWrapper(CarbonGasCoordinatorPageReqVO pageReqVO) {
        LambdaQueryWrapperX<CarbonGasCoordinatorDO> wrapper = new LambdaQueryWrapperX<CarbonGasCoordinatorDO>()
                .inIfPresent(CarbonGasCoordinatorDO::getId, pageReqVO.getIds())
                .likeIfPresent(CarbonGasCoordinatorDO::getName, pageReqVO.getName())
                .likeIfPresent(CarbonGasCoordinatorDO::getPhone, pageReqVO.getPhone())
                .eqIfPresent(CarbonGasCoordinatorDO::getEnterpriseId, pageReqVO.getEnterpriseId())
                .eqIfPresent(CarbonGasCoordinatorDO::getStaffStatus, pageReqVO.getStaffStatus())
                .eqIfPresent(CarbonGasCoordinatorDO::getIsVillageCommitteeMember, pageReqVO.getIsVillageCommitteeMember());
        // 区域代码匹配：取路径最后一级编码，LIKE 前缀匹配 area JSON 字符串
        if (StrUtil.isNotBlank(pageReqVO.getAreaCode())) {
            String lastCode = pageReqVO.getAreaCode().split(",")[pageReqVO.getAreaCode().split(",").length - 1];
            wrapper.like(CarbonGasCoordinatorDO::getArea, lastCode);
        }
        wrapper.orderByDesc(CarbonGasCoordinatorDO::getId);
        return wrapper;
    }

    default List<CarbonGasCoordinatorDO> selectByEnterpriseId(Long enterpriseId) {
        return selectList(new LambdaQueryWrapperX<CarbonGasCoordinatorDO>()
                .eq(CarbonGasCoordinatorDO::getEnterpriseId, enterpriseId));
    }

    default List<CarbonGasCoordinatorDO> selectByEnterpriseIds(Collection<Long> enterpriseIds) {
        return selectList(new LambdaQueryWrapperX<CarbonGasCoordinatorDO>()
                .in(CarbonGasCoordinatorDO::getEnterpriseId, enterpriseIds));
    }
}
