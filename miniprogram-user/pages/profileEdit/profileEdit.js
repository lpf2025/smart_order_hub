Page({
  data: {
    userInfo: null,
    form: {
      nickName: '',
      phone: '',
      avatarUrl: ''
    }
  },

  onLoad() {
    this.loadUserInfo();
  },

  loadUserInfo() {
    const app = getApp();
    const userInfo = app.globalData.userInfo || {};
    this.setData({
      userInfo,
      form: {
        nickName: userInfo.nickName || '',
        phone: userInfo.phone || '',
        avatarUrl: userInfo.avatarUrl || ''
      }
    });
  },

  chooseAvatar() {
    wx.chooseMedia({
      count: 1,
      mediaType: ['image'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        const tempFilePath = res.tempFiles[0].tempFilePath;
        this.setData({ 'form.avatarUrl': tempFilePath });
      }
    });
  },

  onNickNameInput(e) {
    this.setData({ 'form.nickName': e.detail.value });
  },

  onPhoneInput(e) {
    this.setData({ 'form.phone': e.detail.value });
  },

  save() {
    const { nickName, phone, avatarUrl } = this.data.form;
    
    if (!nickName.trim()) {
      wx.showToast({
        title: '请输入昵称',
        icon: 'none'
      });
      return;
    }
    
    if (!phone.trim()) {
      wx.showToast({
        title: '请输入手机号',
        icon: 'none'
      });
      return;
    }
    
    if (!/^1[3-9]\d{9}$/.test(phone)) {
      wx.showToast({
        title: '手机号格式不正确',
        icon: 'none'
      });
      return;
    }
    
    const app = getApp();
    app.globalData.userInfo = {
      ...app.globalData.userInfo,
      nickName,
      phone,
      avatarUrl
    };
    
    wx.showToast({
      title: '保存成功',
      icon: 'success'
    });
    
    setTimeout(() => {
      wx.navigateBack();
    }, 1500);
  }
});
