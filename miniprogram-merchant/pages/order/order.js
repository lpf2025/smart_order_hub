const app = getApp();

Page({
  data: {
    currentStatus: 0,
    orders: [],
    page: 1,
    size: 10,
    hasMore: true,
    merchantUserId: null,
    type: null
  },

  onLoad(options) {
    if (options.status) {
      this.setData({ currentStatus: parseInt(options.status) });
    }
    if (options.type === 'my') {
      this.setData({ type: 'my' });
    }
    if (app.globalData.userInfo && app.globalData.userInfo.id) {
      this.setData({ merchantUserId: app.globalData.userInfo.id });
    }
    this.loadOrders();
  },

  onShow() {
    if (app.globalData.userInfo && app.globalData.userInfo.id) {
      this.setData({ merchantUserId: app.globalData.userInfo.id });
    }
    
    if (app.globalData.orderStatus !== undefined && app.globalData.orderStatus !== null) {
      this.setData({ currentStatus: app.globalData.orderStatus });
      app.globalData.orderStatus = undefined;
    }
    
    this.loadOrders();
  },

  onReachBottom() {
    if (this.data.hasMore) {
      this.loadMoreOrders();
    }
  },

  filterOrders(e) {
    const status = parseInt(e.currentTarget.dataset.status);
    this.setData({ 
      currentStatus: status,
      page: 1,
      hasMore: true
    });
    this.doLoadOrders();
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
    const type = this.data.type;
    const params = {
      page: this.data.page,
      size: this.data.size
    };
    
    if (type === 'my') {
      const merchantUserId = app.globalData.userInfo.id;
      params.merchantUserId = merchantUserId;
    } else {
      const merchantId = app.globalData.merchantId;
      params.merchantId = merchantId;
    }
    
    if (this.data.currentStatus > 0) {
      params.status = this.data.currentStatus;
    }
    
    const apiUrl = type === 'my' ? '/order/merchant-user' : '/order/merchant';
    
    app.request(apiUrl, 'POST', params, (data) => {
      const newOrders = data.records || [];
      const formattedOrders = newOrders.map(order => {
        console.log('订单原始时间:', order.createdAt);
        const formattedTime = app.formatDateTime(order.createdAt);
        console.log('订单格式化时间:', formattedTime);
        return {
          ...order,
          createdAtFormatted: formattedTime
        };
      });
      const orders = this.data.page === 1 ? formattedOrders : [...this.data.orders, ...formattedOrders];
      
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
      this.loadOrders();
    }, (error) => {
      wx.showToast({
        title: error.message || '接单失败',
        icon: 'none'
      });
    });
  },

  readyOrder(e) {
    const orderId = e.currentTarget.dataset.id;
    this.updateOrderStatus(orderId, 3, '已出餐');
  },

  startDelivery(e) {
    const orderId = e.currentTarget.dataset.id;
    wx.showModal({
      title: '配送信息',
      content: '请输入配送员信息',
      editable: true,
      placeholderText: '配送员姓名 电话',
      success: (res) => {
        if (res.confirm && res.content) {
          const [name, phone] = res.content.split(' ');
          this.updateOrderStatus(orderId, 4, '开始配送', { deliveryName: name, deliveryPhone: phone });
        }
      }
    });
  },

  completeOrder(e) {
    const orderId = e.currentTarget.dataset.id;
    this.updateOrderStatus(orderId, 5, '已完成');
  },

  updateOrderStatus(orderId, status, message, extraData = {}) {
    app.request('/order/status', 'POST', {
      id: orderId,
      status: status,
      ...extraData
    }, () => {
      wx.showToast({
        title: message,
        icon: 'success'
      });
      this.loadOrders();
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
