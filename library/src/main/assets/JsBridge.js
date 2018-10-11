(function () {
    console.log('load');
    if (window.JsBridge) {
        console.log('JsBridge already loaded!!!');
        return;
    }

    var SCHEMA = 'jsbridge';
    var messageIframe;

    function createIframe() {
        messageIframe = document.createElement("iframe");
        messageIframe.setAttribute("width", 0);
        messageIframe.setAttribute("height", 0);
        messageIframe.setAttribute("style", "display: none;");
        document.body.appendChild(messageIframe);
    }

    function init(messageHandler) {
        console.log('init');
        if (JsBridge.messageHandler) {
            throw new Error('WebViewJavascriptBridge.init called twice');
        }
        JsBridge.messageHandler = messageHandler;
    }

    /**
     * 分发消息
     * @param msgJson json字符串
     */
    function dispatchMessage(msgJson) {
        var message = JSON.parse(msgJson);
        if (message.callbackId) {
            /*获取native传来的callbackid*/
            var callbackId = message.callbackId;

            responseCallback = function (responseData) {
                console.log('real response:' + responseData);
                send({
                    responseId: callbackId,
                    data: responseData
                });
            }
        }
        JsBridge.messageHandler(message.data, responseCallback);
    }

    /**
     * 向native发送消息
     * @param msg
     */
    function send(msg) {
        var msgJson = JSON.stringify(msg);
        console.log('send msg to native:' + msgJson);
        messageIframe.src = SCHEMA + "://handleJsCall/" + encodeURIComponent(msgJson);
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

    var JsBridge = window.JsBridge = {
        init: init,
        handleNativeCall: handleNativeCall
    };
    createIframe();

    var readyEvent = document.createEvent('Events');
    readyEvent.initEvent('JsBridgeReady');
    readyEvent.bridge = JsBridge;
    document.dispatchEvent(readyEvent);

    console.log('load JsBridge success');
})();