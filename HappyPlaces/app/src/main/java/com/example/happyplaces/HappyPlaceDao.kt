package com.example.happyplaces

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.io.FileDescriptor

@Dao
interface HappyPlaceDao {
    @Insert
    suspend fun insert(happyPlaceEntity: HappyPlaceEntity)

    @Update
    suspend fun updateUsers(happyPlaceEntity: HappyPlaceEntity)

    @Delete
    suspend fun delete(happyPlaceEntity: HappyPlaceEntity)

    @Query("UPDATE `happy-place-table`SET title=:title,date=:date,description=:description,location=:location ,latitude=:latitude,longitude=:longitude where id=:id")
    suspend fun update(
        id: Int,
        title: String,
        description: String,
        date: String,
        location: String,
        latitude: Double,
        longitude: Double,
    )

    @Query("SELECT * FROM `happy-place-table`")
    fun fetchAllPlace(): Flow<List<HappyPlaceEntity>>

    @Query("SELECT * FROM `happy-place-table` WHERE id=:id")
    fun fetchPlaceById(id: Int): Flow<List<HappyPlaceEntity>>
}