Page({
  data: {
    userInfo: null,
    orderCounts: {
      all: 0,
      receiving: 0,
      comment: 0,
      refund: 0
    }
  },

  onLoad() {
    this.loadUserInfo();
    this.loadOrderCounts();
  },

  onShow() {
    this.loadUserInfo();
    this.loadOrderCounts();
  },

  loadUserInfo() {
    const app = getApp();
    this.setData({ userInfo: app.globalData.userInfo });
  },

  loadOrderCounts() {
    const app = getApp();
    const mockCounts = {
      all: 5,
      receiving: 2,
      comment: 1,
      refund: 0
    };
    this.setData({ orderCounts: mockCounts });
  },

  goToSettings() {
    wx.navigateTo({
      url: '/pages/settings/settings'
    });
  },

  goToOrderList(e) {
    const type = e.currentTarget.dataset.type;
    wx.navigateTo({
      url: `/pages/order/order?type=${type}`
    });
  },

  goToProfileEdit() {
    wx.navigateTo({
      url: '/pages/profileEdit/profileEdit'
    });
  },

  goToAddressList() {
    wx.navigateTo({
      url: '/pages/addressList/addressList'
    });
  },

  goToPaymentSettings() {
    wx.navigateTo({
      url: '/pages/settings/settings?tab=payment'
    });
  },

  goToMessageSettings() {
    wx.navigateTo({
      url: '/pages/settings/settings?tab=message'
    });
  },

  goToCustomerService() {
    wx.showModal({
      title: '客服中心',
      content: '客服电话：400-123-4567\n工作时间：9:00-21:00',
      showCancel: false
    });
  },

  goToAbout() {
    wx.showModal({
      title: '关于我们',
      content: '智能点餐系统 v1.0\n\n提供便捷的点餐服务',
      showCancel: false
    });
  }
});
