package quixada.ufc.br.boaviagem;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import static android.icu.util.Calendar.getInstance;

public class Nova_viagem extends Activity {
    private int ano, mes, dia;
    private Button data_chegada, data_saida;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nova_viagem);
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

}
