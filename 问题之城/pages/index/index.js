// index.js
// 获取应用实例
const app = getApp()

Page({
  data: {

  },
  onShareAppMessage: function(){
    return{
      title:'问题之城',
      path:'pages/login/login',
      imageUrl:'http://127.0.0.1/images/问题之城.png'
    }
  },
  onShareTimeline: function(){
    return{
      title:'问题之城',
      query:'',
      imageUrl:'http://127.0.0.1/images/问题之城.png',
    }
  },
  parent() {
    wx.setStorageSync('content', "在这里你可以问各种家庭教育问题，当你不开心的时候，当你遇到家人不理解的时候，来这里咨询迪朴熊，它会帮你出谋划策。") 
    wx.setStorageSync('type', "p")
    wx.switchTab({
      url: '/pages/chat/chat',
    })
  },
  child() {
    wx.setStorageSync('content', "在这里你可以问各种学习问题，有想吐槽的心里话也可以和迪朴熊交流，它会替你保密，帮你出主意，它会回答你的各种问题。")
    wx.setStorageSync('type', "c")
    wx.switchTab({
      url: '/pages/chat/chat',
    })
  },
  handleContact (e) {
    console.log(e.detail.path)
    console.log(e.detail.query)
  },
  question() {
    wx.setStorageSync('content', "在这里，你尽可以问各种千奇百怪的问题，可以问你遇到的任何问题，来这里咨询迪朴熊，它会给你全世界的智慧。")
    wx.setStorageSync('type', "q")
    wx.switchTab({
      url: '/pages/chat/chat',
    })
  },
  onLoad: function(){
  },
  onShow: function () {
    if (wx.getStorageSync('expireTime') == null || wx.getStorageSync('expireTime') < Date.now()) {
      console.log(wx.getStorageSync('expireTime') + "-->" + Date.now());
      wx.removeStorageSync('expireTime')
      let username = wx.getStorageSync('username')
      wx.removeStorageSync('username')
      wx.request({
        url: 'http://127.0.0.1/user/logout',
        method: "get",
        data: {
          "username": username,
        },
        success: ({
          data
        }) => {
          wx.showToast({
            icon: 'none',
            title: '身份验证到期，请重新登录',
            duration: 2500
          })
        }
      })

    }
    wx.request({
      url: 'http://127.0.0.1/user/checkUserKey',
      method: "get",
      data: {
        "username": wx.getStorageSync('username'),
        "key": wx.getStorageSync('key')
      },
      success: ({
        data
      }) => {
        console.log(data.code);
        if (data.code === 500) {
          wx.showToast({
            icon: 'none',
            title: data.message,
            duration: 2500
          })
          wx.removeStorageSync('username')
          wx.removeStorageSync('key')
          wx.redirectTo({
            url: '/pages/login/login',
          })
        }
      }
    })
    if (wx.getStorageSync('username') == null || wx.getStorageSync('username') === '') {
      wx.redirectTo({
        url: '/pages/login/login',
      })
    }
  }
})
