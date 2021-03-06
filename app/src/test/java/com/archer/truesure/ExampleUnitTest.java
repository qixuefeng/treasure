package com.archer.truesure;

import com.archer.truesure.net.NetOkHttpClient;
import com.archer.truesure.user.UserPres;
import com.archer.truesure.user.register.RegisterInfo;
import com.google.gson.Gson;

import org.junit.Test;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static org.junit.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {

    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    /**
     * 测试网络
     */
    @Test
    public void testNet() {

        OkHttpClient okHttpClient = NetOkHttpClient.getInstance().getOkHttpClient();


        Gson gson = new Gson();
        String content = gson.toJson(new RegisterInfo("xuehseng", "ljakshdk"));

        RequestBody body = RequestBody.create(MediaType.parse("tr"), content);

        Request request = new Request.Builder()
                .url(NetOkHttpClient.APP_URL+"/Handler/UserHandler.ashx?action=login")
                .post(body)
                .build();

        Call call = okHttpClient.newCall(request);

        Response execute;
        String string = null;
        try {
            execute = call.execute();
            string = execute.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertEquals("", string);

    }

    @Test
    public void testSubstring() {
        String url = "/UpLoad/HeadPic/f683f88dc9d14b648ad5fcba6c6bc840_0_1.png";

        String sub = url.substring(url.lastIndexOf("/") + 1, url.length());

        assertEquals("f683f88dc9d14b648ad5fcba6c6bc840_0_1.png", sub);

    }

    @Test
    public void testUrl() {

        String url = UserPres.getString(UserPres.HEAD_PIC_URL);
        assertEquals("f683f88dc9d14b648ad5fcba6c6bc840_0_1.png", url);

    }

}
