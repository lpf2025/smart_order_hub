const app = getApp();

Page({
  data: {
    location: '',
    merchantList: [],
    categories: ['美食', '快餐', '饮品', '甜点', '小吃'],
    currentCategory: '美食',
    sortType: 'comprehensive',
    pageNum: 1,
    pageSize: 20,
    hasMore: true,
    loading: false,
    loadingMore: false,
    longitude: null,
    latitude: null
  },

  onLoad(options) {
    this.getLocation();
    this.getMerchantList();
  },

  onPullDownRefresh() {
    this.setData({
      pageNum: 1,
      hasMore: true,
      merchantList: []
    });
    this.getMerchantList(() => {
      wx.stopPullDownRefresh();
    });
  },

  onReachBottom() {
    if (!this.data.hasMore || this.data.loadingMore) return;
    this.setData({
      pageNum: this.data.pageNum + 1,
      loadingMore: true
    });
    this.getMerchantList();
  },

  getLocation() {
    wx.getLocation({
      type: 'gcj02',
      success: (res) => {
        const { longitude, latitude } = res;
        this.setData({ longitude, latitude });
        
        wx.request({
          url: `https://apis.map.qq.com/ws/geocoder/v1/?location=${latitude},${longitude}&key=YOUR_MAP_KEY`,
          success: (mapRes) => {
            if (mapRes.data.status === 0) {
              const address = mapRes.data.result.address;
              this.setData({ location: address });
            }
          },
          fail: () => {
            this.setData({ location: '北京市' });
          }
        });
      },
      fail: () => {
        this.setData({ location: '北京市' });
      }
    });
  },

  chooseLocation() {
    wx.chooseLocation({
      success: (res) => {
        this.setData({ 
          location: res.name || res.address,
          longitude: res.longitude,
          latitude: res.latitude
        });
        this.refreshMerchantList();
      }
    });
  },

  goToSearch() {
    wx.navigateTo({
      url: '/pages/search/search'
    });
  },

  changeCategory(e) {
    const category = e.currentTarget.dataset.category;
    this.setData({
      currentCategory: category,
      pageNum: 1,
      hasMore: true,
      merchantList: []
    });
    this.getMerchantList();
  },

  changeSortType(e) {
    const sortType = e.currentTarget.dataset.type;
    this.setData({
      sortType,
      pageNum: 1,
      hasMore: true,
      merchantList: []
    });
    this.getMerchantList();
  },

  showFilter() {
    wx.showToast({
      title: '筛选功能开发中',
      icon: 'none'
    });
  },

  getMerchantList(callback) {
    if (this.data.loading) return;
    
    this.setData({ loading: true });
    
    const params = {
      category: this.data.currentCategory,
      sortType: this.data.sortType,
      pageNum: this.data.pageNum,
      pageSize: this.data.pageSize
    };

    if (this.data.longitude && this.data.latitude) {
      params.longitude = this.data.longitude;
      params.latitude = this.data.latitude;
    }

    app.request('/merchant/list', 'POST', params, (data) => {
      let merchantList = [];
      
      if (Array.isArray(data)) {
        merchantList = data.map(merchant => ({
          id: merchant.id,
          name: merchant.name,
          logoUrl: merchant.logoUrl ? merchant.logoUrl.trim().replace(/`/g, '') : '',
          rating: merchant.rating || 5.0,
          monthSales: merchant.monthSales || 0,
          perCapita: merchant.perCapita || 0,
          minOrder: merchant.minOrder || 20,
          deliveryFee: merchant.deliveryFee || 5,
          canReserve: merchant.canReserve === 1,
          salesRank: Math.random() > 0.7 ? Math.floor(Math.random() * 10) + 1 : null,
          hasCoupon: Math.random() > 0.6,
          couponReceived: false,
          deliveryTime: Math.floor(Math.random() * 30) + 20,
          distance: (Math.random() * 5 + 0.5).toFixed(1),
          phone: merchant.phone,
          address: merchant.address,
          businessHours: merchant.businessHours,
          status: merchant.status,
          hotDishes: []
        }));
        
        merchantList.sort((a, b) => b.monthSales - a.monthSales);
        
        this.loadHotDishes(merchantList);
      }

      this.setData({
        merchantList: this.data.pageNum === 1 ? merchantList : [...this.data.merchantList, ...merchantList],
        hasMore: merchantList.length >= this.data.pageSize,
        loading: false,
        loadingMore: false
      });

      callback && callback();
    }, (error) => {
      this.setData({
        loading: false,
        loadingMore: false
      });
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      });
    });
  },

  loadHotDishes(merchantList) {
    merchantList.forEach(merchant => {
      app.request('/dish/hot', 'POST', {
        merchantId: merchant.id
      }, (data) => {
        const hotDishes = data.map(dish => ({
          id: dish.id,
          name: dish.name,
          price: dish.price,
          imageUrl: dish.imageUrl ? dish.imageUrl.trim().replace(/`/g, '') : ''
        }));
        
        const index = this.data.merchantList.findIndex(item => item.id === merchant.id);
        if (index !== -1) {
          const updatedMerchantList = [...this.data.merchantList];
          updatedMerchantList[index].hotDishes = hotDishes;
          this.setData({ merchantList: updatedMerchantList });
        }
      });
    });
  },

  refreshMerchantList() {
    this.setData({
      pageNum: 1,
      hasMore: true,
      merchantList: []
    });
    this.getMerchantList();
  },

  goToMerchantDetail(e) {
    const merchantId = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/menu/menu?merchantId=${merchantId}`
    });
  },

  receiveCoupon(e) {
    const merchantId = e.currentTarget.dataset.id;
    const merchantList = this.data.merchantList;
    const index = merchantList.findIndex(item => item.id === merchantId);
    
    if (index !== -1 && !merchantList[index].couponReceived) {
      merchantList[index].couponReceived = true;
      this.setData({ merchantList });
      
      wx.showToast({
        title: '已领取',
        icon: 'success'
      });
    }
  },

  goToDishDetail(e) {
    const merchantId = e.currentTarget.dataset.merchantId;
    const dishId = e.currentTarget.dataset.dishId;
    wx.navigateTo({
      url: `/pages/menu/menu?merchantId=${merchantId}&dishId=${dishId}`
    });
  }
});