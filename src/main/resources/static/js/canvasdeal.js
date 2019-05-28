var video = document.getElementById('video');
var canvas = document.getElementById('canvas');
var context = canvas.getContext('2d');
function getUserMediaToPhoto(constraints,success,error) {
    if(navigator.mediaDevices.getUserMedia){
        //最新标准API
        navigator.mediaDevices.getUserMedia(constraints).then(success).catch(error);
    }else if (navigator.webkitGetUserMedia) {
        //webkit核心浏览器
        navigator.webkitGetUserMedia(constraints,success,error);
    }else if(navigator.mozGetUserMedia){
        //firefox浏览器
        navigator.mozGetUserMedia(constraints,success,error);
    }else if(navigator.getUserMedia){
        //旧版API
        navigator.getUserMedia(constraints,success,error);
    }
}
//成功回调函数
function success(stream){
    //兼容webkit核心浏览器
    var CompatibleURL = window.URL || window.webkitURL;
    try{
        //将视频流转化为video的源
        video.src = CompatibleURL.createObjectURL(stream);
    }catch (e) {
        //谷歌浏览器更新后无法使用摄像头Failed to execute 'createObjectURL' on 'URL'
        // https://blog.csdn.net/m0_37863265/article/details/84835764
        video.srcObject=stream;
    }
    video.play();//播放视频
}
function error(error) {
    console.log('访问用户媒体失败：',error.name,error.message);
}
if(navigator.mediaDevices.getUserMedia || navigator.webkitGetUserMedia || navigator.mozGetUserMedia || navigator.getUserMedia){
    getUserMediaToPhoto({video:{width:480,height:320}},success,error);
}else{
    alert('你的浏览器不支持访问用户媒体设备');
}
