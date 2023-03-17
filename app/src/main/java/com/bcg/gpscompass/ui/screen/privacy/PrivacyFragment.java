package com.bcg.gpscompass.ui.screen.privacy;

import static com.bcg.gpscompass.utils.Constants.PREF_FIRST_OPEN_KEY;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import com.bcg.gpscompass.MainActivity;
import com.bcg.gpscompass.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PrivacyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PrivacyFragment extends Fragment implements View.OnClickListener {

    private AppCompatTextView mTvPolicy;
    private AppCompatButton mBtnAccept;

    private SharedPreferences prefs;

    public PrivacyFragment() {
        // Required empty public constructor
    }

    public static PrivacyFragment newInstance() {
        PrivacyFragment fragment = new PrivacyFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = requireActivity().getPreferences(Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_privacy, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBtnAccept = view.findViewById(R.id.btn_accept);
        mTvPolicy = view.findViewById(R.id.tv_policy);
        mBtnAccept.setOnClickListener(this);
        mTvPolicy.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_accept:
                prefs.edit().putBoolean(PREF_FIRST_OPEN_KEY, false).apply();
                ((MainActivity) requireActivity()).showCompassUI();
                break;
            case R.id.tv_policy:
                //TODO open link policy
                break;
        }
    }
}