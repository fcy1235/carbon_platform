package cn.iocoder.power.module.system.service.logger;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.common.util.object.BeanUtils;
import cn.iocoder.power.module.system.api.logger.dto.LoginLogCreateReqDTO;
import cn.iocoder.power.module.system.controller.admin.logger.vo.loginlog.LoginLogPageReqVO;
import cn.iocoder.power.module.system.dal.dataobject.logger.LoginLogDO;
import cn.iocoder.power.module.system.dal.mysql.logger.LoginLogMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;

/**
 * 登录日志 Service 实现
 */
@Service
@Validated
public class LoginLogServiceImpl implements LoginLogService {

    @Resource
    private LoginLogMapper loginLogMapper;

    @Override
    public LoginLogDO getLoginLog(Long id) {
        return loginLogMapper.selectById(id);
    }

    @Override
    public PageResult<LoginLogDO> getLoginLogPage(LoginLogPageReqVO pageReqVO) {
        return loginLogMapper.selectPage(pageReqVO);
    }

    @Override
    public void createLoginLog(LoginLogCreateReqDTO reqDTO) {
        LoginLogDO loginLog = BeanUtils.toBean(reqDTO, LoginLogDO.class);
        loginLogMapper.insert(loginLog);
    }

}
