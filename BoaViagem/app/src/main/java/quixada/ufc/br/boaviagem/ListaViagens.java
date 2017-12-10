package quixada.ufc.br.boaviagem;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListaViagens extends ListActivity implements AdapterView.OnItemClickListener {
    private MainViagem main = new MainViagem();
    private List<Map<String, Object>> viagens;
    private AlertDialog alertDialog;
    private AlertDialog dialog_confirmacao;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        String[] identificadores = {"imagem", "destino", "data", "total"};
        int[] ids_retornadas = {R.id.tipoViagem, R.id.destino, R.id.data, R.id.valor};
        SimpleAdapter adapter = new SimpleAdapter(this, listarViagens(), R.layout.lista_viagens, identificadores, ids_retornadas);
        this.alertDialog = cria_alert_dialog();
        this.dialog_confirmacao = cria_dialog_confirmacao();
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);
    }

    private List<Map<String, Object>> listarViagens() {
        viagens = new ArrayList<Map<String,Object>>();
        Map<String, Object> item = new HashMap<String, Object>();

        if(main.nova_viagem.isEmpty()){
            Toast.makeText(this, "Não existe viagem cadastrada !!!", Toast.LENGTH_SHORT).show();
            finish();
        }

        for(int i = 0; i < main.nova_viagem.size(); i++){
            if(main.nova_viagem.get(i).getTipo_viagem().equals("Lazer")){
                item = new HashMap<String, Object>();
                item.put("imagem", R.drawable.lazer);
                item.put("destino", main.nova_viagem.get(i).getDestino());
                item.put("data", main.nova_viagem.get(i).getData_saida() + " a " +main.nova_viagem.get(i).getData_chegada());
                item.put("total", "Gasto total de R$ " + main.nova_viagem.get(i).getOrcamento());
                viagens.add(item);
            }
            else if(main.nova_viagem.get(i).getTipo_viagem().equals("Negócios")){
                item = new HashMap<String, Object>();
                item.put("imagem", R.drawable.negocios);
                item.put("destino", main.nova_viagem.get(i).getDestino());
                item.put("data", main.nova_viagem.get(i).getData_saida() + " a " +main.nova_viagem.get(i).getData_chegada());
                item.put("total", "Gasto total de R$ " + main.nova_viagem.get(i).getOrcamento());
                viagens.add(item);
            }
        }
        return viagens;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        main.viagemSelecionada = i;
        this.alertDialog.show();
    }

    private AlertDialog cria_alert_dialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.opcoes)
                .setItems(R.array.itens, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int i) {
                        switch (i) {
                            case 0:
                                Intent intent = new Intent(ListaViagens.this, Nova_viagem.class);
                                intent.putExtra("pos", main.viagemSelecionada);
                                main.control = true;
                                startActivity(intent);
                                break;
                            case 1:
                                startActivity(new Intent(ListaViagens.this, Novo_gasto.class));
                                break;
                            case 2:
                                startActivity(new Intent(ListaViagens.this, ListaGastos.class));
                                break;
                            case 3:
                                dialog_confirmacao.show();
                                break;
                        }
                    }
                });
        return builder.create();
    }

    private AlertDialog cria_dialog_confirmacao(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirmacao_excluir_viagem)
                .setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        boolean temp = false;
                        for(int i = 0; i < main.novo_gasto.size(); i++){
                            if(main.novo_gasto.get(i).getLocal().equals(main.nova_viagem.get(main.viagemSelecionada).getDestino())) {
                                viagens.remove(main.viagemSelecionada);
                                main.nova_viagem.remove(main.viagemSelecionada);
                                main.novo_gasto.remove(i);
                                temp = true;
                            }
                        }
                        if(!temp){
                            viagens.remove(main.viagemSelecionada);
                            main.nova_viagem.remove(main.viagemSelecionada);
                        }

                        getListView().invalidateViews();
                        if(main.nova_viagem.isEmpty())
                            finish();
                    }
                })
                .setNegativeButton(R.string.nao, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                       dialog_confirmacao.dismiss();
                    }
                });
        return builder.create();
    }
}
