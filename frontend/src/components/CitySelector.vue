<template>
  <el-dropdown @command="handleCityChange" trigger="click">
    <div class="city-selector">
      <el-icon><Location /></el-icon>
      <span>{{ currentCity }}</span>
      <el-icon class="arrow"><ArrowDown /></el-icon>
    </div>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item command="auto">
          <el-icon><Aim /></el-icon>
          自动定位
        </el-dropdown-item>
        <el-dropdown-item divided>热门城市</el-dropdown-item>
        <el-dropdown-item 
          v-for="city in hotCities" 
          :key="city" 
          :command="city"
        >
          {{ city }}
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Location, ArrowDown, Aim } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { initCity, getCityName, saveUserCity } from '@/utils/location'

const currentCity = ref('定位中...')

// 热门城市列表
const hotCities = [
  '全国', '北京', '上海', '广州', '深圳', 
  '杭州', '成都', '重庆', '武汉', '西安',
  '南京', '天津', '苏州', '郑州', '长沙'
]

const emit = defineEmits(['change'])

onMounted(async () => {
  currentCity.value = await initCity()
  emit('change', currentCity.value)
})

const handleCityChange = async (command) => {
  if (command === 'auto') {
    currentCity.value = '定位中...'
    try {
      const city = await getCityName()
      currentCity.value = city
      saveUserCity(city)
      ElMessage.success(`已切换到：${city}`)
      emit('change', city)
    } catch (error) {
      ElMessage.error('定位失败，请手动选择城市')
      currentCity.value = '全国'
    }
  } else {
    currentCity.value = command
    saveUserCity(command)
    ElMessage.success(`已切换到：${command}`)
    emit('change', command)
  }
}
</script>

<style scoped>
.city-selector {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
  color: white;
  font-size: 14px;
  white-space: nowrap;
}

.city-selector:hover {
  background: rgba(255, 255, 255, 0.25);
}

.arrow {
  font-size: 12px;
}
</style>
