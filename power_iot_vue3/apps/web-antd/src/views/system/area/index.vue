<script lang="ts" setup>
import type { SystemAreaApi } from '#/api/system/area';

import { nextTick, onMounted, ref } from 'vue';

import { Page, useVbenModal } from '@vben/common-ui';

import { Card, message } from 'ant-design-vue';

import { getAreaTree } from '#/api/system/area';

import AreaGrid from './modules/area-grid.vue';
import ExportModal from './modules/export.vue';
import FormModal from './modules/form.vue';
import ImportModal from './modules/import.vue';
import SubAreaGrid from './modules/sub-area-grid.vue';

const [FormModalComp, formModalApi] = useVbenModal({
  connectedComponent: FormModal,
  destroyOnClose: true,
});

const [ImportModalComp] = useVbenModal({
  connectedComponent: ImportModal,
  destroyOnClose: true,
});

const [ExportModalComp] = useVbenModal({
  connectedComponent: ExportModal,
  destroyOnClose: true,
});

// 地区树数据
const areaTreeData = ref<any[]>([]);
const expandedKeys = ref<number[]>([]);
const selectedKeys = ref<number[]>([]);
const selectedNodeData = ref<null | SystemAreaApi.Area>(null);

// 左侧表格选中的行数据
const leftSelectedRow = ref<null | SystemAreaApi.Area>(null);

// 组件引用
const areaGridRef = ref<InstanceType<typeof AreaGrid> | null>(null);
const subAreaGridRef = ref<InstanceType<typeof SubAreaGrid> | null>(null);

/** 刷新表格 */
function handleRefresh() {
  areaGridRef.value?.refresh();
}

/** 刷新右侧表格 */
function handleRightRefresh() {
  subAreaGridRef.value?.refresh();
}

/** 编辑行政区划 */
function handleEdit(row: SystemAreaApi.Area) {
  formModalApi.setData(row).open();
}

/** 新增行政区划 */
function handleCreate() {
  if (!leftSelectedRow.value) {
    message.warning('请先选择上级行政区划');
    return;
  }
  formModalApi
    .setData({
      parentId: leftSelectedRow.value.id,
      parentName: leftSelectedRow.value.extName,
    })
    .open();
}

// 加载地区树
async function loadAreaTree() {
  try {
    const result = await getAreaTree({ levels: [0, 1, 2] });
    areaTreeData.value = result || [];

    // 找到河北省节点（name 是"河北"，extName 是"河北省"）
    const hebeiNode = areaTreeData.value.find(
      (node) => node.name === '河北' || node.extName === '河北省',
    );

    if (hebeiNode) {
      // 默认选中河北省最里层的第一个子节点（如长安区）
      const deepestFirstNode = findDeepestFirstNode(hebeiNode);

      // 展开河北省和石家庄市
      const expandKeys: number[] = [hebeiNode.id];
      if (hebeiNode.children && hebeiNode.children.length > 0) {
        const firstChild = hebeiNode.children[0];
        expandKeys.push(firstChild.id);
      }

      expandedKeys.value = [...expandKeys];
      selectedKeys.value = [deepestFirstNode.id];
      selectedNodeData.value = deepestFirstNode || null;
    } else {
      // 如果找不到河北省，使用原逻辑
      const provinceKeys = findProvinceKeys(areaTreeData.value);
      expandedKeys.value = provinceKeys.map(Number);
      if (areaTreeData.value.length > 0) {
        const firstNode = areaTreeData.value[0];
        selectedKeys.value = [firstNode.id];
        selectedNodeData.value = firstNode;
      }
    }
    nextTick(() => {
      handleRefresh();
    });
  } catch {
    message.error('加载行政区划树失败');
  }
}

onMounted(() => {
  loadAreaTree();
});

// 查找省级节点
function findProvinceKeys(nodes: any[]): string[] {
  const keys: string[] = [];
  for (const node of nodes) {
    if (node.children && node.children.length > 0) {
      keys.push(String(node.id));
    }
  }
  return keys;
}

// 递归查找最里层的第一个子节点
function findDeepestFirstNode(node: any): any {
  if (node.children && node.children.length > 0) {
    return findDeepestFirstNode(node.children[0]);
  }
  return node;
}

// 树节点选择
function handleTreeSelect(selectedKeysValue: string[], info: any) {
  selectedKeys.value = selectedKeysValue.map(Number);
  selectedNodeData.value = info?.node?.dataRef || null;
  leftSelectedRow.value = null;
  nextTick(() => {
    handleRefresh();
  });
}

// 树节点展开/收起
function handleTreeExpand(expandedKeysValue: string[]) {
  expandedKeys.value = expandedKeysValue.map(Number);
}

// 左侧表格行选择
function handleAreaSelect(row: SystemAreaApi.Area) {
  leftSelectedRow.value = row;
  nextTick(() => {
    handleRightRefresh();
  });
}
</script>

<template>
  <Page auto-content-height>
    <FormModalComp
      @success="
        () => {
          handleRefresh();
          handleRightRefresh();
        }
      "
    />
    <ImportModalComp
      @success="
        () => {
          handleRefresh();
          handleRightRefresh();
        }
      "
    />
    <ExportModalComp
      @success="
        () => {
          handleRefresh();
          handleRightRefresh();
        }
      "
    />

    <div class="flex h-full overflow-hidden">
      <!-- 左侧树形区 - 固定宽度 270px -->
      <div class="w-[270px] pr-3 flex-shrink-0">
        <Card class="h-full" title="行政区划">
          <a-tree
            :tree-data="areaTreeData"
            :expanded-keys="expandedKeys"
            :selected-keys="selectedKeys"
            :auto-expand-parent="true"
            @select="handleTreeSelect"
            @expand="handleTreeExpand"
            :field-names="{
              children: 'children',
              title: 'extName',
              key: 'id',
            }"
          />
        </Card>
      </div>

      <!-- 右侧两个表格区，各占 50% -->
      <div class="flex-1 flex overflow-hidden">
        <!-- 左侧表格区 -->
        <div class="flex-1 pr-3 min-w-0">
          <AreaGrid
            ref="areaGridRef"
            :parent-id="selectedNodeData?.id"
            :parent-ext-name="selectedNodeData?.extName"
            @edit="handleEdit"
            @select="handleAreaSelect"
          />
        </div>

        <!-- 右侧表格区 -->
        <div class="flex-1 min-w-0">
          <SubAreaGrid
            ref="subAreaGridRef"
            :parent-id="leftSelectedRow?.id"
            :parent-ext-name="leftSelectedRow?.extName"
            @edit="handleEdit"
            @select="handleAreaSelect"
            @create="handleCreate"
          />
        </div>
      </div>
    </div>
  </Page>
</template>
