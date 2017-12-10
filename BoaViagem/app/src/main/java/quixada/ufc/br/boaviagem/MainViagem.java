package quixada.ufc.br.boaviagem;

import android.app.Activity;

import java.util.ArrayList;


public class MainViagem extends Activity {
    public static ArrayList<Viagem> nova_viagem = new ArrayList<>();
    public static ArrayList<Gasto> novo_gasto = new ArrayList<>();
    public static int viagemSelecionada;
    public static boolean control = false;
}
