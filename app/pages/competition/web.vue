<template>
  <view class="webview_page" v-if="src">
    <!-- #ifdef H5 -->
    <iframe width="100%" height="100%" :src="src" :title="title"/>
    <!-- #endif -->
    <!-- #ifndef H5 -->
    <image class="webview_img" v-if="src.includes('png')||src.includes('jpeg')||src.includes('gif')||src.includes('jpg')" :src="src" mode="widthFix"/>
    <web-view v-else :webview-styles="wbStyles" :src="src" :fullscreen="false"/>
    <!-- #endif -->
  </view>
</template>

<script>
export default {
  data() {
    return {
      title: '',
      src: '',
      wbStyles: {
        width: '100%',
        height: '100%',
      },
    }
  },
  onLoad(option) {
    this.title = option?.title
    this.src = option?.src
  },
}
</script>
<style scoped>
.webview_page{

}
.webview_img{
  width: 100%;
  height: auto
}
</style>