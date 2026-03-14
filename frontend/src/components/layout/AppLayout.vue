<!--
  AppLayout.vue
  应用布局组件 - 包含侧边栏和主内容区域
-->
<script setup lang="ts">
  import AppSidebar from './AppSidebar.vue'
</script>

<template>
  <div
    class="flex min-h-screen bg-gradient-to-br from-slate-50 to-indigo-50 dark:from-slate-900 dark:to-slate-800"
  >
    <AppSidebar />
    <main class="flex-1 ml-64 p-10 min-h-screen overflow-y-auto">
      <slot />
    </main>
  </div>
</template>
