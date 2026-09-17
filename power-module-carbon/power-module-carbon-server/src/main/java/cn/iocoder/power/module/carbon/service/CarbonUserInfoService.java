package cn.iocoder.power.module.carbon.service;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonUserInfoListReqVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonUserInfoPageReqVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonUserInfoImportVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonUserInfoSaveReqVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonUserInfoRespVO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonUserInfoDO;
import jakarta.validation.Valid;

import java.util.List;

public interface CarbonUserInfoService {

    Long createUserInfo(@Valid CarbonUserInfoSaveReqVO createReqVO);

    void updateUserInfo(@Valid CarbonUserInfoSaveReqVO updateReqVO);

    void deleteUserInfo(Long id);

    void deleteUserInfoList(List<Long> ids);

    CarbonUserInfoRespVO getUserInfo(Long id);

    PageResult<CarbonUserInfoRespVO> getUserInfoPage(CarbonUserInfoPageReqVO pageReqVO);

    List<CarbonUserInfoRespVO> getUserInfoList(CarbonUserInfoListReqVO listReqVO);

    void importUserInfoList(List<CarbonUserInfoImportVO> list);
}
