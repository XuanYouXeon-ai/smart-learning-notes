<!--
  NoteLibraryPage.vue
  笔记库页面 - 展示所有笔记列表，支持搜索和筛选
-->
<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { noteApi, getErrorMessage } from '@/api';
import { formatDate, formatFileSize } from '@/utils';
import {
  Folder,
  Upload,
  Search,
  FileText,
  Clock,
  Loader2,
  AlertCircle,
  ChevronRight,
  Filter,
} from 'lucide-vue-next';
import type { Note, AnalyzeStatus } from '@/types';

const router = useRouter();

const notes = ref<Note[]>([]);
const loading = ref(true);
const error = ref<string | null>(null);
const searchQuery = ref('');
const statusFilter = ref<AnalyzeStatus | ''>('');
const currentPage = ref(1);
const totalNotes = ref(0);
const pageSize = 12;

const filteredNotes = computed(() => {
  let result = notes.value;

  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    result = result.filter(
      (note) =>
        note.filename.toLowerCase().includes(query) ||
        note.title?.toLowerCase().includes(query) ||
        note.summary?.toLowerCase().includes(query)
    );
  }

  if (statusFilter.value) {
    result = result.filter((note) => note.analyzeStatus === statusFilter.value);
  }

  return result;
});

const fetchNotes = async () => {
  loading.value = true;
  error.value = null;

  try {
    const response = await noteApi.getList(currentPage.value, pageSize);
    notes.value = response.notes;
    totalNotes.value = response.total;
  } catch (err) {
    error.value = getErrorMessage(err);
  } finally {
    loading.value = false;
  }
};

const goToUpload = () => {
  router.push('/notes/upload');
};

const goToDetail = (id: number) => {
  router.push(`/notes/${id}`);
};

