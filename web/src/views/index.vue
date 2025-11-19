<template>
    <el-card style="height: calc(100vh - 90px);">
        <el-row :gutter="10">
            <el-col :xs="24" :sm="12">
                <div class="img-box">
                    <img src="@/assets/images/1111@6x.png" alt="" class="full-width">
                </div>
            </el-col>
            <el-col :xs="24" :sm="1"></el-col>
            <el-col :xs="24" :sm="11">
                <div class="text-left vertical-center">
                    <h2>欢迎进入</h2>
                    <h2>赛事管理系统</h2>
                    <p>开启您的使用之旅吧~</p>
                </div>
            </el-col>
        </el-row>
    </el-card>
</template>

<script setup lang="ts" name="Index">

</script>

<style scoped lang="scss">
.img-box {
    padding: 50px;

    .text-left {
        text-align: left;
    }
}

.vertical-center {
    display: flex;
    flex-direction: column;
    justify-content: center;
    height: 100%;
}

.full-width {
    width: 100%;
    height: 100%;
    object-fit: contain;
}
</style>
