<!--
  HomePage.vue
  首页 - 展示平台介绍、快捷操作和学习统计
-->
<script setup lang="ts">
  import { ref, onMounted } from 'vue'
  import { useRouter } from 'vue-router'
  import { noteApi } from '@/api'
  import {
    Sparkles,
    Upload,
    Folder,
    FileText,
    Brain,
    MessageSquare,
    Target,
    ArrowRight
  } from 'lucide-vue-next'

  const router = useRouter()

  const stats = ref({
    totalNotes: 0,
    completedNotes: 0,
    reviewCount: 0,
    qaCount: 0
  })

  const loading = ref(true)

  onMounted(async () => {
    try {
      const response = await noteApi.getList(1, 100)
      stats.value.totalNotes = response.total
      stats.value.completedNotes = response.notes.filter(
        (n) => n.analyzeStatus === 'COMPLETED'
      ).length
    } catch {
      // 使用默认值
    } finally {
      loading.value = false
    }
  })

  const goToUpload = () => {
    router.push('/notes/upload')
  }

  const goToNotes = () => {
    router.push('/notes')
  }
</script>

<template>
  <div class="space-y-8">
    <!-- 欢迎区域 -->
    <div
      v-motion
      :initial="{ opacity: 0, y: 20 }"
      :enter="{ opacity: 1, y: 0 }"
      class="text-center mb-12"
    >
      <div
        class="inline-flex items-center gap-2 px-4 py-2 bg-primary-100 dark:bg-primary-900/30 text-primary-600 dark:text-primary-400 rounded-full text-sm font-medium mb-6"
      >
        <Sparkles class="w-4 h-4" />
        AI 驱动的智能学习平台
      </div>
      <h1 class="text-4xl md:text-5xl font-bold text-slate-800 dark:text-white mb-4">
        欢迎使用智能学习笔记
      </h1>
      <p class="text-lg text-slate-600 dark:text-slate-300 max-w-2xl mx-auto">
        利用 AI 技术提升学习效率，智能管理笔记，科学复习，轻松掌握知识
      </p>
    </div>

    <!-- 快捷操作 -->
    <div
      v-motion
      :initial="{ opacity: 0, y: 20 }"
      :enter="{ opacity: 1, y: 0, transition: { delay: 100 } }"
      class="grid grid-cols-1 md:grid-cols-2 gap-4"
    >
      <button
        class="group relative bg-gradient-to-r from-primary-500 to-primary-600 rounded-2xl p-6 text-left overflow-hidden"
        @click="goToUpload"
      >
        <div
          class="absolute inset-0 bg-gradient-to-r from-primary-600 to-indigo-600 opacity-0 group-hover:opacity-100 transition-opacity duration-300"
        ></div>
        <div class="relative z-10">
          <div class="w-12 h-12 bg-white/20 rounded-xl flex items-center justify-center mb-4">
            <Upload class="w-6 h-6 text-white" />
          </div>
          <h3 class="text-xl font-bold text-white mb-2">上传笔记</h3>
          <p class="text-white/80 text-sm">上传学习资料，AI 自动分析提取知识点</p>
        </div>
        <ArrowRight
          class="absolute right-6 top-1/2 -translate-y-1/2 w-6 h-6 text-white/50 group-hover:text-white group-hover:translate-x-1 transition-all"
        />
      </button>

      <button
        class="group relative bg-white dark:bg-slate-800 rounded-2xl p-6 text-left border border-slate-200 dark:border-slate-700 hover:border-primary-300 dark:hover:border-primary-600 transition-all duration-300 shadow-sm hover:shadow-lg"
        @click="goToNotes"
      >
        <div
          class="w-12 h-12 bg-primary-100 dark:bg-primary-900/50 rounded-xl flex items-center justify-center mb-4"
        >
          <Folder class="w-6 h-6 text-primary-600 dark:text-primary-400" />
        </div>
        <h3 class="text-xl font-bold text-slate-800 dark:text-white mb-2">浏览笔记库</h3>
        <p class="text-slate-500 dark:text-slate-400 text-sm">查看和管理已上传的所有学习笔记</p>
        <ArrowRight
          class="absolute right-6 top-1/2 -translate-y-1/2 w-6 h-6 text-slate-300 group-hover:text-primary-500 group-hover:translate-x-1 transition-all"
        />
      </button>
    </div>

    <!-- 功能卡片 -->
    <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
      <div
        v-motion
        :initial="{ opacity: 0, y: 20 }"
        :enter="{ opacity: 1, y: 0, transition: { delay: 200 } }"
        class="bg-white dark:bg-slate-800 rounded-2xl shadow-lg hover:shadow-xl transition-all duration-300 p-6 border border-slate-100 dark:border-slate-700"
      >
        <div
          class="w-14 h-14 bg-blue-100 dark:bg-blue-900/30 rounded-xl flex items-center justify-center text-blue-600 dark:text-blue-400 mb-4"
        >
          <FileText class="w-7 h-7" />
        </div>
        <h2 class="text-xl font-semibold text-slate-800 dark:text-white mb-2">笔记管理</h2>
        <p class="text-slate-600 dark:text-slate-400">
          上传、管理和组织您的学习笔记，支持多种格式，轻松查找和分类
        </p>
      </div>

      <div
        v-motion
        :initial="{ opacity: 0, y: 20 }"
        :enter="{ opacity: 1, y: 0, transition: { delay: 300 } }"
        class="bg-white dark:bg-slate-800 rounded-2xl shadow-lg hover:shadow-xl transition-all duration-300 p-6 border border-slate-100 dark:border-slate-700"
      >
        <div
          class="w-14 h-14 bg-emerald-100 dark:bg-emerald-900/30 rounded-xl flex items-center justify-center text-emerald-600 dark:text-emerald-400 mb-4"
        >
          <Brain class="w-7 h-7" />
        </div>
        <h2 class="text-xl font-semibold text-slate-800 dark:text-white mb-2">智能复习</h2>
        <p class="text-slate-600 dark:text-slate-400">
          基于艾宾浩斯遗忘曲线的科学复习系统，智能安排复习时间
        </p>
      </div>

      <div
        v-motion
        :initial="{ opacity: 0, y: 20 }"
        :enter="{ opacity: 1, y: 0, transition: { delay: 400 } }"
        class="bg-white dark:bg-slate-800 rounded-2xl shadow-lg hover:shadow-xl transition-all duration-300 p-6 border border-slate-100 dark:border-slate-700"
      >
        <div
          class="w-14 h-14 bg-purple-100 dark:bg-purple-900/30 rounded-xl flex items-center justify-center text-purple-600 dark:text-purple-400 mb-4"
        >
          <MessageSquare class="w-7 h-7" />
        </div>
        <h2 class="text-xl font-semibold text-slate-800 dark:text-white mb-2">智能问答</h2>
        <p class="text-slate-600 dark:text-slate-400">
          基于知识库的 AI 智能问答助手，快速解答您的学习问题
        </p>
      </div>
    </div>

    <!-- 统计信息 -->
    <div
      v-motion
      :initial="{ opacity: 0, y: 20 }"
      :enter="{ opacity: 1, y: 0, transition: { delay: 500 } }"
      class="bg-white dark:bg-slate-800 rounded-2xl shadow-lg p-6 border border-slate-100 dark:border-slate-700"
    >
      <div class="flex items-center gap-3 mb-6">
        <div
          class="w-10 h-10 bg-primary-100 dark:bg-primary-900/50 rounded-xl flex items-center justify-center"
        >
          <Target class="w-5 h-5 text-primary-600 dark:text-primary-400" />
        </div>
        <h2 class="text-xl font-semibold text-slate-800 dark:text-white">学习概览</h2>
      </div>
      <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
        <div class="text-center p-4 bg-slate-50 dark:bg-slate-700/50 rounded-xl">
          <p class="text-3xl font-bold text-primary-600 dark:text-primary-400">
            {{ stats.totalNotes }}
          </p>
          <p class="text-slate-600 dark:text-slate-400 text-sm mt-1">笔记总数</p>
        </div>
        <div class="text-center p-4 bg-slate-50 dark:bg-slate-700/50 rounded-xl">
          <p class="text-3xl font-bold text-emerald-600 dark:text-emerald-400">
            {{ stats.completedNotes }}
          </p>
          <p class="text-slate-600 dark:text-slate-400 text-sm mt-1">已完成分析</p>
        </div>
        <div class="text-center p-4 bg-slate-50 dark:bg-slate-700/50 rounded-xl">
          <p class="text-3xl font-bold text-blue-600 dark:text-blue-400">
            {{ stats.reviewCount }}
          </p>
          <p class="text-slate-600 dark:text-slate-400 text-sm mt-1">复习次数</p>
        </div>
        <div class="text-center p-4 bg-slate-50 dark:bg-slate-700/50 rounded-xl">
          <p class="text-3xl font-bold text-purple-600 dark:text-purple-400">
            {{ stats.qaCount }}
          </p>
          <p class="text-slate-600 dark:text-slate-400 text-sm mt-1">问答次数</p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
