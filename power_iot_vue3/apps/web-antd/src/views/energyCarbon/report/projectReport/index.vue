<script setup lang="ts">
import type { CarbonProjectReportRespVO } from '#/api/energyCarbon/report';

import { computed, onMounted, ref } from 'vue';

import { Page } from '@vben/common-ui';

import { Button, message } from 'ant-design-vue';
import dayjs from 'dayjs';

import { useVbenForm } from '#/adapter/form';
import {
  getProjectSimpleList,
  type ProjectSimple,
} from '#/api/energyCarbon/project';
import { getProjectReport } from '#/api/energyCarbon/report';

import { useGridFormSchema } from './data';

// 项目列表
const projectList = ref<ProjectSimple[]>([]);
const loading = ref(false);
const reportData = ref<CarbonProjectReportRespVO>();

// 报表内容区域引用（打印用）
const reportContentRef = ref<HTMLElement>();

// 项目选项
const projectOptions = computed(() =>
  projectList.value.map((item) => ({
    label: item.projectName,
    value: item.projectId,
  })),
);

// 格式化数值
function formatNumber(value?: number, digits = 2): string {
  if (value === undefined || value === null || Number.isNaN(value)) return '-';
  return Number(value).toFixed(digits);
}

// 解析行政区
function parseDivision(division?: string) {
  if (!division) {
    return { city: '', district: '', province: '' };
  }
  const provinceMatch = division.match(/^(.*?省|.*?自治区|.*?特别行政区)/);
  const province = provinceMatch ? provinceMatch[1] : '';
  const rest = province ? division.slice(province.length) : division;
  const cityMatch = rest.match(/^(.*?市|.*?州)/);
  const city = cityMatch ? cityMatch[1].replace(/[市州]$/, '') : '';
  const districtRest = cityMatch ? rest.slice(cityMatch[1].length) : rest;
  const districtMatch = districtRest.match(/^(.*?区|.*?县|.*?旗)/);
  const district = districtMatch ? districtMatch[1] : '';
  return { city, district, province };
}

// 判断改造类型文案
function getReformTypeText(data?: CarbonProjectReportRespVO) {
  if (!data) return '电代煤/气代煤';
  const hasElectricity = Number(data.electricityUsage) > 0;
  const hasGas = Number(data.gasUsage) > 0;
  if (hasElectricity && hasGas) return '电代煤/气代煤';
  if (hasElectricity) return '电代煤';
  if (hasGas) return '气代煤';
  return '电代煤/气代煤';
}

// 报告生成日期
const reportDate = computed(() => dayjs().format('YYYY年MM月DD日'));

// 项目周期文本
const projectPeriod = computed(() => {
  const start = reportData.value?.planStartDate
    ? dayjs(reportData.value.planStartDate).format('YYYY年MM月DD日')
    : '-';
  const end = reportData.value?.planEndDate
    ? dayjs(reportData.value.planEndDate).format('YYYY年MM月DD日')
    : '-';
  return `${start} - ${end}`;
});

// 地理位置文本
const locationText = computed(() => {
  const { province, city, district } = parseDivision(reportData.value?.division);
  return `${province || '河北省'}${city || '-'}市${district || '-'}`;
});

// 计入期文本
const creditingPeriod = computed(() => {
  const start = reportData.value?.planStartDate
    ? dayjs(reportData.value.planStartDate).format('YYYY年MM月DD日')
    : '-';
  const end = reportData.value?.planEndDate
    ? dayjs(reportData.value.planEndDate).format('YYYY年MM月DD日')
    : '-';
  return `${start} 至 ${end}`;
});

// 报告结论标题
const reportTitle = computed(() => {
  const data = reportData.value;
  const { city, district, province } = parseDivision(data?.division);
  const start = data?.planStartDate
    ? dayjs(data.planStartDate).format('YYYY年MM月DD日')
    : '-';
  const end = data?.planEndDate
    ? dayjs(data.planEndDate).format('YYYY年MM月DD日')
    : '-';
  const households = data?.reformHouseholds ?? '-';
  const area = formatNumber(data?.buildingArea, 2);
  return `本项目为${province || '河北省'}${city || '-'}市${district || '-'}农村地区“${getReformTypeText(data)}”清洁取暖项目，项目名称为 ${data?.projectName || '-'}，项目计入期为 ${start} 至 ${end}，覆盖改造户数 ${households} 户，清洁取暖建筑面积 ${area}m²，取暖季能源消耗为天然气和电力。`;
});

