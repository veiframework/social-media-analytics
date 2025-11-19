<template>
    <div class="image-gallery-container">
        <!-- 图片展示区域 -->
        <div class="image-gallery" v-loading="loading">
            <!-- 新增图片卡片 -->
            <div class="add-image-card" @click="handleAdd">
                <div class="add-image-content">
                    <el-icon class="add-icon"><Plus /></el-icon>
                    <span class="add-text">新增图片</span>
                </div>
            </div>
            
            <!-- 图片列表 -->
            <div 
                v-for="(item, index) in imageList" 
                :key="item.id" 
                class="image-item"
                @click="handleImageClick(item)"
            >
                <div class="image-wrapper">
                    <el-image
                        :src="item.imageUrl"
                        :alt="item.title || '赛事图片'"
                        fit="cover"
                        class="gallery-image"
                        :preview-src-list="previewList"
                        :initial-index="index"
                        preview-teleported
                    >
                        <template #error>
                            <div class="image-error">
                                <el-icon><Picture /></el-icon>
                                <span>加载失败</span>
                            </div>
                        </template>
                    </el-image>
                    
                    <!-- 图片操作蒙层 -->
                    <div class="image-overlay">
                        <div class="image-actions">
                            <el-button 
                                type="primary" 
                                icon="View" 
                                circle 
                                size="small"
                                @click.stop="handleDetail(item)"
                                title="查看详情"
                            />
                            <el-button 
                                type="success" 
                                icon="Edit" 
                                circle 
                                size="small"
                                @click.stop="handleEdit(item)"
                                title="编辑"
                            />
                            <el-button 
                                type="danger" 
                                icon="Delete" 
                                circle 
                                size="small"
                                @click.stop="handleDelete(item)"
                                title="删除"
                            />
                        </div>
                    </div>
                </div>
                
                <!-- 图片信息 -->
                <div class="image-info">
                    <div class="image-time">{{ formatTime(item.createTime) }}</div>
                </div>
            </div>
            
            <!-- 空状态 -->
            <div v-if="!loading && imageList.length === 0" class="empty-state">
                <el-empty description="暂无图片数据" />
            </div>
        </div>

        <!-- 图片表单弹窗 -->
        <CustomDialog 
            :form="form" 
            :option="optionDialog" 
            :visible="visible" 
            @cancel="visible = false" 
            @save="handleSave"
        />

        <!-- 详情弹窗 -->
        <CustomInfo 
            :option="optionInfo" 
            :visible="infoVisible" 
            :rowData="rowData" 
            @cancel="infoVisible = false"
        />
    </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Picture, Plus } from '@element-plus/icons-vue'
import { getImagesByCompetitionIdApi, batchAddCompetitionImageApi, editCompetitionImageApi, delCompetitionImageApi } from '@/api/competitionImage'

import { getDicts } from '@/api/system/dict/data'
import CustomDialog from "@/components/CustomDialog"
import CustomInfo from "@/components/CustomInfo"

const route = useRoute()
const router = useRouter()

// 页面数据
const imageList = ref([])
const loading = ref(false)

// 路由参数
const registrationId = route.query.registrationId
const userId = route.query.userId
const participantName = route.query.participantName || '参赛者'
const competitionId = route.query.competitionId
const competitionName = route.query.competitionName || '赛事'

// 计算预览图片列表
const previewList = computed(() => {
    return imageList.value.map(item => item.imageUrl).filter(url => url)
})

// 字典数据
const statusDict = ref([])
const imageTypeDict = ref([])

// 表单数据
const form = ref({})
const visible = ref(false)

// 详情弹窗
const infoVisible = ref(false)
const rowData = ref({})

// 获取字典数据
const getDict = async () => {
    try {
        const [statusRes, typeRes] = await Promise.all([
            getDicts('image_status'),
            getDicts('image_type')
        ])
        
        statusDict.value = statusRes.data.map(i => ({
            label: i.dictLabel,
            value: i.dictValue
        })) || []
        
        imageTypeDict.value = typeRes.data.map(i => ({
            label: i.dictLabel,
            value: i.dictValue
        })) || []
    } catch (error) {
        console.error('获取字典数据失败:', error)
    }
}

// 获取状态文本
const getStatusText = (status) => {
    const dict = statusDict.value.find(item => item.value === status)
    return dict ? dict.label : status
}

// 获取数据
const getData = async () => {
    if (!competitionId) {
        ElMessage.error('缺少赛事ID参数')
        return
    }
    
    loading.value = true
    try {
        let params ={
            competitionId: competitionId,
            participantId: userId,
            imageType: 'PERSONAL'
        }
        const response = await getImagesByCompetitionIdApi(params)
        imageList.value = response.data || []
    } catch (error) {
        console.error('获取数据失败:', error)
        ElMessage.error('获取数据失败')
    } finally {
        loading.value = false
    }
}

