package com.xyzinnotech.ddn.network.service;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;
import com.xyzinnotech.ddn.network.RemoteServerAPI;
import com.xyzinnotech.ddn.network.model.ActivateUserRequest;
import com.xyzinnotech.ddn.network.model.AuthenticateUserRequest;
import com.xyzinnotech.ddn.network.model.RegisterUserRequest;
import com.xyzinnotech.ddn.network.model.User;

import java.io.File;

import retrofit.Call;

/**
 * Created by KT on 23/12/15.
 */
public class UserAPIServiceImpl implements UserAPIService {

    private final RemoteServerAPI remoteServerAPI;

    public UserAPIServiceImpl(RemoteServerAPI remoteServerAPI) {
        this.remoteServerAPI = remoteServerAPI;
    }

    @Override
    public Call<Void> uploadImage(String fcmId, File file) {
        RequestBody photo = RequestBody.create(MediaType.parse("image/*"), file);
        RequestBody body = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addFormDataPart("file", file.getName(), photo)
                .build();
        return remoteServerAPI.uploadImage(fcmId, photo);
    }

    @Override
    public Call<User> registerUser(RegisterUserRequest request) {
        return remoteServerAPI.registerUser(request);
    }

    @Override
    public Call<User> authenticate(AuthenticateUserRequest request) {
        return remoteServerAPI.authenticate(request);
    }

    @Override
    public Call<Void> activateUser(final ActivateUserRequest request) {
        return remoteServerAPI.activateUser(request);
    }

}
