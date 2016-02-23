package com.quopn.wallet.shmart;

import android.util.Log;

import com.quopn.wallet.connection.ConnectRequest;
import com.quopn.wallet.data.model.NetworkError;
import com.quopn.wallet.data.model.RemoteOtp;
import com.quopn.wallet.data.model.citrus.CitrusBeneficiaryData;
import com.quopn.wallet.data.model.citrus.CitrusLogWalletStats;
import com.quopn.wallet.data.model.shmart.AddBeneficiaryResponse;
import com.quopn.wallet.data.model.shmart.BalanceResponse;
import com.quopn.wallet.data.model.shmart.BeneficiariesResponse;
import com.quopn.wallet.data.model.shmart.ChangeTxnPwdResponse;
import com.quopn.wallet.data.model.shmart.DeleteBeneficiaryResponse;
import com.quopn.wallet.data.model.shmart.FeaturesResponse;
import com.quopn.wallet.data.model.shmart.GetPrefResponse;
import com.quopn.wallet.data.model.shmart.LoadWalletResponse;
import com.quopn.wallet.data.model.shmart.LocalOtp;
import com.quopn.wallet.data.model.shmart.ShmartResponse;
import com.quopn.wallet.data.model.shmart.TransactionsResponse;
import com.quopn.wallet.data.model.shmart.TransferToBankResponse;
import com.quopn.wallet.data.model.shmart.TransferToMobileResponse;
import com.quopn.wallet.data.model.shmart.UpdatePrefResponse;
import com.quopn.wallet.data.model.shmart.WalletStatusResponse;
import com.quopn.wallet.interfaces.ConnectionListener;
import com.quopn.wallet.interfaces.Response;
import com.quopn.wallet.utils.QuopnApi;

import java.util.ArrayList;

/**
 * Created by hari on 24/9/15.
 */
public class ShmartResponseListener implements ConnectionListener {
    private static final String TAG = "Quopn/ShmartResponse";

    private ShmartFlow shmartFlow;
    private LoadWalletActivity loadWalletActivity;
    private SendMoneyActivity sendMoneyActivity;


    public ShmartResponseListener(ShmartFlow shmartFlow) {
        this.shmartFlow = shmartFlow;
    }

