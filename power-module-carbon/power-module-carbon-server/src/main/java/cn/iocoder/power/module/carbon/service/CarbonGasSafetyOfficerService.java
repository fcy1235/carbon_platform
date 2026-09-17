package cn.iocoder.power.module.carbon.service;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.module.carbon.controller.admin.maintenance.vo.CarbonGasSafetyOfficerPageReqVO;
import cn.iocoder.power.module.carbon.controller.admin.maintenance.vo.CarbonGasSafetyOfficerRespVO;
import cn.iocoder.power.module.carbon.controller.admin.maintenance.vo.CarbonGasSafetyOfficerSaveReqVO;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CarbonGasSafetyOfficerService {

    Long createOfficer(@Valid CarbonGasSafetyOfficerSaveReqVO createReqVO,
                       MultipartFile idCardFrontImage) throws Exception;

    void updateOfficer(@Valid CarbonGasSafetyOfficerSaveReqVO updateReqVO,
                       MultipartFile idCardFrontImage) throws Exception;

    void deleteOfficer(Long id);

    CarbonGasSafetyOfficerRespVO getOfficer(Long id);

    PageResult<CarbonGasSafetyOfficerRespVO> getOfficerPage(CarbonGasSafetyOfficerPageReqVO pageReqVO);

    List<CarbonGasSafetyOfficerRespVO> getOfficerList(CarbonGasSafetyOfficerPageReqVO pageReqVO);

    List<CarbonGasSafetyOfficerRespVO> getOfficerListByEnterpriseId(Long enterpriseId);
}
