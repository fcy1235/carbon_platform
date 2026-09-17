package cn.iocoder.power.module.system.service.logger;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.common.util.object.BeanUtils;
import cn.iocoder.power.framework.common.biz.system.logger.dto.OperateLogCreateReqDTO;
import cn.iocoder.power.module.system.api.logger.dto.OperateLogPageReqDTO;
import cn.iocoder.power.module.system.controller.admin.logger.vo.operatelog.OperateLogPageReqVO;
import cn.iocoder.power.module.system.dal.dataobject.logger.OperateLogDO;
import cn.iocoder.power.module.system.dal.mysql.logger.OperateLogMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * 操作日志 Service 实现类
 *
 * 
 */
@Service
@Validated
@Slf4j
public class OperateLogServiceImpl implements OperateLogService {

    @Resource
    private OperateLogMapper operateLogMapper;

    @Override
    public void createOperateLog(OperateLogCreateReqDTO createReqDTO) {
        OperateLogDO log = BeanUtils.toBean(createReqDTO, OperateLogDO.class);
        operateLogMapper.insert(log);
    }

    @Override
    public OperateLogDO getOperateLog(Long id) {
        return operateLogMapper.selectById(id);
    }

    @Override
    public PageResult<OperateLogDO> getOperateLogPage(OperateLogPageReqVO pageReqVO) {
        return operateLogMapper.selectPage(pageReqVO);
    }

    @Override
    public PageResult<OperateLogDO> getOperateLogPage(OperateLogPageReqDTO pageReqDTO) {
        return operateLogMapper.selectPage(pageReqDTO);
    }

}
