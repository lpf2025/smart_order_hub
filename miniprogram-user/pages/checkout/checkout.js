const app = getApp();

Page({
  data: {
    merchantId: null,
    merchant: {},
    cart: [],
    cartTotal: 0,
    cartCount: 0,
    orderType: 1,
    deliveryType: 'immediate',
    deliveryTypes: [],
    addressId: null,
    selectedAddress: null,
    remark: '',
    timeRange: [[], []],
    timeIndex: [0, 0],
    selectedTime: '',
    estimatedDeliveryTime: '30分钟'
  },

  onLoad(options) {
    this.setData({
      merchantId: app.globalData.currentMerchant?.id,
      cart: app.globalData.cart,
      cartTotal: app.getCartTotal().toFixed(2),
      cartCount: app.getCartCount()
    });
    this.loadMerchantInfo();
    this.loadDefaultAddress();
  },

  onShow() {
    this.setData({
      cart: app.globalData.cart,
      cartTotal: app.getCartTotal().toFixed(2),
      cartCount: app.getCartCount()
    });
    if (this.data.addressId) {
      this.loadAddress(this.data.addressId);
    }
  },

  loadDefaultAddress() {
    app.request('/address/default', 'POST', {
      userId: app.globalData.userInfo.id
    }, (data) => {
      if (data) {
        this.setData({
          addressId: data.id,
          selectedAddress: data
        });
      }
    });
  },

  loadMerchantInfo() {
    app.request('/merchant/get', 'POST', { id: this.data.merchantId }, (data) => {
      let deliveryTypes = data.takeawayDeliveryTypes ? data.takeawayDeliveryTypes.split(',') : ['immediate'];
      
      if (data.canReserve === 0) {
        deliveryTypes = deliveryTypes.filter(type => type !== 'reserve');
      }
      
      let deliveryType = data.defaultDeliveryType || 'immediate';
      if (data.canReserve === 0 || !deliveryTypes.includes(deliveryType)) {
        deliveryType = 'immediate';
      }
      
      let serviceModes = data.serviceModes ? data.serviceModes.split(',').map(Number) : [1, 2];
      let orderType = 1;
      
      if (serviceModes.includes(1)) {
        orderType = 1;
      } else if (serviceModes.includes(2)) {
        orderType = 2;
      }
      
      this.setData({ 
        merchant: data,
        orderType: orderType,
        deliveryType: deliveryType,
        deliveryTypes: deliveryTypes,
        canReserve: data.canReserve,
        serviceModes: serviceModes
      });
      this.generateTimeRange();
    });
  },

  generateTimeRange() {
    const now = new Date();
    const dates = [];
    const times = [];
    
    for (let i = 0; i < 7; i++) {
      const date = new Date(now);
      date.setDate(now.getDate() + i);
      const month = date.getMonth() + 1;
      const day = date.getDate();
      const weekDay = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'][date.getDay()];
      dates.push({
        label: i === 0 ? '今天' : `${month}月${day}日 ${weekDay}`,
        value: `${date.getFullYear()}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`
      });
    }
    
    const startHour = this.data.merchant.startTime ? parseInt(this.data.merchant.startTime.split(':')[0]) : 9;
    const endHour = this.data.merchant.endTime ? parseInt(this.data.merchant.endTime.split(':')[0]) : 22;
    
    for (let hour = startHour; hour <= endHour; hour++) {
      for (let minute = 0; minute < 60; minute += 30) {
        times.push({
          label: `${String(hour).padStart(2, '0')}:${String(minute).padStart(2, '0')}`,
          value: `${String(hour).padStart(2, '0')}:${String(minute).padStart(2, '0')}`
        });
      }
    }
    
    this.setData({
      timeRange: [dates, times]
    });
  },

  selectOrderType(e) {
    const type = parseInt(e.currentTarget.dataset.type);
    this.setData({ orderType: type });
  },

  selectDeliveryType(e) {
    const type = e.currentTarget.dataset.type;
    this.setData({ deliveryType: type });
  },

  onTimeChange(e) {
    const [dateIndex, timeIndex] = e.detail.value;
    const date = this.data.timeRange[0][dateIndex];
    const time = this.data.timeRange[1][timeIndex];
    const selectedTime = `${date.label} ${time.label}`;
    const reserveTime = `${date.value} ${time.value}:00`;
    
    this.setData({
      timeIndex: [dateIndex, timeIndex],
      selectedTime: selectedTime,
      reserveTime: reserveTime
    });
  },

  onTimeColumnChange(e) {
    const { column, value } = e.detail;
    const timeIndex = [...this.data.timeIndex];
    timeIndex[column] = value;
    this.setData({ timeIndex });
  },

  loadAddress(id) {
    app.request('/address/get', 'POST', { id: id }, (data) => {
      this.setData({ selectedAddress: data });
      this.calculateDeliveryTime();
    });
  },

  calculateDeliveryTime() {
    if (!this.data.merchant || !this.data.selectedAddress) {
      this.setData({ estimatedDeliveryTime: '30分钟' });
      return;
    }

    const merchantAddress = this.data.merchant.address;
    const userAddress = `${this.data.selectedAddress.province}${this.data.selectedAddress.city}${this.data.selectedAddress.district}${this.data.selectedAddress.detailAddress}`;

    if (!merchantAddress || !userAddress) {
      this.setData({ estimatedDeliveryTime: '30分钟' });
      return;
    }

    const merchantCity = this.data.merchant.address.substring(0, 2);
    const userCity = this.data.selectedAddress.city.substring(0, 2);

    let estimatedMinutes = 30;

    if (merchantCity !== userCity) {
      estimatedMinutes = 60;
    } else {
      const addressLength = userAddress.length;
      estimatedMinutes = Math.min(Math.max(20 + Math.floor(addressLength / 5), 30), 60);
    }

    this.setData({ estimatedDeliveryTime: `${estimatedMinutes}分钟` });
  },

  selectOrderType(e) {
    const type = parseInt(e.currentTarget.dataset.type);
    this.setData({ orderType: type });
  },

  selectAddress() {
    wx.navigateTo({
      url: '/pages/addressList/addressList'
    });
  },

  onRemarkInput(e) {
    this.setData({ remark: e.detail.value });
  },

  submitOrder() {
    if (this.data.cart.length === 0) {
      wx.showToast({
        title: '购物车为空',
        icon: 'none'
      });
      return;
    }

    if (!app.globalData.userInfo) {
      wx.showToast({
        title: '请先登录',
        icon: 'none'
      });
      return;
    }

    if (this.data.orderType === 1 && !this.data.addressId) {
      wx.showToast({
        title: '请选择收货地址',
        icon: 'none'
      });
      return;
    }

    if (this.data.orderType === 1 && this.data.deliveryType === 'reserve' && !this.data.reserveTime) {
      wx.showToast({
        title: '请选择配送时间',
        icon: 'none'
      });
      return;
    }

    console.log('merchantId:', this.data.merchantId);
    console.log('orderType:', this.data.orderType);
    console.log('deliveryType:', this.data.deliveryType);
    console.log('cart:', this.data.cart);

    const orderData = {
      merchantId: this.data.merchantId,
      orderType: this.data.orderType,
      deliveryType: this.data.orderType === 1 ? this.data.deliveryType : null,
      reserveTime: this.data.orderType === 1 && this.data.deliveryType === 'reserve' ? this.data.reserveTime : null,
      addressId: this.data.orderType === 1 ? this.data.addressId : null,
      orderItems: this.data.cart.map(item => ({
        dishId: item.id,
        specName: '',
        quantity: item.quantity,
        price: item.price
      })),
      remark: this.data.remark,
      payMethod: 'wechat'
    };

    console.log('orderData:', orderData);

    app.request('/order/create', 'POST', orderData, (data) => {
      app.clearCart();
      wx.showToast({
        title: '下单成功',
        icon: 'success'
      });
      setTimeout(() => {
        wx.redirectTo({
          url: `/pages/orderDetail/orderDetail?id=${data.id}`
        });
      }, 1500);
    });
  }
});
