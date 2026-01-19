Page({
  data: {
    activeTab: 'general',
    settings: {
      orderNotification: true,
      activityNotification: true,
      wechatPay: true,
      alipayPay: false
    }
  },

  onLoad(options) {
    if (options.tab) {
      this.setData({ activeTab: options.tab });
    }
    this.loadSettings();
  },

  loadSettings() {
    const app = getApp();
    const settings = app.globalData.settings || {
      orderNotification: true,
      activityNotification: true,
      wechatPay: true,
      alipayPay: false
    };
    this.setData({ settings });
  },

  switchTab(e) {
    const tab = e.currentTarget.dataset.tab;
    this.setData({ activeTab: tab });
  },

  toggleOrderNotification(e) {
    this.setData({ 'settings.orderNotification': e.detail.value });
    this.saveSettings();
  },

  toggleActivityNotification(e) {
    this.setData({ 'settings.activityNotification': e.detail.value });
    this.saveSettings();
  },

  toggleWechatPay(e) {
    this.setData({ 'settings.wechatPay': e.detail.value });
    this.saveSettings();
  },

  toggleAlipayPay(e) {
    this.setData({ 'settings.alipayPay': e.detail.value });
    this.saveSettings();
  },

  saveSettings() {
    const app = getApp();
    app.globalData.settings = this.data.settings;
  },

  logout() {
    wx.showModal({
      title: '确定退出登录？',
      content: '退出登录后需要重新登录',
      success: (res) => {
        if (res.confirm) {
          const app = getApp();
          app.globalData.userInfo = null;
          app.globalData.cart = [];
          
          wx.showToast({
            title: '已退出登录',
            icon: 'success'
          });
          
          setTimeout(() => {
            wx.reLaunch({
              url: '/pages/index/index'
            });
          }, 1500);
        }
      }
    });
  }
});
