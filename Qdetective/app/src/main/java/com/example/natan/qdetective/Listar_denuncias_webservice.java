package com.example.natan.qdetective;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.File;
import java.util.List;
import java.util.Map;

import DAO.DatabaseHelper;
import DAO.DenunciaDAO;
import DAO.WebServiceUtils;

/**
 * Created by natan on 12/12/2017.
 */

public class Listar_denuncias_webservice extends Activity implements AdapterView.OnItemClickListener, SimpleAdapter.ViewBinder{
    private final String url = "http://35.193.98.124/QDetective/rest/";
    private List<Map<String, Object>> mapList;
    private boolean permisaoInternet = false;
    private SimpleAdapter adapter;
    private ListView listView;
    private ImageView fotoDenuncia;
    private ProgressDialog load;
    private DenunciaDAO denunciaDAO;
    private WebServiceUtils web;

    @Override
    protected void onResume() {
        super.onResume();
        carregarDados();
    }

    @Override
    protected void onDestroy() {
        denunciaDAO.close();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.denunciaDAO = new DenunciaDAO(this);
        web = new WebServiceUtils();
        setContentView(R.layout.listar_minhas_denuncias);
        iniciarDownload();
        carregarDados();
    }

    public void carregarDados() {
        mapList = denunciaDAO.listarDenuncias();
        String[] chave = {
                DatabaseHelper.Denuncia.DESCRICAO,
                DatabaseHelper.Denuncia.DATA,
                DatabaseHelper.Denuncia.USUARIO,
                DatabaseHelper.Denuncia.CATEGORIA
        };
        int[] valor = {R.id.descricao_denuncia, R.id.data_denuncia,
                R.id.usuario, R.id.categoria_denuncia};
        adapter = new SimpleAdapter(this, mapList, R.layout.layout_item_denuncia, chave, valor);
        listView = findViewById(R.id.listaContatos);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    if (isOnline()) {
                        permisaoInternet = true;
                        return;
                    } else {
                        permisaoInternet = false;
                        Toast.makeText(this, "Sem conexão de Internet.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    permisaoInternet = false;
                    Toast.makeText(this, "Sem permissão para uso de Internet.", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private void getPermissaoDaInternet() {
        boolean internet = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED;
        boolean redeStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED;
        if (internet && redeStatus) {
            if (isOnline()) {
                permisaoInternet = true;
                return;
            } else {
                permisaoInternet = false;
                Toast.makeText(this, "Sem conexão de Internet.", Toast.LENGTH_LONG).show();
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.INTERNET,
                            Manifest.permission.ACCESS_NETWORK_STATE},
                    1);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public boolean setViewValue(View view, Object o, String s) {
        return false;
    }

    private void iniciarDownload() {
        getPermissaoDaInternet();
        if (permisaoInternet) {
            DownloadDenuncias downloadDenuncias = new DownloadDenuncias();
            downloadDenuncias.execute();
        }
    }

    private File getDiretorioDeSalvamento(String nomeArquivo) {
        File diretorio;
        if (nomeArquivo.contains("/")) {
            int beginIndex = nomeArquivo.lastIndexOf("/") + 1;
            nomeArquivo = nomeArquivo.substring(beginIndex);
        }
        if(nomeArquivo.contains(".jpg")){
            diretorio = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        } else{
            diretorio = this.getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        }
        File pathDaImagem = new File(diretorio, nomeArquivo);
        return pathDaImagem;
    }

    private class DownloadDenuncias extends AsyncTask<Long, Void, WebServiceUtils> {
        @Override
        protected void onPreExecute() {
            load = ProgressDialog.show(Listar_denuncias_webservice.this, "Por favor Aguarde ...", "Recuperando Informações do Servidor...");
        }
        @Override
        protected WebServiceUtils doInBackground(Long... ids) {
            WebServiceUtils webService = new WebServiceUtils();
            String id = (ids != null && ids.length == 1) ? ids[0].toString() : "";
            List<Denuncia> denuncias = webService.getListaDenunciasJson(url, "denuncias", id);
            for (Denuncia denuncia : denuncias) {
                String path = getDiretorioDeSalvamento(denuncia.getUriMidia()).getPath();
                webService.downloadImagemBase64(url + "arquivos", path, denuncia.getId());
                denuncia.setUriMidia(path);
            }
            return webService;
        }

        @Override
        protected void onPostExecute(WebServiceUtils webService) {
            for (Denuncia denuncia : webService.getDenuncias()) {
                Denuncia d = denunciaDAO.buscarDenunciaPorId(denuncia.getId());
                if (d != null) {
                    denunciaDAO.atualizarDenuncia(denuncia);
                } else {
                    denunciaDAO.inserirDenuncia(denuncia);
                }
            }
            load.dismiss();
            Toast.makeText(getApplicationContext(), webService.getRespostaServidor(), Toast.LENGTH_LONG).show();
            carregarDados();
        }
    }
}
