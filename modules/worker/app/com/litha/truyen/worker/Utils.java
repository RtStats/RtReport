package com.litha.truyen.worker;

import jodd.http.HttpBrowser;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;

public class Utils {

    private static String userAgent = "Mozilla/5.0 (Windows NT 5.1; rv:5.0) Gecko/20100101 Firefox/5.0";

    public static String fetchUrl(String url) {
        HttpBrowser browser = new HttpBrowser();
        try {
            HttpRequest request = HttpRequest.get(url).header("User-Agent", userAgent);
            HttpResponse response = browser.sendRequest(request);
            try {
                return browser.getPage();
            } finally {
                response.close();
            }
        } finally {
            browser.close();
        }
    }
}
