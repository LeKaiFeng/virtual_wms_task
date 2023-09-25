package com.lee.util;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Date: 2021/7/27 10:08
 * @Description: TODO
 */
public class DeviceHttpRequest {

    private static final Logger log = LoggerFactory.getLogger(DeviceHttpRequest.class);

    static int HTTP_PORT = 8090;

    public static String sendRequest(String ip, int type, int deviceId, int req1, String barcode1, int req2, String barcode2) {
        String input = String.format("http://%s:%s/input?type=%s&id=%s&reqType=1&req=%s&barcode=%s&req6=%s&barcode6=%s", ip, HTTP_PORT, type, deviceId, req1, barcode1,
                req2, barcode2);
        String result = null;
        try {
            result = HttpUtil.get(input, CharsetUtil.CHARSET_UTF_8);
        } catch (Exception e) {
            log.info("请求失败 -> {}  {}", input, result);
        }
        return result;
    }

    public static String sendRequest(String ip, int type, int deviceId, int reqType, int req1, String barcode1, int req2, String barcode2) {
        String input = String.format("http://%s:%s/input?type=%s&id=%s&reqType=%s&req=%s&barcode=%s&req6=%s&barcode6=%s",
                ip, HTTP_PORT, type, deviceId, reqType, req1, barcode1, req2, barcode2);
        String result = null;
        try {
            result = HttpUtil.get(input, CharsetUtil.CHARSET_UTF_8);
        } catch (Exception e) {
            log.info("请求失败 -> {}  {}", input, result);
        }
        return result;
    }

    public static String sendRequest(String ip, int type, int deviceId, int req1, String barcode1) {
        String input = String.format("http://%s:%s/input?type=%s&id=%s&reqType=1&req=%s&barcode=%s", ip, HTTP_PORT, type, deviceId, req1, barcode1);
        String result = null;
        try {
            result = HttpUtil.get(input, CharsetUtil.CHARSET_UTF_8);
        } catch (Exception e) {
            log.info("请求失败 -> {}  {}", input, result);
        }
        return result;
    }

    public static String senDslPDRequest(String ip, int type, int deviceId, int req, String barcode, int pdCache, int rollState, int outboundAllow) {
        String input = String.format("http://%s:%s/input?type=%s&id=%s&reqType=1&req=%s&barcode=%s&pdCache=%s&rollState=%s&outboundAllow=%s",
                ip, HTTP_PORT, type, deviceId, req, barcode, pdCache, rollState, outboundAllow);
        String result = null;
        try {
            log.debug("input: {}", input);
            result = HttpUtil.get(input, CharsetUtil.CHARSET_UTF_8);
        } catch (Exception e) {
            log.info("请求失败 -> {}  {}", input, result);
        }
        return result;
    }

    public static String sendDslLiftRequest(String ip, int type, int deviceId, int req, String barcode, int weight, int size, int boxType) {
        String input = String.format("http://%s:%s/input?type=%s&id=%s&reqType=1&req=%s&barcode=%s&weight=%s&size=%s&boxType=%s ", ip, HTTP_PORT, type, deviceId, req, barcode, weight, size, boxType);
        String result = null;
        try {
            log.debug("input: {}", input);
            result = HttpUtil.get(input, CharsetUtil.CHARSET_UTF_8);
        } catch (Exception e) {
            log.info("请求失败 -> {}  {}", input, result);
        }
        return result;
    }

    public static String sendTaskGenerate(String ip, String json) {

        try {
            //String url = String.format("http://%s:%s/GSEE/upper/input", "127.0.0.1", 9000);
            String url = String.format("http://%s:%s/GSEE/upper/input", ip, 9000);

            String result = HttpRequest.post(url)
                    .header("Content-Type", "application/json")
                    .header("appAuthorization", "123456")
                    .header("appUser", "wcs")
                    .body(json)
                    .timeout(10000)
                    .execute().body();

       /* String url = String.format("http://%s:%s/stationPick/pick", ip, port);
        try {
            HttpRequest.head("123456");
            return HttpUtil.post(url, json);
            //log.info("url:{} send：{} rec:{}", url, json, post);
        } catch (Exception e) {
            log.info(e.getMessage());
        }*/
            return result;
        } catch (Exception e) {
            log.info("send to ->>{} fail {} {}", ip, e.getMessage(), e.toString());
        }

        return null;
    }

    public static String sendTaskCJCC(String ip, String json) {

        try {
            //String url = String.format("http://%s:%s/GSEE/upper/input", "127.0.0.1", 9000);
            String url = String.format("http://%s:%s/GSEE/http_wms", ip, 8080);

            String result = HttpRequest.post(url)
                    .header("Content-Type", "application/json")
                    //.header("appAuthorization", "123456")
                    //.header("appUser", "wcs")
                    .body(json)
                    .timeout(10000)
                    .execute().body();
            return result;
        } catch (Exception e) {
            log.info("send to ->>{} fail {} {}", ip, e.getMessage(), e.toString());
        }

        return null;
    }

}
