const app = getApp();

Page({
  data: {
    merchantId: null,
    merchant: {},
    serviceMode: 1,
    categories: [],
    currentCategory: '',
    dishes: [],
    cartCount: 0,
    cartTotal: 0
  },

  onLoad(options) {
    const { merchantId } = options;
    this.setData({ merchantId });
    
    app.globalData.currentMerchant = { id: merchantId };
    
    this.loadMerchantInfo();
    this.loadCategories();
    this.loadDishes();
  },

  onShow() {
    this.updateCartInfo();
  },

  loadMerchantInfo() {
    app.request('/merchant/get', 'POST', { id: this.data.merchantId }, (data) => {
      let serviceModes = data.serviceModes ? data.serviceModes.split(',').map(Number) : [1, 2];
      let serviceMode = 1;
      
      if (serviceModes.includes(1)) {
        serviceMode = 1;
      } else if (serviceModes.includes(2)) {
        serviceMode = 2;
      }
      
      this.setData({ 
        merchant: data,
        serviceMode: serviceMode,
        serviceModes: serviceModes
      });
    });
  },

  loadCategories() {
    app.request('/dish/categories', 'POST', {
      merchantId: this.data.merchantId
    }, (data) => {
      this.setData({ 
        categories: data,
        currentCategory: data.length > 0 ? data[0] : ''
      });
    });
  },

  loadDishes() {
    app.request('/dish/merchant', 'POST', {
      merchantId: this.data.merchantId
    }, (data) => {
      const dishes = data.map(dish => ({
        ...dish,
        imageUrl: dish.imageUrl ? dish.imageUrl.trim().replace(/`/g, '') : '',
        quantity: this.getDishQuantity(dish.id),
        discountText: this.calculateDiscount(dish.price, dish.originalPrice)
      }));
      this.setData({ dishes });
    });
  },

  changeServiceMode(e) {
    const mode = parseInt(e.currentTarget.dataset.mode);
    this.setData({ serviceMode: mode });
    this.updateDishesPrice();
  },

  updateDishesPrice() {
    const dishes = this.data.dishes.map(dish => {
      const price = this.data.serviceMode === 2 && dish.takeoutPrice ? dish.takeoutPrice : dish.price;
      return {
        ...dish,
        price: price,
        discountText: this.calculateDiscount(price, dish.originalPrice)
      };
    });
    this.setData({ dishes });
    this.updateCartInfo();
  },

  calculateDiscount(price, originalPrice) {
    if (!originalPrice || originalPrice <= price) {
      return '';
    }
    const discount = (price / originalPrice * 10).toFixed(1);
    return discount + '折';
  },

  selectCategory(e) {
    const category = e.currentTarget.dataset.category;
    this.setData({ currentCategory: category });
    
    app.request('/dish/category', 'POST', {
      merchantId: this.data.merchantId,
      category: category
    }, (data) => {
      const dishes = data.map(dish => ({
        ...dish,
        imageUrl: dish.imageUrl ? dish.imageUrl.trim().replace(/`/g, '') : '',
        quantity: this.getDishQuantity(dish.id),
        discountText: this.calculateDiscount(dish.price, dish.originalPrice)
      }));
      this.setData({ dishes });
    });
  },

  increaseQuantity(e) {
    const dishId = e.currentTarget.dataset.id;
    const dish = this.data.dishes.find(d => d.id === dishId);
    
    if (dish && dish.stock > 0) {
      app.addToCart(dish);
      this.updateDishQuantity(dishId, 1);
      this.updateCartInfo();
    } else {
      wx.showToast({
        title: '库存不足',
        icon: 'none'
      });
    }
  },

  decreaseQuantity(e) {
    const dishId = e.currentTarget.dataset.id;
    app.removeFromCart(dishId);
    this.updateDishQuantity(dishId, -1);
    this.updateCartInfo();
  },

  getDishQuantity(dishId) {
    const item = app.globalData.cart.find(item => item.id === dishId);
    return item ? item.quantity : 0;
  },

  updateDishQuantity(dishId, delta) {
    const dishes = this.data.dishes.map(dish => {
      if (dish.id === dishId) {
        return {
          ...dish,
          quantity: Math.max(0, dish.quantity + delta)
        };
      }
      return dish;
    });
    this.setData({ dishes });
  },

  updateCartInfo() {
    this.setData({
      cartCount: app.getCartCount(),
      cartTotal: app.getCartTotal().toFixed(2)
    });
  },

  showCart() {
    wx.navigateTo({
      url: '/pages/cart/cart'
    });
  },

  goToCheckout() {
    if (app.globalData.cart.length === 0) {
      wx.showToast({
        title: '购物车为空',
        icon: 'none'
      });
      return;
    }
    
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
    
    wx.navigateTo({
      url: '/pages/checkout/checkout'
    });
  }
});
