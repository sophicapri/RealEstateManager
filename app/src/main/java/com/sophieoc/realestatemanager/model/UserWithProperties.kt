package com.sophieoc.realestatemanager.model

import androidx.room.Embedded
import androidx.room.Relation


data class UserWithProperties(
        @Embedded val user: User,
        @Relation(
                parentColumn = "uid",
                entityColumn = "user_id",
        ) val properties: List<Property>
)