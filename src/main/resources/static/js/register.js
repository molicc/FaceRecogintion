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
    var question = $("#question").val();
    var answer = $("#answer").val();

    if (username.replace("\s+g", '') == '') {
        alert("请输入用户名！");
    } else if (question.replace("\s+g", '') == '') {
        alert("请输入安全问题!");
    } else if (answer.replace("\s+g", '') == '') {
        alert("请输入问题答案!");
    } else {
        $.ajax({
            type: "post",
            url: "/registerReq",
            data: {"img": imgData1, "username": username, "question": question, "answer": answer},
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