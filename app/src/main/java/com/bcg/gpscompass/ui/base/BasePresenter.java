package com.bcg.gpscompass.ui.base;

public class BasePresenter<V> {
    protected V mView;

    public void getAttachView(V view) {
        this.mView = view;
    }

    public void getDetachView() {
        this.mView = null;
    }
}
