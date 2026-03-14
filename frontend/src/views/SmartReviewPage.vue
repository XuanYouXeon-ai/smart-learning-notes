<!--
  SmartReviewPage.vue
  智能复习页面 - 基于艾宾浩斯遗忘曲线的科学复习系统
-->
<script setup lang="ts">
  import { BookOpen } from 'lucide-vue-next'
</script>

<template>
  <div class="space-y-6">
    <div>
      <h1 class="text-2xl font-bold text-gray-800">智能复习</h1>
      <p class="text-gray-500 mt-1">基于艾宾浩斯遗忘曲线的科学复习系统</p>
    </div>

    <div class="bg-white rounded-lg shadow-sm border border-gray-100 p-6">
      <div class="text-center py-12">
        <div
          class="w-16 h-16 bg-blue-50 rounded-full flex items-center justify-center mx-auto mb-4"
        >
          <BookOpen class="w-8 h-8 text-blue-500" />
        </div>
        <h3 class="text-lg font-semibold text-gray-800 mb-2">功能开发中</h3>
        <p class="text-gray-500 max-w-md mx-auto">
          智能复习功能即将上线，系统将根据您的学习记录和遗忘曲线，自动安排最优复习计划。
        </p>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
