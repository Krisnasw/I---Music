package com.intivestudio.ryppmusic.data.local;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface DaoAccess {

    @Query("SELECT * FROM users")
    List<UserDAO> getAll();

    @Query("SELECT * FROM users where uid LIKE  :uid")
    UserDAO findByName(String uid);

    @Query("SELECT COUNT(*) from users")
    int countUsers();

    @Insert
    void insertAll(UserDAO... users);

    @Delete
    void delete(UserDAO user);

    @Query("DELETE FROM users")
    void nukeTable();

    @Query("UPDATE users SET name = :name, phone = :phone, birthdate = :birthdate WHERE uid = 1")
    void updateUser(String name, String phone, String birthdate);

}
