const env = require('./env.js');

App({
  globalData: {
    baseUrl: '',
    userInfo: null,
    cart: [],
    currentMerchant: null,
    currentTableNo: null
  },

  onLaunch() {
    this.initBaseUrl();
    this.checkLogin();
  },

  initBaseUrl() {
    const envType = env.getEnv();
    if (envType === 'prod') {
      this.globalData.baseUrl = 'https://smartcan.store/api';
    } else {
      this.globalData.baseUrl = 'http://localhost:8081/api';
    }
  },

  checkLogin() {
    const userInfo = wx.getStorageSync('userInfo');
    if (userInfo) {
      this.globalData.userInfo = userInfo;
    } else {
      this.login();
    }
  },

  login() {
    wx.login({
      success: (res) => {
        if (res.code) {
          this.request('/user/login', 'POST', {
            openid: res.code,
            nickname: '微信用户',
            avatarUrl: ''
          }, (data) => {
            this.globalData.userInfo = data;
            wx.setStorageSync('userInfo', data);
          });
        }
      }
    });
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

  addToCart(dish) {
    const existingItem = this.globalData.cart.find(item => item.id === dish.id);
    if (existingItem) {
      existingItem.quantity += 1;
    } else {
      this.globalData.cart.push({
        ...dish,
        imageUrl: dish.imageUrl ? dish.imageUrl.trim().replace(/`/g, '') : '',
        quantity: 1
      });
    }
    wx.showToast({
      title: '已加入购物车',
      icon: 'success',
      duration: 1000
    });
  },

  removeFromCart(dishId) {
    const index = this.globalData.cart.findIndex(item => item.id === dishId);
    if (index > -1) {
      this.globalData.cart.splice(index, 1);
    }
  },

  updateCartQuantity(dishId, quantity) {
    const item = this.globalData.cart.find(item => item.id === dishId);
    if (item) {
      item.quantity = quantity;
    }
  },

  clearCart() {
    this.globalData.cart = [];
  },

  getCartTotal() {
    return this.globalData.cart.reduce((total, item) => {
      return total + item.price * item.quantity;
    }, 0);
  },

  getCartCount() {
    return this.globalData.cart.reduce((total, item) => {
      return total + item.quantity;
    }, 0);
  }
});
