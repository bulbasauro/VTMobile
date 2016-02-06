package com.bulbasauro.abstracts;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.bulbasauro.misc.CustomTextWatcher;
import com.bulbasauro.vtmobile.R;

/**
 * Created on 01/02/2016.
 * <p/>
 * Abstract para postar com assunto.
 */
public abstract class AbstractPostarAssunto extends AbstractPostar {

    private EditText editTextAssunto;

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        iniciarEditTextAssunto();
    }

    protected void iniciarEditTextAssunto() {
        TextView textViewAssuntoCount = (TextView) findViewById(R.id.textView_postar_assunto_mensagemCount);
        editTextAssunto = (EditText) findViewById(R.id.editText_postar_assunto);
        editTextAssunto.addTextChangedListener(new CustomTextWatcher(textViewAssuntoCount, 100));
    }

    // Getters e Setters

    public EditText getEditTextAssunto() {
        return editTextAssunto;
    }

    public void setEditTextAssunto(EditText editTextAssunto) {
        this.editTextAssunto = editTextAssunto;
    }
}
