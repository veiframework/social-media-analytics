<template>
  <view class="bottom-bar">
    <view
        v-for="(item, index) in tabBar"
        :key="index"
        class="tab-bar-item"
        @click="switchTab(item, index)"
    >
      <!--      <view class="tab-bar-icon">-->
      <!--        &lt;!&ndash; 这里使用简单的文字图标，实际项目中可以替换为图片或图标组件 &ndash;&gt;-->
      <!--        <text class="icon">{{ getIcon(item.text, index === selectedIndex) }}</text>-->
      <!--      </view>-->
      <text class="tab-bar-text" :style="item.checked?{color:'#cea156'}:{color:'#000'}">
        {{ item.text }}
      </text>
    </view>
  </view>
</template>

<script setup>
import {ref, onMounted} from 'vue';

// tabBar配置
const tabBar = ref([]);

// 当前选中的索引
const selectedIndex = ref(0);

const getTabBarList = () => {
  const pages = getCurrentPages();
  const currentPage = pages[pages.length - 1];
  const path = currentPage.route || currentPage.__route__;
  const path0 = '/' + path;
  let res = uni.getStorageSync('routers')
  let arr = [{pagePath: '/pages/index/index', text: '首页'}]
  res.forEach(item => {
    let menuName = item.children[0].name
    let data = null
    if (menuName === 'Work') {
      data = {pagePath: '/pages/work/work', text: '作品'}
    }
    if (menuName === 'Social-media-account') {
      data = {pagePath: '/pages/account/account', text: '账号'}
    }
    if (data) {
      arr.push(data)
    }
  })
  arr.push({pagePath: '/pages/mine/mine', text: '个人中心'})
  arr.forEach(i => {
    if (i.pagePath === path0) {
      i.checked = true
    }
  })
  tabBar.value = arr
}

// 切换tab
const switchTab = (item, index) => {
  // 更新选中状态
  selectedIndex.value = index;
  // 自己封装的页面切换逻辑，不使用switchTab
  uni.redirectTo({
    url: item.pagePath
  });
};

// 获取图标
const getIcon = (text, isSelected) => {
  const icons = {
    '首页': '🏠',
    '作品': '📚',
    '账号': '👤',
    '个人中心': '⚙️'
  };
  return icons[text] || '';
};


// 在组件挂载时更新选中状态
onMounted(() => {
  getTabBarList()
});


</script>

<style scoped>
.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 100rpx;
  background-color: #FFFFFF;
  border-top: 1rpx solid #e8e8e8;
  display: flex;
  justify-content: space-around;
  align-items: center;
  z-index: 999;
  padding-bottom: 30rpx;
}

.tab-bar-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  flex: 1;
}

.tab-bar-icon {
  margin-bottom: 8rpx;
}

.icon {
  font-size: 40rpx;
}

.tab-bar-text {
  font-size: 24rpx;
}


</style>