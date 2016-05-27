package com.read.pan.app;

import com.read.pan.entity.User;
import com.read.pan.util.CyptoUtils;

import org.geometerplus.zlibrary.ui.android.library.ZLAndroidApplication;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * Created by pan on 2016/5/18.
 */
public class ReadApplication extends ZLAndroidApplication{
    private static ReadApplication instance;
    private boolean login;
    private String loginUid;
    @Override
    public void onCreate() {
        super.onCreate();
//        Fresco.initialize(getApplicationContext());
        initLogin();
    }
    private void initLogin() {
        User user = getLoginUser();
        if (null != user && user.getUserId()!=null) {
            login = true;
            loginUid = user.getUserId();
        } else {
            this.cleanLoginInfo();
        }
    }
    /**
     * 获得登录用户的信息
     * @return
     */
    public User getLoginUser() {
        User user = new User();
        user.setUserId(getProperty("user.uid"));
        user.setUserName(getProperty("user.name"));
        String gender=getProperty("user.gender");
        if(gender!=null)
            user.setGender(Integer.parseInt(gender));
        SimpleDateFormat formt=new SimpleDateFormat("yyyyMMdd");
        String birthday=getProperty("user.birthday");
        if(birthday!=null){
            Date date= null;
            try {
                date = formt.parse(birthday);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            user.setBirthday(date);
        }
        user.setPhone(getProperty("user.phone"));
        user.setSaying(getProperty("user.saying"));
        user.setPass(getProperty("user.pwd"));
        user.setAvatar(getProperty("user.face"));
        user.setEmail(getProperty("user.email"));
        return user;
    }
    /**
     * 更新用户信息
     *
     * @param user
     */
    @SuppressWarnings("serial")
    public void updateUserInfo(final User user) {
        setProperties(new Properties() {
            {
                setProperty("user.uid",user.getUserId());
                setProperty("user.name", user.getUserName());
                setProperty("user.gender", String.valueOf(user.getGender()));
                if(user.getBirthday()!=null){
                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date=user.getBirthday();
                    String birthday=sdf.format(date);
                    setProperty("user.birthday", birthday);
                }
                if(user.getPhone()!=null)
                    setProperty("user.phone", String.valueOf(user.getPhone()));
                if(user.getSaying()!=null)
                    setProperty("user.saying",
                            String.valueOf(user.getSaying()));
                setProperty("user.pwd",
                        CyptoUtils.encode("Read", user.getPass()));
                if(user.getAvatar()!=null)
                    setProperty("user.face", user.getAvatar());
                if(user.getEmail()!=null)
                    setProperty("user.email",
                            String.valueOf(user.getEmail()));
            }
        });
    }
    /**
     * 保存登录信息
     *
     */
    public void saveUserInfo(final User user) {
        this.loginUid = user.getUserId();
        this.login = true;
        setProperties(new Properties() {
            {
                setProperty("user.uid",user.getUserId());
                setProperty("user.name", user.getUserName());
                setProperty("user.gender", String.valueOf(user.getGender()));
                if(user.getBirthday()!=null){
                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date=user.getBirthday();
                    String birthday=sdf.format(date);
                    setProperty("user.birthday", birthday);
                }
                if(user.getPhone()!=null)
                    setProperty("user.phone", String.valueOf(user.getPhone()));
                if(user.getSaying()!=null)
                    setProperty("user.saying",
                            String.valueOf(user.getSaying()));
                setProperty("user.pwd",
                        CyptoUtils.encode("Read", user.getPass()));
                if(user.getAvatar()!=null)
                    setProperty("user.face", user.getAvatar());
                if(user.getEmail()!=null)
                    setProperty("user.email",
                            String.valueOf(user.getEmail()));
            }
        });
    }
    /**
     * 清除登录信息
     */
    public void cleanLoginInfo() {
        this.loginUid = "";
        this.login = false;
        removeProperty("user.uid", "user.name","user.pwd","user.face",
                "user.gender","user.email","user.birthday",
                "user.phone","user.saying");
    }
    /**
     * 用户注销
     */
    public void Logout() {
        cleanLoginInfo();
        this.login = false;
        this.loginUid ="";
    }
    /**
     * 获得当前app运行的AppContext
     *
     * @return
     */
    public static ReadApplication getInstance() {
        return instance;
    }

    public boolean containsProperty(String key) {
        Properties props = getProperties();
        return props.containsKey(key);
    }

    public void setProperties(Properties ps) {
        AppConfig.getAppConfig(this).set(ps);
    }

    public Properties getProperties() {
        return AppConfig.getAppConfig(this).get();
    }

    public void setProperty(String key, String value) {
        AppConfig.getAppConfig(this).set(key, value);
    }

    /**
     * 获取cookie时传AppConfig.CONF_COOKIE
     *
     * @param key
     * @return
     */
    public String getProperty(String key) {
        String res = AppConfig.getAppConfig(this).get(key);
        return res;
    }

    public void removeProperty(String... key) {
        AppConfig.getAppConfig(this).remove(key);
    }
    public String getLoginUid() {
        return loginUid;
    }

    public boolean isLogin() {
        return login;
    }
    public static boolean isFristStart() {
//        return getPreferences().getBoolean(KEY_FRITST_START, true);
        return false;
    }

    public static void setFristStart(boolean frist) {
//        set(KEY_FRITST_START, frist);
    }
}
