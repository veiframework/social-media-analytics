package com.chargehub.admin.datasync.tikhub;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.chargehub.admin.datasync.DataSyncService;
import com.chargehub.admin.datasync.domain.SocialMediaUserInfo;
import com.chargehub.admin.enums.SocialMediaPlatformEnum;
import com.chargehub.common.core.properties.HubProperties;
import com.chargehub.common.security.utils.JacksonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author : zhanghaowei
 * @since : 1.0
 */
@Service
public class DataSyncTikhubServiceImpl implements DataSyncService {

    @Autowired
    private HubProperties hubProperties;


    private static final String GET_USER_PROFILE = "/api/v1/douyin/web/handler_user_profile";


    @Override
    public SocialMediaPlatformEnum platform() {
        return SocialMediaPlatformEnum.DOU_YIN;
    }

    /**
     * {
     * "code": 200,
     * "request_id": "bea1319e-a27d-45c5-ada2-9dda784624cb",
     * "message": "Request successful. This request will incur a charge.",
     * "message_zh": "请求成功，本次请求将被计费。",
     * "support": "Discord: https://discord.gg/aMEAS8Xsvz",
     * "time": "2025-11-18 21:59:24",
     * "time_stamp": 1763531964,
     * "time_zone": "America/Los_Angeles",
     * "docs": "https://api.tikhub.io/#/Douyin-Web-API/handler_user_profile_api_v1_douyin_web_handler_user_profile_get",
     * "cache_message": "This request will be cached. You can access the cached result directly using the URL below, valid for 24 hours. Accessing the cache will not incur additional charges.",
     * "cache_message_zh": "本次请求将被缓存，你可以使用下面的 URL 直接访问缓存结果，有效期为 24 小时，访问缓存不会产生额外费用。",
     * "cache_url": "https://cache.tikhub.io/api/v1/cache/public/bea1319e-a27d-45c5-ada2-9dda784624cb?sign=267edd55d735f464e1db521ae0153054af9b6123dcf74835e1b3c695aef61f50",
     * "router": "/api/v1/douyin/web/handler_user_profile",
     * "params": {
     * "sec_user_id": "MS4wLjABAAAAHghfTK_0oCnHfvK1dI_lQFR6mOqG0ubruxoKqK9yKX4"
     * },
     * "data": {
     * "extra": {
     * "fatal_item_ids": [],
     * "logid": "2025111913592361D6705BE7D34BC5E2F9",
     * "now": 1763531963000,
     * "scenes": null
     * },
     * "log_pb": {
     * "impr_id": "2025111913592361D6705BE7D34BC5E2F9"
     * },
     * "status_code": 0,
     * "status_msg": null,
     * "user": {
     * "account_cert_info": "{}",
     * "apple_account": 0,
     * "avatar_168x168": {
     * "height": 720,
     * "uri": "http://tvax4.sinaimg.cn/default/images/default_avatar_male_50.gif?Expires=1566392183&ssig=5DIcFR%2BHkR&KID=imgbed,tva",
     * "url_list": [
     * "http://tvax4.sinaimg.cn/default/images/default_avatar_male_50.gif?Expires=1566392183&ssig=5DIcFR%2BHkR&KID=imgbed,tva"
     * ],
     * "width": 720
     * },
     * "avatar_300x300": {
     * "height": 720,
     * "uri": "http://tvax4.sinaimg.cn/default/images/default_avatar_male_50.gif?Expires=1566392183&ssig=5DIcFR%2BHkR&KID=imgbed,tva",
     * "url_list": [
     * "http://tvax4.sinaimg.cn/default/images/default_avatar_male_50.gif?Expires=1566392183&ssig=5DIcFR%2BHkR&KID=imgbed,tva"
     * ],
     * "width": 720
     * },
     * "avatar_larger": {
     * "height": 720,
     * "uri": "http://tvax4.sinaimg.cn/default/images/default_avatar_male_50.gif?Expires=1566392183&ssig=5DIcFR%2BHkR&KID=imgbed,tva",
     * "url_list": [
     * "https://tvax4.sinaimg.cn/default/images/default_avatar_male_50.gif?Expires=1566392183&ssig=5DIcFR%2BHkR&KID=imgbed,tva"
     * ],
     * "width": 720
     * },
     * "avatar_medium": {
     * "height": 720,
     * "uri": "http://tvax4.sinaimg.cn/default/images/default_avatar_male_50.gif?Expires=1566392183&ssig=5DIcFR%2BHkR&KID=imgbed,tva",
     * "url_list": [
     * "https://tvax4.sinaimg.cn/default/images/default_avatar_male_50.gif?Expires=1566392183&ssig=5DIcFR%2BHkR&KID=imgbed,tva"
     * ],
     * "width": 720
     * },
     * "avatar_thumb": {
     * "height": 720,
     * "uri": "http://tvax4.sinaimg.cn/default/images/default_avatar_male_50.gif?Expires=1566392183&ssig=5DIcFR%2BHkR&KID=imgbed,tva",
     * "url_list": [
     * "https://tvax4.sinaimg.cn/default/images/default_avatar_male_50.gif?Expires=1566392183&ssig=5DIcFR%2BHkR&KID=imgbed,tva"
     * ],
     * "width": 720
     * },
     * "aweme_count": 0,
     * "aweme_count_correction_threshold": -1,
     * "birthday_hide_level": 0,
     * "can_set_item_cover": false,
     * "can_show_group_card": 0,
     * "city": "",
     * "close_friend_type": 0,
     * "commerce_user_info": {
     * "ad_revenue_rits": null,
     * "has_ads_entry": true
     * },
     * "commerce_user_level": 0,
     * "country": "",
     * "cover_and_head_image_info": {
     * "cover_list": null,
     * "profile_cover_list": []
     * },
     * "cover_colour": "#02161823",
     * "cover_url": [
     * {
     * "uri": "c8510002be9a3a61aad2",
     * "url_list": [
     * "https://p3-pc-sign.douyinpic.com/obj/c8510002be9a3a61aad2?lk3s=93de098e&x-expires=1763701200&x-signature=1JN5JXDt0XtPRZDspg4UNvutngY%3D&from=2480802190"
     * ]
     * }
     * ],
     * "custom_verify": "",
     * "district": "",
     * "dongtai_count": 0,
     * "dynamic_cover": {},
     * "elfemoji_status": -1,
     * "enable_ai_double": 0,
     * "enable_wish": false,
     * "enterprise_user_info": "",
     * "enterprise_verify_reason": "",
     * "familiar_confidence": 0,
     * "favorite_permission": 0,
     * "favoriting_count": 0,
     * "follow_guide": true,
     * "follow_status": 0,
     * "follower_count": 0,
     * "follower_request_status": 0,
     * "follower_status": 0,
     * "following_count": 0,
     * "forward_count": 0,
     * "gender": 0,
     * "general_permission": {
     * "fans_page_toast": 0,
     * "is_hit_active_fans_grayed": false
     * },
     * "has_e_account_role": false,
     * "has_subscription": false,
     * "hide_request_update": 0,
     * "image_send_exempt": false,
     * "ins_id": "",
     * "is_activity_user": false,
     * "is_ban": false,
     * "is_block": false,
     * "is_blocked": false,
     * "is_effect_artist": false,
     * "is_gov_media_vip": false,
     * "is_im_oversea_user": 0,
     * "is_mix_user": false,
     * "is_not_show": false,
     * "is_series_user": false,
     * "is_sharing_profile_user": 0,
     * "is_star": false,
     * "life_story_block": {
     * "life_story_block": false
     * },
     * "live_commerce": false,
     * "live_status": 0,
     * "mate_add_permission": 0,
     * "mate_relation": {
     * "mate_apply_forward": 0,
     * "mate_apply_reverse": 0,
     * "mate_status": 0
     * },
     * "max_follower_count": 0,
     * "message_chat_entry": true,
     * "mix_count": 0,
     * "mplatform_followers_count": 0,
     * "nickname": "Mr.Coder",
     * "original_musician": {
     * "digg_count": 0,
     * "music_count": 0,
     * "music_used_count": 0
     * },
     * "pigeon_daren_status": "",
     * "pigeon_daren_warn_tag": "",
     * "profile_component_disabled": [
     * "banner_vs"
     * ],
     * "profile_mob_params": [
     * {
     * "event_key": "enter_personal_tab",
     * "mob_params": "{\"landing_reason\":\"Landing_Default_Post\"}"
     * }
     * ],
     * "profile_show": {
     * "identify_auth_infos": null
     * },
     * "profile_tab_info": {
     * "profile_landing_tab": 0,
     * "profile_tab_list": null,
     * "profile_tab_list_v2": null
     * },
     * "profile_tab_type": 0,
     * "province": "",
     * "public_collects_count": 0,
     * "publish_landing_tab": 1,
     * "r_fans_group_info": {},
     * "recommend_reason_relation": "",
     * "recommend_user_reason_source": 0,
     * "risk_notice_text": "",
     * "room_data": "",
     * "room_id": 0,
     * "school_name": null,
     * "sec_uid": "MS4wLjABAAAAHghfTK_0oCnHfvK1dI_lQFR6mOqG0ubruxoKqK9yKX4",
     * "secret": 1,
     * "series_count": 0,
     * "share_info": {
     * "bool_persist": 1,
     * "life_share_ext": "{\"life_share_id\":\"f578c9de967442e28d54476553debffa\"}",
     * "share_desc": "长按复制此条消息，打开抖音搜索，查看TA的更多作品。",
     * "share_image_url": {
     * "uri": "http://tvax4.sinaimg.cn/default/images/default_avatar_male_50.gif?Expires=1566392183&ssig=5DIcFR%2BHkR&KID=imgbed,tva",
     * "url_list": [
     * "http://tvax4.sinaimg.cn/default/images/default_avatar_male_50.gif?Expires=1566392183&ssig=5DIcFR%2BHkR&KID=imgbed,tva"
     * ]
     * },
     * "share_qrcode_url": {
     * "uri": "2d14a0006b6e64db46e9d",
     * "url_list": [
     * "https://p26.douyinpic.com/obj/2d14a0006b6e64db46e9d",
     * "https://p3.douyinpic.com/obj/2d14a0006b6e64db46e9d",
     * "https://p11.douyinpic.com/obj/2d14a0006b6e64db46e9d"
     * ]
     * },
     * "share_title": "快来加入抖音，让你发现最有趣的我！",
     * "share_url": "www.iesdouyin.com/share/user/MS4wLjABAAAAHghfTK_0oCnHfvK1dI_lQFR6mOqG0ubruxoKqK9yKX4?did=MS4wLjABAAAAzor1cmkJejcoHqe_ta0L2APVvJLJNJPtMjPuBTO1PBphJtTBWtz3bbTqJD00CXaW&iid=MS4wLjABAAAANwkJuWIRFOzg5uCpDRpMj4OX-QryoDgn-yYlXQnRwQQ&with_sec_did=1&sec_uid=MS4wLjABAAAAHghfTK_0oCnHfvK1dI_lQFR6mOqG0ubruxoKqK9yKX4&from_ssr=1&from_aid=6383",
     * "share_weibo_desc": "长按复制此条消息，打开抖音搜索，查看TA的更多作品。"
     * },
     * "short_id": "0",
     * "show_favorite_list": true,
     * "show_subscription": false,
     * "signature": "",
     * "signature_display_lines": 0,
     * "signature_language": "un",
     * "social_real_relation_type": 0,
     * "special_follow_status": 0,
     * "special_state_info": {
     * "content": "关注账号即可查看内容和喜欢",
     * "special_state": 4,
     * "title": "私密账号"
     * },
     * "story_tab_empty": false,
     * "sync_to_toutiao": 0,
     * "tab_settings": {
     * "private_tab": {
     * "private_tab_style": 1,
     * "show_private_tab": false
     * }
     * },
     * "total_favorited": 0,
     * "total_favorited_correction_threshold": -1,
     * "twitter_id": "",
     * "twitter_name": "",
     * "uid": "505388048332680",
     * "unique_id": "dyxz200qj0sh",
     * "urge_detail": {
     * "ctl_map": "{\"toast\":\"✅作者已收到“求更新”提醒～\",\"button_background_url\":\"https://p11-webcast.douyinpic.com/img/webcast/urge_update.png~tplv-obj.image\",\"urge_type\":\"\",\"dispose_to_moment\":\"0\",\"next_urge_type\":\"default\",\"button_text\":\"求更新\"}",
     * "user_urged": 0
     * },
     * "user_age": -1,
     * "user_not_see": 0,
     * "user_not_show": 1,
     * "verification_type": 0,
     * "video_cover": {},
     * "video_icon": {
     * "height": 720,
     * "uri": "",
     * "url_list": [],
     * "width": 720
     * },
     * "watch_status": false,
     * "white_cover_url": [
     * {
     * "uri": "318f1000413827e122102",
     * "url_list": [
     * "https://p3-pc-sign.douyinpic.com/obj/318f1000413827e122102?lk3s=93de098e&x-expires=1763701200&x-signature=6CPwsPUlAguFD659LLr9PEZH4HY%3D&from=2480802190"
     * ]
     * }
     * ],
     * "with_commerce_enterprise_tab_entry": false,
     * "with_commerce_entry": false,
     * "with_fusion_shop_entry": false,
     * "with_new_goods": false,
     * "youtube_channel_id": "",
     * "youtube_channel_title": ""
     * }
     * }
     * }
     *
     * @param secUserId
     * @return
     */
    @Override
    public SocialMediaUserInfo getSocialMediaUserInfo(String secUserId) {
        HubProperties.SocialMediaDataApi socialMediaDataApi = hubProperties.getSocialMediaDataApi();
        String token = socialMediaDataApi.getToken();
        String host = socialMediaDataApi.getHost();
        try (HttpResponse execute = HttpUtil.createGet(host + GET_USER_PROFILE).bearerAuth(token).form("sec_user_id", secUserId).execute()) {
            String body = execute.body();
            JsonNode jsonNode = JacksonUtil.toObj(body);
            JsonNode path = jsonNode.at("/data/user");
            String nickname = path.get("nickname").asText();
            String uniqueId = path.get("unique_id").asText();
            SocialMediaUserInfo socialMediaUserInfo = new SocialMediaUserInfo();
            socialMediaUserInfo.setNickname(nickname);
            socialMediaUserInfo.setUid(uniqueId);
            return socialMediaUserInfo;
        }
    }




}
