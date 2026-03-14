<!--
  SmartQAPage.vue
  智能问答页面 - 基于知识库的 AI 智能问答助手
-->
<script setup lang="ts">
  import { MessageSquare } from 'lucide-vue-next'
</script>

<template>
  <div class="space-y-6">
    <div>
      <h1 class="text-2xl font-bold text-gray-800">智能问答</h1>
      <p class="text-gray-500 mt-1">基于知识库的 AI 智能问答助手</p>
    </div>

    <div class="bg-white rounded-lg shadow-sm border border-gray-100 p-6">
      <div class="text-center py-12">
        <div
          class="w-16 h-16 bg-blue-50 rounded-full flex items-center justify-center mx-auto mb-4"
        >
          <MessageSquare class="w-8 h-8 text-blue-500" />
        </div>
        <h3 class="text-lg font-semibold text-gray-800 mb-2">功能开发中</h3>
        <p class="text-gray-500 max-w-md mx-auto">
          智能问答功能即将上线，您可以基于已上传的笔记内容进行智能问答，快速获取知识点解答。
        </p>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
