package quixada.ufc.br.boaviagem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class Dashboard extends MainViagem {

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
                startActivity(new Intent(this, Configuracoes.class));
                break;
            case R.id.minhas_viagens:
                startActivity(new Intent(this, ListaViagens.class));
                break;
            case R.id.novo_gasto:
                if(nova_viagem.isEmpty())
                    Toast.makeText(this, "Não existe viagem cadastrada !!", Toast.LENGTH_SHORT).show();
                else
                    startActivity(new Intent(this, Novo_gasto.class));
                break;
        }
    }
}
