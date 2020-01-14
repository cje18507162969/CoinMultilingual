package com.coin.market.jpush;

import java.io.Serializable;

/**
 * @author: cjh
 * @date: 2019/11/4
 * @info:
 */
public class JPushModel implements Serializable {

    private String id;
    private String type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
