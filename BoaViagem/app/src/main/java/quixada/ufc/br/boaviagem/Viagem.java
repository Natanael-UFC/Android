package quixada.ufc.br.boaviagem;

public class Viagem {
    private String destino;
    private String tipo_viagem;
    private String data_chegada;
    private String data_saida;
    private String orcamento;
    private String qtd_pessoas;
    private int id_viagem;

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getTipo_viagem() {
        return tipo_viagem;
    }

    public void setTipo_viagem(String tipo_viagem) {
        this.tipo_viagem = tipo_viagem;
    }

    public String getData_chegada() {
        return data_chegada;
    }

    public void setData_chegada(String data_chegada) {
        this.data_chegada = data_chegada;
    }

    public String getData_saida() {
        return data_saida;
    }

    public void setData_saida(String data_saida) {
        this.data_saida = data_saida;
    }

    public String getOrcamento() {
        return orcamento;
    }

    public void setOrcamento(String orcamento) {
        this.orcamento = orcamento;
    }

    public String getQtd_pessoas() {
        return qtd_pessoas;
    }

    public void setQtd_pessoas(String qtd_pessoas) {
        this.qtd_pessoas = qtd_pessoas;
    }

    public int getId_viagem() {
        return id_viagem;
    }

    public void setId_viagem(int id_viagem) {
        this.id_viagem = id_viagem;
    }
}
