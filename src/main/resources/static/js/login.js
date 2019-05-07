//拍照函数
function CatchCode() {
    var canvans = document.getElementById("canvas");
    var video = document.getElementById("video");
    var context = canvas.getContext("2d");

    canvas.width = video.videoWidth;
    canvas.height = video.videoHeight;
    context.drawImage(video,0,0);

    var imgData = canvans.toDataURL();
    //获取图像在前端截取22位以后的字符串作为图像数据
    var imgData1 = imgData.substring(22);

    var username = $("#username").val();
    $.ajax({
        type: "post",
        url: "/loginReq",
        data: {"img": imgData1, "username": username},
        success: function (data) {
            if (data.success) {
                alert(data.msg);
            }else {
                alert(data.msg);
            }
        }, error: function (data) {
            alert("登录失败，请稍后重试！");
        }
    });
}