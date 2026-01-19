const app = getApp();

Page({
  data: {
    orderId: null,
    order: null,
    orderItems: [],
    loading: true
  },

  onLoad(options) {
    const { id } = options;
    this.setData({ orderId: id });
    this.loadOrderDetail();
  },

  loadOrderDetail() {
    this.setData({ loading: true });
    app.request('/order/get', 'POST', { id: this.data.orderId }, (data) => {
      const order = data.order;
      order.createdAtFormatted = app.formatDateTime(order.createdAt);
      
      const address = data.address;
      if (address) {
        if (address.receiverPhone) {
          address.maskedPhone = this.maskPhone(address.receiverPhone);
        }
        if (address.receiverName) {
          address.maskedContact = this.maskContact(address.receiverName);
        }
      }
      
      this.setData({
        order: order,
        orderItems: data.orderItems,
        address: address,
        delivery: data.delivery,
        loading: false
      });
    });
  },

  maskPhone(phone) {
    if (!phone || phone.length < 7) {
      return phone;
    }
    const start = phone.substring(0, 3);
    const end = phone.substring(phone.length - 4);
    return `${start}****${end}`;
  },

  maskContact(contact) {
    if (!contact || contact.length < 2) {
      return contact;
    }
    const firstChar = contact.substring(0, 1);
    return `${firstChar}**`;
  },

  acceptOrder() {
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
      orderId: this.data.orderId,
      merchantUserId: userInfo.id,
      deliveryUserId: deliveryUserId
    }, () => {
      wx.showToast({
        title: '接单成功',
        icon: 'success'
      });
      this.loadOrderDetail();
    }, (error) => {
      wx.showToast({
        title: error.message || '接单失败',
        icon: 'none'
      });
    });
  },

  readyOrder() {
    this.updateOrderStatus(3, '已出餐');
  },

  startDelivery() {
    this.updateOrderStatus(4, '开始配送');
  },

  completeOrder() {
    this.updateOrderStatus(5, '已完成');
  },

  updateOrderStatus(status, message, extraData = {}) {
    app.request('/order/status', 'POST', {
      id: this.data.orderId,
      status: status,
      ...extraData
    }, () => {
      wx.showToast({
        title: message,
        icon: 'success'
      });
      this.loadOrderDetail();
    });
  },

  printReceipt() {
    wx.showToast({
      title: '打印功能开发中',
      icon: 'none'
    });
  },

  copyOrderNo() {
    const orderNo = this.data.order.orderNo;
    wx.setClipboardData({
      data: orderNo,
      success: () => {
        wx.showToast({
          title: '订单号已复制',
          icon: 'success'
        });
      },
      fail: () => {
        wx.showToast({
          title: '复制失败',
          icon: 'none'
        });
      }
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

  getStatusIcon(status) {
    const iconMap = {
      1: '📋',
      2: '🍳',
      3: '📦',
      4: '🚚',
      5: '✓',
      6: '✕'
    };
    return iconMap[status] || '📋';
  }
});
