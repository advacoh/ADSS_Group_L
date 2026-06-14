package service;

public class UserSL {
    private final int userId;
    private final boolean isHR;
    private final boolean isDeliveryManager;

    public UserSL(int userId, boolean isHR, boolean isDeliveryManager) {
        this.userId = userId;
        this.isHR = isHR;
        this.isDeliveryManager = isDeliveryManager;
    }

    public int getUserId() { return userId; }
    public boolean isHR() { return isHR; }
    public boolean isDeliveryManager() { return isDeliveryManager; }
}