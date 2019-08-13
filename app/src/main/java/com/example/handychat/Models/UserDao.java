package com.example.handychat.Models;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
interface UserDao {
    @Insert
    void insert(User... users);

    @Delete
    void Delete(User... users);

    @Update
    void Update(User... users);

    @Query("DELETE FROM users")
    void deleteAll();

    @Query("SELECT * from users")
    List<User> getAllUsers();
}
