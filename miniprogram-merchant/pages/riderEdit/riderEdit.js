const app = getApp();

Page({
  data: {
    formData: {
      avatar: '',
      name: '',
      phone: ''
    }
  },

  onLoad(options) {
    const userInfo = app.globalData.userInfo;
    if (userInfo) {
      this.setData({
        formData: {
          avatar: userInfo.avatarUrl || '',
          name: userInfo.wxNickname || '',
          phone: userInfo.mobile || ''
        }
      });
    }
  },

  onNameInput(e) {
    this.setData({
      'formData.name': e.detail.value
    });
  },

  onPhoneInput(e) {
    this.setData({
      'formData.phone': e.detail.value
    });
  },

  chooseAvatar() {
    const that = this;
    wx.showActionSheet({
      itemList: ['拍照', '从相册选择'],
      success(res) {
        if (res.tapIndex === 0) {
          that.takePhoto();
        } else if (res.tapIndex === 1) {
          that.chooseFromAlbum();
        }
      }
    });
  },

  takePhoto() {
    const that = this;
    wx.chooseMedia({
      count: 1,
      mediaType: ['image'],
      sourceType: ['camera'],
      success(res) {
        const tempFilePath = res.tempFiles[0].tempFilePath;
        const fileSize = res.tempFiles[0].size;
        that.checkAndCompressImage(tempFilePath, fileSize);
      }
    });
  },

  chooseFromAlbum() {
    const that = this;
    wx.chooseMedia({
      count: 1,
      mediaType: ['image'],
      sourceType: ['album'],
      success(res) {
        const tempFilePath = res.tempFiles[0].tempFilePath;
        const fileSize = res.tempFiles[0].size;
        that.checkAndCompressImage(tempFilePath, fileSize);
      }
    });
  },

  checkAndCompressImage(filePath, fileSize) {
    const maxSize = 2 * 1024 * 1024;
    
    if (fileSize <= maxSize) {
      this.uploadAvatar(filePath);
      return;
    }
    
    wx.showModal({
      title: '提示',
      content: `图片大小为${(fileSize / 1024 / 1024).toFixed(2)}MB，超过2MB限制，是否自动压缩？`,
      confirmText: '压缩',
      cancelText: '取消',
      success: (res) => {
        if (res.confirm) {
          this.compressImage(filePath);
        }
      }
    });
  },

  compressImage(filePath) {
    const that = this;
    wx.showLoading({
      title: '压缩中...'
    });

    wx.compressImage({
      src: filePath,
      quality: 70,
      success(res) {
        wx.hideLoading();
        const compressedSize = res.size;
        const originalSize = wx.getFileSystemManager().statSync(filePath).size;
        
        wx.showModal({
          title: '压缩完成',
          content: `原图：${(originalSize / 1024 / 1024).toFixed(2)}MB\n压缩后：${(compressedSize / 1024 / 1024).toFixed(2)}MB\n是否继续上传？`,
          success: (modalRes) => {
            if (modalRes.confirm) {
              that.uploadAvatar(res.tempFilePath);
            }
          }
        });
      },
      fail(err) {
        wx.hideLoading();
        wx.showToast({
          title: '压缩失败，请选择较小的图片',
          icon: 'none'
        });
      }
    });
  },

  uploadAvatar(filePath) {
    const that = this;
    wx.showLoading({
      title: '上传中...'
    });

    wx.uploadFile({
      url: app.globalData.baseUrl + '/upload/image',
      filePath: filePath,
      name: 'file',
      header: {
        'X-User-Id': app.globalData.userInfo ? app.globalData.userInfo.id : ''
      },
      success(res) {
        wx.hideLoading();
        const data = JSON.parse(res.data);
        if (data.code === 200) {
          that.setData({
            'formData.avatar': data.data
          });
          wx.showToast({
            title: '上传成功',
            icon: 'success'
          });
        } else {
          wx.showToast({
            title: data.message || '上传失败',
            icon: 'none'
          });
        }
      },
      fail(err) {
        wx.hideLoading();
        wx.showToast({
          title: '上传失败',
          icon: 'none'
        });
      }
    });
  },

  saveRiderInfo() {
    const { avatar, name, phone } = this.data.formData;
    
    if (!name) {
      wx.showToast({
        title: '请填写姓名',
        icon: 'none'
      });
      return;
    }
    
    if (!phone) {
      wx.showToast({
        title: '请填写手机号',
        icon: 'none'
      });
      return;
    }
    
    if (!/^1[3-9]\d{9}$/.test(phone)) {
      wx.showToast({
        title: '请输入正确的手机号',
        icon: 'none'
      });
      return;
    }

    const userInfo = app.globalData.userInfo;
    if (!userInfo || !userInfo.id) {
      wx.showToast({
        title: '请先登录',
        icon: 'none'
      });
      setTimeout(() => {
        wx.redirectTo({
          url: '/pages/login/login'
        });
      }, 1500);
      return;
    }

    wx.showLoading({
      title: '保存中...'
    });

    app.request('/admin/merchant/user/detail', 'GET', {
      id: userInfo.id
    }, (data) => {
      if (!data) {
        wx.hideLoading();
        wx.showToast({
          title: '用户信息已过期，请重新登录',
          icon: 'none'
        });
        setTimeout(() => {
          wx.redirectTo({
            url: '/pages/login/login'
          });
        }, 1500);
        return;
      }

      app.request('/admin/merchant/user/update', 'POST', {
        id: userInfo.id,
        avatarUrl: avatar,
        wxNickname: name,
        mobile: phone
      }, (data) => {
        wx.hideLoading();
        wx.showToast({
          title: '保存成功',
          icon: 'success'
        });
        
        userInfo.avatarUrl = avatar;
        userInfo.wxNickname = name;
        userInfo.mobile = phone;
        app.globalData.userInfo = userInfo;
        wx.setStorageSync('merchantInfo', userInfo);
        
        setTimeout(() => {
          wx.navigateBack();
        }, 1500);
      }, (error) => {
        wx.hideLoading();
        wx.showToast({
          title: error.message || '保存失败',
          icon: 'none'
        });
      });
    }, (error) => {
      wx.hideLoading();
      wx.showToast({
        title: '用户信息已过期，请重新登录',
        icon: 'none'
      });
      setTimeout(() => {
        wx.redirectTo({
          url: '/pages/login/login'
        });
      }, 1500);
    });
  },

  handleLogout() {
    wx.showModal({
      title: '提示',
      content: '确定要退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          app.logout(() => {
            wx.reLaunch({
              url: '/pages/login/login'
            });
          });
        }
      }
    });
  }
});
