package cn.iocoder.power.module.carbon.convert;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.module.carbon.controller.admin.maintenance.vo.CarbonGasCoordinatorExcelVO;
import cn.iocoder.power.module.carbon.controller.admin.maintenance.vo.CarbonGasCoordinatorRespVO;
import cn.iocoder.power.module.carbon.controller.admin.maintenance.vo.CarbonGasCoordinatorSaveReqVO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonGasCoordinatorDO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CarbonGasCoordinatorConvert {

    CarbonGasCoordinatorConvert INSTANCE = Mappers.getMapper(CarbonGasCoordinatorConvert.class);

    CarbonGasCoordinatorDO convert(CarbonGasCoordinatorSaveReqVO bean);

    CarbonGasCoordinatorRespVO convert(CarbonGasCoordinatorDO bean);

    List<CarbonGasCoordinatorRespVO> convertList(List<CarbonGasCoordinatorDO> list);

    PageResult<CarbonGasCoordinatorRespVO> convertPage(PageResult<CarbonGasCoordinatorDO> page);

    CarbonGasCoordinatorExcelVO convertExcel(CarbonGasCoordinatorRespVO bean);

    List<CarbonGasCoordinatorExcelVO> convertExcelList(List<CarbonGasCoordinatorRespVO> list);
}
