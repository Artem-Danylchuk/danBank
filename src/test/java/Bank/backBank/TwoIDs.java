package Bank.backBank;

public class TwoIDs {
    private final long clientsLoginId;
    private final long clientsInformationId;

    public TwoIDs(long clientsLoginId, long clientsInformationId) {
        this.clientsLoginId = clientsLoginId;
        this.clientsInformationId = clientsInformationId;
    }

    public long getClientsLoginId() {
        return clientsLoginId;
    }

    public long getClientsInformationId() {
        return clientsInformationId;
    }
}
