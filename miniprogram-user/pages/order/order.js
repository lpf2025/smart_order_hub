const app = getApp();

Page({
  data: {
    orders: [],
    page: 1,
    size: 10,
    hasMore: true
  },

  onLoad() {
    if (!app.globalData.userInfo) {
      wx.showModal({
        title: '提示',
        content: '请先登录',
        showCancel: false,
        success: () => {
          wx.switchTab({
            url: '/pages/index/index'
          });
        }
      });
      return;
    }
    this.loadOrders();
  },

  onReachBottom() {
    if (this.data.hasMore) {
      this.loadMoreOrders();
    }
  },

  loadOrders() {
    this.setData({ page: 1, hasMore: true });
    this.doLoadOrders();
  },

  loadMoreOrders() {
    this.setData({ page: this.data.page + 1 });
    this.doLoadOrders();
  },

  doLoadOrders() {
    const userId = app.globalData.userInfo.id;
    app.request('/order/user', 'POST', {
      userId: userId,
      page: this.data.page,
      size: this.data.size
    }, (data) => {
      const newOrders = data.records || [];
      const orders = this.data.page === 1 ? newOrders : [...this.data.orders, ...newOrders];
      
      this.setData({
        orders: orders,
        hasMore: newOrders.length >= this.data.size
      });
    });
  },

  goToDetail(e) {
    const orderId = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/orderDetail/orderDetail?id=${orderId}`
    });
  },

  cancelOrder(e) {
    const orderId = e.currentTarget.dataset.id;
    wx.showModal({
      title: '确认取消',
      content: '确定要取消这个订单吗？',
      success: (res) => {
        if (res.confirm) {
          app.request('/order/cancel', 'POST', { id: orderId }, () => {
            wx.showToast({
              title: '订单已取消',
              icon: 'success'
            });
            this.loadOrders();
          });
        }
      }
    });
  },

  payOrder(e) {
    const orderId = e.currentTarget.dataset.id;
    wx.showToast({
      title: '支付功能开发中',
      icon: 'none'
    });
  },

  getStatusText(status) {
    const statusMap = {
      1: '待接单',
      2: '待出餐',
      3: '待配送',
      4: '配送中',
      5: '已完成',
      6: '已取消'
    };
    return statusMap[status] || '未知';
  },

  getStatusClass(status) {
    const classMap = {
      1: 'status-pending',
      2: 'status-processing',
      3: 'status-processing',
      4: 'status-delivering',
      5: 'status-completed',
      6: 'status-cancelled'
    };
    return classMap[status] || '';
  }
});
