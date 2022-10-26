import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Main {

    public static final String REMOTE_SERVICE_URI = "https://api.nasa.gov/planetary/apod?api_key=aCmnh2Wsdfq52uj3oUDeFh8DAY80B3W6OnJlCkdg";
    public static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setUserAgent("My Test Service")
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)
                        .setSocketTimeout(30000)
                        .setRedirectsEnabled(false)
                        .build())
                .build();

        HttpGet request = new HttpGet(REMOTE_SERVICE_URI);
        request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());

        CloseableHttpResponse response = httpClient.execute(request);
        //Arrays.stream(response.getAllHeaders()).forEach(System.out::println);

        Post post = mapper.readValue(response.getEntity().getContent(), new TypeReference<>() {
        });
        //System.out.println(post);
        System.out.println("URL - " + post.getUrl());

        String remoteFileUri = post.getUrl();
        String[] tokens = remoteFileUri.split("/");
        String fileName = tokens[tokens.length - 1];
        System.out.println("File Name - " + fileName);

        HttpGet getImage = new HttpGet(remoteFileUri);
        CloseableHttpResponse response2 = httpClient.execute(getImage);
        HttpEntity entity = response2.getEntity();

        InputStream is = entity.getContent();
        FileOutputStream fos = new FileOutputStream(fileName);
        int inByte;
        while ((inByte = is.read()) != -1) {
            fos.write(inByte);
        }
        is.close();
        fos.close();

    }

}
