package cn.iocoder.power.module.carbon.service;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.module.carbon.controller.admin.ledger.vo.CarbonLedgerImportDetailPageReqVO;
import cn.iocoder.power.module.carbon.controller.admin.ledger.vo.CarbonLedgerImportDetailRespVO;
import cn.iocoder.power.module.carbon.controller.admin.ledger.vo.CarbonLedgerImportTaskPageReqVO;
import cn.iocoder.power.module.carbon.controller.admin.ledger.vo.CarbonLedgerImportTaskRespVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CarbonLedgerImportService {

    Long createImportTask(String uploadType, MultipartFile file, String cityCode, String districtCode) throws Exception;

    CarbonLedgerImportTaskRespVO getImportTask(Long id);

    PageResult<CarbonLedgerImportTaskRespVO> getImportTaskPage(CarbonLedgerImportTaskPageReqVO pageReqVO);

    void deleteImportTask(Long id);

    void executeImport(Long taskId) throws Exception;

    PageResult<CarbonLedgerImportDetailRespVO> getImportDetailPage(CarbonLedgerImportDetailPageReqVO pageReqVO);

    List<CarbonLedgerImportDetailRespVO> getImportDetailList(Long taskId);

    List<CarbonLedgerImportDetailRespVO> getFailDetailList(Long taskId);
}
