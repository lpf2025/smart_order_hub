const app = getApp();

Page({
  data: {
    orderId: null,
    order: null,
    orderItems: [],
    address: null,
    merchant: null,
    loading: true,
    showPayModal: false,
    selectedPayMethod: 'wechat',
    paying: false
  },

  onLoad(options) {
    const { id } = options;
    this.setData({ orderId: id });
    this.loadOrderDetail();
  },

  loadOrderDetail() {
    this.setData({ loading: true });
    app.request('/order/get', 'POST', { id: this.data.orderId }, (data) => {
      this.setData({
        order: data.order,
        orderItems: data.orderItems,
        address: data.address,
        loading: false
      });
      
      if (data.order.orderType === 2) {
        this.loadMerchantInfo(data.order.merchantId);
      }
      
      this.connectWebSocket();
    });
  },

  loadMerchantInfo(merchantId) {
    app.request('/merchant/get', 'POST', { id: merchantId }, (data) => {
      this.setData({ merchant: data });
    });
  },

  connectWebSocket() {
    const userId = app.globalData.userInfo.id;
    const wsUrl = `ws://localhost:8081/ws/order?userId=${userId}`;
    
    wx.connectSocket({
      url: wsUrl
    });

    wx.onSocketOpen(() => {
      console.log('WebSocket连接成功');
    });

    wx.onSocketMessage((res) => {
      const message = JSON.parse(res.data);
      if (message.type === 'order_update' && message.orderId === this.data.orderId) {
        this.loadOrderDetail();
        wx.vibrateShort();
        wx.showToast({
          title: message.message,
          icon: 'success'
        });
      }
    });

    wx.onSocketError(() => {
      console.log('WebSocket连接失败');
    });
  },

  cancelOrder() {
    wx.showModal({
      title: '确认取消',
      content: '确定要取消这个订单吗？',
      success: (res) => {
        if (res.confirm) {
          app.request('/order/cancel', 'POST', { id: this.data.orderId }, () => {
            wx.showToast({
              title: '订单已取消',
              icon: 'success'
            });
            this.loadOrderDetail();
          });
        }
      }
    });
  },

  payOrder() {
    this.showPayModal();
  },

  showPayModal() {
    this.setData({ showPayModal: true });
  },

  hidePayModal() {
    this.setData({ showPayModal: false });
  },

  stopPropagation() {
    
  },

  selectPayMethod(e) {
    const method = e.currentTarget.dataset.method;
    this.setData({ selectedPayMethod: method });
  },

  confirmPay() {
    if (this.data.paying) return;
    
    this.setData({ paying: true });
    
    app.request('/order/pay', 'POST', { id: this.data.orderId }, (payData) => {
      if (this.data.selectedPayMethod === 'wechat') {
        this.wechatPay(payData);
      } else {
        this.alipayPay(payData);
      }
    }, () => {
      this.setData({ paying: false });
    });
  },

  wechatPay(payData) {
    wx.showModal({
      title: '微信支付',
      content: `模拟微信支付\n订单号：${this.data.order.orderNo}\n金额：¥${this.data.order.totalAmount}`,
      confirmText: '确认支付',
      cancelText: '取消',
      success: (res) => {
        if (res.confirm) {
          this.paySuccess();
        } else {
          this.setData({ paying: false });
        }
      }
    });
  },

  alipayPay(payData) {
    wx.showModal({
      title: '支付宝支付',
      content: `模拟跳转到支付宝支付\n订单号：${payData.orderNo}\n金额：¥${payData.totalAmount}`,
      confirmText: '确认支付',
      cancelText: '取消',
      success: (res) => {
        if (res.confirm) {
          this.paySuccess();
        } else {
          this.setData({ paying: false });
        }
      }
    });
  },

  paySuccess() {
    app.request('/order/pay/callback', 'POST', { orderId: this.data.orderId }, () => {
      this.setData({ 
        paying: false,
        showPayModal: false 
      });
      wx.showToast({
        title: '支付成功',
        icon: 'success',
        duration: 1500
      });
      setTimeout(() => {
        wx.redirectTo({
          url: '/pages/order/order'
        });
      }, 1500);
    });
  },

  getStatusText(status) {
    if (status === 1 && this.data.order && this.data.order.payStatus === 0) {
      return '待支付';
    }
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

  maskPhone(phone) {
    if (!phone) return '';
    return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2');
  },

  onUnload() {
    wx.closeSocket();
  }
});
