<!--
  NoteDetailPage.vue
  笔记详情页面 - 展示笔记内容、知识点摘要、AI 分析结果
-->
<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import type { Component } from 'vue';
import { noteApi, getErrorMessage } from '@/api';
import { formatDate, formatFileSize } from '@/utils';
import {
  ArrowLeft,
  FileText,
  Clock,
  Loader2,
  AlertCircle,
  CheckCircle,
  BookOpen,
  ListChecks,
  RefreshCw,
  Sparkles,
} from 'lucide-vue-next';
import type { NoteDetailResponse, AnalyzeStatus } from '@/types';

const route = useRoute();
const router = useRouter();

const note = ref<NoteDetailResponse | null>(null);
const loading = ref(true);
const error = ref<string | null>(null);

const noteId = computed(() => parseInt(route.params.id as string, 10));

const fetchNote = async () => {
  loading.value = true;
  error.value = null;

  try {
    note.value = await noteApi.getDetail(noteId.value);
  } catch (err) {
    error.value = getErrorMessage(err);
  } finally {
    loading.value = false;
  }
};

const goBack = () => {
  router.push('/notes');
};

interface StatusInfo {
  label: string;
  icon: Component;
  bgColor: string;
  textColor: string;
}

const getStatusInfo = (status: AnalyzeStatus): StatusInfo => {
  const statusMap: Record<AnalyzeStatus, StatusInfo> = {
    PENDING: {
      label: '等待处理',
      icon: Clock,
      bgColor: 'bg-yellow-100 dark:bg-yellow-900/30',
      textColor: 'text-yellow-600 dark:text-yellow-400',
    },
    PROCESSING: {
      label: '正在分析',
      icon: Loader2,
      bgColor: 'bg-blue-100 dark:bg-blue-900/30',
      textColor: 'text-blue-600 dark:text-blue-400',
    },
    COMPLETED: {
      label: '分析完成',
      icon: CheckCircle,
      bgColor: 'bg-emerald-100 dark:bg-emerald-900/30',
      textColor: 'text-emerald-600 dark:text-emerald-400',
    },
    FAILED: {
      label: '分析失败',
      icon: AlertCircle,
      bgColor: 'bg-red-100 dark:bg-red-900/30',
      textColor: 'text-red-600 dark:text-red-400',
    },
  };
  return statusMap[status] || statusMap.PENDING;
};

const parseKeyPoints = (jsonStr: string | null): string[] => {
  if (!jsonStr) return [];
  try {
    return JSON.parse(jsonStr);
  } catch {
    return [];
  }
};

onMounted(() => {
  fetchNote();
});
</script>

