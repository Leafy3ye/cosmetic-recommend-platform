
const AMAP_KEY = '5d2760a7e6b4c0e0cda9f1309d05d4b3' 

/**
 * 获取用户城市信息（基于IP）
 */
export async function getUserCity() {
  try {
    // 方式1：使用高德地图IP定位API
    const response = await fetch(`https://restapi.amap.com/v3/ip?key=${AMAP_KEY}`)
    const data = await response.json()
    
    console.log('高德地图API返回：', data)
    
    // 检查返回状态和城市数据
    if (data.status === '1' && data.city && data.city !== '[]' && !Array.isArray(data.city)) {
      return {
        province: data.province,
        city: data.city,
        adcode: data.adcode
      }
    }
    
    // 如果高德API返回[]或失败，使用备用方案
    console.log('高德定位失败或返回空，使用备用方案')
    return await getUserCityFallback()
  } catch (error) {
    console.error('IP定位失败：', error)
    return await getUserCityFallback()
  }
}

/**
 * 备用方案：使用纯真IP库的免费接口
 */
async function getUserCityFallback() {
  try {
    // 使用ipapi.co的免费IP定位接口（支持HTTPS和本地开发）
    const response = await fetch('https://ipapi.co/json/')
    const data = await response.json()
    
    console.log('备用API返回：', data)
    
    if (data.city) {
      return {
        province: data.region || data.city,
        city: data.city,
        adcode: data.postal || '100000'
      }
    }
    
    // 如果还是失败，返回默认城市
    return getDefaultCity()
  } catch (error) {
    console.error('备用IP定位失败：', error)
    return getDefaultCity()
  }
}

/**
 * 返回默认城市（本地开发时使用）
 */
function getDefaultCity() {
  console.log('使用默认城市')
  return {
    province: '浙江省',
    city: '杭州市',
    adcode: '330100'
  }
}

/**
 * 获取用户城市（简化版，只返回城市名）
 */
export async function getCityName() {
  const cityInfo = await getUserCity()
  return cityInfo.city || '全国'
}

/**
 * 保存用户选择的城市
 */
export function saveUserCity(city) {
  localStorage.setItem('user_city', city)
}

/**
 * 获取用户选择的城市
 */
export function getSavedCity() {
  return localStorage.getItem('user_city')
}

/**
 * 初始化城市信息
 */
export async function initCity() {
  const savedCity = getSavedCity()
  if (savedCity) {
    return savedCity
  }
  
  const cityName = await getCityName()
  saveUserCity(cityName)
  return cityName
}
