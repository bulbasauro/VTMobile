package com.bulbasauro.vtmobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.bulbasauro.abstracts.AbstractNavegacao;
import com.bulbasauro.async.jsoup.JsoupTopicos;
import com.bulbasauro.misc.Comando;

public class MainActivity extends AbstractNavegacao {

    private JsoupTopicos topicos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listViewTopicos = (ListView) findViewById(R.id.listView_main_topicos);
        setListView(listViewTopicos);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        iniciarBotaoNovoTopico();
        abrirPagina(getPaginaAtual(), Comando.ABRIR);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Boolean lg = data.getExtras().getBoolean("logado", false);
                    setLogado(lg);
                    if (!getLogado()) {
                        setCookies(null);
                    }
                    String bemVindo = data.getExtras().getString("bemVindo");
                    atualizarToolbarMenu(bemVindo);
                } else if (resultCode == RESULT_FIRST_USER) {
                    Boolean lg = data.getExtras().getBoolean("logado");
                    setLogado(lg);
                    abrirHome();
                } else if (resultCode == 2) {
                    String l = getSharedPreferences().getString(getString(R.string.sp_logado), "");
                    getEditor().remove(getString(R.string.sp_logado));
                    getEditor().commit();
                    if (!l.equals("")) {
                        setLogado(true);
                    }
                    atualizarToolbarMenu(l);
                } else {
                    getEditor().remove(getString(R.string.sp_logado));
                    getEditor().commit();
                }
                break;
            case 3:
                if (resultCode == RESULT_OK) {
                    abrirPagina(1, Comando.ABRIR);
                }
                break;
            case 4:
                if (resultCode == RESULT_FIRST_USER) {
                    Boolean lg = data.getExtras().getBoolean("logado");
                    setLogado(lg);
                    abrirHome();
                }
                break;
            case 8: // Veio dos Meus Tópicos
                String l = getSharedPreferences().getString(getString(R.string.sp_logado), "");
                Boolean h = getSharedPreferences().getBoolean(getString(R.string.sp_home), false);
                getEditor().remove(getString(R.string.sp_logado));
                getEditor().remove(getString(R.string.sp_home));
                getEditor().commit();
                atualizarToolbarMenu(l);
                if (!l.equals("")) {
                    setLogado(true);
                } else {
                    setLogado(false);
                }
                if (h) {
                    abrirHome();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void abrirPagina(int numeroPagina, Comando comando) {
        topicos = new JsoupTopicos(MainActivity.this, getCookies(), getListView(), numeroPagina, comando);
        topicos.execute();
    }

    @Override
    public void atualizarToolbarNavegacao(int totalPaginas) {
        String strPagAtual = Integer.toString(getPaginaAtual());
        getTextViewPagina().setText(strPagAtual);
    }

    private void iniciarBotaoNovoTopico() {
        // NOVO TÓPICO
        ImageButton imageButtonNovoTopico = (ImageButton) getToolbarNavegacao().findViewById(R.id.imageButton_main_newTopic);
        imageButtonNovoTopico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getLogado()) {
                    Intent intent = new Intent(MainActivity.this, NovoTopicoActivity.class);
                    intent.putExtra("userName", getSupportActionBar().getTitle());
                    startActivityForResult(intent, 3);
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.nao_logado), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void abrirHome() {
        if (getLogado()) {
            iniciarLoginSharedPreferences(1);
        } else {
            abrirPagina(1, Comando.ABRIR);
        }
    }

    @Override
    public int getTotalPaginas() {
        return topicos.getTotalPaginas();
    }

}