<template>
  <div class="max-w-4xl mx-auto">
    <!-- 返回按钮 -->
    <button
      v-motion
      :initial="{ opacity: 0, x: -20 }"
      :enter="{ opacity: 1, x: 0 }"
      class="flex items-center gap-2 text-slate-500 dark:text-slate-400 hover:text-slate-700 dark:hover:text-slate-200 transition-colors mb-6"
      @click="goBack"
    >
      <ArrowLeft class="w-4 h-4" />
      返回笔记库
    </button>

    <!-- 加载状态 -->
    <div v-if="loading" class="flex items-center justify-center py-20">
      <Loader2 class="w-10 h-10 text-primary-500 animate-spin" />
    </div>

    <!-- 错误状态 -->
    <div
      v-else-if="error"
      v-motion
      :initial="{ opacity: 0, y: 20 }"
      :enter="{ opacity: 1, y: 0 }"
      class="flex items-center gap-4 p-6 bg-red-50 dark:bg-red-900/30 border border-red-200 dark:border-red-800 rounded-2xl"
    >
      <AlertCircle class="w-6 h-6 text-red-500" />
      <p class="text-red-600 dark:text-red-400">{{ error }}</p>
    </div>

    <template v-else-if="note">
      <!-- 笔记头部信息 -->
      <div
        v-motion
        :initial="{ opacity: 0, y: 20 }"
        :enter="{ opacity: 1, y: 0 }"
        class="bg-white dark:bg-slate-800 rounded-2xl shadow-lg p-6 border border-slate-100 dark:border-slate-700 mb-6"
      >
        <div class="flex flex-col md:flex-row md:items-start md:justify-between gap-4">
          <div class="flex items-start gap-4">
            <div
              class="w-16 h-16 bg-gradient-to-br from-primary-100 to-primary-200 dark:from-primary-900/50 dark:to-primary-800/50 rounded-2xl flex items-center justify-center flex-shrink-0"
            >
              <FileText class="w-8 h-8 text-primary-600 dark:text-primary-400" />
            </div>
            <div>
              <h1 class="text-2xl font-bold text-slate-800 dark:text-white mb-2">
                {{ note.title || note.filename }}
              </h1>
              <div class="flex flex-wrap items-center gap-4 text-sm text-slate-500 dark:text-slate-400">
                <span class="flex items-center gap-1.5">
                  <Clock class="w-4 h-4" />
                  {{ formatDate(note.uploadTime) }}
                </span>
                <span>{{ formatFileSize(note.fileSize) }}</span>
              </div>
            </div>
          </div>
          <div
            :class="[
              'flex items-center gap-2 px-4 py-2 rounded-xl font-medium',
              getStatusInfo(note.analyzeStatus).bgColor,
              getStatusInfo(note.analyzeStatus).textColor,
            ]"
          >
            <component
              :is="getStatusInfo(note.analyzeStatus).icon"
              :class="['w-5 h-5', note.analyzeStatus === 'PROCESSING' ? 'animate-spin' : '']"
            />
            {{ getStatusInfo(note.analyzeStatus).label }}
          </div>
        </div>
      </div>

      <!-- 内容摘要 -->
      <div
        v-if="note.summary"
        v-motion
        :initial="{ opacity: 0, y: 20 }"
        :enter="{ opacity: 1, y: 0, transition: { delay: 100 } }"
        class="bg-white dark:bg-slate-800 rounded-2xl shadow-lg p-6 border border-slate-100 dark:border-slate-700 mb-6"
      >
        <div class="flex items-center gap-3 mb-4">
          <div class="w-10 h-10 bg-blue-100 dark:bg-blue-900/50 rounded-xl flex items-center justify-center">
            <BookOpen class="w-5 h-5 text-blue-600 dark:text-blue-400" />
          </div>
          <h2 class="text-xl font-semibold text-slate-800 dark:text-white">内容摘要</h2>
        </div>
        <p class="text-slate-600 dark:text-slate-300 leading-relaxed">{{ note.summary }}</p>
      </div>

      <!-- 核心知识点 -->
      <div
        v-if="parseKeyPoints(note.keyPointsJson).length > 0"
        v-motion
        :initial="{ opacity: 0, y: 20 }"
        :enter="{ opacity: 1, y: 0, transition: { delay: 200 } }"
        class="bg-white dark:bg-slate-800 rounded-2xl shadow-lg p-6 border border-slate-100 dark:border-slate-700 mb-6"
      >
        <div class="flex items-center gap-3 mb-6">
          <div
            class="w-10 h-10 bg-emerald-100 dark:bg-emerald-900/50 rounded-xl flex items-center justify-center"
          >
            <ListChecks class="w-5 h-5 text-emerald-600 dark:text-emerald-400" />
          </div>
          <h2 class="text-xl font-semibold text-slate-800 dark:text-white">核心知识点</h2>
        </div>
        <ul class="space-y-4">
          <li
            v-for="(point, index) in parseKeyPoints(note.keyPointsJson)"
            :key="index"
            v-motion
            :initial="{ opacity: 0, x: -20 }"
            :enter="{ opacity: 1, x: 0, transition: { delay: index * 50 } }"
            class="flex items-start gap-4"
          >
            <span
              class="w-8 h-8 bg-gradient-to-br from-primary-500 to-primary-600 rounded-lg flex items-center justify-center flex-shrink-0 text-sm font-bold text-white shadow-lg shadow-primary-500/30"
            >
              {{ index + 1 }}
            </span>
            <p class="text-slate-600 dark:text-slate-300 pt-1">{{ point }}</p>
          </li>
        </ul>
      </div>

      <!-- 处理中提示 -->
      <div
        v-if="note.analyzeStatus === 'PENDING' || note.analyzeStatus === 'PROCESSING'"
        v-motion
        :initial="{ opacity: 0, y: 20 }"
        :enter="{ opacity: 1, y: 0, transition: { delay: 300 } }"
        class="bg-gradient-to-r from-blue-50 to-indigo-50 dark:from-blue-900/20 dark:to-indigo-900/20 border border-blue-200 dark:border-blue-800 rounded-2xl p-6"
      >
        <div class="flex items-center gap-4">
          <div class="w-12 h-12 bg-blue-100 dark:bg-blue-900/50 rounded-xl flex items-center justify-center">
            <Sparkles class="w-6 h-6 text-blue-600 dark:text-blue-400" />
          </div>
          <div class="flex-1">
            <h3 class="font-semibold text-blue-700 dark:text-blue-300 mb-1">
              {{ note.analyzeStatus === 'PENDING' ? '笔记正在排队等待分析...' : 'AI 正在分析您的笔记，请稍候...' }}
            </h3>
            <p class="text-sm text-blue-600 dark:text-blue-400">
              分析完成后将自动显示内容摘要和知识点
            </p>
          </div>
          <button
            class="flex items-center gap-2 px-4 py-2 bg-white dark:bg-slate-800 border border-blue-200 dark:border-blue-700 text-blue-600 dark:text-blue-400 rounded-xl font-medium hover:bg-blue-50 dark:hover:bg-slate-700 transition-colors"
            @click="fetchNote"
          >
            <RefreshCw class="w-4 h-4" />
            刷新状态
          </button>
        </div>
      </div>

      <!-- 分析失败提示 -->
      <div
        v-if="note.analyzeStatus === 'FAILED'"
        v-motion
        :initial="{ opacity: 0, y: 20 }"
        :enter="{ opacity: 1, y: 0, transition: { delay: 300 } }"
        class="bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-2xl p-6"
      >
        <div class="flex items-center gap-4">
          <div class="w-12 h-12 bg-red-100 dark:bg-red-900/50 rounded-xl flex items-center justify-center">
            <AlertCircle class="w-6 h-6 text-red-600 dark:text-red-400" />
          </div>
          <div class="flex-1">
            <h3 class="font-semibold text-red-700 dark:text-red-300 mb-1">分析失败</h3>
            <p class="text-sm text-red-600 dark:text-red-400">
              {{ note.analyzeError || '请尝试重新上传或联系管理员' }}
            </p>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<style scoped>
</style>
