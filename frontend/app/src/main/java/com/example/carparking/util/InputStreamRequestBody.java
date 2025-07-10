package com.example.carparking.util;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.Okio;

public class InputStreamRequestBody extends RequestBody {
    private final MediaType contentType;
    private final InputStream inputStream;

    public InputStreamRequestBody(String contentType, InputStream inputStream) {
        this.contentType = MediaType.parse(contentType);
        this.inputStream = inputStream;
    }

    @Override
    public MediaType contentType() {
        return contentType;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        sink.writeAll(Okio.source(inputStream));
    }
}
