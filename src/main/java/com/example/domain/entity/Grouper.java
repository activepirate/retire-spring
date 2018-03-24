package com.example.domain.entity;

import com.example.domain.enums.CanLogin;

import javax.persistence.*;

/**
 * 组长表
 */
@Entity
@Table(name = "tb_grouper")
public class Grouper {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "group_id")
    private Integer grouperId;      //id

    @OneToOne
    @JoinColumn(name = "user_id",unique = true)
    private User user;          //用户

    @Column(nullable = false)
    private String password;    //密码不为空

    @Column(nullable = true)
    private String lastTime;        //最后登录日可以为空

    @Column(nullable = false)
    private CanLogin canLogin;      //是否允许登陆

    public Grouper() {
    }

    public Grouper(User user, String password, CanLogin canLogin) {
        this.user = user;
        this.password = password;
        this.canLogin = canLogin;
    }

    public Integer getGrouperId() {
        return grouperId;
    }

    public void setGrouperId(Integer grouperId) {
        this.grouperId = grouperId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public CanLogin getCanLogin() {
        return canLogin;
    }

    public void setCanLogin(CanLogin canLogin) {
        this.canLogin = canLogin;
    }
}