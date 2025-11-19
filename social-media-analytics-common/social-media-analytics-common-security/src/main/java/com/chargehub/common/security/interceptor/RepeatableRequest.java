package com.chargehub.common.security.interceptor;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;

/**
 * @author Zhanghaowei
 * @date 2025/08/21 16:08
 */
@Slf4j
public class RepeatableRequest extends HttpServletRequestWrapper {


    private final String body;

    public RepeatableRequest(HttpServletRequest request) {
        super(request);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        String copyBody;
        try {
            ServletInputStream input = request.getInputStream();
            StreamUtils.copy(input, output);
            copyBody = output.toString(CharsetUtil.UTF_8);
        } catch (IOException ioException) {
            log.warn(" [Vei] request copy exception {}", ioException.getMessage());
            copyBody = null;
        }
        this.body = copyBody;
    }

    @Override
    public ServletInputStream getInputStream() {
        return new RepeatableInputStream(body);
    }

    @Override
    public BufferedReader getReader() {
        return IoUtil.getUtf8Reader(this.getInputStream());
    }

    public String getBody() {
        return body;
    }


    public static class RepeatableInputStream extends ServletInputStream {

        private final InputStream sourceStream;
        private boolean finished = false;

        public RepeatableInputStream(@NonNull String body) {
            this.sourceStream = new ByteArrayInputStream(body.getBytes());
        }

        public RepeatableInputStream(InputStream sourceStream) {
            Assert.notNull(sourceStream, "Source InputStream must not be null");
            this.sourceStream = sourceStream;
        }

        public final InputStream getSourceStream() {
            return this.sourceStream;
        }

        @Override
        public int read() throws IOException {
            int data = this.sourceStream.read();
            if (data == -1) {
                this.finished = true;
            }

            return data;
        }

        @Override
        public int available() throws IOException {
            return this.sourceStream.available();
        }

        @Override
        public void close() throws IOException {
            super.close();
            this.sourceStream.close();
        }

        @Override
        public boolean isFinished() {
            return this.finished;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            throw new UnsupportedOperationException();
        }
    }

}