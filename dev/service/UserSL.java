package service;

public class UserSL {
    private final int userId;
    private final boolean isHR;

    public UserSL(int userId, boolean isHR) {
        this.userId = userId;
        this.isHR = isHR;
    }

    public int getUserId() { return userId; }
    public boolean isHR() { return isHR; }
}