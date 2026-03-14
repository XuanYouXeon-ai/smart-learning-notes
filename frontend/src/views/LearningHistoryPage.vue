<!--
  LearningHistoryPage.vue
  学习记录页面 - 展示学习历史和统计信息
-->
<script setup lang="ts">
  import { History } from 'lucide-vue-next'
</script>

<template>
  <div class="space-y-6">
    <div>
      <h1 class="text-2xl font-bold text-gray-800">学习记录</h1>
      <p class="text-gray-500 mt-1">查看您的学习历史和进度统计</p>
    </div>

    <div class="bg-white rounded-lg shadow-sm border border-gray-100 p-6">
      <div class="text-center py-12">
        <div
          class="w-16 h-16 bg-blue-50 rounded-full flex items-center justify-center mx-auto mb-4"
        >
          <History class="w-8 h-8 text-blue-500" />
        </div>
        <h3 class="text-lg font-semibold text-gray-800 mb-2">功能开发中</h3>
        <p class="text-gray-500 max-w-md mx-auto">
          学习记录功能即将上线，您可以查看完整的学习历史、复习记录和效率统计。
        </p>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
