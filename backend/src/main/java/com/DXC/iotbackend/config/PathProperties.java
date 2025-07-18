package com.dxc.iotbackend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "")
public class PathProperties {

    // === Base paths ===
    private String apiBasePath;

    // === Sensor paths ===
    private String sensorTrafficPath;
    private String sensorLightPath;
    private String sensorAirPath;
    private String sensorGeneralPath;
    private String sensorWithAlert;
    private String senorLocation;

    // === Alert settings ===
    private String alertSettingsPath;
    private String alertFiltering;

    // === Auth paths ===
    private String authRegister;
    private String authLogin;
    private String authProfile;
    private String authVerifyPassword;
    private String authPasswordNull;
    private String authUpdatePassword;
    private String authForgotPassword;
    private String authResetPassword;
    private String authOauthSuccess;
    private String authLogout;
    private String authUpdateProfile;
    private String authUpdatePhoto;

    // === Getters and Setters ===

    public String getApiBasePath() {
        return apiBasePath;
    }

    public void setApiBasePath(String apiBasePath) {
        this.apiBasePath = apiBasePath;
    }

    public String getSensorTrafficPath() {
        return sensorTrafficPath;
    }

    public void setSensorTrafficPath(String sensorTrafficPath) {
        this.sensorTrafficPath = sensorTrafficPath;
    }

    public String getSensorLightPath() {
        return sensorLightPath;
    }

    public void setSensorLightPath(String sensorLightPath) {
        this.sensorLightPath = sensorLightPath;
    }

    public String getSensorAirPath() {
        return sensorAirPath;
    }

    public void setSensorAirPath(String sensorAirPath) {
        this.sensorAirPath = sensorAirPath;
    }

    public String getSensorGeneralPath() {
        return sensorGeneralPath;
    }

    public void setSensorGeneralPath(String sensorGeneralPath) {
        this.sensorGeneralPath = sensorGeneralPath;
    }

    public String getSensorWithAlert() {
        return sensorWithAlert;
    }

    public void setSensorWithAlert(String sensorWithAlert) {
        this.sensorWithAlert = sensorWithAlert;
    }

    public String getSenorLocation() {
        return senorLocation;
    }

    public void setSenorLocation(String senorLocation) {
        this.senorLocation = senorLocation;
    }

    public String getAlertSettingsPath() {
        return alertSettingsPath;
    }

    public void setAlertSettingsPath(String alertSettingsPath) {
        this.alertSettingsPath = alertSettingsPath;
    }

    public String getAlertFiltering() {
        return alertFiltering;
    }

    public void setAlertFiltering(String alertFiltering) {
        this.alertFiltering = alertFiltering;
    }

    public String getAuthRegister() {
        return authRegister;
    }

    public void setAuthRegister(String authRegister) {
        this.authRegister = authRegister;
    }

    public String getAuthLogin() {
        return authLogin;
    }

    public void setAuthLogin(String authLogin) {
        this.authLogin = authLogin;
    }

    public String getAuthProfile() {
        return authProfile;
    }

    public void setAuthProfile(String authProfile) {
        this.authProfile = authProfile;
    }

    public String getAuthVerifyPassword() {
        return authVerifyPassword;
    }

    public void setAuthVerifyPassword(String authVerifyPassword) {
        this.authVerifyPassword = authVerifyPassword;
    }

    public String getAuthPasswordNull() {
        return authPasswordNull;
    }

    public void setAuthPasswordNull(String authPasswordNull) {
        this.authPasswordNull = authPasswordNull;
    }

    public String getAuthUpdatePassword() {
        return authUpdatePassword;
    }

    public void setAuthUpdatePassword(String authUpdatePassword) {
        this.authUpdatePassword = authUpdatePassword;
    }

    public String getAuthForgotPassword() {
        return authForgotPassword;
    }

    public void setAuthForgotPassword(String authForgotPassword) {
        this.authForgotPassword = authForgotPassword;
    }

    public String getAuthResetPassword() {
        return authResetPassword;
    }

    public void setAuthResetPassword(String authResetPassword) {
        this.authResetPassword = authResetPassword;
    }

    public String getAuthOauthSuccess() {
        return authOauthSuccess;
    }

    public void setAuthOauthSuccess(String authOauthSuccess) {
        this.authOauthSuccess = authOauthSuccess;
    }

    public String getAuthLogout() {
        return authLogout;
    }

    public void setAuthLogout(String authLogout) {
        this.authLogout = authLogout;
    }

    public String getAuthUpdateProfile() {
        return authUpdateProfile;
    }

    public void setAuthUpdateProfile(String authUpdateProfile) {
        this.authUpdateProfile = authUpdateProfile;
    }

    public String getAuthUpdatePhoto() {
        return authUpdatePhoto;
    }

    public void setAuthUpdatePhoto(String authUpdatePhoto) {
        this.authUpdatePhoto = authUpdatePhoto;
    }
}