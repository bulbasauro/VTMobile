package com.bulbasauro.abstracts;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.bulbasauro.async.misc.Login;
import com.bulbasauro.misc.Comando;
import com.bulbasauro.vtmobile.R;

import java.util.Map;

/**
 * Created on 23/01/2016.
 * <p/>
 * Abstract básico para todos os Activities.
 */
public abstract class AbstractActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private Map<String, String> cookies;
    private Boolean logado = false;
    private int paginaAtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(getString(R.string.shared_preferences), Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        paginaAtual = getSharedPreferences().getInt(getString(R.string.sp_main_ultima_pagina), 1);
    }

    public void iniciarLogin(String user, String password, Comando comando) {
        Login login = new Login(AbstractActivity.this, user, password, comando);
        login.execute();
    }

    public void iniciarLoginSharedPreferences(int pagina) {
        setPaginaAtual(pagina);
        String user = sharedPreferences.getString(getString(R.string.sp_user_logado_user), "");
        String password = sharedPreferences.getString(getString(R.string.sp_user_logado_password), "");
        iniciarLogin(user, password, Comando.VALIDAR);
    }

    public void salvarDadosUsuarioLogado(String user, String password) {
        editor.putString(getString(R.string.sp_user_logado_user), user);
        editor.putString(getString(R.string.sp_user_logado_password), password);
        editor.commit();
    }

    public abstract void abrirPagina(int numeroPagina, Comando comando);

    // Getters e Setters

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public void setSharedPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public SharedPreferences.Editor getEditor() {
        return editor;
    }

    public void setEditor(SharedPreferences.Editor editor) {
        this.editor = editor;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public void setCookies(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public Boolean getLogado() {
        return logado;
    }

    public void setLogado(Boolean logado) {
        this.logado = logado;
    }

    public int getPaginaAtual() {
        return paginaAtual;
    }

    public void setPaginaAtual(int paginaAtual) {
        this.paginaAtual = paginaAtual;
    }
}
