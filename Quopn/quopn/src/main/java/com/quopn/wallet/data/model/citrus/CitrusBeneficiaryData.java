package com.quopn.wallet.data.model.citrus;

import com.quopn.wallet.interfaces.Response;

/**
 * Created by Sandeep on 15-Jan-16.
 */
public class CitrusBeneficiaryData implements Response {
    private String beneficiary_code;
    private String beneficiary_name;
    private String beneficiary_acc;
    private String ifsc_code;

    public String getBeneficiary_code() {
        return beneficiary_code;
    }

    public void setBeneficiary_code(String beneficiary_code) {
        this.beneficiary_code = beneficiary_code;
    }

    public String getBeneficiary_name() {
        return beneficiary_name;
    }

    public void setBeneficiary_name(String beneficiary_name) {
        this.beneficiary_name = beneficiary_name;
    }

    public String getBeneficiary_acc() {
        return beneficiary_acc;
    }

    public void setBeneficiary_acc(String beneficiary_acc) {
        this.beneficiary_acc = beneficiary_acc;
    }

    public String getIfsc_code() {
        return ifsc_code;
    }

    public void setIfsc_code(String ifsc_code) {
        this.ifsc_code = ifsc_code;
    }
}
