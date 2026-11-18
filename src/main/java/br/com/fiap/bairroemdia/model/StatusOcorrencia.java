package br.com.fiap.bairroemdia.model;

import java.util.EnumSet;
import java.util.Set;

/**
 * Ciclo de vida de uma ocorrência.
 * ABERTA -> EM_ANALISE -> EM_ANDAMENTO -> RESOLVIDA; CANCELADA a partir de qualquer estado não final.
 */
public enum StatusOcorrencia {
    ABERTA,
    EM_ANALISE,
    EM_ANDAMENTO,
    RESOLVIDA,
    CANCELADA;

    public Set<StatusOcorrencia> proximosPermitidos() {
        return switch (this) {
            case ABERTA -> EnumSet.of(EM_ANALISE, CANCELADA);
            case EM_ANALISE -> EnumSet.of(EM_ANDAMENTO, CANCELADA);
            case EM_ANDAMENTO -> EnumSet.of(RESOLVIDA, CANCELADA);
            case RESOLVIDA, CANCELADA -> EnumSet.noneOf(StatusOcorrencia.class);
        };
    }

    public boolean podeTransitarPara(StatusOcorrencia destino) {
        return destino != null && proximosPermitidos().contains(destino);
    }

    public boolean isFinal() {
        return this == RESOLVIDA || this == CANCELADA;
    }
}
