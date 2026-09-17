package cn.iocoder.power.module.carbon.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.module.carbon.controller.admin.device.vo.*;
import cn.iocoder.power.module.carbon.convert.CarbonDeviceDataConvert;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonDeviceDataDO;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonDeviceDataMapper;
import cn.iocoder.power.module.carbon.service.CarbonDeviceDataService;
import cn.iocoder.power.module.system.api.area.AreaApi;
import cn.iocoder.power.module.system.api.area.dto.AreaRespDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.power.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.power.module.carbon.enums.ErrorCodeConstants.DEVICE_DATA_NOT_EXISTS;

@Service
@Validated
@Slf4j
public class CarbonDeviceDataServiceImpl implements CarbonDeviceDataService {

    @Resource
    private CarbonDeviceDataMapper deviceDataMapper;

    @Resource
    private AreaApi areaApi;

    @Override
    public Long createDeviceData(CarbonDeviceDataSaveReqVO createReqVO) {
        CarbonDeviceDataDO deviceData = CarbonDeviceDataConvert.INSTANCE.convert(createReqVO);
        deviceDataMapper.insert(deviceData);
        return deviceData.getId();
    }

    @Override
    public CarbonDeviceDataRespVO getDeviceData(Long id) {
        CarbonDeviceDataDO deviceData = deviceDataMapper.selectById(id);
        if (deviceData == null) {
            throw exception(DEVICE_DATA_NOT_EXISTS);
        }
        return CarbonDeviceDataConvert.INSTANCE.convert(deviceData);
    }

    @Override
    public PageResult<CarbonDeviceDataRespVO> getDeviceDataPage(CarbonDeviceDataPageReqVO pageReqVO) {
        // 1. MPJLambdaWrapper 联表查询（分页 + SQL去重，每设备最新一条）
        PageResult<CarbonDeviceDataMapper.CarbonDeviceDataWithUserDTO> dtoPage =
                deviceDataMapper.selectLatestPageWithUser(pageReqVO, pageReqVO.getPageNo(), pageReqVO.getPageSize());
        if (dtoPage.getList().isEmpty()) {
            return new PageResult<>(Collections.emptyList(), 0L);
        }

        // 2. DTO → RespVO（MapStruct 自动映射）
        List<CarbonDeviceDataRespVO> voList = CarbonDeviceDataConvert.INSTANCE.convertDtoList(dtoPage.getList());

        // 3. 批量查询区县名称，填充 division
        fillDivision(voList, dtoPage.getList());

        return new PageResult<>(voList, dtoPage.getTotal());
    }

    @Override
    public List<CarbonDeviceDataRespVO> getDeviceDataList(CarbonDeviceDataPageReqVO pageReqVO) {
        // 联表查询（不分页，用于导出，SQL已去重）
        List<CarbonDeviceDataMapper.CarbonDeviceDataWithUserDTO> dtoList =
                deviceDataMapper.selectLatestListWithUser(pageReqVO);
        if (dtoList.isEmpty()) {
            return Collections.emptyList();
        }

        List<CarbonDeviceDataRespVO> voList = CarbonDeviceDataConvert.INSTANCE.convertDtoList(dtoList);
        fillDivision(voList, dtoList);
        return voList;
    }

    @Override
    public PageResult<CarbonDeviceDataRespVO> getDeviceDataDetail(String deviceCode) {
        List<CarbonDeviceDataDO> list = deviceDataMapper.selectListByDeviceCode(deviceCode);
        return new PageResult<>(CarbonDeviceDataConvert.INSTANCE.convertList(list), (long) list.size());
    }

    @Override
    public void deleteDeviceData(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        deviceDataMapper.deleteBatchIds(ids);
    }

    /**
     * 批量查询区县名称，填充 division 字段
     */
    private void fillDivision(List<CarbonDeviceDataRespVO> voList,
                              List<CarbonDeviceDataMapper.CarbonDeviceDataWithUserDTO> dtoList) {
        Set<Long> areaIds = new HashSet<>();
        for (CarbonDeviceDataMapper.CarbonDeviceDataWithUserDTO dto : dtoList) {
            if (dto.getProvinceCode() != null) areaIds.add(dto.getProvinceCode());
            if (dto.getCityCode() != null) areaIds.add(dto.getCityCode());
            if (dto.getDistrictCode() != null) areaIds.add(dto.getDistrictCode());
        }
        if (areaIds.isEmpty()) return;

        var areaResult = areaApi.getAreaList(new ArrayList<>(areaIds));
        if (areaResult == null || areaResult.getData() == null) return;

        Map<Long, String> areaMap = areaResult.getData().stream()
                .collect(Collectors.toMap(AreaRespDTO::getId, AreaRespDTO::getName, (a, b) -> a));

        for (int i = 0; i < voList.size(); i++) {
            CarbonDeviceDataMapper.CarbonDeviceDataWithUserDTO dto = dtoList.get(i);
            String province = areaMap.get(dto.getProvinceCode());
            String city = areaMap.get(dto.getCityCode());
            String district = areaMap.get(dto.getDistrictCode());
            voList.get(i).setDivision(StringUtils.join(province, city, district));
        }
    }
}
