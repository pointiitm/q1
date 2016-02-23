package com.quopn.wallet.data.model.citrus;

import com.quopn.wallet.interfaces.Response;

/**
 * Created by Sandeep on 15-Jan-16.
 */
public class WalletListData implements Response {

    private int id;
    private String image;
    private String url;
    private String gateway_name;
    private String signin_id;
    private String signup_id;
    private String client_signup_secret;
    private String client_signin_secret;
    private String vanity;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getGateway_name() {
        return gateway_name;
    }

    public void setGateway_name(String gateway_name) {
        this.gateway_name = gateway_name;
    }

    public String getSignin_id() {
        return signin_id;
    }

    public void setSignin_id(String signin_id) {
        this.signin_id = signin_id;
    }

    public String getSignup_id() {
        return signup_id;
    }

    public void setSignup_id(String signup_id) {
        this.signup_id = signup_id;
    }

    public String getClient_signup_secret() {
        return client_signup_secret;
    }

    public void setClient_signup_secret(String client_signup_secret) {
        this.client_signup_secret = client_signup_secret;
    }

    public String getClient_signin_secret() {
        return client_signin_secret;
    }

    public void setClient_signin_secret(String client_signin_secret) {
        this.client_signin_secret = client_signin_secret;
    }

    public String getVanity() {
        return vanity;
    }

    public void setVanity(String vanity) {
        this.vanity = vanity;
    }
}
