import request from '@/utils/request'

const prefix = '/admin-api'


export const getCompetitionById = (params) => {
  return request({
    url: prefix+'/admin/competition/'+params,
    method: 'get'
  })
}

// 获取赛事列表（分页）
export const getCompetitionListApi = (params) => {
  return request({
    url: prefix+'/admin/competition',
    method: 'get',
    params
  })
}

// 创建赛事
export const addCompetitionApi = (data) => {
  return request({
    url: prefix+'/admin/competition',
    method: 'post',
    data
  })
}

// 更新赛事
export const editCompetitionApi = (data, id) => {
  return request({
    url: prefix+`/admin/competition/${id}`,
    method: 'put',
    data
  })
}

// 删除赛事
export const delCompetitionApi = (id) => {
  return request({
    url: prefix+`/admin/competition/${id}`,
    method: 'delete'
  })
}

// 获取全部赛事
export const getAllCompetitionApi = () => {
  return request({
    url: prefix+'/admin/competition/all',
    method: 'get'
  })
}

// 导出赛事Excel
export const exportCompetitionApi = (params) => {
  return request({
    url: prefix+'/admin/competition/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

// 获取赛事分组列表（分页）
export const getCompetitionGroupsApi = (params) => {
  return request({
    url: prefix+'/admin/competition/groups',
    method: 'get',
    params
  })
}

// 创建赛事分组
export const addCompetitionGroupApi = (data) => {
  return request({
    url: prefix+'/admin/competition/groups',
    method: 'post',
    data
  })
}

// 更新赛事分组
export const editCompetitionGroupApi = (data, id) => {
  return request({
    url: prefix+`/admin/competition/groups/${id}`,
    method: 'put',
    data
  })
}

// 删除赛事分组
export const delCompetitionGroupApi = (id) => {
  return request({
    url: prefix+`/admin/competition/groups/${id}`,
    method: 'delete'
  })
}

// 获取参赛人员成绩列表（分页）
export const getCompetitionScoreListApi = (params) => {
  return request({
    url: prefix+'/admin/competition/score',
    method: 'get',
    params
  })
}

// 创建参赛人员成绩
export const addCompetitionScoreApi = (data) => {
  return request({
    url: prefix+'/admin/competition/score',
    method: 'post',
    data
  })
}

// 更新参赛人员成绩
export const editCompetitionScoreApi = (data, id) => {
  return request({
    url: prefix+`/admin/competition/score/${id}`,
    method: 'put',
    data
  })
}

// 删除参赛人员成绩
export const delCompetitionScoreApi = (id) => {
  return request({
    url: prefix+`/admin/competition/score/${id}`,
    method: 'delete'
  })
}

// 导入参赛人员成绩Excel
export const importCompetitionScoreApi = (data, competitionId, groupId) => {
  return request({
    url: prefix+`/admin/competition/score/competition/${competitionId}/group/${groupId}`,
    method: 'post',
    data
  })
}

// 导出参赛人员成绩Excel
export const exportCompetitionScoreApi = (params) => {
  return request({
    url: prefix+'/admin/competition/score/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

// 下载参赛人员成绩导入模板
export const downloadCompetitionScoreTemplateApi = (params) => {
  return request({
    url: prefix+'/admin/competition/score/import-template',
    params,
    method: 'get',
    responseType: 'blob'
  })
}