package quixada.ufc.br.boaviagem;


public class Gasto {
    private String categoria;
    private double valor;
    private String data;
    private String descricao;
    private String local;
    private int id_gasto_viagem;

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public int getId_gasto_viagem() {
        return id_gasto_viagem;
    }

    public void setId_gasto_viagem(int id_gasto_viagem) {
        this.id_gasto_viagem = id_gasto_viagem;
    }
}
