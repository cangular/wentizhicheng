//index.js
//获取应用实例
const app = getApp()
let username = ''
let password = ''
Page({
  data: {
    username: '',
    password: '',
    clientHeight: ''
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
  onLoad() {
    if (wx.getStorageSync('username') != null && wx.getStorageSync('username') != '') {
      wx.switchTab({
        url: '/pages/index/index',
      })
    }
    var that = this
    wx.getSystemInfo({
      success: function (res) {
        // console.log(res.windowHeight)
        that.setData({
          clientHeight: res.windowHeight
        });
      }
    })
  },

  //获取输入款内容
  getUser(e) {
    username = e.detail
  },
  getPassword(e) {
    password = e.detail
  },
  //登录事件
  goadmin() {
    let flag = false //表示账户是否存在,false为初始值
    if (username == '') {
      wx.showToast({
        icon: 'none',
        title: '账号不能为空',
      })
    } else if (password == '') {
      wx.showToast({
        icon: 'none',
        title: '密码不能为空',
      })
    } else {
      wx.request({
        url: 'http://127.0.0.1/user/login',
        method: "get",
        data: {
          "username": username,
          "password": password
        },
        success: ({
          data
        }) => {
          console.log(data);
          if (data.code === 200) {
            wx.showToast({
              icon: 'none',
              title: '登陆成功',
              duration: 2500
            })
            wx.setStorageSync("username", username)
            wx.setStorageSync('expireTime', Date.now() + 60 * 60 * 24 * 1000);
            wx.setStorageSync('key', data.message)
            console.log("到期时间" + wx.getStorageSync('expireTime'))
            wx.switchTab({
              url: '/pages/index/index'
            })
          } else {
            wx.showToast({
              icon: 'none',
              title: data.message,
              duration: 2500
            })
          }
        }
      })
    }
  },
})