package com.raja.ibm.ocrapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ImageToTextConverter extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("Hello World!");
        // File f = new File("src/main/java/com/raja/wt/u4/sax/ap.PNG");
        ServletFileUpload sf = new ServletFileUpload(new DiskFileItemFactory());
        List<FileItem> multifiles = null;
        try {
            multifiles = sf.parseRequest(req);
        } catch (FileUploadException e1) {
            e1.printStackTrace();
        }
        String fileName = "";
        for (FileItem item : multifiles) {
            try {
                item.write(new File("src/resources/", item.getName()));
                fileName = item.getName();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        File f = new File("src/resources/" + fileName);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(
                "http://max-ocr.codait-prod-41208c73af8fca213512856c7a09db52-0000.us-east.containers.appdomain.cloud/model/predict");
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        // builder.addBinaryBody("image",new FileInputStream(f),
        // ContentType.APPLICATION_OCTET_STREAM, f.getName());
        builder.addBinaryBody("image", new FileInputStream(f), ContentType.APPLICATION_OCTET_STREAM, f.getName());

        httpPost.addHeader("accept", "application/json");
        HttpEntity multipart = builder.build();
        httpPost.setEntity(multipart);
        CloseableHttpResponse response = httpClient.execute(httpPost);
        HttpEntity entity = response.getEntity();
        String jsonData = EntityUtils.toString(entity);
        
        resp.getWriter().println(jsonData);
        f.delete();

    }
}