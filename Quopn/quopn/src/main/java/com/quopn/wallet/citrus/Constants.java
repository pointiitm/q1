package com.quopn.wallet.citrus;

import com.citrus.sdk.Environment;

/**
 * Created by Sandeep on 08-Jan-16.
 */
public interface Constants {

    String BILL_URL = "https://salty-plateau-1529.herokuapp.com/billGenerator.sandbox.php";
    String RETURN_URL_LOAD_MONEY = "https://salty-plateau-1529.herokuapp.com/redirectUrlLoadCash.php";

//    String SIGNUP_ID = "5ktro5iim1-signup";
//    String SIGNUP_SECRET = "5ac37989b71c7bbd4085576dc047f817";
//    String SIGNIN_ID = "5ktro5iim1-signin";
//    String SIGNIN_SECRET = "0b920ff9bcf9f522195be33ae80ffab0";
//    String VANITY = "5ktro5iim1";
//    Environment environment = Environment.SANDBOX;

    boolean enableLogging = true;

    String colorPrimaryDark = "#E7961D";
    String colorPrimary = "#F9A323";
    String textColor = "#ffffff";
}