const [Form, formApi] = useVbenForm({
  commonConfig: {
    componentProps: {
      class: 'w-full',
    },
    formItemClass: 'col-span-1',
    labelWidth: 70,
  },
  layout: 'horizontal',
  schema: useGridFormSchema(),
  showDefaultActions: false,
  wrapperClass: 'grid-cols-2',
});

// 加载项目列表
async function loadProjectList() {
  try {
    projectList.value = (await getProjectSimpleList()) || [];
    // 回填下拉选项
    await formApi.updateSchema([
      {
        fieldName: 'projectId',
        componentProps: {
          options: projectOptions.value,
        },
      },
    ]);
    // 默认选中第一条并自动查询
    const firstProjectId = projectList.value[0]?.projectId;
    if (firstProjectId) {
      await formApi.setValues({ projectId: firstProjectId });
      await handleQuery();
    }
  } catch {
    message.error('获取项目列表失败');
  }
}

// 查询报表
async function handleQuery() {
  const values = await formApi.getValues();
  const projectId = values.projectId;
  if (!projectId) {
    message.warning('请选择项目');
    return;
  }
  loading.value = true;
  try {
    reportData.value = (await getProjectReport(Number(projectId))) || undefined;
  } catch {
    // 报表数据不存在时不报错，展示空数据
    reportData.value = undefined;
  } finally {
    loading.value = false;
  }
}

// 打印报表（仅打印报表内容区域）
function handlePrint() {
  if (!reportData.value) {
    message.warning('请先查询报表');
    return;
  }
  const content = reportContentRef.value;
  if (!content) {
    return;
  }

  // 打印样式（封面单独分页，日期居底）
  const printStyle = `
    * { margin: 0; padding: 0; box-sizing: border-box; }
    body { padding: 0; font-family: 'Microsoft YaHei', sans-serif; color: #000; }

    /* 封面页样式 */
    .cover-page {
      page-break-after: always;
      height: 100vh;
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      position: relative;
    }
    .cover-page .report-main-title {
      font-size: 32px;
      font-weight: 700;
      text-align: center;
    }
    .cover-page .report-date {
      position: absolute;
      bottom: 60px;
      font-size: 18px;
      text-align: center;
    }

    /* 正文样式 */
    .report-body { padding: 24px; }
    .section-title { font-size: 18px; font-weight: 600; margin-top: 24px; margin-bottom: 12px; }
    .sub-section-title { font-size: 16px; font-weight: 600; margin-top: 16px; margin-bottom: 8px; }
    .content-paragraph { font-size: 14px; line-height: 1.8; text-indent: 2em; margin-bottom: 8px; }
    .paragraph-list p { font-size: 14px; line-height: 1.8; margin-bottom: 4px; }
    .content-list { font-size: 14px; line-height: 1.8; padding-left: 2em; margin-bottom: 8px; }
    .content-list li { margin-bottom: 4px; }
    .formula { font-size: 14px; text-align: center; margin: 8px 0; font-family: 'Times New Roman', serif; }
    .formula-list { font-size: 14px; line-height: 1.8; padding-left: 2em; list-style: none; margin-bottom: 8px; }
    .formula-list li { margin-bottom: 4px; }
    .report-table { width: 100%; table-layout: fixed; border-collapse: collapse; margin: 12px 0; }
    .report-table td { padding: 12px 8px; border: 1px solid #333; text-align: center; font-size: 14px; }
    .section-header { font-weight: 600; text-align: left; padding-left: 16px; }
    .sub-header { font-weight: 600; }
    .value-cell { height: 48px; }
    .result-value { font-weight: 600; }
    .monitor-table { width: 100%; border-collapse: collapse; margin: 12px 0; font-size: 13px; }
    .monitor-table th, .monitor-table td { border: 1px solid #333; padding: 8px 6px; text-align: center; vertical-align: middle; }
    .monitor-table th { font-weight: 600; }
  `;

  // 使用隐藏 iframe 只打印报表内容
  const iframe = document.createElement('iframe');
  iframe.style.position = 'fixed';
  iframe.style.width = '0';
  iframe.style.height = '0';
  iframe.style.border = 'none';
  document.body.append(iframe);

  const doc = iframe.contentDocument;
  if (!doc) {
    iframe.remove();
    return;
  }
  doc.open();
  doc.write(`<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="utf-8" />
  <title>河北省农村地区清洁取暖降碳产品报告</title>
  <style>${printStyle}</style>
</head>
<body>${content.innerHTML}</body>
</html>`);
  doc.close();

  // 等待 iframe 渲染完成后打印，打印后移除
  iframe.contentWindow?.focus();
  setTimeout(() => {
    iframe.contentWindow?.print();
    setTimeout(() => iframe.remove(), 1000);
  }, 100);
}

