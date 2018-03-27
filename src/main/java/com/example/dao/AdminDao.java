package com.example.dao;

import com.example.domain.entity.Admin;
import com.example.domain.enums.CanLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AdminDao extends JpaRepository<Admin, Integer> {
    boolean existsByJobNum(String jobNum);

    @Modifying(clearAutomatically=true)
    @Query("update Admin set name=:name,canLogin=:canLogin  where adminId=:adminId")
    int updateNameAndCanLogin(@Param("name") String name, @Param("canLogin") CanLogin canLogin, @Param("adminId") Integer adminId);

    @Modifying(clearAutomatically=true)
    @Query("update Admin set password=:password where adminId=:adminId")
    int updatePassword(@Param("password") String password, @Param("adminId") Integer adminId);
}
