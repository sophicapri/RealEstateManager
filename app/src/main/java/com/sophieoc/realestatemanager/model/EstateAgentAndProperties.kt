package com.sophieoc.realestatemanager.model

import androidx.room.Embedded
import androidx.room.Relation

data class EstateAgentAndProperties(
        @Embedded val estateAgent: EstateAgent,
        @Relation(
                parentColumn = "uid",
                entityColumn = "estateAgentId"
        ) val properties: List<Property>
)