package com.example.natan.qdetective;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;

import DAO.DenunciaDAO;

/**
 * Created by natan on 11/12/2017.
 */

public class DetalhesDenuncia extends Activity {
    private LocationManager locationManager;
    private TextView descricaoTextView, dataTextView, usuarioTextView, categoriaTextView;
    private ImageView imagem_denunciaView;
    private VideoView video_denunciaView;
    private Button voltar_tela_principalButton;
    private String urlBase = "http://maps.googleapis.com/maps/api/staticmap" +
            "?size=400x400&sensor=true&markers=color:red|%s,%s&key=AIzaSyCNgjtgzjX7cO-Qy26hSpjdgjuIBGjqE8M";
    private WebView mWebView;
    private Bundle bundle = null;
    private int id;
    private DenunciaDAO denunciaDAO;
    private String latitude, longitude;
    private Uri uri;
    SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalhes_denuncia);
        this.denunciaDAO = new DenunciaDAO(this);

        this.descricaoTextView = findViewById(R.id.descricao_denuncia_detalhes);
        this.dataTextView = findViewById(R.id.data_denuncia_detalhes);
        this.usuarioTextView = findViewById(R.id.usuario_denuncia_detalhes);
        this.categoriaTextView = findViewById(R.id.categoria_denuncia_detalhes);
        this.imagem_denunciaView = findViewById(R.id.imagem_denuncia_detalhes);
        this.video_denunciaView = findViewById(R.id.video_denuncia_detalhes);
        this.voltar_tela_principalButton = findViewById(R.id.voltar);

        mWebView = findViewById(R.id.mapa_denuncia_detalhes);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(false);

        this.bundle = this.getIntent().getExtras();
        if(bundle != null){
            this.id = bundle.getInt("id", 0);
            getDados(id);
        }
        getLocationManager();
    }

    private void getDados(int id){
        Denuncia denuncia = denunciaDAO.buscarDenunciaPorId(id);
        if(denuncia.getUriMidia().contains(".jpg")){
            try {
                video_denunciaView.setVisibility(View.INVISIBLE);
                imagem_denunciaView.setVisibility(View.VISIBLE);
                uri = Uri.fromFile(getDiretorioDeSalvamento(denuncia.getUriMidia(), 0));
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                int bmpWidth = bitmap.getWidth();
                int bmpHeight = bitmap.getHeight();
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bmpWidth, bmpHeight, matrix, true);
                imagem_denunciaView.setImageBitmap(resizedBitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            video_denunciaView.setVisibility(View.VISIBLE);
            imagem_denunciaView.setVisibility(View.INVISIBLE);
            uri = Uri.fromFile(getDiretorioDeSalvamento(denuncia.getUriMidia(), 1));
            video_denunciaView.setVideoURI(uri);
            MediaController mc = new MediaController(this);
            video_denunciaView.setMediaController(mc);
            video_denunciaView.start();
        }
        this.descricaoTextView.setText(denuncia.getDescricao());
        this.dataTextView.setText(formatoData.format(denuncia.getData()));
        this.usuarioTextView.setText(denuncia.getUsuario());
        this.categoriaTextView.setText(denuncia.getCategoria());
        this.latitude = String.valueOf(denuncia.getLatitude());
        this.longitude = String.valueOf(denuncia.getLongitude());
    }

    public void retornarTelaPrincipal(View view){
        finish();
    }

    private File getDiretorioDeSalvamento(String nomeArquivo, int flag) {
        File diretorio = null;
        if (nomeArquivo.contains("/")) {
            int beginIndex = nomeArquivo.lastIndexOf("/") + 1;
            nomeArquivo = nomeArquivo.substring(beginIndex);
        }
        if(flag == 0){
            diretorio = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        }
        else if(flag == 1){
            diretorio = this.getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        }
        File pathDaImagem = new File(diretorio, nomeArquivo);
        return pathDaImagem;
    }

    private void getLocationManager() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.INTERNET},
                    1);
            return;
        }

        Listener listener = new Listener();
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        long tempoAtualizacao = 3600;
        float distancia = 0;
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, tempoAtualizacao, distancia, listener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, tempoAtualizacao, distancia, listener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    getLocationManager();
                } else {
                    Toast.makeText(this, "Sem permissão para uso de gps ou rede.", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private class Listener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
         //   String latitudeStr = String.valueOf(location.getLatitude());
         //   String longitudeStr = String.valueOf(location.getLongitude());
         //   Log.d("lat: ", latitude);
         //   Log.d("longi: ", longitude);
            String url = String.format(urlBase, latitude, longitude);
            mWebView.loadUrl(url);
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {    }
        @Override
        public void onProviderEnabled(String provider) {  }
        @Override
        public void onProviderDisabled(String provider) { }
    }
}
