package cn.iocoder.power.module.carbon.dal.mysql;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.power.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.power.module.carbon.controller.admin.ledger.vo.CarbonLedgerFilePageReqVO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonLedgerFileDO;
import cn.iocoder.power.module.carbon.util.LedgerDataPermissionHelper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CarbonLedgerFileMapper extends BaseMapperX<CarbonLedgerFileDO> {

    default PageResult<CarbonLedgerFileDO> selectPage(CarbonLedgerFilePageReqVO reqVO) {
        LambdaQueryWrapperX<CarbonLedgerFileDO> wrapper = new LambdaQueryWrapperX<CarbonLedgerFileDO>()
                .eqIfPresent(CarbonLedgerFileDO::getCityCode, reqVO.getCityCode())
                .eqIfPresent(CarbonLedgerFileDO::getDistrictCode, reqVO.getDistrictCode())
                .eqIfPresent(CarbonLedgerFileDO::getUploadType, reqVO.getUploadType())
                .eqIfPresent(CarbonLedgerFileDO::getUploadStatus, reqVO.getUploadStatus())
                .eqIfPresent(CarbonLedgerFileDO::getAuditStatus, reqVO.getAuditStatus())
                .betweenIfPresent(CarbonLedgerFileDO::getUploadTime, reqVO.getUploadTime())
                .orderByDesc(CarbonLedgerFileDO::getId);
        applyDataPermission(wrapper);
        return selectPage(reqVO, wrapper);
    }

    default List<CarbonLedgerFileDO> selectList(CarbonLedgerFilePageReqVO reqVO) {
        LambdaQueryWrapperX<CarbonLedgerFileDO> wrapper = new LambdaQueryWrapperX<CarbonLedgerFileDO>()
                .eqIfPresent(CarbonLedgerFileDO::getCityCode, reqVO.getCityCode())
                .eqIfPresent(CarbonLedgerFileDO::getDistrictCode, reqVO.getDistrictCode())
                .eqIfPresent(CarbonLedgerFileDO::getUploadType, reqVO.getUploadType())
                .eqIfPresent(CarbonLedgerFileDO::getUploadStatus, reqVO.getUploadStatus())
                .eqIfPresent(CarbonLedgerFileDO::getAuditStatus, reqVO.getAuditStatus())
                .betweenIfPresent(CarbonLedgerFileDO::getUploadTime, reqVO.getUploadTime())
                .orderByDesc(CarbonLedgerFileDO::getId);
        applyDataPermission(wrapper);
        return selectList(wrapper);
    }

    /**
     * 附加台账模块数据权限过滤条件
     */
    default void applyDataPermission(LambdaQueryWrapperX<CarbonLedgerFileDO> wrapper) {
        LedgerDataPermissionHelper.UserAreaContext context = LedgerDataPermissionHelper.getCurrentUserAreaContext();
        switch (context.getScope()) {
            case DISTRICT -> wrapper.eq(CarbonLedgerFileDO::getDistrictCode, context.getDistrictCode());
            case CITY -> wrapper.eq(CarbonLedgerFileDO::getCityCode, context.getCityCode());
            default -> {
                // 无限制
            }
        }
    }
}
