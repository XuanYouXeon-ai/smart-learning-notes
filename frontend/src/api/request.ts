/**
 * 统一请求封装
 * 基于 fetch 封装，支持泛型响应、错误处理、请求拦截
 */

interface Result<T = unknown> {
  code: number
  message: string
  data: T
}

interface RequestConfig extends RequestInit {
  timeout?: number
}

const BASE_URL = '/api'

const defaultConfig: RequestConfig = {
  headers: {
    'Content-Type': 'application/json'
  },
  timeout: 30000
}

async function request<T>(url: string, config: RequestConfig = {}): Promise<T> {
  const { timeout = 30000, ...fetchConfig } = config

  const controller = new AbortController()
  const timeoutId = setTimeout(() => controller.abort(), timeout)

  try {
    const response = await fetch(`${BASE_URL}${url}`, {
      ...defaultConfig,
      ...fetchConfig,
      signal: controller.signal
    })

    clearTimeout(timeoutId)

    if (!response.ok) {
      throw new Error(`HTTP Error: ${response.status} ${response.statusText}`)
    }

    const result: Result<T> = await response.json()

    if (result.code === 200) {
      return result.data
    }

    throw new Error(result.message || '请求失败')
  } catch (error) {
    clearTimeout(timeoutId)

    if (error instanceof Error) {
      if (error.name === 'AbortError') {
        throw new Error('请求超时')
      }
      throw error
    }

    throw new Error('未知错误')
  }
}

export const http = {
  get<T>(url: string, config?: RequestConfig): Promise<T> {
    return request<T>(url, { ...config, method: 'GET' })
  },

  post<T>(url: string, data?: unknown, config?: RequestConfig): Promise<T> {
    return request<T>(url, {
      ...config,
      method: 'POST',
      body: data ? JSON.stringify(data) : undefined
    })
  },

  put<T>(url: string, data?: unknown, config?: RequestConfig): Promise<T> {
    return request<T>(url, {
      ...config,
      method: 'PUT',
      body: data ? JSON.stringify(data) : undefined
    })
  },

  delete<T>(url: string, config?: RequestConfig): Promise<T> {
    return request<T>(url, { ...config, method: 'DELETE' })
  },

  upload<T>(
    url: string,
    formData: FormData,
    onProgress?: (percent: number) => void,
    config?: RequestConfig
  ): Promise<T> {
    return new Promise((resolve, reject) => {
      const xhr = new XMLHttpRequest()

      xhr.upload.addEventListener('progress', (e) => {
        if (e.lengthComputable && onProgress) {
          const percent = Math.round((e.loaded / e.total) * 100)
          onProgress(percent)
        }
      })

      xhr.addEventListener('load', () => {
        if (xhr.status === 200) {
          try {
            const result: Result<T> = JSON.parse(xhr.responseText)
            if (result.code === 200) {
              resolve(result.data)
            } else {
              reject(new Error(result.message || '上传失败'))
            }
          } catch {
            reject(new Error('解析响应失败'))
          }
        } else {
          reject(new Error(`上传失败: ${xhr.statusText}`))
        }
      })

      xhr.addEventListener('error', () => {
        reject(new Error('网络错误，请重试'))
      })

      xhr.addEventListener('abort', () => {
        reject(new Error('上传已取消'))
      })

      const timeout = config?.timeout || 60000
      xhr.timeout = timeout

      xhr.open('POST', `${BASE_URL}${url}`)
      xhr.send(formData)
    })
  }
}

export function getErrorMessage(error: unknown): string {
  if (error instanceof Error) {
    return error.message
  }
  return '未知错误'
}
