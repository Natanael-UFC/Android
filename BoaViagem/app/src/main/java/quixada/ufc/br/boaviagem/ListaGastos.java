package quixada.ufc.br.boaviagem;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ListaGastos extends ListActivity implements AdapterView.OnItemClickListener {
    private List<Map<String, Object>> gastos;
    private String data_anterior = "";
    private MainViagem main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String[] categorias = {"data", "descricao", "valor", "categoria" };
        int[] ids = { R.id.data, R.id.descricao, R.id.valor, R.id.categoria};
        SimpleAdapter adapter = new SimpleAdapter(this, listarGastos(), R.layout.lista_gastos, categorias, ids);

        this.main = new MainViagem();
        adapter.setViewBinder(new GastoViewBinder());
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);
    }

    private List<Map<String, Object>> listarGastos() {
        boolean temp = false;
        this.gastos = new ArrayList<Map<String, Object>>();
        Map<String, Object> item = new HashMap<String, Object>();

        if(main.novo_gasto.isEmpty()){
            Toast.makeText(this, "Não existe gasto cadastrado !!!", Toast.LENGTH_SHORT).show();
            finish();
        }

        for(int i = 0; i < this.main.novo_gasto.size(); i++){
            if(this.main.novo_gasto.get(i).getId_gasto_viagem() == this.main.nova_viagem.get(main.viagemSelecionada).getId_viagem()){
                item = new HashMap<String, Object>();
                item.put("data", this.main.novo_gasto.get(i).getData());
                item.put("descricao", this.main.novo_gasto.get(i).getDescricao());
                item.put("valor", "R$: " + this.main.novo_gasto.get(i).getValor());

                if(this.main.novo_gasto.get(i).getCategoria().equals("Alimentação")){
                    item.put("categoria", R.color.categoria_alimentacao);
                }
                else if(this.main.novo_gasto.get(i).getCategoria().equals("Combustível") || this.main.novo_gasto.get(i).getCategoria().equals("Outros")){
                    item.put("categoria", R.color.categoria_outros);
                }
                else if(this.main.novo_gasto.get(i).getCategoria().equals("Transporte")){
                    item.put("categoria", R.color.categoria_transporte);
                }
                else if(this.main.novo_gasto.get(i).getCategoria().equals("Hospedagem")){
                    item.put("categoria", R.color.categoria_hospedagem);
                }
                temp = true;
                gastos.add(item);
            }
        }
        if(!temp){
            Toast.makeText(this, "Não existe gasto cadastrado para essa viagem !!!", Toast.LENGTH_SHORT).show();
            finish();
        }
        return gastos;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Map<String, Object> map = gastos.get(i);
        String descricao = (String) map.get("descricao");
        String mensagem = "Selecionou o gasto " + descricao;
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
    }

    private class GastoViewBinder implements SimpleAdapter.ViewBinder {
        @Override
        public boolean setViewValue(View view, Object data, String text) {
            if(view.getId() == R.id.data){
                if(!data_anterior.equals(data)){
                    TextView text_view = (TextView) view;
                    text_view.setText(text);
                    data_anterior = text;
                    view.setVisibility(View.VISIBLE);
                } else {
                    view.setVisibility(View.GONE);
                }
                return true;
            }
            if(view.getId() == R.id.categoria){
                Integer id = (Integer) data;
                view.setBackgroundColor(getResources().getColor(id));
                return true;
            }
            return false;
        }
    }
}
