package cn.iocoder.power.module.carbon.dal.mysql;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.power.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.power.module.carbon.controller.admin.ledger.vo.CarbonLedgerImportDetailPageReqVO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonLedgerImportDetailDO;
import cn.iocoder.power.module.carbon.enums.LedgerImportDetailStatusEnum;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CarbonLedgerImportDetailMapper extends BaseMapperX<CarbonLedgerImportDetailDO> {

    default PageResult<CarbonLedgerImportDetailDO> selectPage(CarbonLedgerImportDetailPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CarbonLedgerImportDetailDO>()
                .eqIfPresent(CarbonLedgerImportDetailDO::getTaskId, reqVO.getTaskId())
                .eqIfPresent(CarbonLedgerImportDetailDO::getImportStatus, reqVO.getImportStatus())
                .orderByDesc(CarbonLedgerImportDetailDO::getId));
    }

    default List<CarbonLedgerImportDetailDO> selectListByTaskId(Long taskId) {
        return selectList(new LambdaQueryWrapperX<CarbonLedgerImportDetailDO>()
                .eq(CarbonLedgerImportDetailDO::getTaskId, taskId)
                .orderByDesc(CarbonLedgerImportDetailDO::getId));
    }

    default List<CarbonLedgerImportDetailDO> selectFailListByTaskId(Long taskId) {
        return selectList(new LambdaQueryWrapperX<CarbonLedgerImportDetailDO>()
                .eq(CarbonLedgerImportDetailDO::getTaskId, taskId)
                .eq(CarbonLedgerImportDetailDO::getImportStatus, LedgerImportDetailStatusEnum.FAIL.getType())
                .orderByDesc(CarbonLedgerImportDetailDO::getId));
    }

    default void deleteByTaskId(Long taskId) {
        delete(new LambdaQueryWrapperX<CarbonLedgerImportDetailDO>()
                .eq(CarbonLedgerImportDetailDO::getTaskId, taskId));
    }
}
