const app = getApp();

Page({
  data: {
    currentCategory: '全部',
    categories: [],
    dishes: []
  },

  onLoad() {
    this.loadCategories();
    this.loadDishes();
  },

  onShow() {
    this.loadDishes();
  },

  loadCategories() {
    const merchantId = app.globalData.merchantId;
    app.request('/dish/categories', 'POST', {
      merchantId: merchantId
    }, (data) => {
      this.setData({ categories: data });
    });
  },

  loadDishes() {
    const merchantId = app.globalData.merchantId;
    app.request('/dish/merchant', 'POST', {
      merchantId: merchantId
    }, (data) => {
      this.setData({ dishes: data });
    });
  },

  filterByCategory(e) {
    const category = e.currentTarget.dataset.category;
    this.setData({ currentCategory: category });
    
    if (category === '全部') {
      this.loadDishes();
    } else {
      const merchantId = app.globalData.merchantId;
      app.request('/dish/category', 'POST', {
        merchantId: merchantId,
        category: category
      }, (data) => {
        this.setData({ dishes: data });
      });
    }
  },

  addDish() {
    wx.showToast({
      title: '添加菜品功能开发中',
      icon: 'none'
    });
  },

  editDish(e) {
    const dishId = e.currentTarget.dataset.id;
    wx.showToast({
      title: '编辑菜品功能开发中',
      icon: 'none'
    });
  },

  handleLogout() {
    wx.showModal({
      title: '提示',
      content: '确定要退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          app.logout(() => {
            wx.redirectTo({
              url: '/pages/login/login'
            });
          });
        }
      }
    });
  },

  goToWechatAgreement() {
    wx.navigateTo({
      url: '/pages/agreement/user/user'
    });
  },

  goToMiniProgramAgreement() {
    wx.navigateTo({
      url: '/pages/agreement/privacy/privacy'
    });
  }
});