const getStatusBadge = (status: AnalyzeStatus): { label: string; class: string } => {
  const statusMap: Record<AnalyzeStatus, { label: string; class: string }> = {
    PENDING: { label: '等待处理', class: 'bg-yellow-100 text-yellow-700 dark:bg-yellow-900/30 dark:text-yellow-400' },
    PROCESSING: { label: '正在分析', class: 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400' },
    COMPLETED: { label: '分析完成', class: 'bg-emerald-100 text-emerald-700 dark:bg-emerald-900/30 dark:text-emerald-400' },
    FAILED: { label: '分析失败', class: 'bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400' },
  };
  return statusMap[status] || { label: status, class: 'bg-gray-100 text-gray-700' };
};

onMounted(() => {
  fetchNotes();
});
</script>

<template>
  <div class="space-y-6">
    <div
      v-motion
      :initial="{ opacity: 0, y: 20 }"
      :enter="{ opacity: 1, y: 0 }"
      class="flex flex-col md:flex-row md:items-center md:justify-between gap-4"
    >
      <div>
        <h1 class="text-3xl font-bold text-slate-800 dark:text-white mb-2">笔记库</h1>
        <p class="text-slate-600 dark:text-slate-400">共 {{ totalNotes }} 篇笔记</p>
      </div>
      <button
        class="inline-flex items-center gap-2 px-6 py-3 bg-gradient-to-r from-primary-500 to-primary-600 text-white rounded-xl font-semibold shadow-lg shadow-primary-500/30 hover:shadow-xl hover:shadow-primary-500/40 transition-all"
        @click="goToUpload"
      >
        <Upload class="w-5 h-5" />
        上传笔记
      </button>
    </div>

    <div
      v-motion
      :initial="{ opacity: 0, y: 20 }"
      :enter="{ opacity: 1, y: 0, transition: { delay: 100 } }"
      class="flex flex-col md:flex-row gap-4"
    >
      <div class="relative flex-1">
        <Search class="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-slate-400" />
        <input
          v-model="searchQuery"
          type="text"
          placeholder="搜索笔记..."
          class="w-full pl-12 pr-4 py-3 bg-white dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent text-slate-800 dark:text-white placeholder-slate-400"
        />
      </div>
      <div class="flex items-center gap-2">
        <Filter class="w-5 h-5 text-slate-400" />
        <select
          v-model="statusFilter"
          class="px-4 py-3 bg-white dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl focus:outline-none focus:ring-2 focus:ring-primary-500 text-slate-800 dark:text-white"
        >
          <option value="">全部状态</option>
          <option value="PENDING">等待处理</option>
          <option value="PROCESSING">正在分析</option>
          <option value="COMPLETED">分析完成</option>
          <option value="FAILED">分析失败</option>
        </select>
      </div>
    </div>

    <div v-if="loading" class="flex items-center justify-center py-20">
      <Loader2 class="w-10 h-10 text-primary-500 animate-spin" />
    </div>

    <div
      v-else-if="error"
      class="flex items-center gap-4 p-6 bg-red-50 dark:bg-red-900/30 border border-red-200 dark:border-red-800 rounded-2xl"
    >
      <AlertCircle class="w-6 h-6 text-red-500" />
      <p class="text-red-600 dark:text-red-400">{{ error }}</p>
    </div>

    <div
      v-else-if="filteredNotes.length === 0"
      v-motion
      :initial="{ opacity: 0, y: 20 }"
      :enter="{ opacity: 1, y: 0 }"
      class="text-center py-20"
    >
      <div class="w-20 h-20 bg-slate-100 dark:bg-slate-700 rounded-2xl flex items-center justify-center mx-auto mb-6">
        <Folder class="w-10 h-10 text-slate-400" />
      </div>
      <h3 class="text-xl font-semibold text-slate-800 dark:text-white mb-2">暂无笔记</h3>
      <p class="text-slate-500 dark:text-slate-400 mb-6">上传您的第一篇笔记开始智能学习之旅</p>
      <button
        class="inline-flex items-center gap-2 px-6 py-3 bg-gradient-to-r from-primary-500 to-primary-600 text-white rounded-xl font-semibold shadow-lg shadow-primary-500/30 hover:shadow-xl transition-all"
        @click="goToUpload"
      >
        <Upload class="w-5 h-5" />
        上传笔记
      </button>
    </div>

    <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
      <div
        v-for="(note, index) in filteredNotes"
        :key="note.id"
        v-motion
        :initial="{ opacity: 0, y: 20 }"
        :enter="{ opacity: 1, y: 0, transition: { delay: index * 50 } }"
        class="group bg-white dark:bg-slate-800 rounded-2xl shadow-sm hover:shadow-lg transition-all duration-300 border border-slate-100 dark:border-slate-700 cursor-pointer overflow-hidden"
        @click="goToDetail(note.id)"
      >
        <div class="p-5">
          <div class="flex items-start gap-4">
            <div class="w-12 h-12 bg-primary-100 dark:bg-primary-900/50 rounded-xl flex items-center justify-center flex-shrink-0">
              <FileText class="w-6 h-6 text-primary-600 dark:text-primary-400" />
            </div>
            <div class="flex-1 min-w-0">
              <div class="flex items-center gap-2 mb-1">
                <h3 class="font-semibold text-slate-800 dark:text-white truncate">
                  {{ note.title || note.filename }}
                </h3>
              </div>
              <span
                :class="[
                  'inline-flex px-2 py-0.5 text-xs rounded-full font-medium',
                  getStatusBadge(note.analyzeStatus).class,
                ]"
              >
                {{ getStatusBadge(note.analyzeStatus).label }}
              </span>
            </div>
          </div>

          <p v-if="note.summary" class="mt-4 text-sm text-slate-500 dark:text-slate-400 line-clamp-2">
            {{ note.summary }}
          </p>

          <div class="flex items-center gap-4 mt-4 text-xs text-slate-400">
            <span class="flex items-center gap-1">
              <Clock class="w-3 h-3" />
              {{ formatDate(note.uploadTime) }}
            </span>
            <span>{{ formatFileSize(note.fileSize) }}</span>
          </div>
        </div>

        <div
          class="px-5 py-3 bg-slate-50 dark:bg-slate-700/50 border-t border-slate-100 dark:border-slate-700 flex items-center justify-between group-hover:bg-primary-50 dark:group-hover:bg-primary-900/20 transition-colors"
        >
          <span class="text-sm text-slate-500 dark:text-slate-400 group-hover:text-primary-600 dark:group-hover:text-primary-400">
            查看详情
          </span>
          <ChevronRight class="w-4 h-4 text-slate-400 group-hover:text-primary-500 transition-colors" />
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
