<template>
  <view v-if="visible" class="watermark-overlay" :style="watermarkStyle"></view>
</template>

<script setup>
import {computed} from 'vue'

// 定义props
const props = defineProps({
  // 控制水印显示/隐藏
  visible: {
    type: Boolean,
    default: true
  },
  // 水印文本内容
  text: {
    type: String,
    default: '水印'
  }
})

// 计算水印样式
const watermarkStyle = computed(() => {
  // 创建SVG背景
  const svg = `<svg xmlns="http://www.w3.org/2000/svg" width="400" height="200" opacity="0.10"><text x="50%" y="50%" font-family="Arial" font-size="14" fill="black" text-anchor="middle" transform="rotate(-15, 100, 100)">${props.text}</text></svg>`
  const svgData = `data:image/svg+xml;utf8,${encodeURIComponent(svg)}`

  return {
    backgroundImage: `url('${svgData}')`,
    backgroundSize: '400px 200px'
  }
})
</script>

<style scoped>
/* 水印遮罩层 */
.watermark-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 9999;
  background-color: transparent;
}
</style>