<!--
  NoteUploadPage.vue
  笔记上传页面 - 支持拖拽上传、进度显示、错误处理
-->
<script setup lang="ts">
  import { useFileUpload } from '@/composables'
  import { Upload, CheckCircle, XCircle, FileText, Sparkles } from 'lucide-vue-next'
  import type { AnalyzeStatus } from '@/types'

  const {
    fileInput,
    state,
    handleDragOver,
    handleDragLeave,
    handleDrop,
    handleFileInputChange,
    reset,
    triggerFileInput
  } = useFileUpload()

  const getStatusLabel = (status: AnalyzeStatus): string => {
    const statusMap: Record<AnalyzeStatus, string> = {
      PENDING: '等待处理',
      PROCESSING: '正在分析',
      COMPLETED: '分析完成',
      FAILED: '分析失败'
    }
    return statusMap[status] || status
  }

  const getStatusColor = (status: AnalyzeStatus): string => {
    const colorMap: Record<AnalyzeStatus, string> = {
      PENDING: 'bg-yellow-100 text-yellow-700 dark:bg-yellow-900/30 dark:text-yellow-400',
      PROCESSING: 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400',
      COMPLETED: 'bg-emerald-100 text-emerald-700 dark:bg-emerald-900/30 dark:text-emerald-400',
      FAILED: 'bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400'
    }
    return colorMap[status] || colorMap.PENDING
  }
</script>

