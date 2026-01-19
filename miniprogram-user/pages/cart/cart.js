Page({
  data: {
    cartItems: [],
    totalAmount: 0,
    totalCount: 0
  },

  onLoad() {
    this.loadCart();
  },

  onShow() {
    this.loadCart();
  },

  loadCart() {
    const app = getApp();
    const cart = app.globalData.cart || [];
    this.calculateCart(cart);
  },

  calculateCart(cart) {
    const totalCount = cart.reduce((sum, item) => sum + item.quantity, 0);
    const totalAmount = cart.reduce((sum, item) => sum + (item.price * item.quantity), 0).toFixed(2);
    
    this.setData({
      cartItems: cart,
      totalCount,
      totalAmount
    });
  },

  increaseQuantity(e) {
    const index = e.currentTarget.dataset.index;
    const app = getApp();
    const cart = app.globalData.cart || [];
    
    cart[index].quantity += 1;
    app.globalData.cart = cart;
    
    this.calculateCart(cart);
    this.updateTabBarBadge();
  },

  decreaseQuantity(e) {
    const index = e.currentTarget.dataset.index;
    const app = getApp();
    const cart = app.globalData.cart || [];
    
    if (cart[index].quantity > 1) {
      cart[index].quantity -= 1;
    } else {
      cart.splice(index, 1);
    }
    
    app.globalData.cart = cart;
    
    this.calculateCart(cart);
    this.updateTabBarBadge();
  },

  removeItem(e) {
    const index = e.currentTarget.dataset.index;
    wx.showModal({
      title: '确认删除',
      content: '确定要删除这个商品吗？',
      success: (res) => {
        if (res.confirm) {
          const app = getApp();
          const cart = app.globalData.cart || [];
          cart.splice(index, 1);
          app.globalData.cart = cart;
          
          this.calculateCart(cart);
          this.updateTabBarBadge();
        }
      }
    });
  },

  updateTabBarBadge() {
    const app = getApp();
    const cart = app.globalData.cart || [];
    const count = cart.reduce((sum, item) => sum + item.quantity, 0);
    
    if (count > 0) {
      wx.setTabBarBadge({
        index: 1,
        text: count > 99 ? '99+' : String(count)
      });
    } else {
      wx.removeTabBarBadge({
        index: 1
      });
    }
  },

  goToCheckout() {
    const app = getApp();
    if (!app.globalData.userInfo) {
      wx.showModal({
        title: '提示',
        content: '请先登录',
        success: (res) => {
          if (res.confirm) {
            wx.navigateTo({
              url: '/pages/login/login'
            });
          }
        }
      });
      return;
    }
    
    if (this.data.cartItems.length === 0) {
      wx.showToast({
        title: '购物车为空',
        icon: 'none'
      });
      return;
    }
    
    wx.navigateTo({
      url: '/pages/checkout/checkout'
    });
  }
});
