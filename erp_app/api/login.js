import {get, post, put} from "../utils/request";


// 登录方法
export function login(username, password, code, uuid) {
    return post('/auth-api/login', {
        username: username,
        password: password,
        code: code,
        uuid: uuid
    }, {
        showLoading: true, // 登录时不显示loading，避免影响启动体验
        addToken: false
    });
}

export const getCodeImg = () => {
    return get('/code', {}, {
        addToken: false,
        showLoading: false,
    });
};


// 获取用户详细信息
export function getInfo() {
    return get('/admin-api/user/getInfo', {}, {});
}

// 获取用户详细信息
export function getRouters() {
    return get('/admin-api/menu/getRouters', {}, {});
}

//修改密码
export function editPassword(oldPassword, newPassword) {
    return put('/admin-api/user/profile/updatePwd?oldPassword='+oldPassword+'&newPassword='+newPassword, {}, {
        showLoading: true,
    });
}