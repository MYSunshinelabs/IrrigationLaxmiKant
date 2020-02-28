package com.ve.irrigation.DataValues;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by laxmi on 13/6/18.
 */

@Dao
public interface ConnectionSourceDAO {

    @Insert
    void insert(ConnectionSourceData connectionSourceData);

    @Query("SELECT * FROM ConnectionSourceData")
    List<ConnectionSourceData> getAllConnectionSource();
}
