package com.quopn.wallet.citrus;

import com.citrus.sdk.TransactionResponse;
import com.citrus.sdk.classes.Amount;
import com.citrus.sdk.classes.CashoutInfo;
import com.quopn.wallet.utils.QuopnConstants;

/**
 * Created by salil on 3/6/15.
 */
public interface WalletFragmentListener {
    void onPaymentComplete(TransactionResponse transactionResponse);

    void onPaymentTypeSelected(QuopnConstants.PaymentType paymentType, Amount amount);

    void onPaymentTypeSelected(QuopnConstants.DPRequestType dpRequestType, Amount originalAmount, String couponCode, Amount alteredAmount);

    void onCashoutSelected(CashoutInfo cashoutInfo);

}
