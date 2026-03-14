/**
 * 笔记相关 API
 */

import { http } from './request'
import type { UploadResponse, NoteListResponse, NoteDetailResponse } from '@/types'

export const noteApi = {
  /**
   * 上传笔记文件
   */
  upload(file: File, onProgress?: (percent: number) => void): Promise<UploadResponse> {
    const formData = new FormData()
    formData.append('file', file)

    return http.upload<UploadResponse>('/notes/upload', formData, onProgress, {
      timeout: 120000
    })
  },

  /**
   * 获取笔记列表
   */
  getList(page = 1, size = 10): Promise<NoteListResponse> {
    return http.get<NoteListResponse>(`/notes?page=${page}&size=${size}`)
  },

  /**
   * 获取笔记详情
   */
  getDetail(id: number): Promise<NoteDetailResponse> {
    return http.get<NoteDetailResponse>(`/notes/${id}/detail`)
  },

  /**
   * 删除笔记
   */
  delete(id: number): Promise<void> {
    return http.delete<void>(`/notes/${id}`)
  }
}
