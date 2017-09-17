package quixada.ufc.br.boaviagem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class Dashboard extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
    }

    public void selecionarOpcao(View view) {
        switch (view.getId()) {
            case R.id.nova_viagem:
                startActivity(new Intent(this, Nova_viagem.class));
                break;
            case R.id.configuracoes:
                startActivity(new Intent(this, ConfiguracoesActivity.class));
                break;
        }
    }
}
