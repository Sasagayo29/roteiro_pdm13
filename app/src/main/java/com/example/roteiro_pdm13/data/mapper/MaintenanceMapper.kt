package com.example.roteiro_pdm13.data.mapper

import com.example.roteiro_pdm13.data.local.entity.MaintenanceEntity
import com.example.roteiro_pdm13.domain.model.MaintenanceItem

fun MaintenanceEntity.toDomain(): MaintenanceItem = MaintenanceItem(
    id = id,
    veiculo = veiculo,
    servico = servico,
    categoria = categoria,
    quilometragemAlvo = quilometragemAlvo,
    dataPrevista = dataPrevista,
    observacoes = observacoes,
    isDeleted = isDeleted
)

fun MaintenanceItem.toEntity(): MaintenanceEntity = MaintenanceEntity(
    id = id,
    veiculo = veiculo,
    servico = servico,
    categoria = categoria,
    quilometragemAlvo = quilometragemAlvo,
    dataPrevista = dataPrevista,
    observacoes = observacoes,
    isDeleted = isDeleted
)