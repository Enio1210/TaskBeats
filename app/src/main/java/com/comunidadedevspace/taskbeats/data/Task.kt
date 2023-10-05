package com.comunidadedevspace.taskbeats.data


import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity

// nesse codigo vamos colocar um id ,,pq assim e mais facil manipular ,,quando tem um id
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int=0,
    val title: String,
    val description: String
) : Serializable
// nossa class da base de dados