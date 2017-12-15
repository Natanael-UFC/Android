package com.example.natan.qdetective;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
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
 * Created by natan on 10/12/2017.
 */

public class Listar_minhas_denuncias extends Activity implements AdapterView.OnItemClickListener, SimpleAdapter.ViewBinder,  MenuDialogFragment.NotificarEscutadorDoDialog {


    private final String url = "http://35.193.98.124/QDetective/rest/";
    //private final String url = "http://192.168.1.13:8080/QDetective/rest/";
    private List<Map<String, Object>> mapList;
    private boolean permisaoInternet = false;
    private DenunciaDAO denunciaDAO;
    private SimpleAdapter adapter;
    private ListView listView;
    private ImageView fotoDenuncia;
    private ProgressDialog load;
    private Integer id_item = null;

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

        denunciaDAO = new DenunciaDAO(this);
        setContentView(R.layout.listar_minhas_denuncias);
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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        MenuDialogFragment fragmento = new MenuDialogFragment();

        Map<String, Object> item = mapList.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("id", (int) item.get(DatabaseHelper.Denuncia._ID));
        fragmento.setArguments(bundle);
        fragmento.show(this.getFragmentManager(), "confirma");
    }

    @Override
    public boolean setViewValue(View view, Object data, String s) {
        return false;
    }

    @Override
    public void onDialogExcluiClick(int id) {
        Denuncia denuncia = denunciaDAO.buscarDenunciaPorId(id);
        String path = denuncia.getUriMidia();
        if (denunciaDAO.removerDenuncia(id)) {
            File file = new File(path);
            file.delete();
            carregarDados();
        }
    }

    @Override
    public void onDialogEditarClick(int id) {
        Denuncia denuncia = denunciaDAO.buscarDenunciaPorId(id);
        Intent intent = null;
        if(denuncia.getUriMidia().contains(".jpg")){
            intent = new Intent(this, Nova_denuncia_foto.class);
        } else{
            intent = new Intent(Listar_minhas_denuncias.this, Nova_denuncia_video.class);
        }
        intent.putExtra("id", id);
        startActivity(intent);
    }

    @Override
    public void onDialogEnviarParaNuvemClick(int id) {
        getPermissaoDaInternet();
        if (permisaoInternet) {
            Denuncia denuncia = denunciaDAO.buscarDenunciaPorId(id);
            UploadDenuncia uploadJson = new UploadDenuncia();
            uploadJson.execute(denuncia);
            id_item = id;
        }
    }

    @Override
    public void onDialogDetalhesDenunciaClick(int id) {
        Intent intent = new Intent(this, DetalhesDenuncia.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

     class UploadDenuncia extends AsyncTask<Denuncia, Void, WebServiceUtils> {
        @Override
        protected void onPreExecute() {
            load = ProgressDialog.show(Listar_minhas_denuncias.this, "Por favor Aguarde ...", "Recuperando Informações do Servidor...");
        }
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected WebServiceUtils doInBackground(Denuncia... denuncias) {
            WebServiceUtils webService = new WebServiceUtils();
            Denuncia denuncia = denuncias[0];
            String urlDados = url + "denuncias";
            if (webService.sendDenunciaJson(urlDados, denuncia)) {
                urlDados = url + "arquivos/postFotoBase64";
                webService.uploadImagemBase64(urlDados, new File(denuncia.getUriMidia().toString()));
                if(id_item != null){
                     String path = denuncia.getUriMidia();
                     if (denunciaDAO.removerDenuncia(id_item)) {
                            File file = new File(path);
                            file.delete();
                            finish();
                     }
                }
            }
            return webService;
        }
        @Override
        protected void onPostExecute(WebServiceUtils webService) {
            Toast.makeText(getApplicationContext(),
                    webService.getRespostaServidor(),
                    Toast.LENGTH_LONG).show();
            load.dismiss();
        }
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

    private File getDiretorioDeSalvamento(String nomeArquivo) {
        if (nomeArquivo.contains("/")) {
            int beginIndex = nomeArquivo.lastIndexOf("/") + 1;
            nomeArquivo = nomeArquivo.substring(beginIndex);
        }
        File diretorio = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File pathDaImagem = new File(diretorio, nomeArquivo);
        return pathDaImagem;
    }
}
