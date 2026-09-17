package cn.iocoder.power.module.carbon.service.impl;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonContactPageReqVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonContactRespVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonContactSaveReqVO;
import cn.iocoder.power.module.carbon.convert.CarbonContactConvert;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonContactDO;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonContactMapper;
import cn.iocoder.power.module.carbon.service.CarbonContactService;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static cn.iocoder.power.module.carbon.dal.redis.RedisKeyConstants.CARBON_CONTACT;

import java.util.List;

import static cn.iocoder.power.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.power.module.carbon.enums.ErrorCodeConstants.*;

@Service
@Validated
@Slf4j
public class CarbonContactServiceImpl implements CarbonContactService {

    @Resource
    private CarbonContactMapper contactMapper;

    @Override
    @CacheEvict(cacheNames = CARBON_CONTACT, allEntries = true)
    public Long createContact(CarbonContactSaveReqVO createReqVO) {
        CarbonContactDO contact = CarbonContactConvert.INSTANCE.convert(createReqVO);
        contactMapper.insert(contact);
        return contact.getId();
    }

    @Override
    @CacheEvict(cacheNames = CARBON_CONTACT, allEntries = true)
    public void updateContact(CarbonContactSaveReqVO updateReqVO) {
        validateContactExists(updateReqVO.getId());
        CarbonContactDO updateObj = CarbonContactConvert.INSTANCE.convert(updateReqVO);
        contactMapper.updateById(updateObj);
    }

    @Override
    @CacheEvict(cacheNames = CARBON_CONTACT, allEntries = true)
    public void deleteContact(Long id) {
        validateContactExists(id);
        contactMapper.deleteById(id);
    }

    @Override
    @CacheEvict(cacheNames = CARBON_CONTACT, allEntries = true)
    public void deleteContactList(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        contactMapper.deleteByIds(ids);
    }

    @Override
    @Cacheable(cacheNames = CARBON_CONTACT, key = "#id")
    public CarbonContactRespVO getContact(Long id) {
        return CarbonContactConvert.INSTANCE.convert(contactMapper.selectById(id));
    }

    @Override
    public PageResult<CarbonContactRespVO> getContactPage(CarbonContactPageReqVO pageReqVO) {
        return CarbonContactConvert.INSTANCE.convertPage(contactMapper.selectPage(pageReqVO));
    }

    @Override
    public List<CarbonContactRespVO> getContactList(CarbonContactPageReqVO reqVO) {
        return CarbonContactConvert.INSTANCE.convertList(contactMapper.selectList(reqVO));
    }

    @VisibleForTesting
    void validateContactExists(Long id) {
        if (id == null) {
            return;
        }
        CarbonContactDO contact = contactMapper.selectById(id);
        if (contact == null) {
            throw exception(CONTACT_NOT_EXISTS);
        }
    }

}
