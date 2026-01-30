<template>
  <div class="product-card" @click="$emit('click')">
    <div class="product-image">
      <img :src="product.image || defaultImage" :alt="product.name" />
    </div>
    <div class="product-info">
      <h4 class="product-name">{{ product.name }}</h4>
      <p class="product-brand">{{ product.brand }}</p>
      <div class="product-bottom">
        <div class="product-price">
          <span class="price">¥{{ product.price }}</span>
          <span class="original-price" v-if="product.originalPrice">¥{{ product.originalPrice }}</span>
        </div>
        <div class="product-sales">
          已售{{ product.sales || 0 }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  product: {
    type: Object,
    required: true
  }
})

defineEmits(['click'])

const defaultImage = 'https://via.placeholder.com/300x300?text=Product+Image'
</script>

<style scoped>
.product-card {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 20px;
}

.product-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transform: translateY(-5px);
}

.product-image {
  width: 100%;
  height: 200px;
  overflow: hidden;
  background: #f5f5f5;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-info {
  padding: 15px;
}

.product-name {
  font-size: 16px;
  color: #333;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  min-height: 44px;
}

.product-brand {
  font-size: 14px;
  color: #999;
  margin-bottom: 12px;
}

.product-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.product-price {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.price {
  font-size: 20px;
  color: #f56c6c;
  font-weight: bold;
}

.original-price {
  font-size: 14px;
  color: #999;
  text-decoration: line-through;
}

.product-sales {
  font-size: 12px;
  color: #999;
}
</style>
