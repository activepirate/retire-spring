package com.example.controller;

import com.example.comm.aop.LoggerManage;
import com.example.domain.bean.ActivityDefSearch;
import com.example.domain.bean.CommSearch;
import com.example.domain.bean.JoinUserSearch;
import com.example.domain.entity.*;
import com.example.domain.enums.SearchType;
import com.example.domain.result.ExceptionMsg;
import com.example.domain.result.Response;
import com.example.service.*;
import com.example.utils.DataUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RequestMapping("/join")
@Controller
public class JoinController extends BaseController{

    @Autowired
    private JoinService joinService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private UserService userService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private NationService nationService;

    @Autowired
    private PoliticsService politicsService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private DutyService dutyService;

    @RequestMapping(value = "/joinNoView/{activityId}")
    @LoggerManage(description = "待报名界面")
    public String joinNoView(@PathVariable Integer activityId, Model model, @RequestParam(value = "page", defaultValue = "0") Integer page){
        Activity activity = activityService.findOne(activityId);
        Page<User> datas = userService.findAllNoJoinCriteria(activityId, new JoinUserSearch(), page);

        assignModel(model, activity);
        model.addAttribute("datas", datas);

        return "admin/join_no";
    }

    @RequestMapping("/joinNoView/superSearch/{activityId}")
    @LoggerManage(description = "待报名界面高级搜索列表")
    public String joinNoViewSuperSearch(@PathVariable Integer activityId, Model model, @ModelAttribute(value = "joinUserSearch") JoinUserSearch joinUserSearch, @RequestParam(value = "page", defaultValue = "0") Integer page){
        Activity activity = activityService.findOne(activityId);
        Page<User> datas = userService.findAllNoJoinCriteria(activityId, joinUserSearch, page);

        assignModel(model, activity);
        model.addAttribute("datas",datas);

//        搜索表单的值，再传入页面
        model.addAttribute("joinUserSearch",joinUserSearch);

        return "admin/join_no";
    }

    @RequestMapping("/joinNoView/{activityId}/{type}/{value}")
    @LoggerManage(description = "待报名界面BySearch")
    public String joinNoViewByType(Model model, @PathVariable Integer activityId, @PathVariable Integer type, @PathVariable String value, @RequestParam(value = "page", defaultValue = "0") Integer page){
        Activity activity = activityService.findOne(activityId);
        assignModel(model, activity);
        model.addAttribute("userCommSearch", new CommSearch(type, value));
        model.addAttribute("searchType", SearchType.search);

        if (type == 1 && value != null){
//        根据工号
            Page<User> datas = userService.findAllNoJoinByJobNum(activityId, value, page);
            model.addAttribute("datas",datas);
            return "admin/join_no";
        }else if (type == 2 && value != null){
//        根据姓名
            Page<User> datas = userService.findAllNoJoinByName(activityId, value, page);
            model.addAttribute("datas",datas);
            return "admin/join_no";
        }
//        重定向
        return "redirect:/join/joinNoView/"+activityId;
    }

    @RequestMapping(value = "/joinOkView/{activityId}")
    @LoggerManage(description = "已报名界面")
    public String joinOkView(@PathVariable Integer activityId, Model model, @RequestParam(value = "page", defaultValue = "0") Integer page){
        Activity activity = activityService.findOne(activityId);
        Page<Join> datas = joinService.findAllByActivity_ActivityId(activityId, page);
        assignModel(model, activity);
        model.addAttribute("datas", datas);

        return "admin/join_ok";
    }

    @RequestMapping(value = "/joinOkView/superSearch/{activityId}")
    @LoggerManage(description = "已报名高级搜索界面")
    public String joinOkViewSuperSearch( HttpServletRequest request, @PathVariable Integer activityId, @ModelAttribute(value = "joinUserSearch") JoinUserSearch joinUserSearch, Model model, @RequestParam(value = "page", defaultValue = "0") Integer page){
        String[] inputDefs = request.getParameterValues("inputDefs");
        String attend = request.getParameter("attend");

        Activity activity = activityService.findOne(activityId);
        Page<Join> datas = joinService.findAllCriteria(activityId, inputDefs, attend, joinUserSearch, page);

        assignModel(model, activity);

        model.addAttribute("datas", datas);

        //        搜索表单的值，再传入页面
        joinUserSearch.assignActivityDefSearches(activity.getActivityDefs());
        List<ActivityDefSearch> activityDefSearches = joinUserSearch.getActivityDefSearches();
        for (int i = 0; i < inputDefs.length; i++){
            activityDefSearches.get(i).setOneInput(inputDefs[i]);
        }
        model.addAttribute("joinUserSearch",joinUserSearch);

        return "admin/join_ok";
    }

