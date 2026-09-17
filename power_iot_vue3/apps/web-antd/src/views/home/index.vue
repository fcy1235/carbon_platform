<script setup lang="ts">
import type { RegionDistributionItem } from '#/api/energyCarbon/report';

import { onMounted, ref } from 'vue';

import { IconifyIcon } from '@vben/icons';

import { getDashboard } from '#/api/energyCarbon/report';

import AreaMap from './modules/area-map.vue';
import BarChart from './modules/bar-chart.vue';
import OverviewCard from './modules/overview-card.vue';
import PieChart from './modules/pie-chart.vue';

const currentTime = ref('');
const loading = ref(false);

const updateTime = () => {
  const now = new Date();
  currentTime.value = now.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
  });
};

const overviewData = ref([
  {
    title: '清洁取暖改造总户数',
    value: 0,
    unit: '户',
    icon: 'home',
    subItems: [
      { label: '煤改气户数', value: 0, unit: '户' },
      { label: '煤改电户数', value: 0, unit: '户' },
    ],
  },
  {
    title: '总碳减排量',
    value: 0,
    unit: 'tCO₂e',
    icon: 'leaf',
    subItems: [
      { label: '基准线排放量', value: 0, unit: 'tCO₂e' },
      { label: '实际排放量', value: 0, unit: 'tCO₂e' },
    ],
  },
  {
    title: '两员总数',
    value: 0,
    unit: '人',
    icon: 'users',
    subItems: [
      { label: '安全员', value: 0, unit: '人' },
      { label: '协管员', value: 0, unit: '人' },
    ],
  },
  {
    title: '维保企业总数',
    value: 0,
    unit: '个',
    icon: 'building',
    subItems: [
      { label: '网点总数', value: 0, unit: '个' },
      { label: '覆盖村数', value: 0, unit: '个' },
    ],
  },
]);

const transformTypeData = ref([
  { name: '煤改气', value: 0, itemStyle: { color: '#1890ff' } },
  { name: '煤改电', value: 0, itemStyle: { color: '#52c41a' } },
]);

const deviceStatusData = ref([
  { name: '正常', value: 0, itemStyle: { color: '#52c41a' } },
  { name: '停运', value: 0, itemStyle: { color: '#faad14' } },
  { name: '故障', value: 0, itemStyle: { color: '#f5222d' } },
]);

const emissionData = ref({
  xAxis: [] as string[],
  series: [
    { name: '碳排放量', data: [] as number[], color: '#1890ff' },
  ],
});

const implementData = ref({
  xAxis: [] as string[],
  series: [
    { name: '煤改气户数', data: [] as number[], color: '#1890ff' },
    { name: '煤改电户数', data: [] as number[], color: '#91cc75' },
  ],
});

const mapAreas = ref<RegionDistributionItem[]>([]);

