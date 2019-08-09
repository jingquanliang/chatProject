package websocket;

public class Constants {
    public static final String SESSION_USER="user";
    public static final String TOKEN_KEY="2";
    public static final String WEBSOCKET_USER_KEY="3";

    public static int isAdminOnline=0;

    public int getIsAdminOnline() {
        return isAdminOnline;
    }

    public void setIsAdminOnline(int isAdminOnline) {
        synchronized (TOKEN_KEY){
            this.isAdminOnline = isAdminOnline;
        }
    }
}
