package cn.iocoder.power.module.carbon.service;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.module.carbon.controller.admin.project.vo.CarbonProjectDocRespVO;
import cn.iocoder.power.module.carbon.controller.admin.project.vo.CarbonProjectPageReqVO;
import cn.iocoder.power.module.carbon.controller.admin.project.vo.CarbonProjectProgressSaveReqVO;
import cn.iocoder.power.module.carbon.controller.admin.project.vo.CarbonProjectRespVO;
import cn.iocoder.power.module.carbon.controller.admin.project.vo.CarbonProjectSaveReqVO;
import cn.iocoder.power.module.carbon.controller.admin.project.vo.CarbonProjectSimpleRespVO;
import cn.iocoder.power.module.carbon.controller.admin.project.vo.CarbonProjectStatusUpdateReqVO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonProjectDocDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonProjectDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonProjectProgressDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonProjectUserDO;
import jakarta.validation.Valid;

import java.util.List;

public interface CarbonProjectService {

    Long createProject(@Valid CarbonProjectSaveReqVO createReqVO);

    void updateProject(@Valid CarbonProjectSaveReqVO updateReqVO);

    void deleteProject(Long id);

    void updateProjectStatus(@Valid CarbonProjectStatusUpdateReqVO reqVO);

    CarbonProjectRespVO getProject(Long id);

    PageResult<CarbonProjectRespVO> getProjectPage(CarbonProjectPageReqVO pageReqVO);

    List<CarbonProjectRespVO> getProjectList(CarbonProjectPageReqVO reqVO);

    /**
     * 获取项目精简列表（项目编号+项目名称）
     */
    List<CarbonProjectSimpleRespVO> getProjectSimpleList();

    List<CarbonProjectUserDO> getProjectUsers(Long projectId);

    List<CarbonProjectDocDO> getProjectDocs(Long projectId);

    List<CarbonProjectProgressDO> getProjectProgressList(Long projectId);

    void deleteProjectDoc(Long projectId, String docName);

    CarbonProjectDocRespVO uploadProjectDoc(Long projectId, org.springframework.web.multipart.MultipartFile file, String docDesc) throws Exception;

    void addProgressRecord(@Valid CarbonProjectProgressSaveReqVO reqVO);
}
