package com.bulbasauro.misc;

import android.content.Intent;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.bulbasauro.vtmobile.MainActivity;
import com.bulbasauro.vtmobile.R;
import com.bulbasauro.vtmobile.TopicoActivity;

import java.util.HashMap;

/**
 * Created on 01/02/2016.
 */
public class CustomGestureListener extends GestureDetector.SimpleOnGestureListener {

    private MainActivity activity;
    private HashMap<String, String> resultp;

    public CustomGestureListener(MainActivity activity, HashMap<String, String> resultp) {
        this.activity = activity;
        this.resultp = resultp;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        int acao = activity.getSharedPreferences().getInt(activity.getResources().getString(R.string.sp_toque_duplo), 0);
        if (acao != 2) {
            Intent intent = new Intent(activity, TopicoActivity.class);
            intent.putExtra("topicoURL", resultp.get("topicoURL"));
            intent.putExtra("topicoRating", resultp.get("topicoRating"));
            intent.putExtra("logado", activity.getLogado());

            if (acao == 0) { // Primeira página
                intent.putExtra("pagina", 1);
            } else if (acao == 1) { // Última página
                String numPag = resultp.get("numeroDePaginas");
                numPag = numPag.replace(" página", "");
                numPag = numPag.replace(" páginas", "");
                numPag = numPag.replace("s", "");
                numPag = numPag.trim();
                int pagina = Integer.parseInt(numPag);
                intent.putExtra("pagina", pagina);
            }

            activity.startActivityForResult(intent, 1);
        }

        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        Intent intent = new Intent(activity, TopicoActivity.class);
        intent.putExtra("topicoURL", resultp.get("topicoURL"));
        intent.putExtra("topicoRating", resultp.get("topicoRating"));
        intent.putExtra("logado", activity.getLogado());

        int acao = activity.getSharedPreferences().getInt(activity.getResources().getString(R.string.sp_toque_simples), 1);
        if (acao == 0) { // Primeira página
            intent.putExtra("pagina", 1);
        } else { // Última página
            String numPag = resultp.get("numeroDePaginas");
            numPag = numPag.replace(" página", "");
            numPag = numPag.replace(" páginas", "");
            numPag = numPag.replace("s", "");
            numPag = numPag.trim();
            int pagina = Integer.parseInt(numPag);
            intent.putExtra("pagina", pagina);
        }

        activity.startActivityForResult(intent, 1);
        return true;
    }
}
