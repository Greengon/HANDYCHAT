package com.example.handychat.Models;

import java.util.List;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
interface JobRequestDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(JobRequest... jobRequests);

    @Update
    void Update(JobRequest... jobRequests);

    @Query("DELETE FROM job_requests")
    void deleteAll();

    @Query("SELECT * FROM job_requests")
    List<JobRequest> getAllJobRequests();

    @Query("SELECT * FROM job_requests WHERE id=:id")
    JobRequest getJobRequest(String id);

    @Query("DELETE FROM job_requests WHERE id=:jobId")
    void DeleteById(String jobId);
}
