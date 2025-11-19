package com.vchaoxi.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class WineUserRelationVo {
    private static final long serialVersionUID = 1L;

    private Integer id;

    /**
     * 父用户id
     */
    private Integer parentUserId;

    /**
     * 子用户id
     */
    private Integer childUserId;

//    /**
//     * 父类的手机号
//     */
//    private String parentPhone;
//
//    /**
//     * 父类的昵称
//     */
//    private String parentNickName;
//
//    /**
//     * 子类的手机号
//     */
//    private String childPhone;
//
//    /**
//     * 子类的昵称
//     */
//    private String childNickName;

    /**
     * 父类用户信息
     */
    private UserVo parentUserVo;

    /**
     * 子类用户信息
     */
    private UserVo childUserVo;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

}
