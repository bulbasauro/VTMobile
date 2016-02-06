package com.bulbasauro.abstracts;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.bulbasauro.misc.Comando;
import com.bulbasauro.misc.LimitadorDeNumero;
import com.bulbasauro.vtmobile.R;

/**
 * Created on 18/01/2016.
 * <p/>
 * Abstract para Activities de página principal, onde possui além do Menu a barra de navegação de páginas inferior.
 */
public abstract class AbstractNavegacao extends AbstractMenu {

    private Toolbar toolbarNavegacao;
    private TextView textViewPagina;
    private ListView listView;
    private Snackbar snackbar;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        Toolbar toolbarNavegacao = (Toolbar) findViewById(R.id.toolbar_navigation);
        setToolbarNavegacao(toolbarNavegacao);

        iniciarToolbarNavegacao();
    }

    private void abrirNavegador() {
        int qtdPaginas = getTotalPaginas();
        if (qtdPaginas < 1) {
            qtdPaginas = 1;
        }

        View view = getLayoutInflater().inflate(R.layout.dialog_navigation, null);
        final EditText editTextPagina = (EditText) view.findViewById(R.id.editText_dialog_nav_pagina);
        editTextPagina.setFilters(new InputFilter[]{new LimitadorDeNumero(1, qtdPaginas)});

        TextView textViewQtdPaginas = (TextView) view.findViewById(R.id.textView_dialog_nav_totalPaginas);
        textViewQtdPaginas.setText("(" + Integer.toString(qtdPaginas) + ")");

        AlertDialog.Builder dialog = new AlertDialog.Builder(AbstractNavegacao.this);
        dialog.setView(view);
        dialog.setPositiveButton(R.string.diag_confirmar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                String numInsert = editTextPagina.getText().toString().trim();
                if (!(numInsert == null || numInsert.isEmpty() || numInsert.equals(""))) {
                    int pagAbrir = Integer.parseInt(numInsert);
                    if (getCookies() != null) {
                        iniciarLoginSharedPreferences(pagAbrir);
                    } else {
                        abrirPagina(pagAbrir, Comando.ABRIR);
                    }
                }
            }
        });
        dialog.setNegativeButton(R.string.diag_cancelar, null);
        dialog.show();
    }

    public void atualizarToolbarNavegacao(int totalPaginas) {
        String strPagAtual = Integer.toString(getPaginaAtual());
        String strTotalPag = Integer.toString(totalPaginas);
        textViewPagina.setText(strPagAtual.concat("/").concat(strTotalPag));
    }

    public void iniciarToolbarNavegacao() {
        // VOLTAR
        ImageButton imageButtonVoltar = (ImageButton) toolbarNavegacao.findViewById(R.id.imageButton_nav_voltar);
        imageButtonVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int proxPagina = getPaginaAtual() - 1;
                if (proxPagina > 0) {
                    if (getLogado()) {
                        try {
                            iniciarLoginSharedPreferences(proxPagina);
                        } catch (Exception e) {
                            setPaginaAtual(proxPagina + 1);
                        }
                    } else {
                        abrirPagina(proxPagina, Comando.ABRIR);
                    }
                }
            }
        });

        // AVANÇAR
        ImageButton imageButtonAvancar = (ImageButton) toolbarNavegacao.findViewById(R.id.imageButton_nav_avancar);
        imageButtonAvancar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int proxPagina = getPaginaAtual() + 1;
                int tp = getTotalPaginas();
                if (proxPagina <= tp) {
                    if (getLogado()) {
                        try {
                            iniciarLoginSharedPreferences(proxPagina);
                        } catch (Exception e) {
                            setPaginaAtual(proxPagina - 1);
                        }
                    } else {
                        abrirPagina(proxPagina, Comando.ABRIR);
                    }
                }
            }
        });

        // REFRESH
        ImageButton imageButtonRefresh = (ImageButton) toolbarNavegacao.findViewById(R.id.imageButton_nav_refresh);
        imageButtonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getLogado()) {
                    String user = getSharedPreferences().getString(getString(R.string.sp_user_logado_user), "");
                    String password = getSharedPreferences().getString(getString(R.string.sp_user_logado_password), "");
                    iniciarLogin(user, password, Comando.ATUALIZAR);
                } else {
                    abrirPagina(getPaginaAtual(), Comando.ATUALIZAR);
                }
            }
        });

        // CONTADOR DE PÁGINA
        textViewPagina = (TextView) toolbarNavegacao.findViewById(R.id.textView_nav_pagina);
        textViewPagina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirNavegador();
            }
        });
    }

    public void salvarPagina(int numeroPagina) {
        getEditor().putInt(getString(R.string.sp_main_ultima_pagina), numeroPagina);
        getEditor().commit();
    }

    public void criarAvisoMP(String mensagem) {
        snackbar = Snackbar.make(findViewById(R.id.coordinatorLayout_principal), mensagem, Snackbar.LENGTH_INDEFINITE);
        snackbar.setActionTextColor(getResources().getColor(R.color.white));
        snackbar.setAction(getString(R.string.snack_verificar), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirMensagensParticulares();
            }
        });
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        snackbar.show();
    }

    // Getters e Setters

    public abstract int getTotalPaginas();

    public Toolbar getToolbarNavegacao() {
        return toolbarNavegacao;
    }

    public void setToolbarNavegacao(Toolbar toolbarNavegacao) {
        this.toolbarNavegacao = toolbarNavegacao;
    }

    public TextView getTextViewPagina() {
        return textViewPagina;
    }

    public void setTextViewPagina(TextView textViewPagina) {
        this.textViewPagina = textViewPagina;
    }

    public ListView getListView() {
        return listView;
    }

    public void setListView(ListView listView) {
        this.listView = listView;
    }

    public Snackbar getSnackbar() {
        return snackbar;
    }

    public void setSnackbar(Snackbar snackbar) {
        this.snackbar = snackbar;
    }
}
