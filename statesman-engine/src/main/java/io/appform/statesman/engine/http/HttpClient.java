package io.appform.statesman.engine.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * @author shashank.g
 */
@Slf4j
@AllArgsConstructor
public class HttpClient {

    private static final MediaType APPLICATION_JSON = MediaType.parse("application/json");
    private static final MediaType APPLICATION_PDF = MediaType.parse("application/pdf");

    public final ObjectMapper mapper;
    public final OkHttpClient client;

    public Response post(String url,
                         final Object payload,
                         final Map<String, String> headers) throws IOException {
        final HttpUrl httpUrl = HttpUrl.get(url);
        Request.Builder postBuilder;
        if(payload instanceof String) {
             postBuilder =  new Request.Builder()
                     .url(httpUrl)
                     .post(RequestBody.create(APPLICATION_JSON, (String)payload));
        }
        else {
            postBuilder = new Request.Builder()
                    .url(httpUrl)
                    .post(RequestBody.create(APPLICATION_JSON, mapper.writeValueAsBytes(payload)));
        }
        if (headers != null) {
            headers.forEach(postBuilder::addHeader);
        }
        final Request request = postBuilder.build();
        return client.newCall(request).execute();
    }

    public Response get(final String url,
                        final Map<String, String> headers) throws IOException {
        final HttpUrl httpUrl = HttpUrl.get(url);
        final Request.Builder getBuilder = new Request.Builder()
                .url(httpUrl)
                .get();
        if (headers != null) {
            headers.forEach(getBuilder::addHeader);
        }
        final Request request = getBuilder.build();
        return client.newCall(request).execute();
    }

    public Response form(final String url, final byte[] file,
                              Map<String, String> payload,
                              final Map<String, String> fileMeta,
                              final Map<String, String> headers) throws IOException {
        final HttpUrl httpUrl = HttpUrl.get(url);
        MultipartBody.Builder formBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);

        if(payload != null) {
            payload.forEach(formBuilder::addFormDataPart);
        }
        if(fileMeta != null) {
            for(String key : fileMeta.keySet())
                formBuilder.addFormDataPart(key, fileMeta.get(key),
                        RequestBody.create(APPLICATION_PDF, file));
        }
        Request.Builder postBuilder = new Request.Builder()
                .url(httpUrl)
                .post(formBuilder.build());
        if (headers != null) {
            headers.forEach(postBuilder::addHeader);
        }
        final Request request = postBuilder.build();
        return client.newCall(request).execute();
    }

    public static void main(String[] args) throws IOException {
        OkHttpClient client = new OkHttpClient();
//        Request request = new Request.Builder().url("https://cdn-api.co-vin.in/api/v2/registration/certificate/download?beneficiary_reference_id=28702059198440")
//                .addHeader("accept","application/json")
//                .addHeader("authorization","Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiI2MWYyZjUwZC00MjdjLTRkZWMtYTdlYS02MTViMmU3MTllMjIiLCJ1c2VyX2lkIjoiNjFmMmY1MGQtNDI3Yy00ZGVjLWE3ZWEtNjE1YjJlNzE5ZTIyIiwidXNlcl90eXBlIjoiQkVORUZJQ0lBUlkiLCJtb2JpbGVfbnVtYmVyIjo4OTYyODM5MjU3LCJiZW5lZmljaWFyeV9yZWZlcmVuY2VfaWQiOjE5NTYyNTU0MzMzMzcsInNvdXJjZSI6InByb2plY3RzdGVwb25lcHJvZCIsInVhIjoiTW96aWxsYSIsImRhdGVfbW9kaWZpZWQiOiIyMDIyLTAxLTIzVDA4OjIyOjA0Ljc4NloiLCJpYXQiOjE2NDI5MjYxMjQsImV4cCI6MTY0MjkyNzAyNH0.KGVjUNT9zRXDiAGScq4MeXSSVW5a2Hf0ayQDe55eQ6E")
////                .addHeader("x-api-key","uN0Jrhntsg2rvKcuPFwoP7001yIJxQQ251gl1uL7")
//                .get().build();
////        File file = new File("/Users/sandeepan.baidya/Desktop/LIC2020.pdf");
        Path pdfPath = Paths.get("/Users/sandeepan.baidya/Desktop/LIC2020.pdf");
        byte[] response = Files.readAllBytes(pdfPath);
//        byte[] response =  client.newCall(request).execute().body().bytes();
        Map<String, String> headers = new HashMap<>();
        headers.put("api-key","A1d84dec5f9a069708b6339cc7b5fed9c");
        headers.put("Content-Type","text/plain");
        upload("https://api.kaleyra.io/v1/HXAP1680610568IN/messages", response);
    }


    public static void upload(String url, byte[] file) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("media", "random.pdf",
                        RequestBody.create(MediaType.parse("application/pdf"), file))
                .addFormDataPart("from", "919745697456")
                .addFormDataPart("to", "918962839257")
                .addFormDataPart("type", "mediatemplate")
                .addFormDataPart("channel", "whatsapp")
                .addFormDataPart("template_name", "cowin_pdf")
                .build();
        Request request = new Request.Builder().url(url).post(formBody)
                .addHeader("api-key","A1d84dec5f9a069708b6339cc7b5fed9c")
                .addHeader("Content-Type","text/plain")
                .build();
        Response response = client.newCall(request).execute();
        assert response.body() != null;
        System.out.println(response.body().string());
    }
}
