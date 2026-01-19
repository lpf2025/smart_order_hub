const app = getApp();

Page({
  data: {
    currentStatus: 0,
    orders: [],
    stats: {
      pendingCount: 0,
      processingCount: 0,
      completedCount: 0,
      todayAmount: 0
    },
    merchantUserId: null,
    page: 1,
    size: 10,
    hasMore: true,
    riderInfo: {
      avatar: '',
      name: '',
      phone: ''
    }
  },

  onLoad(options) {
    const userInfo = app.globalData.userInfo;
    if (!userInfo) {
      wx.redirectTo({
        url: '/pages/login/login'
      });
      return;
    }
    
    this.setData({ merchantUserId: userInfo.id });
    
    if (options.status) {
      this.setData({ currentStatus: parseInt(options.status) });
    }
    
    this.loadRiderInfo();
    this.loadStats();
    this.loadOrders();
  },

  onShow() {
    const userInfo = app.globalData.userInfo;
    if (!userInfo) {
      wx.redirectTo({
        url: '/pages/login/login'
      });
      return;
    }
    
    this.setData({ merchantUserId: userInfo.id });
    
    if (app.globalData.mineStatus !== undefined && app.globalData.mineStatus !== null) {
      this.setData({ currentStatus: app.globalData.mineStatus });
      app.globalData.mineStatus = undefined;
    }
    
    this.loadRiderInfo();
    this.loadStats();
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
    const merchantUserId = app.globalData.userInfo.id;
    const params = {
      merchantUserId: merchantUserId,
      page: this.data.page,
      size: this.data.size
    };
    
    if (this.data.currentStatus > 0) {
      params.status = this.data.currentStatus;
    }
    
    app.request('/order/merchant-user', 'POST', params, (data) => {
      const newOrders = data.records || [];
      const formattedOrders = newOrders.map(order => ({
        ...order,
        createdAtFormatted: app.formatDateTime(order.createdAt)
      }));
      const orders = this.data.page === 1 ? formattedOrders : [...this.data.orders, ...formattedOrders];
      
      const incompleteOrders = orders.filter(order => order.status !== 5 && order.status !== 6);
      const completedOrders = orders.filter(order => order.status === 5);
      
      incompleteOrders.sort((a, b) => new Date(b.updatedAt) - new Date(a.updatedAt));
      completedOrders.sort((a, b) => new Date(b.updatedAt) - new Date(a.updatedAt));
      
      const sortedOrders = [...incompleteOrders, ...completedOrders];
      
      this.setData({
        orders: sortedOrders,
        hasMore: newOrders.length >= this.data.size
      });
    });
  },

  loadStats() {
    const merchantUserId = app.globalData.userInfo.id;
    app.request('/order/merchant-user', 'POST', {
      merchantUserId: merchantUserId,
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
  },

  loadRiderInfo() {
    const userInfo = app.globalData.userInfo;
    if (userInfo) {
      const name = userInfo.wxNickname || '';
      const phone = userInfo.mobile || '';
      
      this.setData({
        riderInfo: {
          avatar: userInfo.avatarUrl || '',
          name: name,
          phone: phone,
          maskedName: this.maskName(name),
          maskedPhone: this.maskPhone(phone)
        }
      });
    }
  },

  maskName(name) {
    if (!name || name.length < 2) {
      return name;
    }
    const firstChar = name.substring(0, 1);
    return `${firstChar}**`;
  },

  maskPhone(phone) {
    if (!phone || phone.length < 7) {
      return phone;
    }
    const start = phone.substring(0, 3);
    const end = phone.substring(phone.length - 4);
    return `${start}****${end}`;
  },

  editRiderInfo() {
    wx.navigateTo({
      url: '/pages/riderEdit/riderEdit'
    });
  }
});
