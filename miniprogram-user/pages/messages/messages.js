Page({
  data: {
    currentTab: 0,
    tabs: ['全部通知', '订单通知', '活动通知'],
    messages: [],
    unreadCount: 0
  },

  onLoad() {
    this.loadMessages();
  },

  onShow() {
    this.loadMessages();
  },

  onPullDownRefresh() {
    this.loadMessages(() => {
      wx.stopPullDownRefresh();
    });
  },

  loadMessages(callback) {
    const app = getApp();
    const mockMessages = [
      { id: 1, type: 'order', title: '订单已接单', content: '您的订单已被商家接单，正在准备中', time: '10分钟前', unread: true },
      { id: 2, type: 'order', title: '订单已完成', content: '您的订单已完成，感谢您的光临', time: '2小时前', unread: true },
      { id: 3, type: 'activity', title: '新用户专享', content: '新用户注册即送20元优惠券', time: '昨天', unread: false },
      { id: 4, type: 'order', title: '配送中', content: '您的订单正在配送中，请注意查收', time: '昨天', unread: false },
      { id: 5, type: 'activity', title: '限时优惠', content: '全场满50减10，活动进行中', time: '3天前', unread: false }
    ];

    this.setData({
      messages: mockMessages,
      unreadCount: mockMessages.filter(m => m.unread).length
    });

    this.updateTabBarBadge();

    if (callback) callback();
  },

  switchTab(e) {
    const index = e.currentTarget.dataset.index;
    this.setData({ currentTab: index });
    this.filterMessages(index);
  },

  filterMessages(tabIndex) {
    const app = getApp();
    const allMessages = [
      { id: 1, type: 'order', title: '订单已接单', content: '您的订单已被商家接单，正在准备中', time: '10分钟前', unread: true },
      { id: 2, type: 'order', title: '订单已完成', content: '您的订单已完成，感谢您的光临', time: '2小时前', unread: true },
      { id: 3, type: 'activity', title: '新用户专享', content: '新用户注册即送20元优惠券', time: '昨天', unread: false },
      { id: 4, type: 'order', title: '配送中', content: '您的订单正在配送中，请注意查收', time: '昨天', unread: false },
      { id: 5, type: 'activity', title: '限时优惠', content: '全场满50减10，活动进行中', time: '3天前', unread: false }
    ];

    let filteredMessages = allMessages;
    if (tabIndex === 1) {
      filteredMessages = allMessages.filter(m => m.type === 'order');
    } else if (tabIndex === 2) {
      filteredMessages = allMessages.filter(m => m.type === 'activity');
    }

    this.setData({ messages: filteredMessages });
  },

  goToDetail(e) {
    const index = e.currentTarget.dataset.index;
    const messages = this.data.messages;
    messages[index].unread = false;
    
    this.setData({ messages });
    this.updateTabBarBadge();
  },

  onLongPress(e) {
    const index = e.currentTarget.dataset.index;
    wx.showModal({
      title: '确认删除',
      content: '确定要删除这条消息吗？',
      success: (res) => {
        if (res.confirm) {
          const messages = this.data.messages;
          messages.splice(index, 1);
          this.setData({ messages });
          this.updateTabBarBadge();
        }
      }
    });
  },

  updateTabBarBadge() {
    const unreadCount = this.data.messages.filter(m => m.unread).length;
    if (unreadCount > 0) {
      wx.setTabBarBadge({
        index: 2,
        text: unreadCount > 99 ? '99+' : String(unreadCount)
      });
    } else {
      wx.removeTabBarBadge({
        index: 2
      });
    }
  }
});
