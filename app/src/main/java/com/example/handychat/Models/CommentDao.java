package com.example.handychat.Models;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
interface CommentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Comment... comments);

    @Delete
    void Delete(Comment... comments);

    @Update
    void Update(Comment... comments);

    @Query("DELETE FROM comments")
    void deleteAll();

    @Query("SELECT * FROM comments WHERE job_request_id=:jobId")
    List<Comment> getAllCommentsOfJob(String jobId);

    @Query("SELECT * FROM comments WHERE id=:id")
    Comment getComment(String id);
}
