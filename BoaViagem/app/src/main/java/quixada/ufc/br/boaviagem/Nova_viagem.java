package quixada.ufc.br.boaviagem;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import static android.icu.util.Calendar.getInstance;

public class Nova_viagem extends MainViagem {
    private int ano, mes, dia, pos;
    private Button data_chegada, data_saida;
    private RadioButton tp_viagem_lazer, tp_viagem_negocio;
    private EditText destino, orcamento, qtd_pessoas;
    private Button dt_chegada, dt_saida, button_salvar;
    private String temp_destino;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nova_viagem);

        if(control){
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            this.pos = bundle.getInt("pos");
            edita_viagem(pos);
        }

        Calendar calendario = getInstance();
        this.ano = calendario.get(Calendar.YEAR);
        this.mes = calendario.get(Calendar.MONTH);
        this.dia = calendario.get(Calendar.DAY_OF_MONTH);

        this.data_chegada = (Button) findViewById(R.id.data_chegada);
        this.data_saida = (Button) findViewById(R.id.data_saida);

        this.data_chegada.setText(dia + "/" + (mes+1) + "/" + ano);
        this.data_saida.setText(dia + "/" + (mes+1) + "/" + ano);
    }

    public void selecionar_data(View view){
        showDialog(view.getId());
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id){
            case R.id.data_chegada:
                return new DatePickerDialog(this, listener_chegada, ano, mes, dia);
            case R.id.data_saida:
                return new DatePickerDialog(this, listener_saida, ano, mes, dia);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener listener_saida = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int ano_sel, int mes_sel, int dia_sel) {
            ano = ano_sel;
            mes = mes_sel;
            dia = dia_sel;
            data_saida.setText(dia + "/" + (mes+1) + "/" + ano);
        }
    };

    private DatePickerDialog.OnDateSetListener listener_chegada = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int ano_sel, int mes_sel, int dia_sel) {
            ano = ano_sel;
            mes = mes_sel;
            dia = dia_sel;
            data_chegada.setText(dia + "/" + (mes+1) + "/" + ano);
        }
    };

    public void nova_viagem(View view) {
        Viagem viagem = new Viagem();

        destino = (EditText) findViewById(R.id.destino);
        orcamento = (EditText) findViewById(R.id.orcamento_viagem);
        qtd_pessoas = (EditText) findViewById(R.id.qtd_pessoas);
        dt_chegada = (Button) findViewById(R.id.data_chegada);
        dt_saida = (Button) findViewById(R.id.data_saida);

        tp_viagem_lazer = (RadioButton) findViewById(R.id.lazer);
        tp_viagem_negocio = (RadioButton) findViewById(R.id.negocios);

        if(verf_campos(destino, orcamento, qtd_pessoas, data_chegada, data_saida)) {

            if (tp_viagem_lazer.isChecked()) {
                viagem.setTipo_viagem(tp_viagem_lazer.getText().toString());
            } else if (tp_viagem_negocio.isChecked()) {
                viagem.setTipo_viagem(tp_viagem_negocio.getText().toString());
            }

            viagem.setDestino(destino.getText().toString());
            viagem.setData_chegada(dt_chegada.getText().toString());
            viagem.setData_saida(dt_saida.getText().toString());
            viagem.setOrcamento(orcamento.getText().toString());
            viagem.setQtd_pessoas(qtd_pessoas.getText().toString());
            viagem.setId_viagem(nova_viagem.size() + 1);

            if (control) {
                alter_dest_gasto(destino.getText().toString());
                nova_viagem.set(pos, viagem);
            } else
                nova_viagem.add(viagem);

            Toast.makeText(this, "Viagem salva com sucesso !!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, Dashboard.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Você deixou algum campo em branco !!!", Toast.LENGTH_SHORT).show();
        }
    }

    private void edita_viagem(int pos){
        destino = (EditText) findViewById(R.id.destino);
        orcamento = (EditText) findViewById(R.id.orcamento_viagem);
        qtd_pessoas = (EditText) findViewById(R.id.qtd_pessoas);
        dt_chegada = (Button) findViewById(R.id.data_chegada);
        dt_saida = (Button) findViewById(R.id.data_saida);
        button_salvar = (Button) findViewById(R.id.salvar_viagem);

        tp_viagem_lazer = (RadioButton) findViewById(R.id.lazer);
        tp_viagem_negocio = (RadioButton) findViewById(R.id.negocios);

        button_salvar.setText("Salvar");
        destino.setText(nova_viagem.get(pos).getDestino());
        orcamento.setText(nova_viagem.get(pos).getOrcamento());
        qtd_pessoas.setText(nova_viagem.get(pos).getQtd_pessoas());
        dt_chegada.setText(nova_viagem.get(pos).getData_chegada());
        dt_saida.setText(nova_viagem.get(pos).getData_saida());
        temp_destino = nova_viagem.get(pos).getDestino().toString();
    }

    private void alter_dest_gasto(String destino){
        for(int i = 0; i < novo_gasto.size(); i++){
            if(novo_gasto.get(i).getLocal().equals(temp_destino)){
                novo_gasto.get(i).setLocal(destino);
            }
        }
    }

    private boolean verf_campos(EditText destino, EditText orcamento, EditText qtd_pessoas, Button data_chegada, Button data_saida){
        if(destino.getText().toString().equals("") || orcamento.getText().toString().equals("") ||
                qtd_pessoas.getText().toString().equals("") || data_chegada.getText().toString().equals("Selecione") ||
                data_saida.getText().toString().equals("Selecione")){
            return false;
        }
        return true;
    }

}
