package com.example.natan.qdetective;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by natan on 09/12/2017.
 */

public class AlertDialogOpcaoMidia extends DialogFragment {
    private NotificarEscutadorDoDialog escutador;

    public interface NotificarEscutadorDoDialog {
        public void onDialogFotoClick();
        public void onDialogVideoClick();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final CharSequence[] items = {"Registrar Foto", "Registrar Video"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Opções de registro").setItems(items, itemClick);
        escutador = (NotificarEscutadorDoDialog) getActivity();
        return builder.create();
    }

    DialogInterface.OnClickListener itemClick = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int item) {
//            int pos = AlertDialogOpcaoMidia.this.getArguments().getInt("pos");
            switch (item) {
                case 0:
                    escutador.onDialogFotoClick();
                    break;
                case 1:
                    escutador.onDialogVideoClick();
                    break;
            }
        }
    };
}
