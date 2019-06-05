package com.samplayer.interceptor.ex;

public class InterceptorException extends Exception {

    private int mErrorCode = -1;

    public InterceptorException(int errorCode) {
        mErrorCode = errorCode;
    }

    public int getErrorCode() {
        return mErrorCode;
    }

    @Override
    public String toString() {
        return "InterceptorException{" +
                "mErrorCode=" + mErrorCode +
                '}';
    }
}
