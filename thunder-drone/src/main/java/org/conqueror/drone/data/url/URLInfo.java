package org.conqueror.drone.data.url;

import org.conqueror.common.exceptions.serialize.SerializableException;
import org.conqueror.common.utils.file.FileUtils;
import org.conqueror.lion.message.ThunderMessage;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;


public class URLInfo implements ThunderMessage {

    private static final URLInfo empltyInstance = new URLInfo();

    private final int id;
    private final String domain;
    private final String url;
    private final int depth;

    public URLInfo() {
        this(0, null, null, 0);
    }

    public URLInfo(String domain, String url, int depth) {
        this(0, domain, url, depth);
    }

    public URLInfo(int id, String domain, String url, int depth) {
        this.id = id;
        this.domain = domain;
        this.url = url;
        this.depth = depth;
    }

    public static URLInfo getEmpltyInstance() {
        return empltyInstance;
    }

    public int getId() {
        return id;
    }

    public String getDomain() {
        return domain;
    }

    public String getUrl() {
        return url;
    }

    public int getDepth() {
        return depth;
    }

    public static boolean isURI(String uri) {
        try {
            new URI(uri);
            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }

    public boolean equalsDomain(String uri) {
        try {
            return new URI(uri).getHost().equals(domain);
        } catch (Exception e) {
            return false;
        }
    }

    public static String normalize(String uri, boolean includeFragment) {
        try {
            URI normUri = new URI(uri).normalize();

            String params = normalizeParams(normUri);
            return normalize(normUri, params, includeFragment);
        } catch (URISyntaxException e) {
            return null;
        }
    }

    private static String normalizeParams(URI uri) {
        Map<String, String> params = new HashMap<>();
        String query = uri.getQuery();
        if (query == null || query.length() == 0) return null;

        for (String param : query.split("&")) {
            if (param.length() > 0) {
                String[] kv = param.split("=");
                if (kv.length == 2 && kv[0].length() > 0 && kv[1].length() > 0) {
                    params.put(kv[0], kv[1]);
                }
            }
        }
        List<String> paramKeys = new ArrayList<>(params.keySet());
        paramKeys.sort(String::compareTo);
        StringBuilder paramString = new StringBuilder();
        for (String key : paramKeys) {
            if (paramString.length() > 0) paramString.append('&');
            paramString.append(key).append('=').append(params.get(key));
        }
        return paramString.toString();
    }

    private static String normalize(URI uri, String param, boolean includeFragment) {
        // scheme:[//[user:password@]host[:port]][/]path[?query][#fragment]
        StringBuilder sb = new StringBuilder();
        if (uri.getScheme() != null) {
            sb.append(uri.getScheme());
            sb.append(':');
        }
        if (uri.isOpaque()) {
            sb.append(uri.getRawSchemeSpecificPart());
        } else {
            if (uri.getHost() != null) {
                sb.append("//");
                if (uri.getUserInfo() != null) {
                    sb.append(uri.getUserInfo());
                    sb.append('@');
                }
                boolean needBrackets = ((uri.getHost().indexOf(':') >= 0)
                    && !uri.getHost().startsWith("[")
                    && !uri.getHost().endsWith("]"));
                if (needBrackets) sb.append('[');
                sb.append(uri.getHost());
                if (needBrackets) sb.append(']');
                if (uri.getPort() != -1) {
                    sb.append(':');
                    sb.append(uri.getPort());
                }
            } else if (uri.getAuthority() != null) {
                sb.append("//");
                sb.append(uri.getAuthority());
            }
            if (uri.getPath() != null) {
                sb.append(uri.getPath());
                if (!FileUtils.getFileName(uri.getPath()).contains(".") && sb.charAt(sb.length() - 1) != '/') {
                    sb.append('/');
                }
            }
            if (param != null) {
                sb.append('?');
                sb.append(param);
            }
        }
        if (includeFragment && uri.getFragment() != null) {
            sb.append('#');
            sb.append(uri.getFragment());
        }
        return sb.toString();
    }

    @Override
    public void writeObject(DataOutput output) throws SerializableException {
        try {
            output.writeInt(getId());
            output.writeUTF(getDomain());
            output.writeUTF(getUrl());
            output.writeInt(getDepth());
        } catch (IOException e) {
            throw new SerializableException(e);
        }
    }

    @Override
    public URLInfo readObject(DataInput input) throws SerializableException {
        try {
            return new URLInfo(input.readInt(), input.readUTF(), input.readUTF(), input.readInt());
        } catch (IOException e) {
            throw new SerializableException(e);
        }
    }

    @Override
    public String toString() {
        return "URLInfo{" +
            "domain='" + domain + '\'' +
            ", url='" + url + '\'' +
            ", depth=" + depth +
            '}';
    }

}
