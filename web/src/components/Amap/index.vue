<template>
	<div class="amap" ref="container"></div>
</template>
<script setup>
import AMapLoader from '@amap/amap-jsapi-loader';
import large from '@/assets/user_img/large.png'
import { onMounted, ref } from 'vue';
const emit = defineEmits(['changeName']);
let map = null;
let timeout = false;
let time = 2;
let zoom = 17;
const container = ref();
let pramasKey = {
	key: '44d7ff6ca4f64ab5b595dc8d5b6a0199', // 申请好的Web端开发者Key，首次调用 load 时必填
	version: '2.0', // 指定要加载的 JSAPI 的版本，缺省时默认为 1.4.15
	plugins: [], // 需要使用的的插件列表，如比例尺'AMap.Scale'等
}
// 初始化地图
const initMap = () => {
	AMapLoader.load(pramasKey).then(AMap => {
		map = new AMap.Map(container.value, {
			viewMode: '3D', // 是否为3D地图模式
			zoom: 11, // 初始化地图级别
			center: [113.665412, 34.757975], // 初始化地图中心点位置
		});
		setMarker();
		setFitView();
		bindEvents();
	})
}
// 重置地图
const resetMap = () => {
	AMapLoader.reset();
}
// 添加 marker 表示
const setMarker = (item) => {
	const marker = new AMap.Marker({
		position: new AMap.LngLat(parseFloat(113.665412), parseFloat(34.757975)),
		offset: new AMap.Pixel(-10, -10),
		title: item?.name || 'name',
		icon: new AMap.Icon({ size: new AMap.Size(25, 25), image: large, imageSize: new AMap.Size(25, 25) }), // 添加 Icon 图标 URL
	})
	marker.on('click', (e) => {
		map.setZoomAndCenter(zoom, [e.lnglat.lng, e.lnglat.lat]);
		emit('changeName', e.target._originOpts.title);
		openTimeout();
	})
	map.add(marker);
}
// 地图图标整合
const setFitView = () => {
	map.setFitView();
}
// 开启延时整合
const openTimeout = () => {
	clearTimeout(timeout);
	timeout = setTimeout(() => { setFitView() }, time * 1000)
}
// 整合地图绑定事件
const bindEvents = () => {
	map.on('dblclick', openTimeout);
	map.on('dragend', openTimeout);
	map.on('zoomend', openTimeout);
}
onMounted(() => {
	resetMap();
	initMap();
})
</script>
<style lang="scss">
.amap {
	width: 100%;
	height: 100%;
}
</style>