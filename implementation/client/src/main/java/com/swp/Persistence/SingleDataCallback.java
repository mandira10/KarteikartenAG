package com.swp.Persistence;

public interface SingleDataCallback <E>{
    void onSuccess(E data);
    void onFailure(String msg);
}

