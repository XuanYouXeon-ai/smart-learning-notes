<!--
  AppSidebar.vue
  应用侧边栏组件 - 包含导航菜单和主题切换
-->
<script setup lang="ts">
  import { ref, onMounted } from 'vue'
  import { useRoute, useRouter } from 'vue-router'
  import type { Component } from 'vue'
  import {
    Home,
    Folder,
    Upload,
    BookOpen,
    History,
    MessageSquare,
    Moon,
    Sun,
    ChevronRight,
    Sparkles
  } from 'lucide-vue-next'

  const route = useRoute()
  const router = useRouter()

  const isDarkMode = ref(false)

  onMounted(() => {
    const stored = localStorage.getItem('theme')
    const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches
    if (stored === 'dark' || (!stored && prefersDark)) {
      isDarkMode.value = true
      document.documentElement.classList.add('dark')
    }
  })

  const toggleTheme = () => {
    isDarkMode.value = !isDarkMode.value
    document.documentElement.classList.toggle('dark', isDarkMode.value)
    localStorage.setItem('theme', isDarkMode.value ? 'dark' : 'light')
  }

  interface NavItem {
    id: string
    path: string
    label: string
    icon: Component
    description?: string
  }

  interface NavGroup {
    id: string
    title: string
    items: NavItem[]
  }

  const navGroups: NavGroup[] = [
    {
      id: 'learning',
      title: '学习管理',
      items: [
        { id: 'home', path: '/', label: '首页', icon: Home, description: '智能学习笔记平台' },
        { id: 'notes', path: '/notes', label: '笔记库', icon: Folder, description: '管理所有笔记' },
        {
          id: 'upload',
          path: '/notes/upload',
          label: '上传笔记',
          icon: Upload,
          description: '上传学习资料'
        }
      ]
    },
    {
      id: 'features',
      title: '智能功能',
      items: [
        {
          id: 'review',
          path: '/review',
          label: '智能复习',
          icon: BookOpen,
          description: 'AI 辅助复习'
        },
        {
          id: 'history',
          path: '/history',
          label: '学习记录',
          icon: History,
          description: '查看学习历史'
        },
        {
          id: 'qa',
          path: '/qa',
          label: '智能问答',
          icon: MessageSquare,
          description: '基于笔记问答'
        }
      ]
    }
  ]

  const isActive = (path: string) => {
    if (path === '/') {
      return route.path === '/'
    }
    return route.path.startsWith(path)
  }

  const navigateTo = (path: string) => {
    router.push(path)
  }
</script>

<template>
  <aside
    v-motion-slide-left
    class="w-64 bg-white dark:bg-slate-900 border-r border-slate-100 dark:border-slate-700 fixed h-screen left-0 top-0 z-50 flex flex-col"
  >
    <div class="p-6 border-b border-slate-100 dark:border-slate-700">
      <a href="/" class="flex items-center gap-3 group">
        <div
          class="w-10 h-10 bg-gradient-to-br from-primary-500 to-primary-600 rounded-xl flex items-center justify-center text-white shadow-lg shadow-primary-500/30 group-hover:shadow-xl group-hover:shadow-primary-500/40 transition-all duration-300"
        >
          <Sparkles class="w-5 h-5" />
        </div>
        <div>
          <span class="text-lg font-bold text-slate-800 dark:text-white tracking-tight block"
            >Smart Notes</span
          >
          <span class="text-xs text-slate-400 dark:text-slate-500">智能学习笔记</span>
        </div>
      </a>
    </div>

    <div class="px-4 py-3">
      <button
        class="w-full flex items-center justify-center gap-2 px-3 py-2.5 rounded-xl bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-300 hover:bg-slate-200 dark:hover:bg-slate-700 transition-all duration-200"
        @click="toggleTheme"
      >
        <Sun v-if="isDarkMode" class="w-4 h-4" />
        <Moon v-else class="w-4 h-4" />
        <span class="text-sm font-medium">{{ isDarkMode ? '浅色模式' : '深色模式' }}</span>
      </button>
    </div>

    <nav class="flex-1 px-4 overflow-y-auto scrollbar-thin">
      <div class="space-y-6">
        <div v-for="(group, groupIndex) in navGroups" :key="group.id">
          <div class="px-3 mb-2">
            <span
              class="text-xs font-semibold text-slate-400 dark:text-slate-500 uppercase tracking-wider"
            >
              {{ group.title }}
            </span>
          </div>
          <div class="space-y-1">
            <a
              v-for="(item, itemIndex) in group.items"
              :key="item.id"
              v-motion
              :initial="{ opacity: 0, x: -20 }"
              :enter="{
                opacity: 1,
                x: 0,
                transition: { delay: (groupIndex * 3 + itemIndex) * 50 }
              }"
              :href="item.path"
              :class="[
                'group relative flex items-center gap-3 px-3 py-2.5 rounded-xl transition-all duration-200',
                isActive(item.path)
                  ? 'bg-primary-50 dark:bg-primary-900/30 text-primary-600 dark:text-primary-400'
                  : 'text-slate-600 dark:text-slate-400 hover:bg-slate-50 dark:hover:bg-slate-800 hover:text-slate-900 dark:hover:text-white'
              ]"
              @click.prevent="navigateTo(item.path)"
            >
              <div
                :class="[
                  'w-9 h-9 rounded-lg flex items-center justify-center transition-all duration-200',
                  isActive(item.path)
                    ? 'bg-primary-100 dark:bg-primary-900/50 text-primary-600 dark:text-primary-400'
                    : 'bg-slate-100 dark:bg-slate-800 text-slate-500 dark:text-slate-400 group-hover:bg-slate-200 dark:group-hover:bg-slate-700 group-hover:text-slate-700 dark:group-hover:text-white'
                ]"
              >
                <component :is="item.icon" class="w-5 h-5" />
              </div>
              <div class="flex-1 min-w-0">
                <span
                  :class="['text-sm block', isActive(item.path) ? 'font-semibold' : 'font-medium']"
                >
                  {{ item.label }}
                </span>
                <span
                  v-if="item.description"
                  class="text-xs text-slate-400 dark:text-slate-500 truncate block"
                >
                  {{ item.description }}
                </span>
              </div>
              <ChevronRight v-if="isActive(item.path)" class="w-4 h-4 text-primary-400" />
            </a>
          </div>
        </div>
      </div>
    </nav>

    <div class="p-4 border-t border-slate-100 dark:border-slate-700">
      <div
        class="px-4 py-3 bg-gradient-to-r from-primary-50 to-indigo-50 dark:from-primary-900/30 dark:to-slate-800 rounded-xl"
      >
        <p class="text-xs text-primary-600 dark:text-primary-400 font-semibold">
          智能学习笔记 v1.0
        </p>
        <p class="text-xs text-slate-400 dark:text-slate-500 mt-0.5">Powered by AI</p>
      </div>
    </div>
  </aside>
</template>
