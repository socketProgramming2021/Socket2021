package Http;

public enum StatusCode {

    /**
     * 请求成功
     */
    SUCCESS(200, "OK"),
    /**
     * 永久移动。请求的资源已被永久的移动到新URI，返回信息会包括新的URI，
     * 浏览器会自动定向到新URI。
     *  今后任何新的请求都应使用新的URI代替
     */
    MOVED_PERMANENTLY(301, "Moved Permanently"),
    /**
     *临时移动。与301类似。但资源只是临时被移动。客户端应继续使用原有URI
     */
    MOVED_TEMPORARILY(302, "Moved Temporarily"),
    /**
     * 未修改。所请求的资源未修改，服务器返回此状态码时，不会返回任何资源。
     * 客户端通常会缓存访问过的资源，
     * 通过提供一个头信息指出客户端希望只返回在指定日期之后修改的资源
     */
    NOT_MODIFIED(304, "Not Modified"),
    /**
     * 请求要求用户的身份认证
     */
    UNAUTHORIZED(401, "Unauthorized"),
    /**
     * 服务器无法根据客户端的请求找到资源（网页）。通过此代码，
     * 网站设计人员可设置"您所请求的资源无法找到"的个性页面
     */
    NOT_FOUND(404, "Not Found"),
    /**
     * 客户端请求中的方法被禁止
     */
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    /**
     * 服务器内部错误，无法完成请求
     */
    INTERNAL_SERVER_ERROR(500,"Internal Server Error");

    public final int code;
    public final String description;

    StatusCode(int code, String description){
        this.code = code;
        this.description = description;
    }

    public static StatusCode getStatusCode(int code){
        switch (code){
            case 200:
                return SUCCESS;
            case 301:
                return MOVED_PERMANENTLY;
            case 302:
                return MOVED_TEMPORARILY;
            case 304:
                return NOT_MODIFIED;
            case 404:
                return NOT_FOUND;
            case 405:
                return METHOD_NOT_ALLOWED;
            case 500:
                return INTERNAL_SERVER_ERROR;
            default:
                return null;
        }
    }
}