    @RequestMapping(value = "/joinOkView/{activityId}/{type}/{value}")
    @LoggerManage(description = "已报名界面BySearch")
    public String joinOkViewByType(@PathVariable Integer activityId, @PathVariable Integer type, @PathVariable String value, Model model, @RequestParam(value = "page", defaultValue = "0") Integer page){
        Activity activity = activityService.findOne(activityId);
        assignModel(model, activity);
        model.addAttribute("userCommSearch", new CommSearch(type, value));
        model.addAttribute("searchType", SearchType.search);
        if (type == 1 && value != null){
//        根据工号
            Page<Join> datas = joinService.findAllByActivity_ActivityIdAndUser_JobNum(activityId, value, page);
            model.addAttribute("datas",datas);
            return "admin/join_ok";
        }else if (type == 2 && value != null){
//        根据姓名
            Page<Join> datas = joinService.findAllByActivity_ActivityIdAndUser_Name(activityId, value, page);
            model.addAttribute("datas",datas);
            return "admin/join_ok";
        }
//        重定向
        return "redirect:/join/joinOkView/"+activityId;
    }

    @ResponseBody
    @RequestMapping(value = "/joinActivity")
    @LoggerManage(description = "活动报名")
    public Response joinActivity(HttpServletRequest request){

        try {
            Integer userId = Integer.parseInt(request.getParameter("userId"));
            Integer activityId = Integer.parseInt(request.getParameter("activityId"));
            String[] inputDefs = request.getParameterValues("inputDefs");
            String attend = request.getParameter("attend");

            if (!activityService.canJoin(activityId)){
                return result(ExceptionMsg.JoinForCloseFailed);
            }

            if (joinService.existsByActivityIdAndUserId(activityId, userId)){
                return result(ExceptionMsg.JoinAlreadyFailed);
            }

            joinService.save(userId,activityId,inputDefs,attend);

            return result(ExceptionMsg.JoinSuccess);
        }catch (Exception e){
            e.printStackTrace();
            return result(ExceptionMsg.JoinFailed);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/delete/{activityId}")
    @LoggerManage(description = "删除报名")
    public Response delete(@PathVariable String activityId, HttpServletRequest request){
        String[] joinIds = request.getParameterValues("id");
        Integer[] ids = DataUtils.turn(joinIds);
        try {
            if (!activityService.canJoin(Integer.parseInt(activityId))){
                return result(ExceptionMsg.JoinDelForCloseFailed);
            }
            joinService.delete(ids);
            return result(ExceptionMsg.JoinDelSuccess);
        }catch (Exception e){
            return result(ExceptionMsg.JoinDelFailed);
        }
    }

    private Model assignModel(Model model, Activity activity){
        //        所有的组
        List<Group> groups = groupService.findAll();

//        所有的民族
        List<Nation> nations = nationService.findAll();

        //        所有的政治面貌
        List<Politics> politicss = politicsService.findAll();

        //        所有的部门
        List<Department> departments = departmentService.findAll();

        //        所有的职务
        List<Duty> duties = dutyService.findAll();

//        用于用户高级搜索
        JoinUserSearch joinUserSearch = new JoinUserSearch(activity.getActivityDefs());

//       活动
        model.addAttribute("activity", activity);

        model.addAttribute("groups",groups);

        model.addAttribute("nations",nations);

        model.addAttribute("politicss",politicss);

        model.addAttribute("departments",departments);

        model.addAttribute("duties",duties);

        model.addAttribute("userCommSearch", new CommSearch(1, ""));

        model.addAttribute("joinUserSearch",joinUserSearch);

        model.addAttribute("searchType", SearchType.allOrSuper);

        return model;
    }

}
