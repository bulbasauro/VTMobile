package com.bulbasauro.async.misc;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import com.bulbasauro.abstracts.AbstractActivity;
import com.bulbasauro.misc.Comando;
import com.bulbasauro.vtmobile.R;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.util.Map;

/**
 * Created on 20/01/2016.
 */
public class Login extends AsyncTask<String, Void, Boolean> {

    private static final String URL_LOGIN = "https://acesso.uol.com.br/login.html";

    private AbstractActivity activity;
    private ProgressDialog progressDialog;

    private String user = "";
    private String password = "";
    private Comando comando = Comando.ABRIR;

    private Map<String, String> cookies;

    public Login(AbstractActivity activity, String user, String password, Comando comando) {
        this.activity = activity;
        this.password = password;
        this.user = user;
        this.comando = comando;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (comando.equals(Comando.VALIDAR) || (comando.equals(Comando.ATUALIZAR) && activity.getLogado())) {
            progressDialog = ProgressDialog.show(activity, null, activity.getResources().getString(R.string.validando), true);
        } else {
            progressDialog = ProgressDialog.show(activity, null, activity.getResources().getString(R.string.logando), true);
        }
        progressDialog.show();
    }

    @Override
    protected Boolean doInBackground(String... params) {
        try {
            Connection.Response res = Jsoup.connect(URL_LOGIN)
                    .data("user", user, "pass", password)
                    .method(Connection.Method.POST)
                    .timeout(6000)
                    .execute();

            cookies = res.cookies();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            if (cookies.size() > 0) {
                activity.setLogado(true);
                activity.setCookies(cookies);
                activity.salvarDadosUsuarioLogado(user, password);
                activity.abrirPagina(activity.getPaginaAtual(), comando);
                progressDialog.dismiss();
                return;
            }
            Toast.makeText(activity.getApplicationContext(), activity.getString(R.string.erro), Toast.LENGTH_SHORT).show();
        } else {
            activity.abrirPagina(activity.getPaginaAtual(), comando);
        }
        progressDialog.dismiss();
    }

}
