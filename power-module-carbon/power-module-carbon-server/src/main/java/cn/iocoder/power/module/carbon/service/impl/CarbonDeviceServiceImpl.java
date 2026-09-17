package cn.iocoder.power.module.carbon.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.common.util.collection.CollectionUtils;
import cn.iocoder.power.module.carbon.controller.admin.device.vo.CarbonDevicePageReqVO;
import cn.iocoder.power.module.carbon.controller.admin.device.vo.CarbonDeviceRespVO;
import cn.iocoder.power.module.carbon.controller.admin.device.vo.CarbonDeviceSaveReqVO;
import cn.iocoder.power.module.carbon.controller.admin.device.vo.CarbonDeviceSimpleRespVO;
import cn.iocoder.power.module.carbon.controller.admin.device.vo.CarbonDeviceStatusUpdateReqVO;
import cn.iocoder.power.module.carbon.controller.admin.maintenance.vo.CarbonMaintenanceEnterpriseRespVO;
import cn.iocoder.power.module.carbon.convert.CarbonDeviceConvert;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonDeviceDO;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonDeviceMapper;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonDeviceWithUserDTO;
import cn.iocoder.power.module.carbon.service.CarbonDeviceService;
import cn.iocoder.power.module.carbon.service.CarbonMaintenanceEnterpriseService;
import cn.iocoder.power.module.carbon.util.CarbonUserInfoHelper;
import cn.iocoder.power.module.carbon.util.GenerateCode;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.power.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.power.module.carbon.enums.ErrorCodeConstants.DEVICE_NOT_EXISTS;

@Service("carbonDeviceService")
@Validated
@Slf4j
public class CarbonDeviceServiceImpl implements CarbonDeviceService {

    /**
     * 设备编码兜底前缀（前端未传设备类型对应前缀时使用）
     */
    private static final String DEFAULT_DEVICE_CODE_PREFIX = "DEV";

    @Resource
    private CarbonDeviceMapper deviceMapper;

    @Resource
    private CarbonUserInfoHelper carbonUserInfoHelper;

    @Resource
    private CarbonMaintenanceEnterpriseService maintenanceEnterpriseService;

    @Override
    public Long createDevice(CarbonDeviceSaveReqVO createReqVO) {
        CarbonDeviceDO device = CarbonDeviceConvert.INSTANCE.convert(createReqVO);
        // 设备编码规则：（设备类型前缀）-6位序号，如 GM-000001。
        // 优先使用前端按设备类型预取的编码；为空或已被占用（并发抢号）时按前缀重新生成，保证唯一。
        String code = device.getDeviceCode();
        if (StrUtil.isBlank(code)) {
            device.setDeviceCode(generateCode(DEFAULT_DEVICE_CODE_PREFIX));
        } else if (deviceMapper.selectByDeviceCode(code) != null) {
            String prefix = code.contains("-") ? code.substring(0, code.indexOf('-')) : DEFAULT_DEVICE_CODE_PREFIX;
            device.setDeviceCode(generateCode(prefix));
        }
        deviceMapper.insert(device);
        return device.getId();
    }

    @Override
    public void updateDevice(CarbonDeviceSaveReqVO updateReqVO) {
        validateDeviceExists(updateReqVO.getId());
        CarbonDeviceDO updateObj = CarbonDeviceConvert.INSTANCE.convert(updateReqVO);
        deviceMapper.updateById(updateObj);
    }

    @Override
    public void deleteDevice(Long id) {
        validateDeviceExists(id);
        deviceMapper.deleteById(id);
    }

    @Override
    public void updateDeviceStatus(CarbonDeviceStatusUpdateReqVO reqVO) {
        validateDeviceExists(reqVO.getId());
        CarbonDeviceDO updateObj = new CarbonDeviceDO();
        updateObj.setId(reqVO.getId());
        updateObj.setStatus(reqVO.getStatus());
        deviceMapper.updateById(updateObj);
    }