onMounted(loadProjectList);
</script>

<template>
  <Page auto-content-height>
    <div class="project-report-page">
      <!-- 查询区域 -->
      <div class="report-search">
        <Form />
        <div class="search-actions">
          <Button v-access:code="['carbon:report:query']" type="primary" :loading="loading" @click="handleQuery">
            查询
          </Button>
          <Button v-access:code="['carbon:report:query']" :disabled="!reportData" @click="handlePrint">打印</Button>
        </div>
      </div>

      <!-- 报表内容 -->
      <div ref="reportContentRef" class="report-content">
        <!-- 封面页：标题居中，日期固定页面底部 -->
        <div class="cover-page">
          <h1 class="report-main-title">河北省农村地区清洁取暖降碳产品报告</h1>
          <p class="report-date">{{ reportDate }}</p>
        </div>

        <!-- 正文内容 -->
        <div class="report-body">
          <!-- 1 项目基本信息 -->
          <h2 class="section-title">1 项目基本信息</h2>

          <h3 class="sub-section-title">1.1 概况</h3>
          <div class="paragraph-list">
            <p>项目名称：{{ reportData?.projectName || '-' }}</p>
            <p>项目负责人：{{ reportData?.contactName || '-' }}</p>
            <p>项目描述：{{ reportData?.projectDesc || '-' }}</p>
            <p>项目周期：{{ projectPeriod }}</p>
          </div>

          <h3 class="sub-section-title">1.2 地理位置</h3>
          <p class="content-paragraph">{{ locationText }}</p>

          <h3 class="sub-section-title">1.3 项目活动的技术说明</h3>
          <p class="content-paragraph">
            项目通过清洁能源替代方式，解决农村散煤燃烧带来的污染问题。技术路线核心在于“以电/气代煤”，即在县级及以上城市人民政府驻地的镇、乡、村庄规划区以外，将居民传统的散煤取暖设备替换为清洁的分散电采暖设备或燃气壁挂炉。项目实施区域严格界定为上述规划区以外的农村地区。设备分类与选型技术如下：
          </p>
          <ol class="content-list">
            <li>分散电采暖设备：本项目推广的设备以电力为能源，热源设置在户内且支持灵活控制。根据施工需求分为即插即用型和系统铺设类，根据加热原理主要分为直热型、热泵型及蓄热型三类。</li>
            <li>燃气壁挂炉：以燃气作为燃料，通过能量转换设备进行供暖。</li>
            <li>被替代设备：传统散煤炉具。定义为未经加工成型、用于居民分散式取暖的动力用煤设备。</li>
          </ol>

          <!-- 2 方法学应用 -->
          <h2 class="section-title">2 方法学应用</h2>

          <h3 class="sub-section-title">2.1 采用的方法学及适用条件</h3>
          <p class="content-paragraph">
            采用《河北省农村地区清洁取暖降碳产品方法学》(版本号V01)，本方法学规定了河北省范围内农村地区实施电代煤或气代煤清洁取暖项目(简称“双代”项目)所产生的二氧化碳减排量的核算流程和方法。
          </p>
          <p class="content-paragraph">采用本方法学的“双代”项目活动，应适用以下条件:</p>
          <ol class="content-list">
            <li>利用天然气或电力作为能源的既有建筑“双代”清洁取暖活动；</li>
            <li>单一用户取暖季使用燃气量超过100m³或用电量超过500kWh;</li>
            <li>隶属同一行政县或乡镇边界内农村地区清洁取暖项目可以整合为一个项目申请；</li>
            <li>政府部门直接投资(部分或全部)，提供补贴安装燃气取暖设备及电力取暖设备的项目可由项目建设单位对降碳产品整体开发；</li>
            <li>“双代”项目建设单位和“双代”使用单位或个人应协商明确项目减排量收益。</li>
          </ol>

          <h3 class="sub-section-title">2.2 项目边界的确定</h3>
          <p class="content-paragraph">
            项目边界的空间范围包括提供给农村居民房屋终端用户取暖热量的燃气壁挂炉、分散电采暖设备、供回水管道、散热器与建筑物之间的热量传输系统。
          </p>
          <p class="content-paragraph">
            项目基准线情景下二氧化碳核算边界：农村地区因冬季取暖安置在户外燃煤锅炉、屋内燃煤火炉燃烧散煤产生的直接二氧化碳排放。
          </p>
          <p class="content-paragraph">
            项目活动二氧化碳核算边界：农村地区因冬季取暖所需消耗天然气或电力产生的直接或间接二氧化碳排放。
          </p>

          <h3 class="sub-section-title">2.3 温室气体排放源的选择</h3>
          <p class="content-paragraph">
            基准线情景下（即由于项目活动被替代的取暖方式消耗散煤产生的CO₂排放）主要排放源为二氧化碳；
          </p>
          <p class="content-paragraph">
            项目活动情景下（即项目活动导致的天然气、电力消耗产生的排放）主要排放源为二氧化碳。
          </p>

          <!-- 3 项目减排量的计入期情况 -->
          <h2 class="section-title">3 项目减排量的计入期情况</h2>
          <p class="content-paragraph">
            项目寿命期限的开始时间为项目改造完成日期，项目寿命期限的结束时间应在项目设备正式退役之前。
          </p>
          <p class="content-paragraph">
            项目计入期为可申请项目减排量登记的时间期限，从项目业主申请登记的项目减排量的产生时间开始，最长不超过10年。项目计入期须在项目寿命期限范围之内。项目计入期开始时间不早于2016年1月1日。
          </p>
          <p class="content-paragraph">本项目计入期为：{{ creditingPeriod }}</p>

          <!-- 4 减排量计算情况 -->
          <h2 class="section-title">4 减排量计算情况</h2>

          <h3 class="sub-section-title">4.1 基准线情景</h3>
          <p class="content-paragraph">
            在采取“双代”项目活动前，其基准线情景设定为：与项目建筑在同一气候子区内，农村地区采用室外燃煤锅炉及屋内燃煤火炉为室内取暖。基准线排放量为基准线下产生的二氧化碳排放量。
          </p>

          <h3 class="sub-section-title">4.2 基准线排放</h3>
          <p class="content-paragraph">
            基准线建筑与项目建筑位于同一气候子区，近三年持续被使用，且核算边界无变化。则基准线排放量按照公式(1)进行计算：
          </p>
          <p class="formula">BE<sub>y</sub> = DE × (A<sub>y</sub> / 1000) &nbsp;&nbsp;&nbsp;&nbsp; 公式（1）</p>
          <p class="content-paragraph">式中:</p>
          <ul class="formula-list">
            <li>BE<sub>y</sub>：项目活动中，第y年取暖季的基准线排放量(tCO₂e/取暖季)；</li>
            <li>DE：项目活动中，农村建筑基准线碳排放强度(kgCO₂e/m²·取暖季)；</li>
            <li>A<sub>y</sub>：项目活动中，第y年取暖季的清洁取暖建筑面积(m²)。</li>
          </ul>

          <h3 class="sub-section-title">4.3 项目排放</h3>
          <p class="content-paragraph">采用排放因子法计算二氧化碳排放量，按以下公式(2)、公式(3)进行计算：</p>
          <p class="formula">PE<sub>y</sub> = PE<sub>gas,y</sub> &nbsp;&nbsp;&nbsp;&nbsp; 公式（2）</p>
          <p class="formula">PE<sub>y</sub> = PE<sub>EC,y</sub> &nbsp;&nbsp;&nbsp;&nbsp; 公式（3）</p>
          <p class="content-paragraph">其中：</p>
          <ul class="formula-list">
            <li>PE<sub>y</sub>：项目活动中，第y年取暖季项目中的排放量(tCO₂e/取暖季)；</li>
            <li>PE<sub>gas,y</sub>：项目活动中，第y年取暖季项目消耗天然气造成的直接排放量(tCO₂e/取暖季)；</li>
            <li>PE<sub>EC,y</sub>：项目活动中，第y年取暖季项目消耗电力造成的间接排放量 (tCO₂e/取暖季)；</li>
          </ul>

          <p class="content-paragraph">(1) 由于天然气消耗产生的项目直接排放量，按以下公式(4)进行计算：</p>
          <p class="formula">PE<sub>gas,y</sub> = EG<sub>gas,y</sub> × EF<sub>gas</sub> &nbsp;&nbsp;&nbsp;&nbsp; 公式（4）</p>
          <p class="content-paragraph">其中：</p>
          <ul class="formula-list">
            <li>EG<sub>gas,y</sub>：项目活动中，第y年取暖季项目天然气的消耗量(万Nm³);</li>
            <li>EF<sub>gas</sub>：项目活动中，天然气的CO₂排放因子(tCO₂e/万Nm³)。</li>
          </ul>
          <p class="content-paragraph">上式中，天然气的CO₂排放因子，按以下公式(5)进行计算：</p>
          <p class="formula">EF<sub>gas</sub> = NCV<sub>gas</sub> × CC<sub>gas</sub> × OF<sub>gas</sub> / 1000 × (44/12) &nbsp;&nbsp;&nbsp;&nbsp; 公式（5）</p>
          <p class="content-paragraph">其中:</p>
          <ul class="formula-list">
            <li>EF<sub>gas</sub>：项目活动中，天然气的CO₂排放因子(tCO₂e/万Nm³)；</li>
            <li>NCV<sub>gas</sub>：项目活动中，天然气的平均低位发热量(GJ/万Nm³);</li>
            <li>CC<sub>gas</sub>：项目活动中，天然气的单位热值含碳量(tC/TJ);</li>
            <li>OF<sub>gas</sub>：项目活动中，天然气的碳氧化率，以%表示；</li>
            <li>44/12：二氧化碳与碳的相对分子质量之比。</li>
          </ul>

          <p class="content-paragraph">(2) 由于电力消耗产生的项目间接排放量，按以下公式(6)进行计算:</p>
          <p class="formula">PE<sub>EC,y</sub> = EG<sub>EC,y</sub> × EF<sub>grid,CM,y</sub> &nbsp;&nbsp;&nbsp;&nbsp; 公式（6）</p>
          <p class="content-paragraph">其中：</p>
          <ul class="formula-list">
            <li>EG<sub>EC,y</sub>：项目活动中，第y年取暖季项目电力的消耗量(MWh);</li>
            <li>EF<sub>grid,CM,y</sub>：第y年华北区域电网电力组合边际因子(tCO₂e/MWh)。</li>
          </ul>
          <p class="content-paragraph">电力组合边际因子，按以下公式(7)进行计算：</p>
          <p class="formula">EF<sub>grid,CM,y</sub> = EF<sub>grid,OM,y</sub> × ω<sub>OM</sub> + EF<sub>grid,BM,y</sub> × ω<sub>BM</sub> &nbsp;&nbsp;&nbsp;&nbsp; 公式（7）</p>
          <p class="content-paragraph">其中：</p>
          <ul class="formula-list">
            <li>EF<sub>grid,CM,y</sub>：第y年华北区域电网电力组合边际因子(tCO₂e/MWh)；</li>
            <li>EF<sub>grid,OM,y</sub>：第y年华北区域电网电量边际排放因子(tCO₂e/MWh)；</li>
            <li>ω<sub>OM</sub>：电量边际排放因子权重，默认0.5；</li>
            <li>EF<sub>grid,BM,y</sub>：第y年华北区域电网容量边际排放因子(tCO₂e/MWh);</li>
            <li>ω<sub>BM</sub>：容量边际排放因子权重，默认0.5。</li>
          </ul>

          <h3 class="sub-section-title">4.4 泄漏排放</h3>
          <p class="content-paragraph">
            “双代”项目取代的农村取暖设施一般都很陈旧，农民将直接淘汰，不构成设备转移，所以本项目不考虑泄露。
          </p>

          <h3 class="sub-section-title">4.5 计算结果</h3>
          <table class="report-table">
            <tbody>
            <!-- 项目基准线排放量 -->
            <tr>
              <td class="section-header" colspan="4">项目基准线排放量（tCO₂e/取暖季）</td>
            </tr>
            <tr>
              <td class="sub-header" colspan="2">清洁取暖建筑面积（m²）</td>
              <td class="sub-header" colspan="2">区域基准线碳排放强度（kgCO₂e/(m²·取暖季)）</td>
            </tr>
            <tr>
              <td class="value-cell" colspan="2">
                {{ formatNumber(reportData?.buildingArea, 2) }}
              </td>
              <td class="value-cell" colspan="2">
                {{ formatNumber(reportData?.baselineIntensity, 4) }}
              </td>
            </tr>
            <tr>
              <td class="result-value" colspan="4">
                {{ formatNumber(reportData?.baselineEmission, 4) }}
              </td>
            </tr>

            <!-- 项目实际排放量 -->
            <tr>
              <td class="section-header" colspan="4">项目实际排放量（tCO₂e/取暖季）</td>
            </tr>
            <tr>
              <td class="sub-header">电力消耗量（MWh）</td>
              <td class="sub-header">电力排放因子（tCO₂e/MWh）</td>
              <td class="sub-header">燃气消耗量（万 Nm³）</td>
              <td class="sub-header">燃气排放因子（tCO₂e/万 Nm³）</td>
            </tr>
            <tr>
              <td class="value-cell">
                {{ formatNumber(reportData?.electricityUsage, 2) }}
              </td>
              <td class="value-cell">
                {{ formatNumber(reportData?.electricityFactor, 4) }}
              </td>
              <td class="value-cell">
                {{ formatNumber(reportData?.gasUsage, 2) }}
              </td>
              <td class="value-cell">
                {{ formatNumber(reportData?.gasFactor, 4) }}
              </td>
            </tr>
            <tr>
              <td class="result-value" colspan="4">
                {{ formatNumber(reportData?.actualEmission, 4) }}
              </td>
            </tr>

            <!-- 项目减排量 -->
            <tr>
              <td class="section-header" colspan="4">项目减排量（tCO₂e/取暖季）</td>
            </tr>
            <tr>
              <td class="result-value" colspan="4">
                {{ formatNumber(reportData?.reduction, 4) }}
              </td>
            </tr>
            </tbody>
          </table>

          <!-- 5 报告结论 -->
          <h2 class="section-title">5 报告结论</h2>
          <p class="content-paragraph">{{ reportTitle }}</p>
          <p class="content-paragraph">
            项目属于河北省农村地区既有建筑"双代"清洁取暖活动，单一用户取暖季用气量＞100 m³（或用电量＞500 kWh），隶属同一行政县/乡镇边界内可整合申报，符合全部适用条件。
          </p>
          <p class="content-paragraph">
            经核算，本项目在核查期内（{{ creditingPeriod }}）共产生减排量 {{ formatNumber(reportData?.reduction, 4) }} tCO₂e，计算过程可测量、可报告、可核查。
          </p>

          <!-- 6 数据来源与监测程序 -->
          <h2 class="section-title">6 数据来源与监测程序</h2>

          <h3 class="sub-section-title">6.1 监测数据和监测程序</h3>
          <p class="content-paragraph">
            应当对收集的所有数据进行电子存档并且至少保存至计入期结束后两年。应当对所有数据进行监测，除非有特别说明。所有的测量值均应来自测量仪器，测量仪器需要经过检定或校准，且符合相关的国家标准和行业标准。
          </p>

          <table class="monitor-table">
            <thead>
            <tr>
              <th>数据</th>
              <th>单位</th>
              <th>数据描述</th>
              <th>数据来源</th>
              <th>测量程序</th>
              <th>监测频率</th>
              <th>QA/QC程序</th>
            </tr>
            </thead>
            <tbody>
            <tr>
              <td>EG<sub>gas,y</sub></td>
              <td>万立方米</td>
              <td>第y年取暖季农村地区清洁取暖天然气消耗量</td>
              <td>燃气表</td>
              <td>燃气表测量</td>
              <td>连续测量</td>
              <td>与财务账单和能源使用记录的数值交叉核对</td>
            </tr>
            <tr>
              <td>EG<sub>EC,y</sub></td>
              <td>MWh</td>
              <td>第y年取暖季农村地区清洁取暖电力消耗量</td>
              <td>电表</td>
              <td>电表测量</td>
              <td>连续测量</td>
              <td>与财务账单和能源使用记录的数值交叉核对</td>
            </tr>
            <tr>
              <td>A<sub>y</sub></td>
              <td>m²</td>
              <td>第y年农村地区清洁取暖建筑面积</td>
              <td>实际改造面积。如无相关统计数据，根据保守性原则，每户按60m²计算</td>
              <td>——</td>
              <td>每年</td>
              <td>——</td>
            </tr>
            </tbody>
          </table>

          <h3 class="sub-section-title">6.2 数据质量保证与管理措施</h3>
          <p class="content-paragraph">
            项目申请者需成立专门的降碳产品管理工作组，负责实施监测计划。该工作组由河北省农村地区清洁取暖高级管理人员担任项目负责人，统一负责协调项目的管理和监测工作。项目申请者应采取下列数据质量保证与管理措施，确保调查和监测数据的真实可靠。
          </p>
          <ol class="content-list">
            <li>遵循项目设计阶段确定的数据监测程序与方法要求，制定详细的监测方案；</li>
            <li>电能和天然气计量装置应按照国家标准和相关行业标准及规范的技术要求进行配置。项目运行前，电能、天然气计量装置由项目申请者和当地供电公司和燃气公司检查验收；</li>
            <li>电表和燃气表定期检定校准工作应按照国家标准和相关行业标准及规范执行；</li>
            <li>建立健全电力、天然气消耗和清洁取暖供热面积的台账记录及凭证；</li>
            <li>建立文档的管理规范，保存年度二氧化碳排放核算和报告的文件和有关的数据资料。</li>
          </ol>
        </div>
      </div>
    </div>
  </Page>
