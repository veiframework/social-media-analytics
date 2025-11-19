<script>
import { wechatLogin, getWechatCode, checkLoginStatus, getAgreementList } from './api/auth.js';
import authManager from './utils/auth.js';
import { getAppConfig } from './config/index.js';
export default {
	async onLaunch() {
		console.log('App Launch');
		
	},
	
	async onShow() {
			// 应用启动时进行初始化
			await this.initApp();
	},
	
	onHide() {
		console.log('App Hide');
	},
	
	methods: {
		/**
		 * 应用初始化
		 */
		async initApp() {
			try {
				console.log('开始应用初始化');
				
				// 设置初始化状态
				this.globalData = this.globalData || {};
				this.globalData.initCompleted = false;
				this.globalData.initError = null;
				
				// 未登录，执行微信登录
				await this.performWechatLogin();
				
				// 登录成功后的其他初始化工作
				await this.afterLoginInit();
				
				// 标记初始化完成
				this.globalData.initCompleted = true;
				console.log('应用初始化完成');
				
			} catch (error) {
				console.error('应用初始化失败:', error);
				this.globalData = this.globalData || {};
				this.globalData.initError = error;
				this.handleInitError(error);
			}
		},
		
		/**
		 * 执行微信登录
		 */
		async performWechatLogin() {
			try {
				console.log('开始微信登录...');
				
				// 获取微信授权码
				const wxMaCode = await getWechatCode();
				
				// 调用登录接口
				const loginParams = {
					wxMaAppId: getAppConfig().appId,
					wxMaCode: wxMaCode
				};
				
				const loginResult = await wechatLogin(loginParams);
				// 保存登录信息
				if (loginResult.access_token) {
					// 使用认证管理器保存登录状态
					authManager.setLoginState(loginResult.access_token, loginResult.userInfo);
					
					console.log('微信登录成功');
					
					// 登录成功后获取协议列表并存储
					await this.fetchAndStoreAgreementList();
				} else {
					throw new Error('登录接口返回数据异常');
				}
				
			} catch (error) {
				console.error('微信登录失败:', error);
				throw error;
			}
		},
		
		/**
		 * 获取并存储协议列表
		 */
		async fetchAndStoreAgreementList() {
			try {
				console.log('获取协议列表...');
				
				const agreementList = await getAgreementList();
				
				// 存储协议列表到本地
				if (agreementList) {
					uni.setStorageSync('agreementList', agreementList);
					console.log('协议列表存储成功:', agreementList);
				}
				
			} catch (error) {
				console.error('获取协议列表失败:', error);
				// 协议列表获取失败不影响登录流程，只记录错误
			}
		},
		
		/**
		 * 验证token有效性
		 */
		async validateToken() {
			try {
				// 这里可以调用一个验证token的接口
				// 如果token无效，清除本地存储并重新登录
				console.log('验证token有效性...');
				
				// 示例：如果需要验证token，可以调用用户信息接口
				// const userInfo = await getUserInfo();
				// 如果接口返回401，说明token失效，需要重新登录
				
			} catch (error) {
				console.error('Token验证失败:', error);
				// 清除无效的登录信息
				authManager.clearLoginState();
				// 重新执行登录
				await this.performWechatLogin();
			}
		},
		
		/**
		 * 登录成功后的初始化工作
		 */
		async afterLoginInit() {
			console.log('执行登录后初始化...');
			
			// 可以在这里执行一些需要登录后才能进行的初始化工作
			// 比如：获取用户配置、同步数据等
			
			// 更新全局登录状态
			authManager.updateGlobalData();
		},
		
		/**
		 * 处理初始化错误
		 */
		handleInitError(error) {
			console.error('应用初始化错误:', error);
			
			// 在loading页面中会处理错误显示和重试
			// 这里不再显示弹窗，避免与loading页面冲突
		},
		
		/**
		 * 强制重新初始化（供loading页面调用）
		 */
		async forceReinit() {
			console.log('强制重新初始化');
			
			// 重置状态
			this.globalData = this.globalData || {};
			this.globalData.initCompleted = false;
			this.globalData.initError = null;
			
			// 重新执行初始化
			await this.initApp();
		}
	}
}
</script>

<style>
	/*每个页面公共css */
</style>
