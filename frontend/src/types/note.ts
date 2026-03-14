/**
 * 笔记相关接口类型定义
 */

export type AnalyzeStatus = 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'FAILED'

export interface Note {
  id: number
  title: string
  filename: string
  fileType: string
  fileSize: number
  analyzeStatus: AnalyzeStatus
  analyzeError: string | null
  uploadTime: string
  analysisTime: string | null
  summary: string | null
  keyPointsJson: string | null
  category: string | null
  difficulty: number | null
  estimatedStudyMinutes: number | null
  prerequisitesJson: string | null
}

export interface UploadResponse {
  note: Note
  duplicate: boolean
}

export interface NoteListResponse {
  notes: Note[]
  total: number
}

export interface NoteDetailResponse {
  id: number
  title: string
  filename: string
  fileType: string
  fileSize: number
  analyzeStatus: AnalyzeStatus
  analyzeError: string | null
  uploadTime: string
  analysisTime: string | null
  summary: string | null
  keyPointsJson: string | null
  category: string | null
  difficulty: number | null
  estimatedStudyMinutes: number | null
  prerequisitesJson: string | null
}
