const app = getApp();

Page({
  data: {
    stats: {
      pendingCount: 0,
      processingCount: 0,
      completedCount: 0,
      todayAmount: 0
    },
    recentOrders: []
  },

  onLoad() {
    const userInfo = app.globalData.userInfo;
    if (!userInfo) {
      wx.redirectTo({
        url: '/pages/login/login'
      });
      return;
    }
    
    this.loadStats();
    this.loadRecentOrders();
    app.connectWebSocket();
  },

  onShow() {
    const userInfo = app.globalData.userInfo;
    if (!userInfo) {
      wx.redirectTo({
        url: '/pages/login/login'
      });
      return;
    }
    
    this.loadStats();
    this.loadRecentOrders();
  },

  loadStats() {
    const merchantId = app.globalData.merchantId;
    app.request('/order/merchant', 'POST', {
      merchantId: merchantId,
      page: 1,
      size: 100
    }, (data) => {
      const orders = data.records || [];
      const today = new Date().toDateString();
      
      const todayOrders = orders.filter(order => {
        return new Date(order.createdAt).toDateString() === today;
      });
      
      const stats = {
        pendingCount: orders.filter(o => o.status === 1).length,
        processingCount: orders.filter(o => o.status >= 2 && o.status <= 4).length,
        completedCount: orders.filter(o => o.status === 5).length,
        todayAmount: todayOrders.reduce((sum, order) => sum + order.totalAmount, 0).toFixed(2)
      };
      
      this.setData({ stats });
    });
  },

  loadRecentOrders() {
    const merchantId = app.globalData.merchantId;
    app.request('/order/merchant', 'POST', {
      merchantId: merchantId,
      page: 1,
      size: 5
    }, (data) => {
      const orders = data.records || [];
      const formattedOrders = orders.map(order => ({
        ...order,
        createdAtFormatted: app.formatDateTime(order.createdAt)
      }));
      this.setData({ recentOrders: formattedOrders });
    });
  },

  goToPendingOrders() {
    app.globalData.orderStatus = 1;
    wx.switchTab({
      url: '/pages/order/order'
    });
  },

  goToMineAll() {
    app.globalData.mineStatus = 0;
    wx.switchTab({
      url: '/pages/mine/mine'
    });
  },

  goToOrderAll() {
    app.globalData.orderStatus = 0;
    wx.switchTab({
      url: '/pages/order/order'
    });
  },

  goToMineStatus(e) {
    const status = e.currentTarget.dataset.status;
    app.globalData.mineStatus = status;
    wx.switchTab({
      url: '/pages/mine/mine'
    });
  },

  goToOrders(e) {
    const status = e.currentTarget.dataset.status;
    app.globalData.orderStatus = status;
    wx.switchTab({
      url: '/pages/order/order'
    });
  },

  goToMenu() {
    wx.switchTab({
      url: '/pages/menu/menu'
    });
  },

  goToOrderDetail(e) {
    const orderId = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/orderDetail/orderDetail?id=${orderId}`
    });
  },

  acceptOrder(e) {
    const orderId = e.currentTarget.dataset.id;
    
    if (!app.globalData.userInfo || !app.globalData.userInfo.id) {
      wx.showToast({
        title: '请先登录',
        icon: 'none'
      });
      return;
    }
    
    const userInfo = app.globalData.userInfo;
    const deliveryUserId = userInfo.hasDeliveryPerm === 1 ? userInfo.id : null;
    
    app.request('/order/accept', 'POST', {
      orderId: orderId,
      merchantUserId: userInfo.id,
      deliveryUserId: deliveryUserId
    }, () => {
      wx.showToast({
        title: '接单成功',
        icon: 'success'
      });
      this.loadRecentOrders();
      this.loadStats();
    }, (error) => {
      wx.showToast({
        title: error.message || '接单失败',
        icon: 'none'
      });
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
