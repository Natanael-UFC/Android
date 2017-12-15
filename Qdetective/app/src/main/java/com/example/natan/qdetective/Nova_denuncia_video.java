package com.example.natan.qdetective;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.Date;

import DAO.DenunciaDAO;
import DAO.WebServiceUtils;

/**
 * Created by natan on 11/12/2017.
 */

public class Nova_denuncia_video extends Activity{
    private static final int CAPTURAR_VIDEO = 1;
    private Spinner categoria;
    private EditText nomeUsuarioEditText, descricaoEditText;
    private Button registrarButton, visualizarButton;
    private DenunciaDAO denunciaDAO;
    private Denuncia denuncia;
    private Date data;
    private Calendar cal;
    private Bundle bundle = null;
    private int id;
    private VideoView videoDenuncia;
    private Uri uri;
    private LocationManager locationManager;
    private String latitude, longitude;
    private Boolean possuiCartaoSD = false;
    private Boolean dispositivoSuportaCartaoSD = false;

    @Override
    protected void onResume() {
        super.onResume();
        bundle = this.getIntent().getExtras();
        if (bundle != null) {
            id = bundle.getInt("id", 0);
            if (id > 0) {
                Button button = findViewById(R.id.salvar_denuncia_video);
                button.setText("Atualizar");
                carregarDados(id);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nova_denuncia_video);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        this.data = new Date();
        this.denunciaDAO = new DenunciaDAO(this);
        this.denuncia = new Denuncia();
        videoDenuncia = findViewById(R.id.videoDenuncia);
        descricaoEditText = findViewById(R.id.descricao_video);
        nomeUsuarioEditText = findViewById(R.id.nome_usuario_video);
        categoria = findViewById(R.id.categoria_video);
        visualizarButton = findViewById(R.id.visualizar_video);
        registrarButton = findViewById(R.id.registrar_video);
        possuiCartaoSD = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        dispositivoSuportaCartaoSD = Environment.isExternalStorageRemovable();
        getLocationManager();
    }

    public void salvarDenunciaVideo(View view){
        if (TextUtils.isEmpty(nomeUsuarioEditText.getText().toString().trim())) {
            nomeUsuarioEditText.setError("Campo obrigatório.");
            return;
        }
        if (TextUtils.isEmpty(descricaoEditText.getText().toString().trim())) {
            descricaoEditText.setError("Campo obrigatório.");
            return;
        }
        String categoria = this.categoria.getSelectedItem().toString();
        String nome = nomeUsuarioEditText.getText().toString();
        String descricao = descricaoEditText.getText().toString();
        String video = null;
        if (uri != null)
            video = getDiretorioDeSalvamento(uri.toString()).toString();
       if(id > 0){
           Log.d("URI: ", video);
           Denuncia denuncia1 = new Denuncia(id, descricao, data, Double.parseDouble(longitude), Double.parseDouble(latitude), video, nome, categoria);
           if(denunciaDAO.atualizarDenuncia(denuncia1)> 0){
               Toast.makeText(this, "Denuncia Atualizada com sucesso.", Toast.LENGTH_LONG).show();
           }
       } else {
           Denuncia denuncia1 = new Denuncia(descricao, data, Double.parseDouble(longitude), Double.parseDouble(latitude), video, nome, categoria);
           if (denunciaDAO.inserirDenuncia(denuncia1) > 0) {
               Toast.makeText(this, "Denuncia Cadastrada com sucesso.", Toast.LENGTH_LONG).show();
           }
       }
        finish();
    }

    public void cancelarDenunciaVideo(View view){
        finish();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURAR_VIDEO) {
            if (resultCode == RESULT_OK) {
                String msg = "Vídeo gravado em " + data.getDataString();
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Video não gravado!", Toast.LENGTH_LONG).show();
            }
        }
    }


    public void visualizarVideo(View v) {
        videoDenuncia.setVideoURI(uri);
        MediaController mc = new MediaController(this);
        videoDenuncia.setMediaController(mc);
        videoDenuncia.start();
    }

    public void registrarVideo(View v) {
        getPermissoes();
    }

    private void getPermissoes() {
        String CAMERA = Manifest.permission.CAMERA;
        String WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        String READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
        int PERMISSION_GRANTED = PackageManager.PERMISSION_GRANTED;

        boolean permissaoCamera = ActivityCompat.checkSelfPermission(this, CAMERA) == PERMISSION_GRANTED;
        boolean permissaoEscrita = ActivityCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) == PERMISSION_GRANTED;
        boolean permissaoLeitura = ActivityCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED;

        if (permissaoCamera && permissaoEscrita && permissaoLeitura) {
            iniciarGravacaoDeVideo();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{CAMERA, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, 1);
        }
    }
    private void iniciarGravacaoDeVideo() {
        try {
            setArquivoVideo();
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
            startActivityForResult(intent, CAPTURAR_VIDEO);
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao iniciar a câmera.", Toast.LENGTH_LONG).show();
        }
    }

    private void setArquivoVideo() {
        File diretorio = this.getExternalFilesDir(Environment.DIRECTORY_MOVIES);

        File pathVideo = new File(diretorio + "/" + System.currentTimeMillis() + ".mp4");

        if (android.os.Build.VERSION.SDK_INT >= 23) {
            String authority = this.getApplicationContext().getPackageName() + ".fileprovider";
            uri = FileProvider.getUriForFile(this, authority, pathVideo);
        } else {
            uri = Uri.fromFile(pathVideo);
        }
    }

    private File getDiretorioDeSalvamento(String nomeArquivo) {
        if (nomeArquivo.contains("/")) {
            int beginIndex = nomeArquivo.lastIndexOf("/") + 1;
            nomeArquivo = nomeArquivo.substring(beginIndex);
        }
        File diretorio = this.getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        File pathDaImagem = new File(diretorio, nomeArquivo);
        return pathDaImagem;
    }

    private void getLocationManager() {
        Listener listener = new Listener();
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        long tempoAtualizacao = 0;
        float distancia = 0;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.INTERNET},
                    1);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, tempoAtualizacao, distancia, listener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, tempoAtualizacao, distancia, listener);
    }

    private class Listener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            latitude = String.valueOf(location.getLatitude());
            longitude = String.valueOf(location.getLongitude());
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        @Override
        public void onProviderEnabled(String provider) {}
        @Override
        public void onProviderDisabled(String provider) {}
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[2] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[3] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[4] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[5] == PackageManager.PERMISSION_GRANTED) {
                    iniciarGravacaoDeVideo();
                } else {
                    Toast.makeText(this, "Sem permissão para uso de câmera.", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    public void carregarDados(int id) {
        denuncia = denunciaDAO.buscarDenunciaPorId(id);
        String video = denuncia.getUriMidia();
        String descricao = denuncia.getDescricao();
        String usuario = denuncia.getUsuario();

        descricaoEditText.setText(descricao);
        nomeUsuarioEditText.setText(usuario);
        if (video != null) {
                videoDenuncia.setVideoURI(uri);
                MediaController mc = new MediaController(this);
                videoDenuncia.setMediaController(mc);
                videoDenuncia.start();
        }
    }
}
