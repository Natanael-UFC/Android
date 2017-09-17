package quixada.ufc.br.boaviagem;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class BoaViagem extends AppCompatActivity {
    private EditText usuario;
    private EditText senha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        this.usuario = (EditText) findViewById(R.id.usuario);
        this.senha = (EditText) findViewById(R.id.senha);
    }

    public void entrarOnClick(View v){
        String usuarioInformado = this.usuario.getText().toString();
        String senhaInformada = this.senha.getText().toString();

        if(usuarioInformado.equals("leitor") && senhaInformada.equals("123")){
            Intent intent = new Intent(BoaViagem.this, Dashboard.class);
            startActivity(intent);

        } else{
            String mensagemError = getString(R.string.erro_autenticacao);
            Toast toast = Toast.makeText(this, mensagemError, Toast.LENGTH_SHORT);
            toast.show();
        }

    }
}
