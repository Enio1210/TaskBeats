package com.comunidadedevspace.taskbeats.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(task: Task)

    @Query("Select * from task")
    fun getAll():List<Task>

    //UPDATE encotrar a tarefa que queremos
    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(task: Task)


// deleta todos
    @Query( " Delete from task")
    fun deleteAll()


    // deletar por id
    @Query( " Delete from task where id= :id")
    fun deleteById(id:Int)


}

//isso e o que acessa