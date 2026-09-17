<script setup lang="ts">
import type { EchartsUIType } from '@vben/plugins/echarts';
import { ref, onMounted, watch } from 'vue';
import { Card } from 'ant-design-vue';
import { EchartsUI, useEcharts } from '@vben/plugins/echarts';

interface Props {
 title: string;
 xAxisData: string[];
 series: {
 name: string;
 data: number[];
 color?: string;
 }[];
 xLabel?: string;
 yLabel?: string;
}
const props = withDefaults(defineProps<Props>(), {
 xLabel: '',
 yLabel: '',
});
const chartRef = ref<EchartsUIType>();
const { renderEcharts } = useEcharts(chartRef);
const initChart = () => {
 if (!chartRef.value)
 return;
 
 // 计算显示的柱子数量，超过7个时启用滚动
 const showCount = 7;
 const dataLength = props.xAxisData.length;
 const endPercent = dataLength > showCount ? Math.round((showCount / dataLength) * 100) : 100;
 
 const option = {
 tooltip: {
 trigger: 'axis',
 axisPointer: {
 type: 'shadow',
 },
 },
 legend: {
 top: '2%',
 itemGap: 20,
 textStyle: {
 fontSize: 12,
 color: '#666',
 },
 },
 grid: {
 left: '3%',
 right: '4%',
 bottom: dataLength > showCount ? '12%' : '8%',
 top: '12%',
 containLabel: true,
 },
 dataZoom: [
 {
 type: 'slider',
 show: dataLength > showCount,
 xAxisIndex: [0],
 start: 0,
 end: endPercent,
 bottom: '2%',
 height: 18,
 borderColor: '#ddd',
 fillerColor: 'rgba(24, 144, 255, 0.15)',
 handleStyle: {
 color: '#1890ff',
 },
 },
 ],
 xAxis: {
 type: 'category',
 data: props.xAxisData,
 axisLabel: {
 fontSize: 11,
 color: '#666',
 interval: 0,
 rotate: dataLength > showCount ? 0 : (dataLength > 5 ? 30 : 0),
 },
 axisLine: {
 lineStyle: {
 color: '#ddd',
 },
 },
 },
 yAxis: {
 type: 'value',
 axisLabel: {
 fontSize: 11,
 color: '#666',
 },
 axisLine: {
 lineStyle: {
 color: '#ddd',
 },
 },
 splitLine: {
 lineStyle: {
 color: '#f0f0f0',
 },
 },
 },
 series: props.series.map((s) => ({
 name: s.name,
 type: 'bar',
 data: s.data,
 itemStyle: {
 color: s.color || '#1890ff',
 borderRadius: [4, 4, 0, 0],
 },
 barWidth: '40%',
 })),
 };
 renderEcharts(option);
};
onMounted(() => {
 initChart();
});
watch([() => props.xAxisData, () => props.series], () => {
 initChart();
}, { deep: true });
</script>

<template>
  <Card :title="title" class="h-full">
    <EchartsUI ref="chartRef" />
    <div v-if="xLabel || yLabel" class="mt-2 text-xs text-gray-400 text-center">
      <span v-if="xLabel">{{ xLabel }}</span>
      <span v-if="xLabel && yLabel"> | </span>
      <span v-if="yLabel">{{ yLabel }}</span>
    </div>
  </Card>
</template>
