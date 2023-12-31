package com.qboxus.tictic.activitesfragments.accounts;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.klinker.android.link_builder.Link;
import com.klinker.android.link_builder.LinkBuilder;
import com.klinker.android.link_builder.TouchableMovementMethod;
import com.qboxus.tictic.activitesfragments.WebviewA;
import com.qboxus.tictic.Constants;
import com.qboxus.tictic.activitesfragments.profile.privatevideos.PrivateVideoF;
import com.qboxus.tictic.models.UserRegisterModel;
import com.qboxus.tictic.R;
import com.qboxus.tictic.apiclasses.ApiLinks;
import com.volley.plus.VPackages.VolleyRequest;
import com.volley.plus.interfaces.Callback;
import com.qboxus.tictic.simpleclasses.Functions;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class PhoneF extends Fragment implements View.OnClickListener {

    View view;
    RelativeLayout mainRlt;
    FrameLayout container;
    String fromWhere;
    EditText phoneEdit;
    UserRegisterModel userRegisterModel;
    Button btnSendCode;
    TextView loginTermsConditionTxt;
    CheckBox chBox;
    RelativeLayout tabTermsCondition;
    String phoneNo;
    CountryCodePicker ccp;
    List<Link> links = new ArrayList<>();

    public PhoneF(UserRegisterModel userRegisterModel, String fromWhere) {
        this.userRegisterModel = userRegisterModel;
        this.fromWhere = fromWhere;
    }

    public PhoneF() {
    }

    public static PhoneF newInstance(UserRegisterModel userRegisterModel, String fromWhere) {
        PhoneF fragment = new PhoneF(userRegisterModel, fromWhere);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_phone, container, false);
        initView();

        // this will get the current country of mobile device
        if (fromWhere != null && fromWhere.equals("login")) {

            tabTermsCondition.setVisibility(View.GONE);

        }

        SetupScreenData();
        return view;
    }


    private void SetupScreenData() {

        Link link = new Link(view.getContext().getString(R.string.terms_of_use));
        link.setTextColor(ContextCompat.getColor(view.getContext(),R.color.black));
        link.setTextColorOfHighlightedLink(ContextCompat.getColor(view.getContext(),R.color.appColor));
        link.setUnderlined(true);
        link.setBold(false);
        link.setHighlightAlpha(.20f);
        link.setOnClickListener(new Link.OnClickListener() {
            @Override
            public void onClick(String clickedText) {
                openWebUrl(view.getContext().getString(R.string.terms_of_use),Constants.terms_conditions);
            }
        });

        Link link2 = new Link(view.getContext().getString(R.string.privacy_policy));
        link2.setTextColor(ContextCompat.getColor(view.getContext(),R.color.black));
        link2.setTextColorOfHighlightedLink(ContextCompat.getColor(view.getContext(),R.color.appColor));
        link2.setUnderlined(true);
        link2.setBold(false);
        link2.setHighlightAlpha(.20f);
        link2.setOnClickListener(new Link.OnClickListener() {
            @Override
            public void onClick(String clickedText) {
                openWebUrl(view.getContext().getString(R.string.privacy_policy),Constants.privacy_policy);
            }
        });
        links.add(link);
        links.add(link2);
        CharSequence sequence = LinkBuilder.from(view.getContext(), loginTermsConditionTxt.getText().toString())
                .addLinks(links)
                .build();
        loginTermsConditionTxt.setText(sequence);
        loginTermsConditionTxt.setMovementMethod(TouchableMovementMethod.getInstance());
    }

    public void openWebUrl(String title, String url) {

        Intent intent=new Intent(view.getContext(), WebviewA.class);
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);

    }



    // this will initialize all the views
    private void initView() {

        chBox = view.findViewById(R.id.chBox);
        tabTermsCondition = view.findViewById(R.id.tabTermsCondition);
        loginTermsConditionTxt = view.findViewById(R.id.login_terms_condition_txt);
        container = view.findViewById(R.id.container);
        mainRlt = view.findViewById(R.id.main_rlt);
        phoneEdit = view.findViewById(R.id.phone_edit);
        btnSendCode = view.findViewById(R.id.btn_send_code);
        btnSendCode.setOnClickListener(this);
        chBox.setOnClickListener(this);

        phoneEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int count) {
                String txtName = phoneEdit.getText().toString();
                if (loginTermsConditionTxt.getCurrentTextColor()==ContextCompat.getColor(view.getContext(),R.color.redColor))
                {
                    loginTermsConditionTxt.setError(null);
                    loginTermsConditionTxt.setTextColor(ContextCompat.getColor(view.getContext(),R.color.darkgray));
                }
                if (txtName.length() > 0) {
                    btnSendCode.setEnabled(true);
                    btnSendCode.setClickable(true);
                } else {
                    btnSendCode.setEnabled(false);
                    btnSendCode.setClickable(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ccp = view.findViewById(R.id.ccp);
        ccp.setCountryForNameCode(ccp.getDefaultCountryNameCode());
        ccp.registerPhoneNumberTextView(phoneEdit);
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chBox:
            {
                if (loginTermsConditionTxt.getCurrentTextColor()==ContextCompat.getColor(view.getContext(),R.color.redColor))
                {
                    loginTermsConditionTxt.setError(null);
                    loginTermsConditionTxt.setTextColor(ContextCompat.getColor(view.getContext(),R.color.darkgray));
                }
            }
                break;
            case R.id.btn_send_code:
                if (checkValidation()) {

                    if (!ccp.isValid()) {
                        phoneEdit.setError(view.getContext().getString(R.string.invalid_phone_number));
                        phoneEdit.setFocusable(true);
                        return;
                    }

                    phoneNo= phoneEdit.getText().toString();
                    phoneNo=Functions.applyPhoneNoValidation(phoneNo,ccp.getSelectedCountryCodeWithPlus());

                    if (!(fromWhere != null && fromWhere.equals("login")))
                    {
                        if (!(chBox.isChecked()))
                        {
                            loginTermsConditionTxt.setError(view.getContext().getString(R.string.please_confirm_terms_and_condition));
                            loginTermsConditionTxt.setTextColor(ContextCompat.getColor(view.getContext(),R.color.redColor));
                            return;
                        }
                    }


                    callApiOtp();
                }

                break;
        }
    }

    public boolean checkValidation() {

        final String st_phone = phoneEdit.getText().toString();

        if (TextUtils.isEmpty(st_phone)) {
            Toast.makeText(getActivity(), view.getContext().getString(R.string.please_enter_phone_number), Toast.LENGTH_SHORT).show();
            return false;
        }


        return true;
    }

    private void callApiOtp() {

        JSONObject parameters = new JSONObject();
        try {

            parameters.put("phone", phoneNo);
            parameters.put("verify", "0");
        } catch (
                Exception e) {
            e.printStackTrace();
        }

        Functions.showLoader(getActivity(), false, false);
        VolleyRequest.JsonPostRequest(getActivity(), ApiLinks.verifyPhoneNo, parameters,Functions.getHeadersWithOutLogin(getActivity()), new Callback() {
            @Override
            public void onResponce(String resp) {
                Functions.checkStatus(getActivity(),resp);
                Functions.cancelLoader();
                parseLoginData(resp);
            }
        });


    }

    // if api return the ok responce then open the get opt screen
    public void parseLoginData(String loginData) {
        try {
            JSONObject jsonObject = new JSONObject(loginData);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                Functions.showToast(getActivity(), jsonObject.optString("msg"));
                PhoneOtpF phoneOtp_f = new PhoneOtpF();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                Bundle bundle = new Bundle();
                String phone_no = phoneEdit.getText().toString();
                bundle.putString("phone_number", phoneNo);
                userRegisterModel.phoneNo = phone_no;
                bundle.putSerializable("user_data", userRegisterModel);
                phoneOtp_f.setArguments(bundle);
                transaction.addToBackStack(null);
                transaction.replace(R.id.sign_up_fragment, phoneOtp_f).commit();

            } else {
                Functions.showToast(getActivity(), jsonObject.optString("msg"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}