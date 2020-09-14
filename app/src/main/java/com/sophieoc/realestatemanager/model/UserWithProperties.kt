package com.sophieoc.realestatemanager.model

import androidx.room.Embedded
import androidx.room.Relation


data class UserWithProperties(
        @Embedded var user: User,
        @Relation(
                parentColumn = "uid",
                entityColumn = "user_id",
        ) var properties: List<Property>
)