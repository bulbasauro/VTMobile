package com.bulbasauro.misc;

import com.bulbasauro.vtmobile.R;

/**
 * Created on 18/01/2016.
 */
public class RatingSelector {

    public static int definirRating(String rating) {
        int ret;
        switch (rating) {
            case "0.0":
                ret = R.drawable.star00;
                break;
            case "0.5":
                ret = R.drawable.star05;
                break;
            case "1.0":
                ret = R.drawable.star10;
                break;
            case "1.5":
                ret = R.drawable.star15;
                break;
            case "2.0":
                ret = R.drawable.star20;
                break;
            case "2.5":
                ret = R.drawable.star25;
                break;
            case "3.0":
                ret = R.drawable.star30;
                break;
            case "3.5":
                ret = R.drawable.star35;
                break;
            case "4.0":
                ret = R.drawable.star40;
                break;
            case "4.5":
                ret = R.drawable.star45;
                break;
            case "5.0":
                ret = R.drawable.star50;
                break;
            default:
                ret = R.drawable.star00;
                break;
        }
        return ret;
    }
}
