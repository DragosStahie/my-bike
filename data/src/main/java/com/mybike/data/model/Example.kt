package com.mybike.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Example(
    @PrimaryKey val id: Int,
)
