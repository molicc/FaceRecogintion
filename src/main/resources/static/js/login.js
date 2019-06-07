//拍照函数

$("#verificaitondiv").hide();
var risklogin = false;

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
    var verification = $("#verification").val();
    if (username.replace("\s+g", "") == '') {
        alert("请输入用户名!")
    } else if (risklogin == false) {
        $.ajax({
            type: "post",
            url: "/loginReq",
            data: {"img": imgData1, "risklogin": risklogin, "username": username},
            success: function (data) {
                if (data.success) {
                    $("#verificaitondiv").hide();
                    $("#verification").val("");
                    risklogin = false;
                    alert(data.msg);
                } else {
                    if (data.risklogin) {
                        risklogin = true;
                        $("#verificaitondiv").show();
                        alert("此次登录有风险，请输入验证码!");
                    }else {
                        alert(data.msg);
                    }
                }
            }, error: function (data) {
                alert("登录失败，请稍后重试！");
            }
        });
    } else if (risklogin == true) {
        if (verification.replace("\s+g", "") == '') {
            alert("请输入验证码!")
        } else {
            $.ajax({
                type: "post",
                url: "/loginReq",
                data: {"img": imgData1, "risklogin": risklogin, "username": username, "verification": verification},
                success: function (data) {
                    if (data.success) {
                        $("#verificaitondiv").hide();
                        $("#verification").val("");
                        risklogin = false;
                        alert(data.msg);
                    } else {
                        if (data.risklogin) {
                            risklogin = true;
                            $("#verificaitondiv").show();
                            alert("此次登录有风险，请做安全校验!");
                        } else {
                            alert(data.msg);
                        }
                    }
                }, error: function (data) {
                    alert("登录失败，请稍后重试！");
                }
            });
        }
    }

}