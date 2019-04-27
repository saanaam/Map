package com.example.sanamyavarpour.map.View.Fragment;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import com.example.sanamyavarpour.map.MainActivity;

import org.greenrobot.eventbus.EventBus;


import butterknife.ButterKnife;

public abstract class BaseFragment extends Fragment {

    public View rootView;
    protected String TAG = getFragment().getClass().getSimpleName();
    protected Context context;
    private MainActivity mainActivity;;
    protected rx.Subscription subscription;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        //int tabState = getTabState();
        try {
            View view = getMainActivity().getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (Exception e) {

        }


//        if (tabState != -1) {
//            mainActivity.tabChanged(tabState);
//        }
        if (getMainActivity() != null)
            //getMainActivity().checkTab();
            if (rootView != null) {
                return rootView;
            }
//        if (isTab()) {
//            Log.e(TAG, "Inflate View");
//        }
        rootView = inflater.inflate(getLayout(), container, false);
        try {
            ButterKnife.bind(getFragment(), rootView);
        } catch (Exception e) {
            e.printStackTrace();
        }
        init();
        return rootView;
    }


    public abstract
    @NonNull
    BaseFragment getFragment();

    public abstract
    @LayoutRes
    int getLayout();

    protected abstract void init();

    @Override
    public void onStart() {
        super.onStart();
        try {
            EventBus.getDefault().register(getFragment());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            EventBus.getDefault().unregister(getFragment());
        } catch (Exception e) {

        }
    }

    protected Context getBaseContext() {
        return getContext();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof MainActivity)
            mainActivity = (MainActivity) context;
    }

    public MainActivity getMainActivity() {
        return mainActivity;
    }

    protected void finish() {
        mainActivity.onBackPressed();
    }

    protected void finishactivity() {
        ((Activity) context).finish();
    }


}