    @Override
    public CarbonDeviceRespVO getDevice(Long id) {
        CarbonDeviceDO device = deviceMapper.selectById(id);
        if (device == null) {
            return null;
        }
        CarbonDeviceRespVO respVO = CarbonDeviceConvert.INSTANCE.convert(device);
        // 填充用户信息
        fillUserInfoAndEnterprise(Collections.singletonList(respVO));
        return respVO;
    }
    @Override
    public Map<Long, CarbonDeviceRespVO> getDeviceMap(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyMap();
        }
        List<CarbonDeviceDO> deviceList = deviceMapper.selectByIds(ids);
        if (deviceList == null || deviceList.isEmpty()) {
            return Collections.emptyMap();
        }
        List<CarbonDeviceRespVO> voList = CarbonDeviceConvert.INSTANCE.convertList(deviceList);
        return CollectionUtils.convertMap(voList, CarbonDeviceRespVO::getId);
    }
    @Override
    public PageResult<CarbonDeviceRespVO> getDevicePage(CarbonDevicePageReqVO pageReqVO) {
        // 连表查询：设备 + 用户信息
        PageResult<CarbonDeviceWithUserDTO> pageResult = deviceMapper.selectPageWithUser(pageReqVO);
        if (pageResult.getList().isEmpty()) {
            return PageResult.empty();
        }

        // 转换为 RespVO
        List<CarbonDeviceRespVO> list = pageResult.getList().stream()
                .map(this::convertToRespVO)
                .collect(Collectors.toList());

        // 填充行政区划名称
        carbonUserInfoHelper.fillDivision(list,
                CarbonDeviceRespVO::getProvinceCode,
                CarbonDeviceRespVO::getCityCode,
                CarbonDeviceRespVO::getDistrictCode,
                CarbonDeviceRespVO::setDivision);

        // 填充维保企业信息
        fillEnterpriseInfo(list);

        return new PageResult<>(list, pageResult.getTotal());
    }

    @Override
    public List<CarbonDeviceRespVO> getDeviceList(CarbonDevicePageReqVO pageReqVO) {
        // 连表查询：设备 + 用户信息
        List<CarbonDeviceWithUserDTO> dtoList = deviceMapper.selectListWithUser(pageReqVO);
        if (dtoList.isEmpty()) {
            return Collections.emptyList();
        }

        // 转换为 RespVO
        List<CarbonDeviceRespVO> list = dtoList.stream()
                .map(this::convertToRespVO)
                .collect(Collectors.toList());

        // 填充行政区划名称
        carbonUserInfoHelper.fillDivision(list,
                CarbonDeviceRespVO::getProvinceCode,
                CarbonDeviceRespVO::getCityCode,
                CarbonDeviceRespVO::getDistrictCode,
                CarbonDeviceRespVO::setDivision);

        // 填充维保企业信息
        fillEnterpriseInfo(list);

        return list;
    }
    @Override
    public List<CarbonDeviceSimpleRespVO> getDeviceSimpleList(CarbonDevicePageReqVO pageReqVO) {
        List<CarbonDeviceDO> deviceList = deviceMapper.selectSimpleList(pageReqVO);
        if (deviceList.isEmpty()) {
            return Collections.emptyList();
        }
        return CarbonDeviceConvert.INSTANCE.convertSimpleList(deviceList);
    }
    @Override
    public String generateCode(String prefix) {
        // 编码规则：（前缀）-6位序号，如 GM-000001；基于库内该前缀最大编码 +1
        CarbonDeviceDO latest = deviceMapper.selectLatestByCodePrefix(prefix);
        String lastCode = latest != null ? latest.getDeviceCode() : null;
        return GenerateCode.generateSeqCode(prefix, lastCode);
    }

    @VisibleForTesting
    void validateDeviceExists(Long id) {
        if (id == null) {
            return;
        }
        CarbonDeviceDO device = deviceMapper.selectById(id);
        if (device == null) {
            throw exception(DEVICE_NOT_EXISTS);
        }
    }

    /**
     * 将联表查询结果转换为 RespVO
     */
    private CarbonDeviceRespVO convertToRespVO(CarbonDeviceWithUserDTO dto) {
        CarbonDeviceRespVO respVO = new CarbonDeviceRespVO();
        // 设备信息
        respVO.setId(dto.getId());
        respVO.setDeviceCode(dto.getDeviceCode());
        respVO.setCarbonUserInfoId(dto.getCarbonUserInfoId());
        respVO.setDeviceName(dto.getDeviceName());
        respVO.setDeviceType(dto.getDeviceType());
        respVO.setManufacturer(dto.getManufacturer());
        respVO.setProductionDate(dto.getProductionDate());
        respVO.setServiceLife(dto.getServiceLife());
        respVO.setInstallDate(dto.getInstallDate());
        respVO.setOperationDate(dto.getOperationDate());
        respVO.setStopDate(dto.getStopDate());
        respVO.setStatus(dto.getStatus());
        respVO.setGasEnterpriseId(dto.getGasEnterpriseId());
        respVO.setWallMountedStoveInstallYear(dto.getWallMountedStoveInstallYear());
        respVO.setGasMeterType(dto.getGasMeterType());
        respVO.setSafetyDeviceStatus(dto.getSafetyDeviceStatus());
        respVO.setDeviceBrandModel(dto.getDeviceBrandModel());
        respVO.setRemark(dto.getRemark());
        respVO.setCreateTime(dto.getCreateTime());
        // 用户信息
        respVO.setUsername(dto.getUsername());
        respVO.setIdCard(dto.getIdCard());
        respVO.setPhone(dto.getPhone());
        respVO.setAddress(dto.getAddress());
        respVO.setReformType(dto.getReformType());
        respVO.setReformMode(dto.getReformMode());
        respVO.setTownName(dto.getTownCode() != null ? String.valueOf(dto.getTownCode()) : null);
        respVO.setVillageName(dto.getVillageCode() != null ? String.valueOf(dto.getVillageCode()) : null);
        respVO.setGasUserCode(dto.getGasUserCode());
        respVO.setGasId(dto.getGasId());
        respVO.setElectricityId(dto.getElectricityId());
        // 保存省市区编码（用于后续填充行政区划名称）
        respVO.setProvinceCode(dto.getProvinceCode());
        respVO.setCityCode(dto.getCityCode());
        respVO.setDistrictCode(dto.getDistrictCode());
        return respVO;
    }

    /**
     * 填充维保企业信息
     */
    private void fillEnterpriseInfo(List<CarbonDeviceRespVO> list) {
        Set<Long> enterpriseIds = CollectionUtils.convertSet(list, CarbonDeviceRespVO::getGasEnterpriseId);
        enterpriseIds.remove(null);
        if (!enterpriseIds.isEmpty()) {
            Map<Long, CarbonMaintenanceEnterpriseRespVO> enterpriseMap = enterpriseIds.stream()
                    .map(id -> maintenanceEnterpriseService.getEnterprise(id))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(CarbonMaintenanceEnterpriseRespVO::getId,
                            vo -> vo, (a, b) -> a));
            list.forEach(item -> {
                if (item.getGasEnterpriseId() != null) {
                    CarbonMaintenanceEnterpriseRespVO enterprise = enterpriseMap.get(item.getGasEnterpriseId());
                    if (enterprise != null) {
                        item.setEnterpriseCreditCode(enterprise.getUnifiedSocialCreditCode());
                        item.setGasEnterpriseName(enterprise.getEnterpriseName());
                    }
                }
            });
        }
    }

    /**
     * 填充用户关联信息和维保企业信息（保留用于单个查询）
     */
    private void fillUserInfoAndEnterprise(List<CarbonDeviceRespVO> list) {
        // 填充维保企业信用代码和供气企业名称
        fillEnterpriseInfo(list);
    }
}