// 格式化时间
const formatTime = (timeStr) => {
    if (!timeStr) return ''
    const date = new Date(timeStr)
    return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
    })
}

// 新增图片
const handleAdd = () => {
    form.value = {
        competitionId: competitionId,
            participantId: userId,
            imageType: 'PERSONAL'
    }
    visible.value = true
}

// 图片点击处理
const handleImageClick = (item) => {
    // 可以在这里添加点击图片的逻辑，比如显示大图
    console.log('点击图片:', item)
}

// 查看详情
const handleDetail = (item) => {
    rowData.value = item
    infoVisible.value = true
}

// 编辑图片
const handleEdit = (item) => {
    form.value = { ...item }
    visible.value = true
}



// 保存处理
const handleSave = async (formData) => {
    try {
        if (formData.id) {
            await editCompetitionImageApi(formData, formData.id)
            ElMessage.success('修改成功')
        } else {
            await batchAddCompetitionImageApi(formData)
            ElMessage.success('新增成功')
        }
        visible.value = false
        getData()
    } catch (error) {
        console.error('保存失败:', error)
        ElMessage.error('保存失败')
    }
}

// 删除处理
const handleDelete = async (item) => {
    try {
        await ElMessageBox.confirm(
            `确认删除图片"${item.title || '未命名'}"吗？`,
            '确认操作',
            {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }
        )
        
        await delCompetitionImageApi(item.id)
        ElMessage.success('删除成功')
        getData()
    } catch (error) {
        if (error !== 'cancel') {
            console.error('删除失败:', error)
            ElMessage.error('删除失败')
        }
    }
}



// 表单配置项
const optionDialog = reactive({
    dialogTitle: '赛事图片信息',
    dialogClass: 'dialog_md',
    labelWidth: '120px',
    formitem: [
        

        {
            type: "uploads",
            label: "图片文件",
            prop: "imageUrls",
            fileType: "img",
            path: "registration",
            uploadType: "ali",
            span: 24,
            default: null
        },
         
    ],
   rules: {
     imageUrl: [{ required: true, message: '请上传图片', trigger: 'change' }],
     status: [{ required: true, message: '请选择状态', trigger: 'change' }],
   }
})

// 详情配置项
const optionInfo = reactive({
    dialogClass: 'dialog_lg',
    labelWidth: '120px',
    /** 表格详情配置项 */
    tableInfoItem: [
        {
            title: '基本信息', 
            column: 2, 
            infoData: [
               
 
            ]
        },
        {
            title: '图片信息', 
            column: 1, 
            infoData: [
                { 
                    type: 'img', 
                    label: '图片预览', 
                    prop: 'imageUrl', 
                    isShow: true,
                    span: 2,
                    imgWidth: 200
                },
                
            ]
        },
        {
            title: '时间信息', 
            column: 2, 
            infoData: [
                { 
                    type: 'text', 
                    label: '上传时间', 
                    prop: 'createTime', 
                    isShow: true 
                }
            ]
        }
    ]
})

// 初始化
onMounted(async () => {
    await getDict()
    getData()
})
</script>

<style scoped>
.image-gallery-container {
  padding: 20px;
  background-color: #f5f5f5;
  min-height: calc(100vh - 140px);
}

.image-gallery {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  padding: 0;
}

.add-image-card {
  background: #fff;
  border: 2px dashed #d9d9d9;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 200px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.add-image-card:hover {
  border-color: #1890ff;
  background-color: #f6ffed;
}

.add-image-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #999;
}

.add-icon {
  font-size: 32px;
  margin-bottom: 8px;
  color: #d9d9d9;
  transition: color 0.3s ease;
}

.add-image-card:hover .add-icon {
  color: #1890ff;
}

.add-text {
  font-size: 14px;
  color: #999;
  transition: color 0.3s ease;
}

.add-image-card:hover .add-text {
  color: #1890ff;
}

.image-item {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  cursor: pointer;
}

.image-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.image-wrapper {
  position: relative;
  width: 100%;
  height: 200px;
  overflow: hidden;
}

.gallery-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.image-item:hover .gallery-image {
  transform: scale(1.05);
}

.image-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #999;
  background-color: #f5f5f5;
}

.image-error .el-icon {
  font-size: 32px;
  margin-bottom: 8px;
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.image-item:hover .image-overlay {
  opacity: 1;
}

.image-actions {
  display: flex;
  gap: 8px;
}

.image-info {
  padding: 16px;
  text-align: center;
}

.image-time {
  font-size: 12px;
  color: #999;
}

.empty-state {
  grid-column: 1 / -1;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 300px;
  border-radius: 8px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .image-gallery {
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
    gap: 16px;
  }
  
  .image-wrapper {
    height: 160px;
  }
  
  .image-gallery-container {
    padding: 16px;
  }
}

@media (max-width: 480px) {
  .image-gallery {
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 12px;
  }
  
  .image-wrapper {
    height: 140px;
  }
}
</style>