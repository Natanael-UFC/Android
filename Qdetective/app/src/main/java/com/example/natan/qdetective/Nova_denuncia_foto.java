package com.example.natan.qdetective;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import DAO.DenunciaDAO;

/**
 * Created by natan on 09/12/2017.
 */

public class Nova_denuncia_foto extends Activity {
    private static final int CAPTURAR_IMAGEM = 1;
    private Spinner categoria;
    private EditText nomeUsuarioEditText, descricaoEditText;
    private Button registrarButton;
    private DenunciaDAO denunciaDAO;
    private Denuncia denuncia;
    private Date data;
    private Calendar cal;
    private Bundle bundle = null;
    private int id;
    private ImageView fotoDenuncia;
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
                Button button = findViewById(R.id.salvar_denuncia);
                button.setText("Atualizar");
                carregarDados(id);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nova_denuncia_foto);
        this.data = new Date();
        this.denunciaDAO = new DenunciaDAO(this);
        this.denuncia = new Denuncia();
        this.categoria = findViewById(R.id.categoria);
        this.nomeUsuarioEditText = findViewById(R.id.nome_usuario);
        this.descricaoEditText = findViewById(R.id.descricao);
        this.fotoDenuncia = findViewById(R.id.fotoDenuncia);
        possuiCartaoSD = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        dispositivoSuportaCartaoSD = Environment.isExternalStorageRemovable();
        getLocationManager();
    }
    public void salvarDenuncia(View view) {
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
        String foto = null;

        if (uri != null) {
            foto = getDiretorioDeSalvamento(uri.toString()).toString();
        }

        if(id > 0){
            Denuncia denuncia1 = new Denuncia(id, descricao, data, Double.parseDouble(longitude), Double.parseDouble(latitude), foto, nome, categoria);
            if(denunciaDAO.atualizarDenuncia(denuncia1) > 0){
                Toast.makeText(this, "Denuncia Atualizada com sucesso.", Toast.LENGTH_LONG).show();
            }
        }else {
            Denuncia denuncia1 = new Denuncia(descricao, data, Double.parseDouble(longitude), Double.parseDouble(latitude), foto, nome, categoria);
            if (denunciaDAO.inserirDenuncia(denuncia1) > 0) {
                Toast.makeText(this, "Denuncia Cadastrada com sucesso.", Toast.LENGTH_LONG).show();
            }
        }
        finish();
    }

    public void cancelarDenuncia(View view){
        finish();
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

    private Uri setArquivoImagem(String nomeArquivo) {
        File pathDaImagem = getDiretorioDeSalvamento(nomeArquivo);
        Uri uri = null;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                String authority = "com.example.natan.qdetective.fileprovider";
                uri = FileProvider.getUriForFile(this.getApplicationContext(), authority, pathDaImagem);
            } catch (Exception e) {
                Log.d("exececao", String.valueOf(e));
                Toast.makeText(this, "Erro a acessar o FileProvider.", Toast.LENGTH_LONG).show();
            }
        } else {
            uri = Uri.fromFile(pathDaImagem);
        }
        return uri;
    }

    private void iniciarCapturaDeFotos() {
        try {
            String nomeArquivo = (denuncia.getUriMidia() == null) ? System.currentTimeMillis() + ".jpg" : denuncia.getUriMidia();
            uri = setArquivoImagem(nomeArquivo);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent, CAPTURAR_IMAGEM);
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao iniciar a câmera.", Toast.LENGTH_LONG).show();
        }
    }

    public void registrarFoto(View v) {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            getPermissoes();
        } else {
            iniciarCapturaDeFotos();
        }
    }

    private void getPermissoes() {
        String CAMERA = Manifest.permission.CAMERA;
        String WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        String READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
        String FINE =  Manifest.permission.ACCESS_FINE_LOCATION;
        String COARSE =   Manifest.permission.ACCESS_COARSE_LOCATION;
        String INTERNET =       Manifest.permission.INTERNET;
        int PERMISSION_GRANTED = PackageManager.PERMISSION_GRANTED;

        boolean permissaoCamera = ActivityCompat.checkSelfPermission(this, CAMERA) == PERMISSION_GRANTED;
        boolean permissaoEscrita = ActivityCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) == PERMISSION_GRANTED;
        boolean permissaoLeitura = ActivityCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED;
   //     boolean fine = ActivityCompat.checkSelfPermission(this, FINE) == PERMISSION_GRANTED;
   //     boolean coarse = ActivityCompat.checkSelfPermission(this, COARSE) == PERMISSION_GRANTED;
   //     boolean intenert = ActivityCompat.checkSelfPermission(this, INTERNET) == PERMISSION_GRANTED;

        if (permissaoCamera && permissaoEscrita && permissaoLeitura) {
            iniciarCapturaDeFotos();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{CAMERA, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE, FINE, COARSE, INTERNET}, 1);
        }
    }

    private void getLocationManager() {
        Listener listener = new Listener();
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        long tempoAtualizacao = 60;
        float distancia = 0;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.INTERNET},
                    1);
       //     getLocationManager();
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
                        grantResults[2] == PackageManager.PERMISSION_GRANTED) {

                    /*

                     &&
                        grantResults[3] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[4] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[5] == PackageManager.PERMISSION_GRANTED
                     */

                   iniciarCapturaDeFotos();
                } else {
                    Toast.makeText(this, "Sem permissão para uso de câmera.", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURAR_IMAGEM) {
            if (resultCode == RESULT_OK) {
                    RecarregarImagem recarregarImagem = new RecarregarImagem();
                    recarregarImagem.execute();
            } else {
                Toast.makeText(this, "Imagem não capturada!", Toast.LENGTH_LONG).show();
            }
        }
    }

    class RecarregarImagem extends AsyncTask<Void, Integer, Bitmap> {
        @Override
        protected Bitmap doInBackground(Void... voids) {
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                int bmpWidth = bitmap.getWidth();
                int bmpHeight = bitmap.getHeight();
                Matrix matrix = new Matrix();
                matrix.postRotate(-90);
                Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bmpWidth, bmpHeight, matrix, true);
                denuncia.setUriMidia(getDiretorioDeSalvamento(uri.getPath()).getPath());
                return resizedBitmap;
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Imagem não encontrada!", Toast.LENGTH_LONG).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            fotoDenuncia.setImageBitmap(bitmap);
        }
    }

    public void carregarDados(int id) {
        denuncia = denunciaDAO.buscarDenunciaPorId(id);
        String foto = denuncia.getUriMidia();
        String descricao = denuncia.getDescricao();
        String usuario = denuncia.getUsuario();

        descricaoEditText.setText(descricao);
        nomeUsuarioEditText.setText(usuario);
        if (foto != null) {
            try {
                uri = Uri.fromFile(getDiretorioDeSalvamento(foto));
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                fotoDenuncia.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
