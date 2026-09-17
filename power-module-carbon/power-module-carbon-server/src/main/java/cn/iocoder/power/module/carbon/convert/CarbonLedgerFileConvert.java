package cn.iocoder.power.module.carbon.convert;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.module.carbon.controller.admin.ledger.vo.CarbonLedgerFileExcelVO;
import cn.iocoder.power.module.carbon.controller.admin.ledger.vo.CarbonLedgerFilePageReqVO;
import cn.iocoder.power.module.carbon.controller.admin.ledger.vo.CarbonLedgerFileRespVO;
import cn.iocoder.power.module.carbon.controller.admin.ledger.vo.CarbonLedgerFileSaveReqVO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonLedgerFileDO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CarbonLedgerFileConvert {

    CarbonLedgerFileConvert INSTANCE = Mappers.getMapper(CarbonLedgerFileConvert.class);

    CarbonLedgerFileDO convert(CarbonLedgerFileSaveReqVO bean);

    CarbonLedgerFileRespVO convert(CarbonLedgerFileDO bean);

    List<CarbonLedgerFileRespVO> convertList(List<CarbonLedgerFileDO> list);

    PageResult<CarbonLedgerFileRespVO> convertPage(PageResult<CarbonLedgerFileDO> page);

    CarbonLedgerFileExcelVO convertExcel(CarbonLedgerFileRespVO bean);

    List<CarbonLedgerFileExcelVO> convertExcelList(List<CarbonLedgerFileRespVO> list);
}
