package com.bulbasauro.abstracts;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bulbasauro.adapters.Emoticons;
import com.bulbasauro.erros.ExceptionAntiflood;
import com.bulbasauro.erros.ExceptionMensagemPequena;
import com.bulbasauro.misc.CustomTextWatcher;
import com.bulbasauro.vtmobile.R;

import java.io.UnsupportedEncodingException;

/**
 * Created on 25/01/2016.
 * <p/>
 * Abstract para todos Activities que permitem postar.
 */
public abstract class AbstractPostar extends AbstractActivity {

    private EditText editTextMensagem;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getIntent().getExtras().getString("userName"));

        iniciarEditTextMensagem();
        iniciarToolbarResposta();
    }

    protected void iniciarEditTextMensagem() {
        TextView textViewMensagemCount = (TextView) findViewById(R.id.textView_postar_mensagemCount);
        editTextMensagem = (EditText) findViewById(R.id.editText_postar_mensagem);
        editTextMensagem.addTextChangedListener(new CustomTextWatcher(textViewMensagemCount, 15000));
    }

    protected void iniciarToolbarResposta() {
        // ENVIAR
        TextView textViewEnviar = (TextView) findViewById(R.id.textView_toolbar_postar_enviar);
        textViewEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    enviarMensagem();

                    // Retrai o teclado
                    InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                    iniciarLoginSharedPreferences(getPaginaAtual());
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(AbstractPostar.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // EMOTICONS
        ImageView imageViewEmoticon = (ImageView) findViewById(R.id.imageView_toolbar_postar_emoticon);
        imageViewEmoticon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View view = getLayoutInflater().inflate(R.layout.dialog_emoticon, null);
                AlertDialog.Builder dialog = new AlertDialog.Builder(AbstractPostar.this);
                dialog.setView(view);
                GridView gridViewEmoticon = (GridView) view.findViewById(R.id.gridView_diag_emoticon);
                gridViewEmoticon.setAdapter(new Emoticons(AbstractPostar.this));
                dialog.setNegativeButton(R.string.diag_fechar, null);
                dialog.show();
            }
        });

        // CANCELAR
        TextView textViewCancelar = (TextView) findViewById(R.id.textView_toolbar_postar_cancelar);
        textViewCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public abstract void enviarMensagem() throws ExceptionAntiflood, ExceptionMensagemPequena, UnsupportedEncodingException;

    // Getters e Setters

    public EditText getEditTextMensagem() {
        return editTextMensagem;
    }

    public void setEditTextMensagem(EditText editTextMensagem) {
        this.editTextMensagem = editTextMensagem;
    }
}
