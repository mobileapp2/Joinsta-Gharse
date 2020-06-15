package in.oriange.joinstagharse.utilities;

import android.app.ProgressDialog;
import android.content.Context;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.oriange.joinstagharse.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static in.oriange.joinstagharse.utilities.ApplicationConstants.NUMVERIFY_ACCESS_TOKEN;

public class MobileNumberVerification {

    private static Context mContext;

    public MobileNumberVerification(Context context) {
        mContext = context;
    }

    public boolean isMobileNumberValid( String countryCode, String mobile) {
        if (countryCode.equals("+91")) {
            return isValidMobileno(mobile);
        } else {
            return verifyByApi(countryCode + mobile);
        }
    }

    private static boolean verifyByApi(String number) {
        final boolean[] isMobileNumValid = {false};
        ProgressDialog pd = new ProgressDialog(mContext, R.style.CustomDialogTheme);
        pd.setMessage("Please wait ...");
        pd.setCancelable(false);
        pd.show();

        OkHttpClient client = new OkHttpClient();
        List<ParamsPojo> param = new ArrayList<ParamsPojo>();
        param.add(new ParamsPojo("access_key", NUMVERIFY_ACCESS_TOKEN));
        param.add(new ParamsPojo("number", number));
        param.add(new ParamsPojo("format", "1"));

        HttpUrl.Builder urlBuilder = HttpUrl.parse(ApplicationConstants.NUMVERIFYAPI).newBuilder();

        for (int i = 0; i < param.size(); i++)
            urlBuilder.addQueryParameter(param.get(i).getParam_Key(),
                    param.get(i).getParam_Value());

        String url = urlBuilder.build().toString();

        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                try {
                    pd.dismiss();
                    if (!result.equals("")) {
                        JSONObject jsonObject = new JSONObject(result);
                        isMobileNumValid[0] = jsonObject.getBoolean("valid");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    isMobileNumValid[0] = Utilities.isValidMobileno(number);
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                pd.dismiss();
                isMobileNumValid[0] = Utilities.isValidMobileno(number);
            }
        });

        return isMobileNumValid[0];

    }

    private static boolean isValidMobileno(String mobileno) {
        String Mobile_PATTERN = "^[6-9]{1}[0-9]{9}$";
        Pattern pattern = Pattern.compile(Mobile_PATTERN);
        Matcher matcher = pattern.matcher(mobileno);
        return matcher.matches();
    }


}
