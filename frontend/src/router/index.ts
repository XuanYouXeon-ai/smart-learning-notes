import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/HomePage.vue')
    },
    {
      path: '/notes',
      name: 'notes',
      component: () => import('@/views/NoteLibraryPage.vue')
    },
    {
      path: '/notes/upload',
      name: 'note-upload',
      component: () => import('@/views/NoteUploadPage.vue')
    },
    {
      path: '/notes/:id',
      name: 'note-detail',
      component: () => import('@/views/NoteDetailPage.vue')
    },
    {
      path: '/review',
      name: 'review',
      component: () => import('@/views/SmartReviewPage.vue')
    },
    {
      path: '/history',
      name: 'history',
      component: () => import('@/views/LearningHistoryPage.vue')
    },
    {
      path: '/qa',
      name: 'qa',
      component: () => import('@/views/SmartQAPage.vue')
    }
  ]
})

export default router
