package com.xyzinnotech.ddn.network.service;

import com.xyzinnotech.ddn.network.model.ActivateUserRequest;
import com.xyzinnotech.ddn.network.model.AuthenticateUserRequest;
import com.xyzinnotech.ddn.network.model.RegisterUserRequest;
import com.xyzinnotech.ddn.network.model.User;

import java.io.File;

import retrofit.Call;

/**
 * Created by KT on 23/12/15.
 */
public interface UserAPIService {

    Call<Void> uploadImage(String fcmId, File file);

    Call<User> registerUser(RegisterUserRequest request);

    Call<User> authenticate(AuthenticateUserRequest request);

    Call<Void> activateUser(ActivateUserRequest request);

}
