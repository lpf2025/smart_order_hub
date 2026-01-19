const app = getApp();

Page({
  data: {
    addresses: []
  },

  onLoad() {
    this.loadAddresses();
  },

  loadAddresses() {
    app.request('/address/list', 'POST', {
      userId: app.globalData.userInfo.id
    }, (data) => {
      this.setData({ addresses: data });
    });
  },

  selectAddress(e) {
    const addressId = e.currentTarget.dataset.id;
    const address = this.data.addresses.find(item => item.id === addressId);
    
    if (address) {
      const pages = getCurrentPages();
      const prevPage = pages[pages.length - 2];
      
      if (prevPage && prevPage.route === 'pages/checkout/checkout') {
        prevPage.setData({
          addressId: addressId,
          selectedAddress: address
        });
      }
      
      wx.navigateBack();
    }
  },

  addAddress() {
    wx.navigateTo({
      url: '/pages/addressAdd/addressAdd'
    });
  },

  editAddress(e) {
    const addressId = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/addressAdd/addressAdd?id=${addressId}`
    });
  },

  deleteAddress(e) {
    const addressId = e.currentTarget.dataset.id;
    wx.showModal({
      title: '确认删除',
      content: '确定要删除这个地址吗？',
      success: (res) => {
        if (res.confirm) {
          app.request('/address/delete', 'POST', { id: addressId }, () => {
            wx.showToast({
              title: '删除成功',
              icon: 'success'
            });
            this.loadAddresses();
          });
        }
      }
    });
  },

  setDefault(e) {
    const addressId = e.currentTarget.dataset.id;
    app.request('/address/setDefault', 'POST', { id: addressId }, () => {
      wx.showToast({
        title: '设置成功',
        icon: 'success'
      });
      this.loadAddresses();
    });
  }
});
