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
   * js response to native
   * @param {*} rspMsg 
   */
  function response(rspMsg) {
    var rspMsgJson = JSON.stringify(rspMsg);
    console.log("js:response--->" + rspMsgJson.toString());
    messageIframe.src =
      SCHEMA + "://JsResponse/" + encodeURIComponent(rspMsgJson);
  }

  /**
   * js request native
   * @param msg
   */
  function request(msg, callback) {
    var reqMsg = { data: msg };
    if (callback) {
      reqMsg.callbackId = "cb_" + ++seq + "_" + new Date().getTime();
      /* 加入到callback队列中 */
      callbackQueue[reqMsg.callbackId] = callback;
    }
    var reqMsgJson = JSON.stringify(reqMsg);
    console.log("js:request---->" + reqMsgJson.toString());
    messageIframe.src =
      SCHEMA + "://JsRequest/" + encodeURIComponent(reqMsgJson);
  }

  /**
   * handle native request js
   * @param msgJson json字符串
   */
  function handleNativeRequest(msgJson) {
    console.log("js:handleNativeRequest-->" + msgJson);
    if (msgJson) {
      var reqMsg = JSON.parse(msgJson);
      var responseCallback;

      /* native 需要js response */
      if (reqMsg.callbackId) {
        /*获取native传来的callbackid*/
        var callbackId = reqMsg.callbackId;
        responseCallback = function(responseData) {
          response({
            responseId: callbackId,
            data: responseData
          });
        };
      }
      JsBridge.messageHandler(reqMsg.data, responseCallback);
    }
  }

  /**
   * handle native response to js
   * @param msgJson json字符串
   */
  function handleNativeResponse(msgJson) {
    console.log("handleNativeResponse:" + msgJson);
    if (msgJson) {
      var rspMsg = JSON.parse(msgJson);
      if (rspMsg.responseId) {
        callbackQueue[rspMsg.responseId](rspMsg.data);
      }
    }
  }

  var JsBridge = (window.JsBridge = {
    init: init,
    handleNativeRequest: handleNativeRequest,
    handleNativeResponse: handleNativeResponse,
    request: request
  });
  createIframe();

  var readyEvent = document.createEvent("Events");
  readyEvent.initEvent("JsBridgeReady");
  readyEvent.bridge = JsBridge;
  document.dispatchEvent(readyEvent);

  console.log("load JsBridge success");
})();