</template>

<style scoped>
.project-report-page {
  padding: 16px;
  background: #fff;
}

.report-search {
  display: flex;
  gap: 16px;
  align-items: flex-start;
  margin-bottom: 16px;
}

.search-actions {
  display: flex;
  flex-shrink: 0;
  gap: 8px;
  padding-top: 4px;
}

.report-content {
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  background: #fff;
}

/* 封面：全屏高度，标题居中，日期绝对定位到底部 */
.cover-page {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  min-height: 90vh;
  page-break-after: always;
  padding: 40px;
  position: relative;
}

.report-main-title {
  font-size: 32px;
  font-weight: 700;
  text-align: center;
}

.report-date {
  position: absolute;
  bottom: 60px;
  font-size: 18px;
  text-align: center;
}

/* 正文区域 */
.report-body {
  padding: 24px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  margin-top: 24px;
  margin-bottom: 12px;
}

.sub-section-title {
  font-size: 16px;
  font-weight: 600;
  margin-top: 16px;
  margin-bottom: 8px;
}

.content-paragraph {
  font-size: 14px;
  line-height: 1.8;
  text-indent: 2em;
  margin-bottom: 8px;
}

.paragraph-list p {
  font-size: 14px;
  line-height: 1.8;
  margin-bottom: 4px;
}

