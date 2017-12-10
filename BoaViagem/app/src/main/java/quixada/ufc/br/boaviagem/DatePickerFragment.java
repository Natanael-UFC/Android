package quixada.ufc.br.boaviagem;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.widget.DatePicker;


public class DatePickerFragment extends DialogFragment {
    private NotificarEscutadorDoDialog escutador;

    public interface NotificarEscutadorDoDialog {
        public void onDateSelectedClick(DialogFragment dialog, int ano, int mes, int dia);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int ano = c.get(Calendar.YEAR);
        int mes = c.get(Calendar.MONTH);
        int dia = c.get(Calendar.DAY_OF_MONTH);
        Activity activity = getActivity();
        DatePickerDialog datePickerDialog = new DatePickerDialog(activity, confirmaData, ano, mes, dia);
        escutador = (NotificarEscutadorDoDialog) activity;
        return datePickerDialog;
    }

    DatePickerDialog.OnDateSetListener confirmaData = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int ano, int mes, int dia) {
            escutador.onDateSelectedClick(DatePickerFragment.this, ano, mes + 1, dia);
        }
    };
}
