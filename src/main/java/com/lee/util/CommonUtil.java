package com.lee.util;

import cn.hutool.core.util.RandomUtil;

import java.util.List;

public class CommonUtil {

    public static <T> T randomFromList(List<T> list) {
        return list.get(RandomUtil.randomInt(list.size()));
    }

    public static int getTimes(int createNum, int aisleNum) {
        int num = 1;
        if (createNum < aisleNum) {
            return num;
        } else {
            if (createNum % aisleNum == 0) {
                return createNum / aisleNum;
            } else {
                return createNum / aisleNum + 1;
            }
        }
    }

    public static int posPre(int pos) {
        if (pos < 100000 && pos > 100) {
            return pos;
        }
        return pos / 100;
    }
}
