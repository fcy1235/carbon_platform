<script setup lang="ts">
import type { EchartsUIType } from '@vben/plugins/echarts';

import type { RegionDistributionItem } from '#/api/energyCarbon/report';

import { onMounted, onUnmounted, ref, watch } from 'vue';

import { echarts, EchartsUI, useEcharts } from '@vben/plugins/echarts';

import { Card } from 'ant-design-vue';

import hebeiGeoJson from '../../../assets/hebei.json';

interface Props {
  title?: string;
  areas?: RegionDistributionItem[];
}

const props = withDefaults(defineProps<Props>(), {
  title: '石家庄市区域分布',
  areas: () => [],
});

const chartRef = ref<EchartsUIType>();
const { renderEcharts, getChartInstance } = useEcharts(chartRef);

// 行政区划编码到城市名称的映射
const regionCodeToName: Record<number, string> = {
  1301: '石家庄市',
  1302: '唐山市',
  1303: '秦皇岛市',
  1304: '邯郸市',
  1305: '邢台市',
  1306: '保定市',
  1307: '张家口市',
  1308: '承德市',
  1309: '沧州市',
  1310: '廊坊市',
  1311: '衡水市',
};

const initChart = () => {
  // 使用本地河北省地图数据 (行政区划代码 130000)
  echarts.registerMap('hebei', hebeiGeoJson as any);

  // 使用传入的区域数据，根据 regionCode 映射到地图名称
  const data = props.areas.length > 0 
    ? props.areas.map(item => ({
        name: regionCodeToName[item.regionCode] || item.regionName || '',
        value: item.value || 0,
      }))
    : [];

  // 计算最大最小值
  const values = data.map(item => item.value);
  const min = values.length > 0 ? Math.min(...values) : 0;
  const max = values.length > 0 ? Math.max(...values) : 100;

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}<br/>碳排放量: {c}'
    },
    toolbox: {
      show: false,
      orient: 'vertical',
      left: 'right',
      top: 'center',
      feature: {
        dataView: { readOnly: false },
        restore: {},
        saveAsImage: {}
      }
    },
    visualMap: {
      show: false,
      min,
      max,
      text: ['高', '低'],
      realtime: false,
      calculable: true,
      inRange: {
        color: ['#e6f7ff', '#bae7ff', '#91d5ff', '#69c0ff', '#40a9ff', '#1890ff', '#096dd9', '#0050b3']
      },
      textStyle: {
        color: '#333'
      }
    },
    series: [
      {
        name: '河北省区域分布',
        type: 'map',
        map: 'hebei',
        label: {
          show: true,
          fontSize: 11
        },
        emphasis: {
          label: {
            fontWeight: 'bold',
            fontSize: 13
          },
          itemStyle: {
            areaColor: '#1890ff'
          }
        },
        data
      }
    ]
  };

  renderEcharts(option);
};

onMounted(() => {
  initChart();
});

// 监听areas属性变化，重新渲染图表
watch(() => props.areas, () => {
  initChart();
}, { deep: true });

onUnmounted(() => {
  const chartInstance = getChartInstance();
  if (chartInstance) {
    chartInstance.dispose();
  }
});
</script>

<template>
  <Card :title="title" class="h-full">
    <EchartsUI ref="chartRef" class="w-full h-full min-h-[900px]" />
  </Card>
</template>
