package com.xgs.dto;

import com.xgs.model.MetaDomain;

/**
 * 标签、分类列表
 * Created by xgs on 2022/11/30.
 */
public class MetaDto extends MetaDomain {

    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
