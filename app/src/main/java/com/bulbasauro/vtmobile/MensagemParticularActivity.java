package com.bulbasauro.vtmobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bulbasauro.abstracts.AbstractMenu;
import com.bulbasauro.adapters.MensagensParticularesFragment;
import com.bulbasauro.async.jsoup.JsoupMpInbox;
import com.bulbasauro.async.jsoup.JsoupMpOutbox;
import com.bulbasauro.misc.Comando;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MensagemParticularActivity extends AbstractMenu {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ListView listViewInbox;

    private ListView listViewOutbox;
    private JsoupMpInbox inbox;
    private JsoupMpOutbox outbox;

    private Boolean aprovado = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensagem_particular);

        viewPager = (ViewPager) findViewById(R.id.viewPager_mp);
        tabLayout = (TabLayout) findViewById(R.id.tablayout_mp);

        FragmentManager manager = getSupportFragmentManager();
        MensagensParticularesFragment mps = new MensagensParticularesFragment(manager);
        viewPager.setAdapter(mps);

        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setTabsFromPagerAdapter(mps);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        String bemVindo = getIntent().getExtras().getString("userName");
        getSupportActionBar().setTitle(bemVindo);

        iniciarLoginSharedPreferences(1);
    }

    @Override
    public void abrirHome() {
        Intent intent = new Intent();
        intent.putExtra("logado", getLogado());
        setResult(RESULT_FIRST_USER, intent);
        finish();
    }

    @Override
    protected void sair() {
        setLogado(false);
        abrirHome();
    }

    @Override
    public void abrirPagina(int numeroPagina, Comando comando) {
        listViewInbox = (ListView) viewPager.findViewById(R.id.listView_mp_inbox);
        listViewOutbox = (ListView) viewPager.findViewById(R.id.listView_mp_outbox);
        inbox = new JsoupMpInbox(MensagemParticularActivity.this, getCookies(), listViewInbox);
        inbox.execute();
        outbox = new JsoupMpOutbox(MensagemParticularActivity.this, getCookies(), listViewOutbox);
        outbox.execute();
    }

    public void setInfo(Document document) {
        Elements ele = document.select("ul[id=mps-statistics]");
        String lis[] = new String[6];
        int c = 0;
        for (Element li : ele.select("li")) {
            lis[c] = li.text();
            c++;
        }

        String percent = document.select("div[class=progressBar]").attr("style");
        percent = percent.replace("width: ", "");
        percent = percent.replace("%", "");

        String total = document.select("div[class=pms-quota left]").text();

        TextView textViewNaoLidas = (TextView) findViewById(R.id.textView_mp_naoLidas);
        TextView textViewLimite = (TextView) findViewById(R.id.textView_mp_limite);
        TextView textViewLidas = (TextView) findViewById(R.id.textView_mp_lidas);
        TextView textViewOcupacao = (TextView) findViewById(R.id.textView_mp_ocupacao);
        TextView textViewEnviadas = (TextView) findViewById(R.id.textView_mp_enviadas);
        TextView textViewReceber = (TextView) findViewById(R.id.textView_mp_receber);

        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frameLayout_mp_barraProgresso);
        ImageView imageViewProgressBar = (ImageView) findViewById(R.id.imageView_mp_progressBar);

        textViewNaoLidas.setText(lis[0]);
        textViewLimite.setText(lis[1]);
        textViewLidas.setText(lis[2]);
        textViewOcupacao.setText(lis[3]);
        textViewEnviadas.setText(lis[4]);
        textViewReceber.setText(lis[5]);

        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) imageViewProgressBar.getLayoutParams();
        int width = frameLayout.getWidth();
        int percentNumber = Integer.parseInt(percent);
        double vezes = percentNumber * 0.01;
        double percentWidth = width * vezes;
        int def = (int) percentWidth;
        lp.width = def;
        imageViewProgressBar.setLayoutParams(lp);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mensagem_particular, menu);
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
                if (MensagemParticularActivity.this instanceof MensagemParticularActivity) {
                    iniciarLoginSharedPreferences(1);
                } else {
                    Intent intent = new Intent(MensagemParticularActivity.this, MensagemParticularActivity.class);
                    startActivityForResult(intent, 4);
                }
                return true;
            case R.id.action_nova_mp:
                if (getAprovado()) {
                    Intent intent = new Intent(MensagemParticularActivity.this, MandarMpActivity.class);
                    intent.putExtra("destinatario", "");
                    intent.putExtra("assunto", "");
                    intent.putExtra("userName", getSupportActionBar().getTitle());
                    startActivityForResult(intent, 55);
                } else {
                    Toast.makeText(MensagemParticularActivity.this, getString(R.string.apelido_aprovado), Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_refresh_mp:
                iniciarLoginSharedPreferences(1);
                return true;
            case R.id.action_configuracoes:
                Intent intente = new Intent(MensagemParticularActivity.this, ConfiguracoesActivity.class);
                startActivity(intente);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 22:
                if (resultCode == 2) {
                    recreate();
                }
                break;
            case 55:
                if (resultCode == RESULT_OK) {
                    iniciarLoginSharedPreferences(1);
                }
                break;
            case 66:
                if (resultCode == RESULT_OK) {
                    iniciarLoginSharedPreferences(1);
                }
                break;
        }
    }

    public Boolean getAprovado() {
        return aprovado;
    }

    public void setAprovado(Boolean aprovado) {
        this.aprovado = aprovado;
    }
}
