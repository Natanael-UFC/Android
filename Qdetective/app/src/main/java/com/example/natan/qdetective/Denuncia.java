package com.example.natan.qdetective;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by natan on 09/12/2017.
 */

public class Denuncia implements Serializable {
    private Integer id;
    private String descricao;
    private Date data;
    private Double longitude;
    private Double latitude;
    private String uriMidia;
    private String usuario;
    private String categoria;

    public Denuncia(Integer id, String descricao, Date data, Double longitude, Double latitude, String uriMidia, String usuario, String categoria){
        this.id = id;
        this.descricao = descricao;
        this.data = data;
        this.longitude = longitude;
        this.latitude = latitude;
        this.uriMidia = uriMidia;
        this.usuario = usuario;
        this.categoria = categoria;
    }

    public Denuncia(String descricao, Date data, Double longitude, Double latitude, String uriMidia, String usuario, String categoria){
        this.descricao = descricao;
        this.data = data;
        this.longitude = longitude;
        this.latitude = latitude;
        this.uriMidia = uriMidia;
        this.usuario = usuario;
        this.categoria = categoria;
    }

    public Denuncia(){}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getUriMidia() {
        return uriMidia;
    }

    public void setUriMidia(String uriMidia) {
        this.uriMidia = uriMidia;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Denuncia)) return false;
        Denuncia denuncia = (Denuncia) o;
        if (!getId().equals(denuncia.getId())) return false;
        if (getDescricao() != null ? !getDescricao().equals(denuncia.getDescricao()) : denuncia.getDescricao() != null)
            return false;
        if (getData() != null ? !getData().equals(denuncia.getData()) : denuncia.getData() != null)
            return false;
        if (getLongitude() != null ? !getLongitude().equals(denuncia.getLongitude()) : denuncia.getLongitude() != null)
            return false;
        if (getLatitude() != null ? !getLatitude().equals(denuncia.getLatitude()) : denuncia.getLatitude() != null)
            return false;
        if (getUriMidia() != null ? !getUriMidia().equals(denuncia.getUriMidia()) : denuncia.getUriMidia() != null)
            return false;
        if (getUsuario() != null ? !getUsuario().equals(denuncia.getUsuario()) : denuncia.getUsuario() != null)
            return false;
        return getCategoria() != null ? getCategoria().equals(denuncia.getCategoria()) : denuncia.getCategoria() == null;
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + (getDescricao() != null ? getDescricao().hashCode() : 0);
        result = 31 * result + (getData() != null ? getData().hashCode() : 0);
        result = 31 * result + (getLongitude() != null ? getLongitude().hashCode() : 0);
        result = 31 * result + (getLatitude() != null ? getLatitude().hashCode() : 0);
        result = 31 * result + (getUriMidia() != null ? getUriMidia().hashCode() : 0);
        result = 31 * result + (getUsuario() != null ? getUsuario().hashCode() : 0);
        result = 31 * result + (getCategoria() != null ? getCategoria().hashCode() : 0);
        return result;
    }
}
