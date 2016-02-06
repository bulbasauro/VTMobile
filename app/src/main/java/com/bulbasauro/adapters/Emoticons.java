package com.bulbasauro.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bulbasauro.abstracts.AbstractActivity;
import com.bulbasauro.vtmobile.R;

/**
 * Created on 25/01/2016.
 */
public class Emoticons extends BaseAdapter {

    private AbstractActivity activity;

    public final static Integer[] imageResIds = new Integer[]{
            R.drawable.icon_smile, R.drawable.icon_biggrin, R.drawable.icon_surprised, R.drawable.icon_lol, R.drawable.icon_lolsuper, R.drawable.icon_vryhappy,
            R.drawable.icon_sad, R.drawable.icon_nvm, R.drawable.icon_cry, R.drawable.icon_vrysad, R.drawable.icon_cryingalot, R.drawable.icon_sweattz,
            R.drawable.icon_neutral, R.drawable.icon_crylolim, R.drawable.icon_mad, R.drawable.icon_rolleyes, R.drawable.icon_confused, R.drawable.icon_omgrollt,
            R.drawable.icon_cool, R.drawable.icon_rock, R.drawable.icon_coolcry, R.drawable.icon_oopscool, R.drawable.icon_eek, R.drawable.icon_wtff,
            R.drawable.icon_love, R.drawable.icon_loveangry, R.drawable.icon_ilove, R.drawable.icon_vamp, R.drawable.icon_redface, R.drawable.icon_awesome,
            R.drawable.icon_not, R.drawable.icon_omg, R.drawable.icon_rimbuk, R.drawable.icon_rimkuk2, R.drawable.icon_facepalm, R.drawable.icon_fuuu,
            R.drawable.icon_ha, R.drawable.icon_mrgreen, R.drawable.icon_xis, R.drawable.icon_razz, R.drawable.icon_mustache, R.drawable.icon_wink,
            R.drawable.icon_shocknorw, R.drawable.icon_shockpink, R.drawable.icon_emosit, R.drawable.icon_shockblond, R.drawable.icon_girlblink, R.drawable.icon_pinkarrow,
            R.drawable.icon_evil, R.drawable.icon_twisted, R.drawable.icon_emotion, R.drawable.icon_cryingoffelicity,
            R.drawable.icon_arrow, R.drawable.icon_exclaim, R.drawable.icon_question, R.drawable.icon_idea, R.drawable.icon_vb
    };

    public final static String[] imageTextoIds = new String[]{
            " :-) ", " :-D ", " :-o ", " :lol: ", " :lolsuper: ", " :feliz2: ",
            " :-( ", " :magoado:", " :chorar:", " :triste2:", " :chorar3: ", " :suando: ",
            " :-| ", " :lagrimas:", " :-x", " :roll:", ":-?", " :omg3: ",
            " 8-) ", " :rock:", " :chorar2:", " :oopscool:", " :chocado: ", " :queixo: ",
            " :love: ", " :amorodio: ", " :gamado: ", " :vamp: ", " :oops: ", " :incrivel: ",
            " :nem: ", " :omg2: ", " :rimbuk: ", " :rimbuk2: ", " :cobrindorosto: ", " :fuuu: ",
            " :ha: ", " :ironico: ", " :xis: ", " :-P ", " :bigode: ", " ;-) ",
            " :girlhappy: ", " :girlsad: ", " :girl: ", " :girlshocked: ", " :piscadela: ", " :setarosa: ",
            " :demonio: ", " :malefico: ", " :emocao: ", " :chorar4: ",
            " :seta: ", " :!: ", " :?: ", " :ideia: ", " :vritualboy: "
    };

    public Emoticons(AbstractActivity activity) {
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return imageResIds.length;
    }

    @Override
    public Object getItem(int position) {
        return imageResIds[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null) {
            itemView = LayoutInflater.from(activity).inflate(R.layout.gridview_emoticons, null);
        }
        ImageButton imageButtonEmoticon = (ImageButton) itemView.findViewById(R.id.imageButton_emoticon_image);
        imageButtonEmoticon.setImageResource(imageResIds[position]);
        imageButtonEmoticon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et = (EditText) activity.getCurrentFocus();
                String texto = et.getText().toString();
                String textoMod = texto.concat(imageTextoIds[position]);
                et.setText(textoMod);
                et.setSelection(textoMod.length());
                Toast.makeText(activity, activity.getString(R.string.adicionado), Toast.LENGTH_SHORT).show();
            }
        });

        return itemView;
    }
}
