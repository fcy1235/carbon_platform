package cn.iocoder.power.module.carbon.convert;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.module.carbon.controller.admin.ledger.vo.*;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonLedgerAuditLogDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonLedgerReportDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonLedgerReportWithFileDO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CarbonLedgerReportConvert {

    CarbonLedgerReportConvert INSTANCE = Mappers.getMapper(CarbonLedgerReportConvert.class);

    CarbonLedgerReportDO convert(CarbonLedgerReportSaveReqVO bean);

    CarbonLedgerReportRespVO convert(CarbonLedgerReportDO bean);

    List<CarbonLedgerReportRespVO> convertList(List<CarbonLedgerReportDO> list);

    PageResult<CarbonLedgerReportRespVO> convertPage(PageResult<CarbonLedgerReportDO> page);

    CarbonLedgerAuditLogRespVO convert(CarbonLedgerAuditLogDO bean);

    List<CarbonLedgerAuditLogRespVO> convertAuditLogList(List<CarbonLedgerAuditLogDO> list);

    /**
     * 联表查询结果转换为 RespVO
     */
    @org.mapstruct.Mapping(source = "fileName", target = "ledgerFileName")
    @org.mapstruct.Mapping(source = "fileUrl", target = "ledgerFileUrl")
    CarbonLedgerReportRespVO convertFromWithFile(CarbonLedgerReportWithFileDO bean);

    /**
     * 联表查询结果列表转换为 RespVO 列表
     */
    @org.mapstruct.Mapping(source = "fileName", target = "ledgerFileName")
    @org.mapstruct.Mapping(source = "fileUrl", target = "ledgerFileUrl")
    List<CarbonLedgerReportRespVO> convertListFromWithFile(List<CarbonLedgerReportWithFileDO> list);

    /**
     * 联表查询分页结果转换为 RespVO 分页结果
     */
    default PageResult<CarbonLedgerReportRespVO> convertPageFromWithFile(PageResult<CarbonLedgerReportWithFileDO> page) {
        return new PageResult<>(convertListFromWithFile(page.getList()), page.getTotal());
    }
}
