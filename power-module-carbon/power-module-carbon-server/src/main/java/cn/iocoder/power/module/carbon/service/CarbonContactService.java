package cn.iocoder.power.module.carbon.service;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonContactPageReqVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonContactSaveReqVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonContactRespVO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonContactDO;
import jakarta.validation.Valid;

import java.util.List;

public interface CarbonContactService {

    Long createContact(@Valid CarbonContactSaveReqVO createReqVO);

    void updateContact(@Valid CarbonContactSaveReqVO updateReqVO);

    void deleteContact(Long id);

    void deleteContactList(List<Long> ids);

    CarbonContactRespVO getContact(Long id);

    PageResult<CarbonContactRespVO> getContactPage(CarbonContactPageReqVO pageReqVO);

    List<CarbonContactRespVO> getContactList(CarbonContactPageReqVO reqVO);
}