.content-list {
  font-size: 14px;
  line-height: 1.8;
  padding-left: 2em;
  margin-bottom: 8px;
}

.content-list li {
  margin-bottom: 4px;
}

.formula {
  font-size: 14px;
  text-align: center;
  margin: 8px 0;
  font-family: 'Times New Roman', serif;
}

.formula-list {
  font-size: 14px;
  line-height: 1.8;
  padding-left: 2em;
  list-style: none;
  margin-bottom: 8px;
}

.formula-list li {
  margin-bottom: 4px;
}

.report-table {
  width: 100%;
  table-layout: fixed;
  border-collapse: collapse;
  margin: 12px 0;
}

.report-table td {
  padding: 12px 8px;
  border: 1px solid #333;
  text-align: center;
  font-size: 14px;
}

.section-header {
  font-weight: 600;
  text-align: left;
  padding-left: 16px;
}

.sub-header {
  font-weight: 600;
}

.value-cell {
  height: 48px;
}

.result-value {
  font-weight: 600;
}

.monitor-table {
  width: 100%;
  border-collapse: collapse;
  margin: 12px 0;
  font-size: 13px;
}

.monitor-table th,
.monitor-table td {
  border: 1px solid #333;
  padding: 8px 6px;
  text-align: center;
  vertical-align: middle;
}

.monitor-table th {
  font-weight: 600;
}
</style>
