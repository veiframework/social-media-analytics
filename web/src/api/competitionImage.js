import request from '@/utils/request'

const prefix = '/admin-api'

// 获取赛事图片列表（分页）
export const getCompetitionImageListApi = (params) => {
  return request({
    url: prefix + '/admin/competition-images',
    method: 'get',
    params
  })
}

// 创建赛事图片
export const addCompetitionImageApi = (data) => {
  return request({
    url: prefix + '/admin/competition-images',
    method: 'post',
    data
  })
}

/**
 * 
 * @param {imageUrls,imageType,competitionId,participantId} data 
 * @returns 
 */
export const batchAddCompetitionImageApi = (data) => {
  return request({
    url: prefix + '/admin/competition-images/batch',
    method: 'post',
    data
  })
}

// 更新赛事图片
export const editCompetitionImageApi = (data, id) => {
  return request({
    url: prefix + `/admin/competition-images/${id}`,
    method: 'put',
    data
  })
}

// 删除赛事图片
export const delCompetitionImageApi = (id) => {
  return request({
    url: prefix + `/admin/competition-images/${id}`,
    method: 'delete'
  })
}

// 获取赛事图片详情
export const getCompetitionImageDetailApi = (id) => {
  return request({
    url: prefix + `/admin/competition-images/${id}`,
    method: 'get'
  })
}

// 根据赛事ID获取图片列表
export const getImagesByCompetitionIdApi = (params) => {
  return request({
    url: prefix + `/admin/competition-images/all`,
    method: 'get',
    params
  })
}

// 根据参赛者ID获取图片列表
export const getImagesByParticipantIdApi = (participantId) => {
  return request({
    url: prefix + `/admin/competition-images/participant/${participantId}`,
    method: 'get'
  })
}

// 根据图片类型获取图片列表
export const getImagesByTypeApi = (competitionId, imageType) => {
  return request({
    url: prefix + `/admin/competition-images/competition/${competitionId}/type/${imageType}`,
    method: 'get'
  })
}

// 导出赛事图片Excel
export const exportCompetitionImageApi = (params) => {
  return request({
    url: prefix + '/admin/competition-images/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}