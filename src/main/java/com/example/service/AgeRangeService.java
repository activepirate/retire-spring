package com.example.service;

import com.example.domain.entity.AgeRange;
import com.example.domain.enums.Exist;
import com.example.utils.UserAgePage;

/**
 * Create by : Zhangxuemeng
 * csdn：https://blog.csdn.net/Luck_ZZ
 */
public interface AgeRangeService extends BaseCrudService<AgeRange,Integer>{

    /*保存*/
    AgeRange save(Integer minAge, Integer maxAge);

    /*根据jobNum查询*/
    UserAgePage findAllUserAndAgeByJobNum(String jobNum, Integer page);

    /*根据name查询*/
    UserAgePage findAllUserAndAgeByName(String name, Integer page);

    /*查找年龄范围内用户*/
    UserAgePage findAllByAgeRange(Exist exist, Integer ageRangeId, Integer page);

}
