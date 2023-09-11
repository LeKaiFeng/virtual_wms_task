package com.lee.util;

import cn.hutool.core.util.RandomUtil;

import java.util.List;

public class CommonUtil {

    public static <T> T randomFromList(List<T> list) {
        return list.get(RandomUtil.randomInt(list.size()));
    }

}
