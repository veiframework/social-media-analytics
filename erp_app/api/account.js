import {get, post, put} from "../utils/request";


// 社交媒体账号API
const prefix = '/admin-api/social-media/account'

/**
 * 账号分页
 * @param userId 用户id
 * @param type 账号类型
 * @param platformId 平台类型
 * @param syncWorkStatus 同步状态
 * @param nickname 昵称
 * @param pageNum 页码
 * @param pageSize 页码大小
 * @returns {Promise | Promise<unknown>}
 */
export function listSocialMediaAccount(userId, type, platformId, syncWorkStatus, nickname, pageNum, pageSize) {
    return get(prefix, {userId, type, platformId, syncWorkStatus, nickname, pageNum, pageSize}, {});
}

//查询账号详情
export function getSocialMediaAccount(id) {
    return get(prefix + "/" + id, {}, {});
}

/**
 * 根据分享链接创建账号
 * @param shareLink 分享链接 必填
 * @param type 账号类型 必填
 * @returns {Promise | Promise<unknown>}
 */
export function createByShareLink(shareLink, type) {
    return post(prefix + "/share-link", {shareLink, type}, {});
}

/**
 * 根据微信视频号名称创建账号
 * @param nickname  名称 必填
 * @param type 账号类型 必填
 * @returns {Promise | Promise<unknown>}
 */
export function createByWechatVideoNickname(nickname, type) {
    return post(prefix + "/wechat-video-nickname", {nickname, type}, {});
}

/**
 * 是否启用自动同步
 * @param id 账号id
 * @param autoSync 是否自动同步
 * @returns {Promise | Promise<unknown>}
 */
export function updateAutoSync(id, autoSync) {
    let state = autoSync ? 'enable' : 'disable'
    return get(prefix + "/account/" + id + "/sync/" + state, {}, {});
}

/**
 * 单个账号同步作品
 * @param id
 * @returns {Promise | Promise<unknown>}
 */
export function syncWork(id) {
    return get(prefix + "/sync/work/" + id, {}, {
        showLoading: false
    });
}