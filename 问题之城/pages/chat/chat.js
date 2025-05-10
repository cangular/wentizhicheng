const app = getApp();
var msgList = [];
var parentMsg = [];
var childMsg = [];
var questionMsg = [];
var inputVal = '';
var windowWidth = wx.getSystemInfoSync().windowWidth;
var windowHeight = wx.getSystemInfoSync().windowHeight;
var keyHeight = 0;

let socketOpen = false;
let socketMsgQueue = [];
let lineCount = Math.floor(windowWidth / 16) - 6;
let curAnsCount = 0;


/**
 * 初始化数据
 */

function sendSocketMessage(msg) {
  if (socketOpen) {
    wx.sendSocketMessage({
      data: msg
    })
  } else {
    socketMsgQueue.push(msg)
  }
}



Page({
  /**
   * 页面的初始数据
   */
  data: {
    scrollHeight: '100vh',
    inputBottom: 0,
    inputVal: '',
    flag: false,
    system: true
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
  syncMsgList:function(){
    let type = wx.getStorageSync('type')
    if(type === "p"){
      parentMsg = msgList
      this.setData
      ({
        parentMsg
      })
    }
    if(type === "c"){
      childMsg = msgList
      this.setData({
        childMsg
      })
    }
    if(type === "q"){
      questionMsg = msgList
      this.setData({
        questionMsg
      })
    }
  },
  loadCachedMsg: function(){
  if(wx.getStorageSync('type') === "p"){
    msgList = parentMsg
    this.setData
    ({
      msgList
    })
  }
  if(wx.getStorageSync('type') === "c"){
    msgList = childMsg
    this.setData({
      msgList
    })
  }
  if(wx.getStorageSync('type') === "q"){
    msgList = questionMsg
    this.setData({
      msgList
    })
  }
},
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.setData({
      cusHeadIcon: "/images/用户.png",
    });
  },



  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {
    if (wx.getStorageSync('expireTime') == null || wx.getStorageSync('expireTime') < Date.now()) {
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
    if(wx.getStorageSync('type')===""){
      wx.setStorageSync('type', "q")
    }
    this.loadCachedMsg()
    console.log(msgList.length)
    if(msgList.length == 0){
    if (wx.getStorageSync('content')!=""){
      this.setData({
        msgList: [{
          speaker: 'server',
          contentType: 'text',
          content: wx.getStorageSync('content')
        }, ]
      })
    }else{
      this.setData({
        msgList: [{
          speaker: 'server',
          contentType: 'text',
          content: "在这里，你尽可以问各种千奇百怪的问题，可以问你遇到的任何问题，来这里咨询迪朴熊，它会给你全世界的智慧。"
        }, ]
      })
    }
    }
    wx.connectSocket({
      url: 'ws://127.0.0.1:80/chatWebSocket/' + wx.getStorageSync('username')
    })



    wx.onSocketOpen((res) => {
      socketOpen = true
      console.log("打开socket");
      socketMsgQueue = []
      wx.onSocketMessage((result) => {
        this.setData({flag:true})
        result.data = result.data.replace(" ", "&nbsp;");
        // console.log(result.data+"--->"+result.data.length+"--->"+  result.data.charAt(result.data.length-1).charCodeAt())
        curAnsCount++;
        // console.log(lineCount+"----"+curAnsCount);
          wx.createSelectorQuery().select('#chatPage').boundingClientRect(function (rect) {
            // 使页面滚动到底部
            console.log("????????")
            wx.pageScrollTo({
              scrollTop: 10000
            })
          }).exec()
        
        msgList[msgList.length - 1].content = msgList[msgList.length - 1].content + result.data
        this.setData({
          msgList,
          flag: false
        })
        this.syncMsgList()
      })
    })
    wx.onKeyboardHeightChange(re => {
    let res = wx.getSystemInfoSync()
    const menuButtonInfo = wx.getMenuButtonBoundingClientRect();
    let navBarHeight = (menuButtonInfo.top - res.statusBarHeight) * 2 + menuButtonInfo.height + res.statusBarHeight; 
    keyHeight = re.height - navBarHeight;
    if (keyHeight < 0) {
      keyHeight = 0
    }
    this.setData({
      scrollHeight: (windowHeight - keyHeight) + 'px'
    });
    this.setData({
      toView: 'msg-' + (msgList.length - 1),
      inputBottom: (keyHeight + 8) + 'px'
    })
    })
  },
  onHide: function () {
    wx.closeSocket()
    wx.onSocketClose((result) => {
      console.log("socket关闭成功");
    })
  },
  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function () {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {

  },

  /**
   * 获取聚焦
   */
  focus: function (e) {
    let res = wx.getSystemInfoSync()
    const menuButtonInfo = wx.getMenuButtonBoundingClientRect();
    let navBarHeight = (menuButtonInfo.top - res.statusBarHeight) * 2 + menuButtonInfo.height + res.statusBarHeight; 
    keyHeight = e.detail.height - navBarHeight;
    if (keyHeight < 0) {
      keyHeight = 0
    }
    this.setData({
      scrollHeight: (windowHeight - keyHeight) + 'px'
    });
    this.setData({
      toView: 'msg-' + (msgList.length - 1),
      inputBottom: (keyHeight + 8) + 'px'
    })
  },

  //失去聚焦(软键盘消失)
  blur: function (e) {
    this.setData({
      scrollHeight: '100vh',
      inputBottom: 0
    })
    this.setData({
      toView: 'msg-' + (msgList.length - 1)
    })

  },

  /**
   * 发送点击监听
   */
  sendClick: function (e) {
    if(inputVal === ''){
      return
    }
    if(this.data.flag) return
    sendSocketMessage(wx.getStorageSync('type')+inputVal)
    msgList.push({
      speaker: 'customer',
      contentType: 'text',
      content: inputVal
    })
    msgList.push({
      speaker: 'server',
      contentType: 'text',
      content: ''
    })

    inputVal=''
    this.setData({
      msgList,
      inputVal
    });
    this.syncMsgList();
  },
  handleInput(e){
    inputVal = e.detail.value
    this.setData({
      inputVal
    })
  },
  /**
   * 退回上一页
   */
  toBackClick: function () {
    wx.navigateBack({})
  }

})