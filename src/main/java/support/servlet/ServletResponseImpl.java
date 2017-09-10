package support.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;

/**
 * ServletResponse实现，仅为了测试
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class ServletResponseImpl implements ServletResponse {

    private final Map< String, Object > attributes = new ConcurrentHashMap< String, Object >();

    public Object getAttribute(String name) {
        return attributes.get( name );
    }

    public void setAttribute(String name, Object o) {
        attributes.put( name, o );
    }

    @Override
    public String getCharacterEncoding() {

        return null;
    }

    @Override
    public String getContentType() {

        return null;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {

        return null;
    }

    @Override
    public PrintWriter getWriter() throws IOException {

        return null;
    }

    @Override
    public void setCharacterEncoding(String charset) {

    }

    @Override
    public void setContentLength(int len) {

    }

    @Override
    public void setContentLengthLong(long len) {

    }

    @Override
    public void setContentType(String type) {

    }

    @Override
    public void setBufferSize(int size) {

    }

    @Override
    public int getBufferSize() {

        return 0;
    }

    @Override
    public void flushBuffer() throws IOException {

    }

    @Override
    public void resetBuffer() {

    }

    @Override
    public boolean isCommitted() {

        return false;
    }

    @Override
    public void reset() {

    }

    @Override
    public void setLocale(Locale loc) {

    }

    @Override
    public Locale getLocale() {

        return null;
    }

}
