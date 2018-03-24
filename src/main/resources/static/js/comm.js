var gGroups;

$(function () {
    loadGroup();
    toastrOpt();
})

/**
 * toastrOpt提示框
 */
function toastrOpt() {
    toastr.options = {
        'closeButton': true,
        'positionClass': 'toast-top-center',
        'timeOut': '5000',
    };
}

/**
 *
 * 无数据交互，走此处
 * 为风格统一，url以"/"开头
 * @param url */
/*function directUrl(url) {
    window.location.href="/direct"+url;
}*/


/**
 * 加载组
 */
function loadGroup() {
    $.ajax({
        async: false,
        type: 'POST',
        dataType: 'json',
        url: '/group/groupList',
        error : function(XMLHttpRequest, textStatus, errorThrown) {
            console.log(XMLHttpRequest);
            console.log(textStatus);
            console.log(errorThrown);
        },
        success: function(data){
            gGroups=data.data;
            initFollows(gGroups);
        }
    });
}

/**
 * 生成html代码
 * @param gGroups
 */
function initFollows(gGroups) {
    for(var i=0;i<gGroups.length;i++) {
        var groupId=gGroups[i].groupId;
        var groupName=gGroups[i].groupName;
        var url = "/group/"+groupId;
        var id= groupId;
        var group= "<li class=\"sidebar-nav-link\">";
        group = group+"<a href=\""+url+"\" id=\""+id+"\" th:class=\"(${activeId} eq '"+id+"')?'active':''\">";
        group = group + "<span class=\"am-icon-angle-right sidebar-nav-link-logo\"></span>"+groupName;
        group = group+"</a></li>";
        $("#newGroup").after(group)
    }
}