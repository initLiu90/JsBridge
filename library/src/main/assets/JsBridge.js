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
  function request(msg, callback, method) {
    var reqMsg = { data: msg };

    if (method) {
      reqMsg.methodName = method;
    }

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
   * native call this method
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
   * native call this method
   * native registe jsbridge interface
   */
  function handleNativeRegisteRequest(msgJson) {
    console.log("js:handleNativeRegisteRequest-->" + msgJson);
    if (msgJson) {
      var reqMsg = JSON.parse(msgJson);
      var methodNameArray = reqMsg.data.methodNames.split(",");
      var instanceName = reqMsg.data.instanceName;
      for (var item in methodNameArray) {
        createRequestMethod(methodNameArray[item]);
      }
      window[instanceName] = window.JsBridge;
    }
  }

  /**
   * 根据native传来的方法名，在window.JsBridge创建对应的方法
   * @param {*} methodName
   */
  function createRequestMethod(methodName) {
    console.log("methoName=" + methodName);
    JsBridge[methodName] = function(msg, callback) {
      console.log("call " + methodName);
      this.request(msg, callback, methodName);
    };
  }

  /**
   * native call this method
   * handle native response to js
   * @param msgJson json字符串
   */
  function handleNativeResponse(msgJson) {
    console.log("handleNativeResponse:" + msgJson);
    if (msgJson) {
      var rspMsg = JSON.parse(msgJson);
      if (rspMsg.responseId) {
        if (rspMsg.data instanceof Object) {
          callbackQueue[rspMsg.responseId](JSON.stringify(rspMsg.data));
        } else {
          callbackQueue[rspMsg.responseId](rspMsg.data);
        }
      }
    }
  }

  var JsBridge = (window.JsBridge = {
    init: init,
    handleNativeRequest: handleNativeRequest,
    handleNativeResponse: handleNativeResponse,
    handleNativeRegisteRequest: handleNativeRegisteRequest,
    request: request
  });
  createIframe();

  var readyEvent = document.createEvent("Events");
  readyEvent.initEvent("JsBridgeReady");
  readyEvent.bridge = JsBridge;
  document.dispatchEvent(readyEvent);

  console.log("load JsBridge success");
})();
