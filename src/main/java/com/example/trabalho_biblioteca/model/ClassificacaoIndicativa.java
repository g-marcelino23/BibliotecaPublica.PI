package com.example.trabalho_biblioteca.model;

public enum ClassificacaoIndicativa {
    L("Livre", 0),
    DEZ("10 anos", 10),
    DOZE("12 anos", 12),
    QUATORZE("14 anos", 14),
    DEZESSEIS("16 anos", 16),
    DEZOITO("18 anos", 18);

    private final String descricao;
    private final int idadeMinima;

    ClassificacaoIndicativa(String descricao, int idadeMinima) {
        this.descricao = descricao;
        this.idadeMinima = idadeMinima;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getIdadeMinima() {
        return idadeMinima;
    }
}
