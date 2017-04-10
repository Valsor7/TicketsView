package com.example.root.myapplication;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Credentials;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by root on 09.04.17.
 */

public class VpnSetter {
    private static final String TAG = "VpnSetter";
    private static final String VPN = "VPN_";
    public static final Set<String> VPN_PROFILE_KEYS = getMappedFields().keySet(); // contains keys for quicker generation of key-value map for each


    private static Map<String, Class<?>> getMappedFields() {
        Map<String, Class<?>> fieldsAndTypes = new HashMap<String, Class<?>>();
        fieldsAndTypes.put("name", String.class);        // 0
        fieldsAndTypes.put("type", int.class);   // 1
        fieldsAndTypes.put("server", String.class);        // 2
        fieldsAndTypes.put("username", String.class);
        fieldsAndTypes.put("password", String.class);
        fieldsAndTypes.put("dnsServers", String.class);
        fieldsAndTypes.put("searchDomains", String.class);
        fieldsAndTypes.put("routes", String.class);
        fieldsAndTypes.put("mppe", boolean.class);
        fieldsAndTypes.put("l2tpSecret", String.class);
        fieldsAndTypes.put("ipsecIdentifier", String.class);
        fieldsAndTypes.put("ipsecSecret", String.class);
        fieldsAndTypes.put("ipsecUserCert", String.class);
        fieldsAndTypes.put("ipsecCaCert", String.class);
        fieldsAndTypes.put("saveLogin", boolean.class);
        return fieldsAndTypes;
    }

    public static void listProfiles(Context context, Object keyStore) {
        Log.d(TAG, "listProfiles: class " + keyStore.getClass());
//        Class[] decodeparameterTypes = new Class[]{String.class, byte[].class};
        Class[] profileparameterTypes = new Class[]{keyStore.getClass(), int[].class};
        List<?> profiles  = new ArrayList<>();
        try {
//            Method methodSaw = keyStore.getClass().getDeclaredMethod("saw", String.class);
//            methodSaw.setAccessible(true);
//            String[] keys = (String[]) methodSaw.invoke(keyStore, VPN);
//
//            Method getInstanceMethod = keyStore.getClass().getDeclaredMethod("get", String.class);
//            getInstanceMethod.setAccessible(true);
//
//            Class<?> vpnProfileClass = Class.forName("com.android.internal.net.VpnProfile");
//            Method decodeMethod = vpnProfileClass.getDeclaredMethod("decode", decodeparameterTypes);
//            decodeMethod.setAccessible(true);
//
//            if (keys != null) {
//                for (String key : keys) {
//                    Object vpnProfileObject = decodeMethod.invoke(new Object[]{
//                                    VPN,
//                                    getInstanceMethod.invoke(keyStore, VPN + key)
//                            });
//                    Log.d(TAG, "listProfiles: " + vpnProfileObject);
//                }
//            }

            Context vpnset = context.createPackageContext("com.android.settings", Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
            Class<?> vpnSettings = Class.forName("com.android.settings.vpn2.VpnSettings", true, vpnset.getClassLoader());

            Method method = vpnSettings.getDeclaredMethod("loadVpnProfiles", profileparameterTypes);
            method.setAccessible(true);
            profiles = (List<?>) method.invoke(null, keyStore, new int[]{});
            Log.d(TAG, "listProfiles: " + profiles);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "listProfiles: ");
    }

    public static void addVpnProfile(Context applicationContext, String vpnProfileKey, Map<String, Object> values) throws ClassNotFoundException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException, PackageManager.NameNotFoundException {
        Context vpnset = applicationContext.createPackageContext("com.android.settings", Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
//        Context vpnnet = applicationContext.createPackageContext("com.android.internal", Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
        Class<?> vpnSettings = Class.forName("com.android.settings.vpn2.VpnSettings", true, vpnset.getClassLoader());

        Class<?>[] privateVpnSettingsClasses = vpnSettings.getDeclaredClasses();
        Class<?> vpnPreference = null;
        Class<?> vpnProfileClass = Class.forName("com.android.internal.net.VpnProfile");
        for (Class<?> priv : privateVpnSettingsClasses) {
            if (priv.getSimpleName().equals("VpnPreference")) {
                vpnPreference = priv;
                break;
            }
        }
        Field vpnProfileFromVpnPreferenceField = vpnPreference.getDeclaredField("mProfile");
        vpnProfileFromVpnPreferenceField.setAccessible(true);
        Object vpnProfile = vpnProfileFromVpnPreferenceField.get(vpnProfileClass);
        Constructor<?> constructor = vpnProfileFromVpnPreferenceField.getClass().getConstructors()[0];
        constructor.setAccessible(true);
        vpnProfile = constructor.newInstance(vpnProfileKey);//creating new instance of VpnProfile class
        Map<String, Class<?>> vpnProfileMap = getMappedFields();
        Iterator<String> profileKeysIterator = vpnProfileMap.keySet().iterator();
        while (profileKeysIterator.hasNext()) {
            String key = profileKeysIterator.next();
            Log.d(TAG, "addVpnProfile: key " + key + "val " + values.get(key));
            Field field = vpnProfile.getClass().getField(key);
            field.setAccessible(true);
            if (vpnProfileMap.get(key).equals(String.class) && values.get(key) != null) {
                String s = new String();
                field.set(s, values.get(key));//change this
            } else if (vpnProfileMap.get(key).equals(boolean.class) && values.get(key) != null) {
                int i = 0;
                field.setInt(i, (int) values.get(key));// change this
            } else if (values.get(key) != null) {
                boolean b = false;
                field.setBoolean(b, (boolean) values.get(key));// change this
            }

        }
        vpnSettings = Class.forName("com.android.settings.vpn.VpnSettings"); //time to add it to settings
        Method addProfileMethod = vpnSettings.getDeclaredMethod("addProfile", vpnProfile.getClass());
        addProfileMethod.setAccessible(true);
        addProfileMethod.invoke(vpnSettings, vpnProfile);
    }
}
