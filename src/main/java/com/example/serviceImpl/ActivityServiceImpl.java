package com.example.serviceImpl;

import com.example.dao.ActivityDao;
import com.example.domain.entity.Activity;
import com.example.domain.enums.ActivityStatus;
import com.example.service.ActivityService;
import com.example.utils.DataUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityServiceImpl extends BaseCrudServiceImpl<Activity,Integer,ActivityDao> implements ActivityService{
    @Autowired
    private ActivityDao activityDao;

    @Override
    public Activity save(Activity activity) {
//        把活动保存到草稿箱
        activity.setActivityStatus(ActivityStatus.draft);
        return activityDao.save(activity);
    }

    @Override
    public List<Activity> findAll() {
        List<Activity> activities = activityDao.findAll();
        for (Activity activity : activities) {
            String[] strings = activity.getInputDefs();
            String[][] strings1 = DataUtils.oneStrToTwoStr(strings);
            activity.setInputDefss(strings1);
        }
        return activities;
    }
}
