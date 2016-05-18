package com.read.pan.app;

import com.read.pan.entity.User;

import org.geometerplus.zlibrary.ui.android.library.ZLAndroidApplication;

import java.util.Properties;
import java.util.Stack;

/**
 * Created by pan on 2016/5/18.
 */
public class ReadApplication extends ZLAndroidApplication{
    private static ReadApplication instance;
    private boolean login;
    private String loginUid;
    private static Stack<String> history=new Stack<String>();
    @Override
    public void onCreate() {
        super.onCreate();
        initLogin();
    }
    private void initLogin() {
        User user = getLoginUser();
        if (null != user && user.getUserId()!=null&&!user.getUserId().equals("")) {
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
//        user.setUserId(getProperty("user.uid"));
//        user.setUserName(getProperty("user.name"));
//        user.setPic(getProperty("user.face"));
//        user.setLocation(getProperty("user.location"));
//        user.setFollowsNum(StringUtils.toInt(getProperty("user.followers"), 0));
//        user.setFansNum(StringUtils.toInt(getProperty("user.fans"), 0));
//        user.setGender(getProperty("user.gender"));
//        user.setEmail(getProperty("user.email"));
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
//                setProperty("user.name", user.getUserName());
//                setProperty("user.face", user.getPic());// 用户头像-文件名
//                setProperty("user.followers",
//                        String.valueOf(user.getFollowsNum()));
//                setProperty("user.fans", String.valueOf(user.getFansNum()));
//                setProperty("user.gender", String.valueOf(user.getGender()));
//                setProperty("user.email",
//                        String.valueOf(user.getEmail()));
//                setProperty("user.location", user.getLocation());
            }
        });
    }
    /**
     * 保存登录信息
     *
     * @param username
     * @param pwd
     */
    @SuppressWarnings("serial")
    public void saveUserInfo(final User user) {
        this.loginUid = user.getUserId();
        this.login = true;
        setProperties(new Properties() {
            {
//                setProperty("user.uid", String.valueOf(user.getUserId()));
//                setProperty("user.name", user.getUserName());
//                setProperty("user.face", user.getPic());// 用户头像-文件名
//                setProperty("user.pwd",
//                        CyptoUtils.encode("letuTravelApp", user.getPassword()));
//                setProperty("user.email",
//                        String.valueOf(user.getEmail()));
//                setProperty("user.followers",
//                        String.valueOf(user.getFollowsNum()));
//                setProperty("user.fans", String.valueOf(user.getFansNum()));
//                setProperty("user.gender", String.valueOf(user.getGender()));
//                setProperty("user.location", user.getLocation());
            }
        });
    }
    /**
     * 清除登录信息
     */
    public void cleanLoginInfo() {
        this.loginUid = "";
        this.login = false;
        removeProperty("user.uid", "user.name","user.pwd","user.face", "user.location",
                "user.followers", "user.fans", "user.gender","user.email",
                "article.articleId","article.numOfday","article.title",
                "history1","history2","history3","history4","history5","articleTitle",
                "articleContent");
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
