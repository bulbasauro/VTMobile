package com.bulbasauro.abstracts;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.bulbasauro.misc.Comando;
import com.bulbasauro.vtmobile.ConfiguracoesActivity;
import com.bulbasauro.vtmobile.MensagemParticularActivity;
import com.bulbasauro.vtmobile.MeusTopicosActivity;
import com.bulbasauro.vtmobile.R;
import com.bulbasauro.vtmobile.TopicoActivity;

/**
 * Created on 30/01/2016.
 * <p/>
 * Classe abstrata que trata do menu superior.
 * <p/>
 * Qualquer Activity que tenha o menu superior deve extender pelo menos deste abstract.
 */
public abstract class AbstractMenu extends AbstractActivity {

    private Menu menu;
    private String userNumber;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_menu);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        setMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_home:
                abrirHome();
                return true;
            case R.id.action_logar:
                abrirLogin();
                return true;
            case R.id.action_sair:
                sair();
                return true;
            case R.id.action_mp:
                if (AbstractMenu.this instanceof MensagemParticularActivity) {
                    iniciarLoginSharedPreferences(1);
                } else if (AbstractMenu.this instanceof MeusTopicosActivity) {
                    abrirMensagensParticulares();
                    finish();
                } else {
                    abrirMensagensParticulares();
                }
                return true;
            case R.id.action_topicos:
                Intent intentMeusTopicos = new Intent(AbstractMenu.this, MeusTopicosActivity.class);
                intentMeusTopicos.putExtra("userNumber", userNumber);
                intentMeusTopicos.putExtra("logado", getLogado());
                startActivityForResult(intentMeusTopicos, 8);
                if (AbstractMenu.this instanceof TopicoActivity) {
                    finish();
                } else if (AbstractMenu.this instanceof MensagemParticularActivity) {
                    finish();
                }
                return true;
            case R.id.action_configuracoes:
                Intent intent = new Intent(AbstractMenu.this, ConfiguracoesActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void abrirMensagensParticulares() {
        if (getLogado()) {
            Intent intent = new Intent(AbstractMenu.this, MensagemParticularActivity.class);
            intent.putExtra("userName", getSupportActionBar().getTitle().toString());
            startActivityForResult(intent, 4);
        }
    }

    protected void abrirLogin() {
        final View view = getLayoutInflater().inflate(R.layout.dialog_login, null);
        AlertDialog.Builder dialog = new AlertDialog.Builder(AbstractMenu.this);

        final EditText editTextEmail = (EditText) view.findViewById(R.id.editText_diag_login_email);
        final EditText editTextSenha = (EditText) view.findViewById(R.id.editText_diag_login_senha);
        final CheckBox checkBoxMostrarSenha = (CheckBox) view.findViewById(R.id.checkBox_mostrar_senha);

        Boolean salvar = getSharedPreferences().getBoolean(getString(R.string.sp_salvar_login_senha), false);
        if (salvar) {
            editTextEmail.setText(getSharedPreferences().getString(getString(R.string.sp_user_logado_user), ""));
            editTextSenha.setText(getSharedPreferences().getString(getString(R.string.sp_user_logado_password), ""));
            editTextEmail.setSelection(editTextEmail.getText().toString().length());
        }

        checkBoxMostrarSenha.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkBoxMostrarSenha.setChecked(isChecked);
                int pos = editTextSenha.getSelectionStart();
                if (isChecked) {
                    editTextSenha.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    editTextSenha.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                editTextSenha.setSelection(pos);
                getEditor().putBoolean(getString(R.string.sp_user_logado_mostrar_senha), isChecked);
                getEditor().commit();
            }
        });
        Boolean sel = getSharedPreferences().getBoolean(getString(R.string.sp_user_logado_mostrar_senha), false);
        checkBoxMostrarSenha.setChecked(sel);
        if (sel) {
            editTextSenha.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            editTextSenha.setInputType(InputType.TYPE_CLASS_TEXT| InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }

        dialog.setView(view);
        dialog.setPositiveButton(R.string.diag_entrar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                String user = editTextEmail.getText().toString();
                String pass = editTextSenha.getText().toString();

                try {
                    if (user.isEmpty() || pass.isEmpty()) {
                        Toast.makeText(AbstractMenu.this, getString(R.string.erro), Toast.LENGTH_SHORT).show();
                    } else {
                        iniciarLogin(user, pass, Comando.LOGIN);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        dialog.setNegativeButton(R.string.diag_cancelar, null);
        dialog.show();
    }

    public void atualizarToolbarMenu(String nomeUsuario) {
        if ("".equals(nomeUsuario)) {
            getSupportActionBar().setTitle(getString(R.string.app_name));
            menu.removeItem(R.id.action_sair);
            menu.removeItem(R.id.action_mp);
            menu.removeItem(R.id.action_topicos);
            MenuItem miLogar = menu.findItem(R.id.action_logar);
            if (miLogar == null) {
                menu.add(Menu.NONE, R.id.action_logar, 2, R.string.logar);
            }
        } else {
            getSupportActionBar().setTitle(nomeUsuario);
            menu.removeItem(R.id.action_logar);

            MenuItem miSair = menu.findItem(R.id.action_sair);
            if (miSair == null) {
                menu.add(Menu.NONE, R.id.action_sair, 3, R.string.sair);
            }
            MenuItem miMP = menu.findItem(R.id.action_mp);
            if (miMP == null) {
                menu.add(Menu.NONE, R.id.action_mp, 4, R.string.mp);
            }
            MenuItem miTopicos = menu.findItem(R.id.action_topicos);
            if (miTopicos == null) {
                menu.add(Menu.NONE, R.id.action_topicos, 5, R.string.topicos);
            }
        }
    }

    protected void sair() {
        setCookies(null);
        setLogado(false);
        abrirPagina(getPaginaAtual(), Comando.SAIR);
    }

    public abstract void abrirHome();

    // Getters e Setters

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public String getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(String urlPerfil) {
        this.userNumber = urlPerfil;
    }

}
