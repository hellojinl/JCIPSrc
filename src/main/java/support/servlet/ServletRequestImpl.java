package support.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * ServletRequest实现，只是用于测试
 *
 * @author Jin Lei Stormborn, the Unburnt, King of of Meereen, King of the
 *         Andals and the Rhoynar and the First Men, Lord of the Seven Kingdoms,
 *         Protector of the Realm, Caho of the Great Grass Sea, Breaker of
 *         Shackles, Father of Dragons.
 */
public class ServletRequestImpl implements ServletRequest {

    private final Map< String, Object > attributes = new ConcurrentHashMap< String, Object >();

    @Override
    public Object getAttribute(String name) {
        return attributes.get( name );
    }

    @Override
    public void setAttribute(String name, Object o) {
        attributes.put( name, o );
    }

    @Override
    public Enumeration< String > getAttributeNames() {
        return null;
    }

    @Override
    public String getCharacterEncoding() {
        return null;
    }

    @Override
    public void setCharacterEncoding(String env) throws UnsupportedEncodingException {

    }

    @Override
    public int getContentLength() {
        return 0;
    }

    @Override
    public long getContentLengthLong() {
        return 0;
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return null;
    }

    @Override
    public String getParameter(String name) {
        return null;
    }

    @Override
    public Enumeration< String > getParameterNames() {
        return null;
    }

    @Override
    public String[] getParameterValues(String name) {
        return null;
    }

    @Override
    public Map< String, String[] > getParameterMap() {
        return null;
    }

    @Override
    public String getProtocol() {
        return null;
    }

    @Override
    public String getScheme() {
        return null;
    }

    @Override
    public String getServerName() {
        return null;
    }

    @Override
    public int getServerPort() {
        return 0;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return null;
    }

    @Override
    public String getRemoteAddr() {
        return null;
    }

    @Override
    public String getRemoteHost() {
        return null;
    }

    @Override
    public void removeAttribute(String name) {

    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public Enumeration< Locale > getLocales() {
        return null;
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        return null;
    }

    @Override
    public String getRealPath(String path) {
        return null;
    }

    @Override
    public int getRemotePort() {
        return 0;
    }

    @Override
    public String getLocalName() {
        return null;
    }

    @Override
    public String getLocalAddr() {
        return null;
    }

    @Override
    public int getLocalPort() {
        return 0;
    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        return null;
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse)
            throws IllegalStateException {
        return null;
    }

    @Override
    public boolean isAsyncStarted() {
        return false;
    }

    @Override
    public boolean isAsyncSupported() {
        return false;
    }

    @Override
    public AsyncContext getAsyncContext() {
        return null;
    }

    @Override
    public DispatcherType getDispatcherType() {
        return null;
    }

}
