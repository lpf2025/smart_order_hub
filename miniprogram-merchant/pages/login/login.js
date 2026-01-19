const app = getApp();

Page({
  data: {
    loading: false,
    agreed: false,
    phoneNumber: null,
    phoneCode: null
  },

  onLoad() {
    const userInfo = wx.getStorageSync('merchantInfo');
    if (userInfo) {
      app.globalData.userInfo = userInfo;
      app.globalData.merchantId = userInfo.merchantId;
      wx.switchTab({
        url: '/pages/index/index'
      });
    }
    
    this.generateDeviceOpenid();
  },

  generateDeviceOpenid() {
    let savedOpenid = wx.getStorageSync('merchantOpenid');
    
    if (!savedOpenid) {
      try {
        const systemInfo = wx.getSystemInfoSync();
        const deviceInfo = systemInfo.model + systemInfo.system + systemInfo.platform;
        savedOpenid = 'dev_' + this.hashCode(deviceInfo);
        wx.setStorageSync('merchantOpenid', savedOpenid);
        console.log('生成设备openid:', savedOpenid);
      } catch (e) {
        console.error('生成设备openid失败:', e);
        savedOpenid = 'dev_' + Date.now();
        wx.setStorageSync('merchantOpenid', savedOpenid);
      }
    }
  },

  hashCode(str) {
    let hash = 0;
    for (let i = 0; i < str.length; i++) {
      const char = str.charCodeAt(i);
      hash = ((hash << 5) - hash) + char;
      hash = hash & hash;
    }
    return Math.abs(hash).toString(16);
  },

  handleGetPhoneNumber(e) {
    if (e.detail.errMsg === 'getPhoneNumber:ok') {
      const code = e.detail.code;
      console.log('获取手机号code:', code);
      
      this.setData({
        phoneCode: code
      });
      
      this.doLogin(code);
    } else {
      console.error('获取手机号失败:', e.detail.errMsg);
      wx.showToast({
        title: '获取手机号失败',
        icon: 'none'
      });
    }
  },

  handleLogin() {
    if (this.data.loading) return;
    
    if (!this.data.agreed) {
      wx.showToast({
        title: '请先阅读并同意协议',
        icon: 'none'
      });
      return;
    }
    
    this.setData({ loading: true });
    
    console.log('开始登录流程...');
    
    wx.login({
      success: (res) => {
        console.log('wx.login成功，code:', res.code);
        
        if (res.code) {
          this.doLogin(res.code, null, null);
        } else {
          console.error('wx.login失败，没有获取到code');
          this.setData({ loading: false });
          wx.showToast({
            title: '登录失败',
            icon: 'none'
          });
        }
      },
      fail: (err) => {
        console.error('wx.login失败:', err);
        this.setData({ loading: false });
        wx.showToast({
          title: '登录失败',
          icon: 'none'
        });
      }
    });
  },
  
  doLogin(phoneCode) {
    let savedOpenid = wx.getStorageSync('merchantOpenid');
    
    if (!savedOpenid) {
      try {
        const systemInfo = wx.getSystemInfoSync();
        const deviceInfo = systemInfo.model + systemInfo.system + systemInfo.platform;
        savedOpenid = 'dev_' + this.hashCode(deviceInfo);
        wx.setStorageSync('merchantOpenid', savedOpenid);
        console.log('生成设备openid:', savedOpenid);
      } catch (e) {
        console.error('生成设备openid失败:', e);
        savedOpenid = 'dev_' + Date.now();
        wx.setStorageSync('merchantOpenid', savedOpenid);
      }
    }
    
    const openid = savedOpenid;
    
    console.log('准备调用登录接口，参数:', {
      openid: openid,
      nickname: null,
      avatarUrl: null,
      merchantId: app.globalData.merchantId,
      phoneCode: phoneCode
    });
    
    app.request('/admin/merchant/user/login', 'POST', {
      openid: openid,
      nickname: null,
      avatarUrl: null,
      merchantId: app.globalData.merchantId,
      phoneCode: phoneCode
    }, (data) => {
      console.log('登录成功，返回数据:', data);
      
      if (data.mobile) {
        this.setData({
          phoneNumber: data.mobile
        });
      }
      
      app.globalData.userInfo = data;
      wx.setStorageSync('merchantInfo', data);
      wx.showToast({
        title: '登录成功',
        icon: 'success'
      });
      setTimeout(() => {
        wx.switchTab({
          url: '/pages/index/index'
        });
      }, 1500);
    }, (error) => {
      console.error('登录接口失败:', error);
      this.setData({ loading: false });
      wx.showToast({
        title: '登录失败',
        icon: 'none'
      });
    });
  },
  
  toggleAgreement() {
    this.setData({
      agreed: !this.data.agreed
    });
  },
  
  goToWechatAgreement() {
    wx.navigateTo({
      url: '/pages/agreement/user/user'
    });
  },
  
  goToMiniProgramAgreement() {
    wx.navigateTo({
      url: '/pages/agreement/privacy/privacy'
    });
  }
});
