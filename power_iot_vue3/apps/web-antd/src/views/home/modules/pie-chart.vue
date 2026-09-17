<script setup lang="ts">
import type { EchartsUIType } from '@vben/plugins/echarts';
import { ref, onMounted, watch } from 'vue';
import { Card } from 'ant-design-vue';
import { EchartsUI, useEcharts } from '@vben/plugins/echarts';

interface Props {
  title: string;
  data: {
    name: string;
    value: number;
    itemStyle?: {
      color: string;
    };
  }[];
}

const props = defineProps<Props>();
const chartRef = ref<EchartsUIType>();
const { renderEcharts } = useEcharts(chartRef);

const initChart = () => {
  if (!chartRef.value) return;
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)',
    },
    legend: {
      orient: 'horizontal',
      bottom: '5%',
      left: 'center',
      itemGap: 15,
      textStyle: {
        fontSize: 12,
        color: '#666',
      },
    },
    series: [
      {
        name: props.title,
        type: 'pie',
        radius: '65%',
        center: ['50%', '45%'],
        avoidLabelOverlap: true,
        itemStyle: {
          borderColor: '#fff',
          borderWidth: 2,
        },
        label: {
          show: true,
          position: 'outside',
          fontSize: 12,
          formatter: '{b}',
        },
        emphasis: {
          scale: true,
          scaleSize: 10,
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.2)',
          },
        },
        labelLine: {
          show: true,
          length: 15,
          length2: 10,
        },
        data: props.data,
      },
    ],
  };
  renderEcharts(option);
};

onMounted(() => {
  initChart();
});

watch(() => props.data, () => {
  initChart();
}, { deep: true });
</script>

<template>
  <Card :title="title" class="h-full">
    <EchartsUI ref="chartRef" />
  </Card>
</template>
