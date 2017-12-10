package quixada.ufc.br.boaviagem;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Novo_gasto extends MainViagem implements DatePickerFragment.NotificarEscutadorDoDialog{
    private Spinner categoria, local_viagem;
    private EditText valor;
    private Date data;
    private Button buttonSelecionaData;
    private Gasto gasto;
    private List<String> itens = new ArrayList<String>();
    private MainViagem main = new MainViagem();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.novo_gasto);

        for(int i = 0; i < main.nova_viagem.size(); i++){
            itens.add(main.nova_viagem.get(i).getDestino());
        }

        local_viagem = (Spinner) findViewById(R.id.local);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, itens);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        local_viagem.setAdapter(dataAdapter);

        gasto = new Gasto();
        ArrayAdapter<CharSequence> adapter = null;
        categoria = (Spinner) findViewById(R.id.categoria);
        adapter = ArrayAdapter.createFromResource(this, R.array.categoria_gasto,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoria.setAdapter(adapter);
        valor = findViewById(R.id.ed_valor);
        buttonSelecionaData = findViewById(R.id.bt_data);
    }

    public void escolherData(View view) {
        DatePickerFragment meuDatePicker = new DatePickerFragment();
        meuDatePicker.show(getFragmentManager(), "Selecione a data");
    }

    @Override
    public void onDateSelectedClick(DialogFragment dialog, int ano, int mes, int dia) {
        String data = dia + "/" + mes + "/" + ano;
        this.buttonSelecionaData.setText(data);
    }

    public void novo_gasto(View view){
        EditText valor, descricao;
        Button data;
        Spinner categoria, local;

        categoria = (Spinner) findViewById(R.id.categoria);
        data = (Button) findViewById(R.id.bt_data);
        valor = (EditText) findViewById(R.id.ed_valor);
        descricao = (EditText) findViewById(R.id.ed_descricao);
        local = (Spinner) findViewById(R.id.local);

        if(verifica_campos(categoria, local, data, valor, descricao)){
                this.gasto.setId_gasto_viagem(local.getSelectedItemPosition() + 1);
                this.gasto.setCategoria(categoria.getSelectedItem().toString());
                this.gasto.setData(data.getText().toString());
                this.gasto.setValor(Double.parseDouble(valor.getText().toString()));
                this.gasto.setDescricao(descricao.getText().toString());
                this.gasto.setLocal(local.getSelectedItem().toString());
                novo_gasto.add(this.gasto);
                Toast.makeText(this, "Gasto cadastrado com sucesso !", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(new Intent(Novo_gasto.this, Dashboard.class));
        } else {
            Toast.makeText(this, "Você deixou um campo em branco !!!", Toast.LENGTH_SHORT).show();
        }
    }
    private boolean verifica_viagem_gasto(String local){
        for(int i = 0; i < nova_viagem.size(); i++){
            if(nova_viagem.get(i).getDestino().equals(local)){
                return true;
            }
        }
        return false;
    }

    private boolean verifica_campos(Spinner categoria, Spinner local, Button data, EditText valor, EditText descricao){
        if(categoria.getSelectedItem().toString().equals("") || local.getSelectedItem().toString().equals("") ||
                data.getText().toString().equals("Data") || valor.getText().toString().equals("") ||
                descricao.getText().toString().equals("")){
            return false;
        }
        return true;
    }
}
