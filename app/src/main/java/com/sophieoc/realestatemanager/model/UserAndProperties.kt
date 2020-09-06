package com.sophieoc.realestatemanager.model

import androidx.room.Embedded
import androidx.room.Relation

data class UserAndProperties(
        @Embedded val user: User,
        @Relation(
                parentColumn = "uid",
                entityColumn = "userId"
        ) val properties: List<Property>
)