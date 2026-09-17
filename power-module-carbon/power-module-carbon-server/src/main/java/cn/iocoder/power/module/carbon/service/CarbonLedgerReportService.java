package cn.iocoder.power.module.carbon.service;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.module.carbon.controller.admin.ledger.vo.CarbonLedgerReportPageReqVO;
import cn.iocoder.power.module.carbon.controller.admin.ledger.vo.CarbonLedgerReportRespVO;
import cn.iocoder.power.module.carbon.controller.admin.ledger.vo.CarbonLedgerReportReviewReqVO;
import cn.iocoder.power.module.carbon.controller.admin.ledger.vo.CarbonLedgerReportSaveReqVO;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

public interface CarbonLedgerReportService {

    Long createLedgerReport(@Valid CarbonLedgerReportSaveReqVO createReqVO, MultipartFile stampedReportFile) throws Exception;

    void updateLedgerReport(@Valid CarbonLedgerReportSaveReqVO updateReqVO, MultipartFile stampedReportFile) throws Exception;

    void deleteLedgerReport(Long id);

    CarbonLedgerReportRespVO getLedgerReport(Long id);

    PageResult<CarbonLedgerReportRespVO> getLedgerReportPage(CarbonLedgerReportPageReqVO pageReqVO);

    void submitLedgerReport(Long id);

    void reviewLedgerReport(@Valid CarbonLedgerReportReviewReqVO reviewReqVO);
}
