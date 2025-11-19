<template>
	<div class="amap" ref="container"></div>
</template>
<script setup>
import AMapLoader from '@amap/amap-jsapi-loader';
import { ElMessage } from 'element-plus';
import { onMounted, onUnmounted, ref } from 'vue';
const emit = defineEmits(['changeLnglat', 'changeAddress']);
let map = null;
let geocoder = null;
const container = ref();
let pramasKey = {
	key: '44d7ff6ca4f64ab5b595dc8d5b6a0199', // 申请好的Web端开发者Key，首次调用 load 时必填
	version: '2.0', // 指定要加载的 JSAPI 的版本，缺省时默认为 1.4.15
	plugins: [], // 需要使用的的插件列表，如比例尺'AMap.Scale'等
}
let isInit = false;
// 初始化地图
const initMap = () => {
	return new Promise((resolve, reject) => {
		window._AMapSecurityConfig = { securityJsCode: "1178d4c1f6b125694994233775bb5e41" }
		AMapLoader.load(pramasKey).then(AMap => {
			map = new AMap.Map(container.value, {
				viewMode: '3D', // 是否为3D地图模式
				zoom: 11, // 初始化地图级别
				center: [113.665412, 34.757975], // 初始化地图中心点位置
			});
			isInit = true;
			map.on("click", clickListener);
			AMap.plugin("AMap.Geocoder", () => geocoder = new AMap.Geocoder())
			resolve();
		})
	})
}
// 重置地图
const resetMap = () => {
	AMapLoader.reset();
}
// 点击事件
const clickListener = function (e) {
	regeoCode(e.lnglat.getLng(), e.lnglat.getLat());
	addMarker({ lng: e.lnglat.getLng(), lat: e.lnglat.getLat() })
};
// 添加坐标
const addMarker = (e) => {
	setMarker(e);
	emit('changeLnglat', { lng: e.lng, lat: e.lat });
}
// 设置地图坐标点
const setMarker = (e) => {
	if (isInit) {
		clearMarker();
		new AMap.Marker({ position: new AMap.LngLat(parseFloat(e.lng), parseFloat(e.lat)), map: map });
		map.panTo([e.lng, e.lat]);
	} else {
		initMap().then(res => {
			new AMap.Marker({ position: new AMap.LngLat(parseFloat(e.lng), parseFloat(e.lat)), map: map });
			map.panTo([e.lng, e.lat]);
		})
	}
}
// 输入坐标失焦解析
const blurMarker = (e) => {
	if (!e.lng || !e.lat) return;
	if (map) {
		const lng = Number(e.lng).toFixed(6);
		const lat = Number(e.lat).toFixed(6);
		regeoCode(lng, lat)
		setMarker({ lng, lat })
	} else {
		initMap().then(() => {
			blurMarker(e);
		})
	}

}
// 清除坐标点
const clearMarker = () => {
	map.clearMap();
}
// 初始化地图
const reloadMap = () => {
	if (isInit) {
		map.setZoomAndCenter(11, [113.665412, 34.757975]);
		clearMarker();
	} else {
		initMap();
	}
}
// 坐标解析地址
const regeoCode = (lng, lat) => {
	geocoder.getAddress([lng, lat], (status, result) => {
		status === 'complete' && result.regeocode ? emit('changeAddress', result.regeocode) : ElMessage.warning("根据经纬度查询地址失败！");
	});
}
// 地址解析坐标
const geoCode = (address) => {
	geocoder.getLocation(address, (status, result) => {
		status === 'complete' && result.geocodes.length ? addMarker(result.geocodes[0].location) : ElMessage.warning("未解析到坐标！");
	});
}
onMounted(() => {
	resetMap();
})
onUnmounted(() => {
	map = null;
})
defineExpose({ geoCode, blurMarker, reloadMap, setMarker })
</script>
<style lang="scss">
.amap {
	width: 100%;
	height: 100%;
}
</style>