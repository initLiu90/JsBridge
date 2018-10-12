(function() {
  console.log("load");
  if (window.JsBridge) {
    console.log("JsBridge already loaded!!!");
    return;
  }

  var SCHEMA = "jsbridge";
  var messageIframe;
  var callbackQueue = {};
  var seq = 0;

  function createIframe() {
    messageIframe = document.createElement("iframe");
    messageIframe.setAttribute("width", 0);
    messageIframe.setAttribute("height", 0);
    messageIframe.setAttribute("style", "display: none;");
    document.body.appendChild(messageIframe);
  }

  function init(messageHandler) {
    console.log("init");
    if (JsBridge.messageHandler) {
      throw new Error("WebViewJavascriptBridge.init called twice");
    }
    JsBridge.messageHandler = messageHandler;
  }

  /**
   * 分发消息
   * @param msgJson json字符串
   */
  function dispatchMessage(msgJson) {
    var message = JSON.parse(msgJson);
    var responseCallback;
    /* native调用js，native需要callback */
    if (message.callbackId) {
      /*获取native传来的callbackid*/
      var callbackId = message.callbackId;

      responseCallback = function(responseData) {
        console.log("real response:" + responseData);
        _send({
          responseId: callbackId,
          data: responseData
        });
      };
      JsBridge.messageHandler(message.data, responseCallback);
    } else {
      /* js调用native后，native的response */
      if (message.responseId) {
        callbackQueue[message.responseId](message.data);
      }
    }
  }

  /**
   * 真正向native发消息的接口
   * @param {*} msg object类型{data:xxx,responseId:xxx}
   * @param {*} callback 回调
   */
  function _send(msg, callback) {
    if (callback) {
      msg.callbackId = "cb_" + ++seq + "_" + new Date().getTime();
      /* 加入到callback队列中 */
      callbackQueue[msg.callbackId] = callback;
    }
    var msgJson = JSON.stringify(msg);
    console.log("send msg to native:" + msgJson);
    messageIframe.src =
      SCHEMA + "://handleJsCall/" + encodeURIComponent(msgJson);
  }

  /**
   * 提供给H5调用的接口，向nativie发送消息
   * @param msg
   */
  function send(msg, callback) {
    _send({ data: msg }, callback);
  }

  /**
   * 由native调用
   * @param msgJson json字符串
   */
  function handleNativeCall(msgJson) {
    console.log("handleNativeCall:" + msgJson);
    if (msgJson) {
      dispatchMessage(msgJson);
    }
  }

  var JsBridge = (window.JsBridge = {
    init: init,
    handleNativeCall: handleNativeCall,
    send: send
  });
  createIframe();

  var readyEvent = document.createEvent("Events");
  readyEvent.initEvent("JsBridgeReady");
  readyEvent.bridge = JsBridge;
  document.dispatchEvent(readyEvent);

  console.log("load JsBridge success");
})();