    @Override
    public void onResponse(int responseResult, Response response) {
        if (response != null) {
            ShmartResponse shmartResponse = null;
            if (response instanceof ShmartResponse) {
                Log.d(TAG, "This is a Shmart response");
                shmartResponse = (ShmartResponse) response;
            }

            if (response instanceof BalanceResponse) {
                shmartFlow.onBalanceAvailable(((BalanceResponse) response).getBalance());
                if(ShmartFlow.getInstance().getActivity() != null) {
                    ShmartFlow.getInstance().getActivity().hideCustomProgress();
                }
            } else if (response instanceof BeneficiariesResponse) {
                BeneficiariesResponse beneficiariesResponse = (BeneficiariesResponse) response;
                switch (beneficiariesResponse.getErrorCode()) {
                    case 4:
                        shmartFlow.onNoBeneficiary();
                        break;
                    default:
                        shmartFlow.onBeneficiaryAvailable(
                                beneficiariesResponse.getCode()
                                , beneficiariesResponse.getName()
                                , beneficiariesResponse.getAccount()
                                , beneficiariesResponse.getBank());
                        break;
                }
            } else if (response instanceof AddBeneficiaryResponse) {
                AddBeneficiaryResponse addBeneficiaryResponse = (AddBeneficiaryResponse) response;
                if (addBeneficiaryResponse.isSuccess()) {
                    shmartFlow.onBeneficiaryAdded(addBeneficiaryResponse.getMessage());
                } else {
                    shmartFlow.onBeneficiaryNotAdded(addBeneficiaryResponse.getErrorCode()
                            , addBeneficiaryResponse.getMessage());
                }
            } else if (response instanceof TransferToBankResponse) {
                TransferToBankResponse transferToBankResponse = (TransferToBankResponse) response;
                if (transferToBankResponse.isSuccess()) {
                    shmartFlow.onBankTransferSuccess(transferToBankResponse.getMessage());
                } else {
                    shmartFlow.onBankTransferFailure(transferToBankResponse.getErrorCode()
                            , transferToBankResponse.getMessage());
                }
            } else if (response instanceof LoadWalletResponse) {
                     shmartFlow.onLoadWalletHtmlAvailable(((LoadWalletResponse) response).getHtml());
//                     loadWalletActivity.hideCustomProgress();
            } else if (response instanceof TransferToMobileResponse) {
                TransferToMobileResponse transferToMobileResponse = (TransferToMobileResponse) response;
                if (transferToMobileResponse.isSuccess()) {
                    shmartFlow.onSendMoneySuccess(transferToMobileResponse.getMessage());
                    } else {
                    shmartFlow.onSendMoneyFailure(transferToMobileResponse.getErrorCode()
                            , transferToMobileResponse.getMessage());
                }
            } else if (response instanceof ChangeTxnPwdResponse) {
                ChangeTxnPwdResponse changeTxnPwdResponse = (ChangeTxnPwdResponse) response;
                if (changeTxnPwdResponse.isSuccess()) {
                    shmartFlow.onTxnPwdChanged(changeTxnPwdResponse.getMessage());
//                    sendMoneyActivity.hideCustomProgress();
                } else {
                    shmartFlow.onTxnPwdNotChanged(changeTxnPwdResponse.getErrorCode()
                            , changeTxnPwdResponse.getMessage());
                }
            } else if (shmartResponse instanceof GetPrefResponse) {
                GetPrefResponse getPrefResponse = (GetPrefResponse) shmartResponse;

                if (getPrefResponse.isSuccess()) {
                    shmartFlow.onPrefReceived(getPrefResponse.getPref());
                }
            } else if (shmartResponse instanceof UpdatePrefResponse) {
                if (shmartResponse.isSuccess()) {
                    shmartFlow.onPrefSetSuccess(shmartResponse.getMessage(), (UpdatePrefResponse)shmartResponse);
                } else {
                    shmartFlow.onPrefSetFailure(shmartResponse.getErrorCode()
                            , shmartResponse.getMessage());
                }
            } else if (shmartResponse instanceof FeaturesResponse) {
                shmartFlow.onFeaturesReceived(((FeaturesResponse) shmartResponse).getFeatures());
            } else if (shmartResponse instanceof TransactionsResponse) {
                shmartFlow.onTransactionsReceived(
                        ((TransactionsResponse) shmartResponse).getTransactions());
            } else if (shmartResponse instanceof DeleteBeneficiaryResponse) {
                if (shmartResponse.isSuccess()) {
                    shmartFlow.onDeleteBeneficiarySuccess(shmartResponse.getMessage());
                } else {
                    shmartFlow.onDeleteBeneficiaryFailure(shmartResponse.getMessage());
                }
            } else if (shmartResponse instanceof WalletStatusResponse) {
                shmartFlow.onWalletStatusAvailable(""+shmartResponse.getErrorCode());
            } else if (response instanceof LocalOtp) {
                shmartFlow.onLocalOtp((LocalOtp) response);
            } else if (response instanceof RemoteOtp) {
                shmartFlow.onRemoteOtp((RemoteOtp) response);
            } else if (response instanceof CitrusLogWalletStats) {
                CitrusLogWalletStats citrusLogWalletStats = (CitrusLogWalletStats) response;
                String apiName = citrusLogWalletStats.getApiName();
                switch (apiName) {
                    case QuopnApi.ParamKey.ADDBENEFICIARY:
                    {
                        if (citrusLogWalletStats.isSuccess()) {
                            //success
                            shmartFlow.onBeneficiaryAdded(citrusLogWalletStats.getMessage());
                        } else {
                            shmartFlow.onBeneficiaryNotAdded(citrusLogWalletStats.getError_code_int()
                                    , citrusLogWalletStats.getMessage());
                        }
                    }
                            break;
                    case QuopnApi.ParamKey.GETBENEFICIARYLIST:
                    {
//                        if (citrusLogWalletStats.getError_code().equalsIgnoreCase("000")) {
                            //success
//                            shmartFlow.onBeneficiaryAdded(citrusLogWalletStats.getMessage());
//                        } else {
//                            shmartFlow.onBeneficiaryNotAdded(Integer.parseInt(citrusLogWalletStats.getError_code()),citrusLogWalletStats.getMessage());
//                        }

                        // TODO: cleanup
                        if (!citrusLogWalletStats.getError_code().isEmpty()) {
                            switch (citrusLogWalletStats.getError_code_int()) {
                                case 4:
                                    shmartFlow.onNoBeneficiary();
                                    break;
                                case 0: {
                                    ArrayList<CitrusBeneficiaryData> citrusBeneficiaryDatas = citrusLogWalletStats.getBeneficiary_list();
                                    CitrusBeneficiaryData citrusBeneficiaryData = citrusBeneficiaryDatas.get(0);
                                    shmartFlow.onBeneficiaryAvailable(
                                            citrusBeneficiaryData.getBeneficiary_code()
                                            , citrusBeneficiaryData.getBeneficiary_name()
                                            , citrusBeneficiaryData.getBeneficiary_acc()
                                            , citrusBeneficiaryData.getIfsc_code());
                                }
                                break;
                                default: {
                                    ArrayList<CitrusBeneficiaryData> citrusBeneficiaryDatas = citrusLogWalletStats.getBeneficiary_list();
                                    CitrusBeneficiaryData citrusBeneficiaryData = citrusBeneficiaryDatas.get(0);
                                    shmartFlow.onBeneficiaryAvailable(
                                            citrusBeneficiaryData.getBeneficiary_code()
                                            , citrusBeneficiaryData.getBeneficiary_name()
                                            , citrusBeneficiaryData.getBeneficiary_acc()
                                            , citrusBeneficiaryData.getIfsc_code());
                                }
                                break;
                            }
                        } else {
                            ArrayList<CitrusBeneficiaryData> citrusBeneficiaryDatas = citrusLogWalletStats.getBeneficiary_list();
                            CitrusBeneficiaryData citrusBeneficiaryData = citrusBeneficiaryDatas.get(0);
                            shmartFlow.onBeneficiaryAvailable(
                                    citrusBeneficiaryData.getBeneficiary_code()
                                    , citrusBeneficiaryData.getBeneficiary_name()
                                    , citrusBeneficiaryData.getBeneficiary_acc()
                                    , citrusBeneficiaryData.getIfsc_code());
                        }
                    }
                    break;
                    case QuopnApi.ParamKey.DELETEBENEFICIERY:
                    {
                        if (citrusLogWalletStats.isSuccess()) {
                            shmartFlow.onDeleteBeneficiarySuccess(citrusLogWalletStats.getMessage());
                        } else {
                            shmartFlow.onDeleteBeneficiaryFailure(citrusLogWalletStats.getMessage());
                        }
                    }
                    break;
                    default:
                        break;
                }
            } else if (response instanceof NetworkError) {
                NetworkError error = (NetworkError) response;
//                if (error.getError().networkResponse.statusCode == 302) {
//                    String url = error.getError().networkResponse.headers.get("Location");
//                    shmartFlow.onLoadWalletUrlAvailable(url);
//                }

                if (error != null) {
                    if (error.getError().networkResponse != null) {
                        if (error.getError().networkResponse.statusCode == 302) {
                            String url = error.getError().networkResponse.headers.get("Location");
                            shmartFlow.onLoadWalletUrlAvailable(url);
                            System.out.println("======ShmartresponseTimeOut==3333===");
                        }
                    } else {
                        // timeouyt code
                        System.out.println("======ShmartresponseTimeOut==44444===");
                        //shmartFlow.onTimeOutWallet();

                    }
                }
            }
        }
    }

    @Override
    public void onTimeout(ConnectRequest request) {}

    @Override
    public void myTimeout(String requestTag) {
        if(requestTag.equals("103")){
            shmartFlow.onTimeOutWallet();
        }
    }
}
