package com.example.roteiro_pdm13.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "maintenance_items")
data class MaintenanceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val veiculo: String,
    val servico: String,
    val categoria: String,
    @ColumnInfo(name = "quilometragem_alvo") val quilometragemAlvo: String,
    @ColumnInfo(name = "data_prevista") val dataPrevista: String,
    val observacoes: String,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean = false
)