/** 加载仪表盘数据 */
const loadDashboardData = async () => {
  loading.value = true;
  try {
    const data = await getDashboard();

    // 更新概览卡片数据
    if (data.reformHouseholdStats) {
      overviewData.value[0].value = data.reformHouseholdStats.totalCount || 0;
      overviewData.value[0].subItems[0].value = data.reformHouseholdStats.gasCoalCount || 0;
      overviewData.value[0].subItems[1].value = data.reformHouseholdStats.electricCoalCount || 0;
    }

    if (data.carbonReductionStats) {
      overviewData.value[1].value = data.carbonReductionStats.totalReduction/1000 || 0;
      overviewData.value[1].subItems[0].value = data.carbonReductionStats.baselineEmission/1000 || 0;
      overviewData.value[1].subItems[1].value = data.carbonReductionStats.actualEmission/1000 || 0;
    }

    if (data.staffStats) {
      overviewData.value[2].value = data.staffStats.totalCount || 0;
      overviewData.value[2].subItems[0].value = data.staffStats.safetyOfficerCount || 0;
      overviewData.value[2].subItems[1].value = data.staffStats.coordinatorCount || 0;
    }

    if (data.maintenanceStats) {
      overviewData.value[3].value = data.maintenanceStats.enterpriseCount || 0;
      overviewData.value[3].subItems[0].value = data.maintenanceStats.stationCount || 0;
      overviewData.value[3].subItems[1].value = data.maintenanceStats.coveredVillageCount || 0;
    }

    // 更新改造类别统计数据
    if (data.reformCategoryStats && data.reformCategoryStats.length > 0) {
      transformTypeData.value = data.reformCategoryStats.map((item, index) => ({
        name: item.name || '',
        value: item.count || 0,
        itemStyle: { color: index === 0 ? '#1890ff' : '#52c41a' },
      }));
    }

    // 更新设备状态统计数据
    if (data.deviceStatusStats && data.deviceStatusStats.length > 0) {
      const colorMap: Record<string, string> = {
        normal: '#52c41a',
        offline: '#faad14',
        fault: '#f5222d',
      };
      deviceStatusData.value = data.deviceStatusStats.map(item => ({
        name: item.name || '',
        value: item.count || 0,
        itemStyle: { color: colorMap[item.status || ''] || '#1890ff' },
      }));
    }

    // 更新各区县碳排放量数据（单位转换：kg -> t，除以1000）
    if (data.districtCarbonEmissions && data.districtCarbonEmissions.length > 0) {
      emissionData.value.xAxis = data.districtCarbonEmissions.map(item => item.districtName || '');
      emissionData.value.series[0].data = data.districtCarbonEmissions.map(item => (item.carbonEmission || 0) / 1000);
    }

    // 更新各区县双代实施情况数据
    if (data.districtReformStats && data.districtReformStats.length > 0) {
      implementData.value.xAxis = data.districtReformStats.map(item => item.districtName || '');
      implementData.value.series[0].data = data.districtReformStats.map(item => item.gasCoalCount || 0);
      implementData.value.series[1].data = data.districtReformStats.map(item => item.electricCoalCount || 0);
    }

    // 更新区域分布数据
    if (data.regionDistribution && data.regionDistribution.length > 0) {
      mapAreas.value = data.regionDistribution;
    }
  } catch (error) {
    console.error('Failed to load dashboard data:', error);
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  updateTime();
  setInterval(updateTime, 1000);
  loadDashboardData();
});
</script>

<template>
  <div class="min-h-screen bg-gray-50 p-3">
    <div class="mx-auto">
      <div class="bg-white rounded-xl border border-gray-200 p-3 mb-3 relative">
        <div class="flex items-center justify-center">
          <h1 class="text-2xl font-bold text-gray-800">农村能碳信息管理平台</h1>
        </div>
        <div class="absolute top-3 right-3 flex items-center gap-2 text-gray-500">
          <IconifyIcon icon="lucide:clock" class="w-5 h-5" />
          <span class="text-lg font-medium">{{ currentTime }}</span>
        </div>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-3">
        <OverviewCard
          v-for="(item, index) in overviewData"
          :key="index"
          :title="item.title"
          :value="item.value"
          :unit="item.unit"
          :icon="item.icon"
          :sub-items="item.subItems"
        />
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-4 gap-4">
        <div class="lg:col-span-1 flex flex-col h-[1000px] gap-3">
          <PieChart title="改造类别统计" :data="transformTypeData" class="flex-1" />
          <BarChart
            title="石家庄市各区县碳排放量"
            :x-axis-data="emissionData.xAxis"
            :series="emissionData.series"
            x-label="横轴为区县名称"
            y-label="纵轴为碳排放量"
            class="flex-1"
          />
        </div>

        <div class="lg:col-span-2 h-[1000px]">
          <AreaMap title="河北省区域分布" :areas="mapAreas" class="h-full" />
        </div>

        <div class="lg:col-span-1 flex flex-col h-[1000px] gap-3">
          <PieChart title="设备状态" :data="deviceStatusData" class="flex-1" />
          <BarChart
            title="石家庄市各区县双代实施情况"
            :x-axis-data="implementData.xAxis"
            :series="implementData.series"
            x-label="横轴为区县名称"
            y-label="纵轴为户数"
            class="flex-1"
          />
        </div>
      </div>
    </div>
  </div>
</template>
