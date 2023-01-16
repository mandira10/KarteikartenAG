package com.swp.Controller;

import java.util.List;

public interface DataCallback <E>
{
    void onSuccess(List<E> data);
    void onFailure(String msg);
    void onInfo(String msg);
}