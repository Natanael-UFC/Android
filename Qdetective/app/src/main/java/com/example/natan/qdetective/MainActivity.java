package com.example.natan.qdetective;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements AlertDialogOpcaoMidia.NotificarEscutadorDoDialog {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void selecionarOpcao(View view){
        switch(view.getId()){
            case R.id.denunciar:{
                AlertDialogOpcaoMidia fragmento = new AlertDialogOpcaoMidia();
                fragmento.show(this.getFragmentManager(), "confirma");
                break;
            }
            case R.id.listar_denuncias:{
                Intent intent = new Intent(MainActivity.this, Listar_minhas_denuncias.class);
                startActivity(intent);
                break;
            }
            case R.id.servidor:{
                Intent intent = new Intent(MainActivity.this, Listar_denuncias_webservice.class);
                startActivity(intent);
                break;
            }
            case R.id.sair:{
                finish();
                break;
            }
        }
    }

    @Override
    public void onDialogFotoClick() {
        Intent intent = new Intent(MainActivity.this, Nova_denuncia_foto.class);
        startActivity(intent);
    }

    @Override
    public void onDialogVideoClick() {
        Intent intent = new Intent(MainActivity.this, Nova_denuncia_video.class);
        startActivity(intent);
    }
}
