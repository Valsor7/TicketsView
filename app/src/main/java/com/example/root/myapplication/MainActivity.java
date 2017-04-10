package com.example.root.myapplication;

import android.content.pm.PackageManager;
import android.net.Credentials;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Object keyStore = new Object();
        try {
            Class<?> keyStoreClass = Class.forName("android.security.KeyStore");

            Method getInstanceMethod = keyStoreClass.getDeclaredMethod("getInstance");
            getInstanceMethod.setAccessible(true);
            keyStore = getInstanceMethod.invoke(null);
            Method getIsUnlockedMethod = keyStore.getClass().getDeclaredMethod("isUnlocked");
            boolean isUnlocked = (boolean) getIsUnlockedMethod.invoke(keyStore);
            if (isUnlocked){
                VpnSetter.listProfiles(getApplicationContext(), keyStore);
            }
            Log.d(TAG, "onCreate: keystore isUnlocked " + isUnlocked);


            Map<String, Object> fields = new HashMap<>();
            fields.put("name", "MySecond");        // 0
            fields.put("type", 1);   // 1
            fields.put("server", "162.32.62.10");        // 2
            fields.put("username", "test1");
            fields.put("password", "qwerty");
            fields.put("dnsServers", "");
            fields.put("searchDomains", "");
            fields.put("routes", "");
            fields.put("mppe", false);
            fields.put("l2tpSecret", "");
            fields.put("ipsecIdentifier", "");
            fields.put("ipsecSecret", "vpn");
            fields.put("ipsecUserCert", "");
            fields.put("ipsecCaCert", "");
            fields.put("saveLogin", true);

            try {
                VpnSetter.addVpnProfile(getApplicationContext(), "qwerty", fields);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
