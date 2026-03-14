/**
 * 文件上传 Composable
 * 封装文件上传逻辑，支持拖拽、进度、错误处理
 */

import { ref, reactive } from 'vue'
import { noteApi, getErrorMessage } from '@/api'
import { validateFile } from '@/utils'
import type { Note } from '@/types'

interface UploadState {
  isDragging: boolean
  isUploading: boolean
  progress: number
  error: string | null
  success: boolean
  note: Note | null
}

export function useFileUpload() {
  const fileInput = ref<HTMLInputElement | null>(null)

  const state = reactive<UploadState>({
    isDragging: false,
    isUploading: false,
    progress: 0,
    error: null,
    success: false,
    note: null
  })

  const handleDragOver = (e: DragEvent) => {
    e.preventDefault()
    state.isDragging = true
  }

  const handleDragLeave = (e: DragEvent) => {
    e.preventDefault()
    state.isDragging = false
  }

  const handleDrop = (e: DragEvent) => {
    e.preventDefault()
    state.isDragging = false

    if (e.dataTransfer) {
      const files = e.dataTransfer.files
      if (files.length > 0) {
        handleFileSelect(files[0])
      }
    }
  }

  const handleFileInputChange = (e: Event) => {
    const target = e.target as HTMLInputElement
    if (target.files && target.files.length > 0) {
      handleFileSelect(target.files[0])
    }
  }

  const handleFileSelect = async (file: File) => {
    const validation = validateFile(file)
    if (!validation.valid) {
      state.error = validation.error || '文件验证失败'
      return
    }

    state.isUploading = true
    state.progress = 0
    state.error = null
    state.success = false
    state.note = null

    try {
      const response = await noteApi.upload(file, (percent) => {
        state.progress = percent
      })

      state.success = true
      state.note = response.note
    } catch (err) {
      state.error = getErrorMessage(err)
    } finally {
      state.isUploading = false
    }
  }

  const reset = () => {
    state.isDragging = false
    state.isUploading = false
    state.progress = 0
    state.error = null
    state.success = false
    state.note = null
  }

  const triggerFileInput = () => {
    fileInput.value?.click()
  }

  return {
    fileInput,
    state,
    handleDragOver,
    handleDragLeave,
    handleDrop,
    handleFileInputChange,
    handleFileSelect,
    reset,
    triggerFileInput
  }
}