<template>
  <div class="max-w-4xl mx-auto">
    <!-- 页面标题 -->
    <div v-motion :initial="{ opacity: 0, y: 20 }" :enter="{ opacity: 1, y: 0 }" class="mb-8">
      <h1 class="text-3xl font-bold text-slate-800 dark:text-white mb-2">上传学习笔记</h1>
      <p class="text-slate-600 dark:text-slate-400">
        上传您的学习资料，AI 将自动分析并提取核心知识点
      </p>
    </div>

    <!-- 上传区域 -->
    <div
      v-motion
      :initial="{ opacity: 0, y: 20 }"
      :enter="{ opacity: 1, y: 0, transition: { delay: 100 } }"
      class="bg-white dark:bg-slate-800 rounded-2xl shadow-lg p-8 border border-slate-100 dark:border-slate-700"
    >
      <!-- 拖拽上传区 -->
      <div
        class="relative rounded-2xl cursor-pointer transition-all duration-300"
        :class="{
          'scale-[1.02]': state.isDragging
        }"
        @dragover="handleDragOver"
        @dragleave="handleDragLeave"
        @drop="handleDrop"
        @click="triggerFileInput"
      >
        <!-- 渐变边框 -->
        <div
          class="absolute inset-0 rounded-2xl p-[2px] -z-10"
          :class="
            state.isDragging
              ? 'bg-gradient-to-r from-primary-400 via-purple-400 to-primary-400'
              : 'bg-gradient-to-r from-slate-200 via-slate-200 to-slate-200 dark:from-slate-700 dark:via-slate-700 dark:to-slate-700'
          "
        >
          <div class="w-full h-full bg-white dark:bg-slate-800 rounded-2xl"></div>
        </div>

        <input
          ref="fileInput"
          type="file"
          class="hidden"
          accept=".pdf,.doc,.docx,.txt,.md"
          @change="handleFileInputChange"
        />

        <div class="py-16 text-center">
          <div class="mb-6">
            <div
              class="w-20 h-20 mx-auto rounded-2xl flex items-center justify-center transition-all duration-300"
              :class="
                state.isDragging
                  ? 'bg-primary-100 dark:bg-primary-900/50'
                  : 'bg-slate-100 dark:bg-slate-700'
              "
            >
              <Upload
                class="w-10 h-10 transition-all duration-300"
                :class="state.isDragging ? 'text-primary-500' : 'text-slate-400'"
              />
            </div>
          </div>
          <h3 class="text-xl font-semibold text-slate-800 dark:text-white mb-2">
            {{ state.isDragging ? '松开以上传文件' : '点击或拖拽文件到此处' }}
          </h3>
          <p class="text-slate-500 dark:text-slate-400 mb-6">
            支持 PDF、Word、Markdown、文本文件（最大 50MB）
          </p>
          <button
            type="button"
            class="inline-flex items-center gap-2 px-6 py-3 bg-gradient-to-r from-primary-500 to-primary-600 text-white rounded-xl font-semibold shadow-lg shadow-primary-500/30 hover:shadow-xl hover:shadow-primary-500/40 transition-all"
            @click.stop="triggerFileInput"
          >
            <FileText class="w-5 h-5" />
            选择文件
          </button>
        </div>
      </div>

      <!-- 上传进度 -->
      <div v-if="state.isUploading" class="mt-8">
        <div class="flex items-center justify-between mb-2">
          <span class="text-sm font-medium text-slate-700 dark:text-slate-300">上传中...</span>
          <span class="text-sm font-medium text-primary-600 dark:text-primary-400"
            >{{ state.progress }}%</span
          >
        </div>
        <div class="w-full bg-slate-200 dark:bg-slate-700 rounded-full h-3 overflow-hidden">
          <div
            class="bg-gradient-to-r from-primary-500 to-primary-600 h-3 rounded-full transition-all duration-300 ease-out"
            :style="{ width: `${state.progress}%` }"
          ></div>
        </div>
      </div>

      <!-- 错误提示 -->
      <div
        v-if="state.error"
        v-motion
        :initial="{ opacity: 0, y: -10 }"
        :enter="{ opacity: 1, y: 0 }"
        class="mt-6 p-4 bg-red-50 dark:bg-red-900/30 border border-red-200 dark:border-red-800 rounded-xl"
      >
        <div class="flex items-start gap-3">
          <XCircle class="w-5 h-5 text-red-500 flex-shrink-0 mt-0.5" />
          <div>
            <h4 class="font-semibold text-red-700 dark:text-red-400">上传失败</h4>
            <p class="text-red-600 dark:text-red-300 mt-1">{{ state.error }}</p>
          </div>
        </div>
      </div>

      <!-- 成功提示 -->
      <div
        v-if="state.success && state.note"
        v-motion
        :initial="{ opacity: 0, y: 10 }"
        :enter="{ opacity: 1, y: 0 }"
        class="mt-6 p-6 bg-emerald-50 dark:bg-emerald-900/30 border border-emerald-200 dark:border-emerald-800 rounded-xl"
      >
        <div class="flex items-start gap-4">
          <div
            class="w-12 h-12 bg-emerald-100 dark:bg-emerald-900/50 rounded-xl flex items-center justify-center flex-shrink-0"
          >
            <CheckCircle class="w-6 h-6 text-emerald-600 dark:text-emerald-400" />
          </div>
          <div class="flex-1">
            <h4 class="text-lg font-semibold text-emerald-700 dark:text-emerald-300">上传成功！</h4>
            <div class="mt-3 space-y-2">
              <div class="flex items-center gap-2">
                <FileText class="w-4 h-4 text-slate-500" />
                <span class="text-slate-700 dark:text-slate-300">{{ state.note.filename }}</span>
              </div>
              <div class="flex items-center gap-2">
                <span
                  :class="[
                    'px-2.5 py-1 rounded-full text-xs font-medium',
                    getStatusColor(state.note.analyzeStatus)
                  ]"
                >
                  {{ getStatusLabel(state.note.analyzeStatus) }}
                </span>
              </div>
            </div>
            <div class="mt-4 flex gap-3">
              <RouterLink
                :to="`/notes/${state.note.id}`"
                class="inline-flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-primary-500 to-primary-600 text-white rounded-xl font-medium shadow-lg shadow-primary-500/30 hover:shadow-xl transition-all"
              >
                <Sparkles class="w-4 h-4" />
                查看分析结果
              </RouterLink>
              <button
                class="px-4 py-2 border border-slate-200 dark:border-slate-600 text-slate-600 dark:text-slate-300 rounded-xl font-medium hover:bg-slate-50 dark:hover:bg-slate-700 transition-colors"
                @click="reset"
              >
                上传新笔记
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 使用说明 -->
    <div
      v-motion
      :initial="{ opacity: 0, y: 20 }"
      :enter="{ opacity: 1, y: 0, transition: { delay: 200 } }"
      class="mt-8 grid grid-cols-1 md:grid-cols-3 gap-4"
    >
      <div
        class="bg-white dark:bg-slate-800 rounded-xl p-4 border border-slate-100 dark:border-slate-700"
      >
        <div class="flex items-center gap-3">
          <div
            class="w-8 h-8 bg-primary-100 dark:bg-primary-900/50 rounded-lg flex items-center justify-center text-primary-600 dark:text-primary-400 font-semibold text-sm"
          >
            1
          </div>
          <div>
            <h4 class="font-medium text-slate-800 dark:text-white">上传文件</h4>
            <p class="text-xs text-slate-500 dark:text-slate-400">拖拽或点击上传</p>
          </div>
        </div>
      </div>
      <div
        class="bg-white dark:bg-slate-800 rounded-xl p-4 border border-slate-100 dark:border-slate-700"
      >
        <div class="flex items-center gap-3">
          <div
            class="w-8 h-8 bg-primary-100 dark:bg-primary-900/50 rounded-lg flex items-center justify-center text-primary-600 dark:text-primary-400 font-semibold text-sm"
          >
            2
          </div>
          <div>
            <h4 class="font-medium text-slate-800 dark:text-white">AI 分析</h4>
            <p class="text-xs text-slate-500 dark:text-slate-400">自动提取知识点</p>
          </div>
        </div>
      </div>
      <div
        class="bg-white dark:bg-slate-800 rounded-xl p-4 border border-slate-100 dark:border-slate-700"
      >
        <div class="flex items-center gap-3">
          <div
            class="w-8 h-8 bg-primary-100 dark:bg-primary-900/50 rounded-lg flex items-center justify-center text-primary-600 dark:text-primary-400 font-semibold text-sm"
          >
            3
          </div>
          <div>
            <h4 class="font-medium text-slate-800 dark:text-white">开始学习</h4>
            <p class="text-xs text-slate-500 dark:text-slate-400">智能复习与问答</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
