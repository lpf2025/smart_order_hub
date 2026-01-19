const env = require('./env.js');

App({
  globalData: {
    baseUrl: env.baseUrl,
    merchantId: 1,
    userInfo: null,
    mineStatus: 0,
    orderStatus: 0
  },

  onLaunch() {
    console.log('当前环境:', env.env);
    console.log('API地址:', env.baseUrl);
    
    const userInfo = wx.getStorageSync('merchantInfo');
    if (userInfo) {
      this.globalData.userInfo = userInfo;
      this.globalData.merchantId = userInfo.merchantId;
    }
  },

  request(url, method, data, success, fail) {
    const fullUrl = this.globalData.baseUrl + url;
    console.log('请求URL:', fullUrl);
    console.log('请求方法:', method);
    console.log('请求数据:', data);
    
    wx.request({
      url: fullUrl,
      method: method,
      data: data,
      header: {
        'Content-Type': 'application/json',
        'X-User-Id': this.globalData.userInfo ? this.globalData.userInfo.id : ''
      },
      success: (res) => {
        console.log('响应状态码:', res.statusCode);
        console.log('响应数据:', res.data);
        
        if (res.statusCode === 200 && res.data.code === 200) {
          if (success) success(res.data.data);
        } else {
          console.error('请求失败:', res.data);
          wx.showToast({
            title: res.data.message || '请求失败',
            icon: 'none'
          });
          if (fail) fail(res.data);
        }
      },
      fail: (err) => {
        console.error('网络错误:', err);
        console.error('错误详情:', JSON.stringify(err));
        wx.showToast({
          title: '网络错误: ' + (err.errMsg || '未知错误'),
          icon: 'none',
          duration: 3000
        });
        if (fail) fail(err);
      }
    });
  },

  connectWebSocket() {
    if (!this.globalData.userInfo || !this.globalData.userInfo.id) {
      console.log('用户未登录，跳过WebSocket连接');
      return;
    }
    
    const userId = this.globalData.userInfo.id;
    const wsUrl = `ws://localhost:8081/ws/order?userId=${userId}`;
    
    wx.connectSocket({
      url: wsUrl
    });

    wx.onSocketOpen(() => {
      console.log('WebSocket连接成功');
    });

    wx.onSocketMessage((res) => {
      const message = JSON.parse(res.data);
      if (message.type === 'new_order') {
        wx.vibrateShort();
        wx.showToast({
          title: '新订单提醒',
          icon: 'success'
        });
      }
    });

    wx.onSocketError(() => {
      console.log('WebSocket连接失败');
    });
  },

  formatDateTime(dateTimeStr) {
    if (!dateTimeStr) return '';
    
    try {
      const date = new Date(dateTimeStr);
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      const hours = String(date.getHours()).padStart(2, '0');
      const minutes = String(date.getMinutes()).padStart(2, '0');
      const seconds = String(date.getSeconds()).padStart(2, '0');
      
      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
    } catch (error) {
      console.error('时间格式化失败:', error, '原始时间:', dateTimeStr);
      return dateTimeStr;
    }
  },

  logout(callback) {
    if (!this.globalData.userInfo || !this.globalData.userInfo.id) {
      console.log('用户未登录');
      this.globalData.userInfo = null;
      wx.removeStorageSync('merchantInfo');
      if (callback) callback();
      return;
    }

    this.request('/admin/merchant/user/logout', 'POST', {
      userId: this.globalData.userInfo.id
    }, () => {
      console.log('退出登录成功');
      this.globalData.userInfo = null;
      wx.removeStorageSync('merchantInfo');
      wx.showToast({
        title: '退出成功',
        icon: 'success'
      });
      if (callback) callback();
    }, (error) => {
      console.error('退出登录失败:', error);
      this.globalData.userInfo = null;
      wx.removeStorageSync('merchantInfo');
      wx.showToast({
        title: '已退出登录',
        icon: 'success'
      });
      if (callback) callback();
    });
  }
});
