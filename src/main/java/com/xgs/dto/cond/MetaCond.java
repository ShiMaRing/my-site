package com.xgs.dto.cond;

/**
 * meta查询条件
 * Created by xgs on 2022/11/30.
 */
public class MetaCond {

    /**
     * meta Name
     */
    private String name;
    /**
     * 类型
     */
    private String type;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
