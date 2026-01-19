<template>
  <div class="image-upload">
    <div class="upload-list">
      <div
        v-for="(url, index) in imageList"
        :key="index"
        class="upload-item"
        :class="{ 'is-cover': index === 0 }"
        draggable="true"
        @dragstart="handleDragStart(index)"
        @dragover.prevent="handleDragOver"
        @drop="handleDrop(index)"
      >
        <el-image
          :src="url"
          fit="cover"
          class="upload-image"
          :preview-src-list="imageList"
          :initial-index="index"
        />
        <div class="upload-actions">
          <el-button
            type="primary"
            link
            size="small"
            @click="handlePreview(index)"
          >
            <el-icon><ZoomIn /></el-icon>
          </el-button>
          <el-button
            type="danger"
            link
            size="small"
            @click="handleRemove(index)"
          >
            <el-icon><Delete /></el-icon>
          </el-button>
        </div>
        <div v-if="index === 0" class="cover-badge">封面</div>
      </div>

      <div
        v-if="imageList.length < maxCount"
        class="upload-trigger"
        @click="handleClick"
        @dragover.prevent="handleDragOver"
        @drop="handleFileDrop"
      >
        <el-icon class="upload-icon"><Plus /></el-icon>
        <div class="upload-text">点击或拖拽上传</div>
        <div class="upload-hint">最多{{ maxCount }}张图片</div>
      </div>
    </div>

    <input
      ref="fileInput"
      type="file"
      accept="image/*"
      multiple
      style="display: none"
      @change="handleFileChange"
    />

    <el-dialog
      v-model="previewVisible"
      title="图片预览"
      width="800px"
      append-to-body
    >
      <el-image
        v-if="previewUrl"
        :src="previewUrl"
        fit="contain"
        style="width: 100%; height: 500px"
      />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, ZoomIn, Delete } from '@element-plus/icons-vue'
import request from '@/utils/request'

const props = defineProps({
  modelValue: {
    type: Array,
    default: () => []
  },
  maxCount: {
    type: Number,
    default: 5
  }
})

const emit = defineEmits(['update:modelValue', 'change'])

const fileInput = ref(null)
const imageList = ref([...props.modelValue])
const previewVisible = ref(false)
const previewUrl = ref('')
const uploading = ref(false)
const uploadProgress = ref(0)

watch(() => props.modelValue, (newVal) => {
  imageList.value = [...newVal]
}, { deep: true })

const handleClick = () => {
  fileInput.value.click()
}

const handleFileChange = async (e) => {
  const files = Array.from(e.target.files)
  if (files.length === 0) return

  await uploadFiles(files)
  e.target.value = ''
}

const handleFileDrop = async (e) => {
  e.preventDefault()
  const files = Array.from(e.dataTransfer.files).filter(file => file.type.startsWith('image/'))
  if (files.length === 0) return

  await uploadFiles(files)
}

const uploadFiles = async (files) => {
  const remainingSlots = props.maxCount - imageList.value.length
  if (remainingSlots <= 0) {
    ElMessage.warning(`最多只能上传${props.maxCount}张图片`)
    return
  }

  const filesToUpload = files.slice(0, remainingSlots)
  
  try {
    uploading.value = true
    uploadProgress.value = 0

    const uploadPromises = filesToUpload.map(async (file) => {
      const formData = new FormData()
      formData.append('file', file)

      const response = await request.post('/upload/image', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        },
        onUploadProgress: (progressEvent) => {
          const percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total)
          uploadProgress.value = percentCompleted
        }
      })

      return response
    })

    const uploadedUrls = await Promise.all(uploadPromises)
    imageList.value = [...imageList.value, ...uploadedUrls]
    emit('update:modelValue', imageList.value)
    emit('change', imageList.value)
    ElMessage.success('上传成功')
  } catch (error) {
    console.error('上传失败', error)
    ElMessage.error('上传失败')
  } finally {
    uploading.value = false
    uploadProgress.value = 0
  }
}

const handleRemove = (index) => {
  imageList.value.splice(index, 1)
  emit('update:modelValue', imageList.value)
  emit('change', imageList.value)
}

const handlePreview = (index) => {
  previewUrl.value = imageList.value[index]
  previewVisible.value = true
}

let dragIndex = null

const handleDragStart = (index) => {
  dragIndex = index
}

const handleDragOver = (e) => {
  e.preventDefault()
}

const handleDrop = (index) => {
  e.preventDefault()
  if (dragIndex === null || dragIndex === index) return

  const item = imageList.value.splice(dragIndex, 1)[0]
  imageList.value.splice(index, 0, item)
  
  emit('update:modelValue', imageList.value)
  emit('change', imageList.value)
  
  dragIndex = null
}
</script>

<style scoped>
.image-upload {
  width: 100%;
}

.upload-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.upload-item {
  position: relative;
  width: 120px;
  height: 120px;
  border-radius: 8px;
  overflow: hidden;
  cursor: move;
  transition: all 0.3s;
}

.upload-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.upload-item.is-cover {
  border: 2px solid #ff4d4f;
}

.upload-image {
  width: 100%;
  height: 100%;
}

.upload-actions {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  background-color: rgba(0, 0, 0, 0.5);
  opacity: 0;
  transition: opacity 0.3s;
}

.upload-item:hover .upload-actions {
  opacity: 1;
}

.upload-actions .el-button {
  color: white;
  font-size: 18px;
}

.cover-badge {
  position: absolute;
  top: 5px;
  right: 5px;
  background-color: #ff4d4f;
  color: white;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 4px;
  z-index: 1;
}

.upload-trigger {
  width: 120px;
  height: 120px;
  border: 2px dashed #d9d9d9;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
  background-color: #fafafa;
}

.upload-trigger:hover {
  border-color: #ff4d4f;
  background-color: #fff5f5;
}

.upload-icon {
  font-size: 32px;
  color: #8c8c8c;
  margin-bottom: 8px;
}

.upload-text {
  font-size: 12px;
  color: #666;
  margin-bottom: 4px;
}

.upload-hint {
  font-size: 10px;
  color: #999;
}
</style>
