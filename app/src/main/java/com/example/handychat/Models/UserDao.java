package com.example.handychat.Models;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
interface UserDao {
    @Insert
    void insert(User... users);

    @Query("SELECT * FROM users WHERE email=:userEmail")
    User getUser(String userEmail);

    @Query("DELETE FROM users")
    void deleteAll();
}
