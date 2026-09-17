package cn.iocoder.power.module.carbon.convert;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.module.carbon.controller.admin.ledger.vo.*;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonLedgerImportDetailDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonLedgerImportTaskDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonLedgerImportTaskWithFileDO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CarbonLedgerImportConvert {

    CarbonLedgerImportConvert INSTANCE = Mappers.getMapper(CarbonLedgerImportConvert.class);

    CarbonLedgerImportTaskRespVO convert(CarbonLedgerImportTaskDO bean);

    CarbonLedgerImportTaskRespVO convertFromWithFile(CarbonLedgerImportTaskWithFileDO bean);

    List<CarbonLedgerImportTaskRespVO> convertTaskList(List<CarbonLedgerImportTaskDO> list);

    List<CarbonLedgerImportTaskRespVO> convertTaskListFromWithFile(List<CarbonLedgerImportTaskWithFileDO> list);

    PageResult<CarbonLedgerImportTaskRespVO> convertTaskPage(PageResult<CarbonLedgerImportTaskDO> page);

    PageResult<CarbonLedgerImportTaskRespVO> convertTaskPageFromWithFile(PageResult<CarbonLedgerImportTaskWithFileDO> page);

    CarbonLedgerImportDetailDO convertDetail(CarbonLedgerImportDataVO bean);

    CarbonLedgerImportDetailDO convertDetail(CarbonLedgerRevokeImportDataVO bean);

    CarbonLedgerImportDetailDO convertDetail(CarbonLedgerChangeImportDataVO bean);

    CarbonLedgerImportDetailRespVO convertDetailResp(CarbonLedgerImportDetailDO bean);

    List<CarbonLedgerImportDetailRespVO> convertDetailList(List<CarbonLedgerImportDetailDO> list);

    PageResult<CarbonLedgerImportDetailRespVO> convertDetailPage(PageResult<CarbonLedgerImportDetailDO> page);

    CarbonLedgerImportDetailExcelVO convertDetailExcel(CarbonLedgerImportDetailRespVO bean);

    List<CarbonLedgerImportDetailExcelVO> convertDetailExcelList(List<CarbonLedgerImportDetailRespVO> list);
}
