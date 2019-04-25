package com.bluve.models;

public class Torneira {
    public String key;
    /**
     * Representa o estado atual da torneira: true para ligada e false para desligada
     */
    public boolean aberta;

    /**
     * Consumo total da torneira, isso é, a soma de todos os usos anteriores
     */
    public long consumoTotal;

    /**
     * Último consumo da torneira
     */
    public long consumo;

    /**
     * Total de horas que a torneira passou aberta
     */
    public long tempoTotal;

    /**
     * Tempo que durou a última abertura da torneira
     */
    public long tempo;


    public Torneira() {

    }

    public Torneira(boolean aberta) {
        this.aberta = aberta;
    }

    public Torneira(String key,
                    boolean aberta,
                    int consumoTotal,
                    int consumo,
                    int tempoTotal,
                    int tempo) {
        this.key = key;
        this.aberta = aberta;
        this.consumoTotal = consumoTotal;
        this.consumo = consumo;
        this.tempoTotal = tempoTotal;
        this.tempo = tempo;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setAberta(boolean aberta) {
        this.aberta = aberta;
    }

    public boolean isAberta() {
        return aberta;
    }

    public void setConsumoTotal(long consumoTotal) {
        this.consumoTotal = consumoTotal;
    }

    public long getConsumoTotal() {
        return consumoTotal;
    }

    public void setConsumo(long consumo) {
        this.consumo = consumo;
    }

    public long getConsumo() {
        return consumo;
    }

    public void setTempoTotal(long tempoTotal) {
        this.tempoTotal = tempoTotal;
    }

    public long getTempoTotal() {
        return tempoTotal;
    }

    public long getTempo() {
        return tempo;
    }
}
