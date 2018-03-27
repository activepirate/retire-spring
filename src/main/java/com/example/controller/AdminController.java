package com.example.controller;

import com.example.comm.aop.LoggerManage;
import com.example.domain.entity.Admin;
import com.example.domain.enums.CanLogin;
import com.example.domain.enums.Gender;
import com.example.domain.result.ExceptionMsg;
import com.example.domain.result.Response;
import com.example.service.AdminService;
import com.example.utils.DateUtils;
import com.example.utils.ImgUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RequestMapping("/admin")
@Controller
public class AdminController extends BaseController{
    @Autowired
    private AdminService adminService;

    @RequestMapping("/adminList")
    @LoggerManage(description = "管理员列表")
    public String adminList(Model model){
        List<Admin> admins = adminService.findAll();
        model.addAttribute("admins",admins);
        return "admin/admin_list";
    }

    /**
     *
     * @param jobNum
     * @return false：用户存在 true：用户不存
     */
    @ResponseBody
    @RequestMapping("/exist")
    @LoggerManage(description = "管理员存在")
    public boolean existsAdmin(@RequestParam(value = "jobNum") String jobNum){
        boolean exist = adminService.existsByJobNum(jobNum);
        logger.info("用户存在:"+exist);
        if (!exist){
            return true;
        }
        return false;
    }

    @ResponseBody
    @RequestMapping(value = "/save",method = RequestMethod.POST)
    @LoggerManage(description = "管理员保存")
    public Response save(@ModelAttribute(value = "admin") Admin admin, Model model){
        if(adminService.existsByJobNum(admin.getJobNum())){
            return  result(ExceptionMsg.FAILED);
        }
        //        设置canLogin的值
        if(admin.getCanLogin() == null){
            admin.setCanLogin(CanLogin.no);
        }
//        设置创建时间
        admin.setCreateTime(DateUtils.getDateSequence());
//            保存
        adminService.save(admin);
        return result(ExceptionMsg.SUCCESS);
    }

    /**
     * 进入增加管理员界面
     * @param model
     * @return
     */
    @RequestMapping(value = "/addView")
    @LoggerManage(description = "增加管理员界面")
    public String addView(Model model){
        model.addAttribute("admin",new Admin());
        return "admin/admin_add";
    }

    @ResponseBody
    @RequestMapping(value = "/delete")
    @LoggerManage(description = "删除管理员")
    public Response delete(HttpServletRequest request){

        String[] adminIds = request.getParameterValues("adminId");

        Integer[] ids = new Integer[adminIds.length];
        for (int i = 0; i < adminIds.length; i++) {
            ids[i] = new Integer(adminIds[i]);
            logger.info("adminId:"+ids[i]);
        }

       if (!adminService.canDelete(ids)){
            return result(ExceptionMsg.DisableAdminDelete);
       }
       try {
            adminService.delete(ids);
           return result(ExceptionMsg.SUCCESS);
       }catch (Exception e){
           return result(ExceptionMsg.FAILED);
       }
    }

    @RequestMapping(value = "/updateView/{adminId}")
    @LoggerManage(description = "修改管理员界面")
    public String updateView(@PathVariable String adminId, Model model){
        Admin admin = adminService.findOne(Integer.parseInt(adminId));
        model.addAttribute("admin",admin);
        return "admin/admin_update";
    }

    @RequestMapping(value = "/datailView/{adminId}")
    @LoggerManage(description = "管理员详细信息界面")
    public String datailView(@PathVariable String adminId, Model model){
        Admin admin = adminService.findOne(Integer.parseInt(adminId));
        model.addAttribute("admin",admin);
        return "admin/admin_datail";
    }

    /**
     * typeId=1：修改姓名、权限
     * typeId=2:修改照片
     * @param typeId
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/update/{typeId}")
    @LoggerManage(description = "修改管理员")
    public Response update(@PathVariable Integer typeId, HttpServletRequest request){
        String adminId = request.getParameter("adminId");
        if (typeId == 1){
            logger.info("修改用户名及登陆状态");
            String name = request.getParameter("name");
            String canLoginStr = request.getParameter("canLogin");
            //        设置canLogin的值
            CanLogin canLogin = CanLogin.no;
            if(canLoginStr == null){
                canLogin=CanLogin.no;
            }else {
                canLogin=CanLogin.yes;
            }
            adminService.updateNameAndCanLogin(name, canLogin, Integer.parseInt(adminId));
            return result(ExceptionMsg.SUCCESS);
        }else if(typeId == 2){
//            修改密码
            logger.info("修改密码");
            String password = request.getParameter("password");
            adminService.updatePassword(password,Integer.parseInt(adminId));
            return result(ExceptionMsg.SUCCESS);
        }
        return result(ExceptionMsg.FAILED);
    }

    /**
     * 实现文件上传
     * */
    @ResponseBody
    @RequestMapping(value="fileUpload",method = RequestMethod.POST)
    @LoggerManage(description = "图片上传")
    public Response fileUpload(@RequestParam(value = "file") MultipartFile file, HttpServletRequest request){

        String adminId = request.getParameter("adminId");
        String jobNum = request.getParameter("jobNum");
        String imgUrl = request.getParameter("imgUrl");
        String fileName = file.getOriginalFilename();

        System.out.println("adminId:"+adminId);
        System.out.println("jobNum:"+jobNum);
        System.out.println("imgUrl:"+imgUrl);
        System.out.println("fileName:"+fileName);

 /*       String filePath = request.getSession().getServletContext().getRealPath("/");
        System.out.println(filePath);*/

        if(file.isEmpty()){
            return result(ExceptionMsg.FAILED);
        }

        return result(ExceptionMsg.SUCCESS);

/*        ImgUtils imgUtils = new ImgUtils(jobNum,file);
        try {
            imgUtils.save();
            return result(ExceptionMsg.SUCCESS);
        } catch (IOException e) {
            e.printStackTrace();
            return result(ExceptionMsg.FAILED);
        }*/
    }
}
