import { defineConfig } from '@vben/vite-config';

export default defineConfig(async () => {
  // 根据环境模式动态设置 base
  // 开发模式（dev）设置为空字符串，生产模式（build）设置为 '/base/power-web/'
  const isProduction: boolean = process.env.NODE_ENV === 'production';
  const base: string = isProduction ? '/base/power-web/' : '';
  const buildName: string = 'power-web';



  return {
    application: {
      // 配置压缩插件，设置压缩文件名
      archiverPluginOptions: {
        name: buildName,
      },
    },
    vite: {
      base,
      server: {
        allowedHosts: true,
        proxy: {
          '/admin-api': {
            changeOrigin: true,
            rewrite: (path) => path.replace(/^\/admin-api/, ''),
            // mock代理目标地址
            target: 'http://localhost:48080/admin-api',
            ws: true,
          },
        },
      },
      optimizeDeps: {
        include: [
          'vue',
          'vue-router',
          'pinia',
          'ant-design-vue',
          'dayjs',
          'axios',
        ],
        exclude: [
          'tinymce',
          '@tinymce/tinymce-vue',
          'bpmn-js',
          'bpmn-js-properties-panel',
          'bpmn-js-token-simulation',
          'video.js',
          '@videojs-player/vue',
          'benz-amr-recorder',
          'markmap-lib',
          'markmap-view',
          '@tinyflow-ai/vue',
        ],
      },
      build: {
        // 构建输出目录设置为 power-web
        outDir: buildName,
        // 优化chunk分割策略，减少小文件数量
        rollupOptions: {
          output: {
            // 手动分割chunk，将大依赖合并（函数格式）
            manualChunks(id) {
              if (id.includes('node_modules')) {
                // Vue核心
                if (id.includes('/vue/') || id.includes('/vue-router/') || id.includes('/pinia/')) {
                  return 'vue-vendor';
                }
                // Ant Design Vue
                if (id.includes('/ant-design-vue/') || id.includes('/@ant-design/')) {
                  return 'antd-vendor';
                }
                // 工具库
                if (id.includes('/dayjs/') || id.includes('/axios/') || id.includes('/lodash-es/') || id.includes('/nanoid/')) {
                  return 'utils-vendor';
                }
                // 图表库
                if (id.includes('/echarts/') || id.includes('/vue-echarts/')) {
                  return 'charts-vendor';
                }
                // 富文本编辑器
                if (id.includes('/tinymce/') || id.includes('/@tinymce/')) {
                  return 'editor-vendor';
                }
                // 拖拽排序
                if (id.includes('/vuedraggable/') || id.includes('/sortablejs/')) {
                  return 'drag-vendor';
                }
                // VXE表格
                if (id.includes('/vxe-table/') || id.includes('/vxe-pc-ui/')) {
                  return 'vxe-vendor';
                }
                // 代码高亮
                if (id.includes('/highlight.js/')) {
                  return 'highlight-vendor';
                }
              }
            },
          },
        },
      },
    },
  };
});
