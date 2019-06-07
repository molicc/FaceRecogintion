//拍照函数
function CatchCode() {
    var canvans = document.getElementById("canvas");
    var video = document.getElementById("video");
    var context = canvas.getContext("2d");

    canvas.width = video.videoWidth;
    canvas.height = video.videoHeight;
    context.drawImage(video, 0, 0);

    var imgData = canvans.toDataURL();
    //获取图像在前端截取22位以后的字符串作为图像数据
    var imgData1 = imgData.substring(22);

    var username = $("#username").val();
    var email = $("#email").val();
    var verification = $("#verification").val();

    if (username.replace("\s+g", '') == '') {
        alert("请输入用户名！");
    } else if (email.replace("\s+g", '') == '') {
        alert("请输入安全邮箱!");
    } else if (verification.replace("\s+g", '') == '') {
        alert("请输入验证码!");
    } else {
        $.ajax({
            type: "post",
            url: "/registerReq",
            data: {"img": imgData1, "username": username, "email": email, "verification": verification},
            success: function (data) {
                if (data.success) {
                    alert(data.msg);
                    window.location.href = "login";
                } else {
                    alert(data.msg);
                }
            }, error: function (data) {
                alert("注册失败，请稍后重试！");
            }
        });
    }

}

//发送邮件
function sendEmail() {
    var email = $("#email").val();

    if (email.replace("\s+g", '') == '') {
        alert("请输入安全邮箱!");
    } else {
        $.ajax({
            type: "post",
            url: "/sendEmailReq",
            data: {"email": email},
            success: function (data) {
                alert(data.msg);
            }, error: function (data) {
                alert("发送失败，请稍后重试！");
            }
        });
    }
}