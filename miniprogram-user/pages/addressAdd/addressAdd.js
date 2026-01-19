const app = getApp();

Page({
  data: {
    id: null,
    receiverName: '',
    receiverPhone: '',
    province: '',
    city: '',
    district: '',
    detailAddress: '',
    isDefault: false,
    region: []
  },

  onLoad(options) {
    if (options.id) {
      this.loadAddress(options.id);
    }
  },

  loadAddress(id) {
    app.request('/address/get', 'POST', { id: id }, (data) => {
      this.setData({
        id: data.id,
        receiverName: data.receiverName,
        receiverPhone: data.receiverPhone,
        province: data.province,
        city: data.city,
        district: data.district,
        detailAddress: data.detailAddress,
        isDefault: data.isDefault,
        region: [data.province, data.city, data.district]
      });
    });
  },

  onReceiverNameInput(e) {
    this.setData({ receiverName: e.detail.value });
  },

  onReceiverPhoneInput(e) {
    this.setData({ receiverPhone: e.detail.value });
  },

  onProvinceInput(e) {
    this.setData({ province: e.detail.value });
  },

  onCityInput(e) {
    this.setData({ city: e.detail.value });
  },

  onDistrictInput(e) {
    this.setData({ district: e.detail.value });
  },

  onDetailAddressInput(e) {
    this.setData({ detailAddress: e.detail.value });
  },

  onRegionChange(e) {
    const region = e.detail.value;
    this.setData({
      region: region,
      province: region[0] || '',
      city: region[1] || '',
      district: region[2] || ''
    });
  },

  onDefaultChange(e) {
    this.setData({ isDefault: e.detail.value });
  },

  chooseLocation() {
    wx.chooseLocation({
      success: (res) => {
        this.setData({
          province: res.province || '',
          city: res.city || '',
          district: res.district || '',
          detailAddress: res.address || res.name,
          region: [res.province || '', res.city || '', res.district || '']
        });
      }
    });
  },

  submit() {
    if (!this.data.receiverName.trim()) {
      wx.showToast({
        title: '请输入收货人姓名',
        icon: 'none'
      });
      return;
    }

    if (!this.data.receiverPhone.trim()) {
      wx.showToast({
        title: '请输入收货人电话',
        icon: 'none'
      });
      return;
    }

    if (!this.data.province.trim() || !this.data.city.trim() || !this.data.district.trim()) {
      wx.showToast({
        title: '请选择省市区',
        icon: 'none'
      });
      return;
    }

    if (!this.data.detailAddress.trim()) {
      wx.showToast({
        title: '请输入详细地址',
        icon: 'none'
      });
      return;
    }

    const addressData = {
      userId: app.globalData.userInfo.id,
      receiverName: this.data.receiverName,
      receiverPhone: this.data.receiverPhone,
      province: this.data.province,
      city: this.data.city,
      district: this.data.district,
      detailAddress: this.data.detailAddress,
      isDefault: this.data.isDefault
    };

    const url = this.data.id ? '/address/update' : '/address/add';
    
    app.request(url, 'POST', addressData, (data) => {
      wx.showToast({
        title: '保存成功',
        icon: 'success'
      });
      
      setTimeout(() => {
        const pages = getCurrentPages();
        const prevPage = pages[pages.length - 2];
        
        if (prevPage && prevPage.route === 'pages/checkout/checkout') {
          const newAddressId = this.data.id || data.id;
          prevPage.setData({
            addressId: newAddressId
          });
          prevPage.loadAddress(newAddressId);
        }
        
        wx.navigateBack();
      }, 1000);
    });
  }
});
