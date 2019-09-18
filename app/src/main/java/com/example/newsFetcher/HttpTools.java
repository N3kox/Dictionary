package com.example.newsFetcher;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpTools {

    public static String myRequest(String urlString, String codingType) {
        BufferedInputStream bis = null;
        ByteArrayOutputStream bos = null;
        InputStream is = null;
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Accept", "*/*");

            conn.connect();

            int responseCode = conn.getResponseCode();

            if (responseCode == 200) {//ok

                is = conn.getInputStream();
                bis = new BufferedInputStream(is);
                bos = new ByteArrayOutputStream();


                int length = 0;
                byte[] by = new byte[1024];
                while ((length = bis.read(by)) != -1) {
                    bos.write(by, 0, length);
                }
                bos.flush();

                String result = new String(bos.toByteArray(), codingType);

                return result;

            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }

                if (bis != null) {
                    bis.close();
                }

                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                //System.out.println("stream close error！");
            }

        }
        return null;
    }
}
