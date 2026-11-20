package com.example.roteiro_pdm13.domain.model

data class MaintenanceItem(
    val id: Long = 0L,
    val veiculo: String,
    val servico: String,
    val categoria: String,
    val quilometragemAlvo: String,
    val dataPrevista: String,
    val observacoes: String,
    val isDeleted: Boolean = false
)