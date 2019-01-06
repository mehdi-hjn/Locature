package com.ensa.locature.main.internal;

import com.ensa.locature.main.BuildConfig;

import retrofit.RequestInterceptor;

public class ApiClientRequestInterceptor implements RequestInterceptor {

    @Override
    public void intercept(RequestFacade request) {
        request.addHeader("User-Agent", "braintree/android-demo-app/" + BuildConfig.VERSION_NAME);
        request.addHeader("Accept", "application/json");
    }
}
