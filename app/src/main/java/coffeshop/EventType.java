package coffeshop;

public enum EventType {
    Registered("R"), Processing("P"), Done("D"), Canceled("C"), Out("O");


    EventType(String type) {
        this.type = type;
    }

    String type;

    public static EventType get(String type) throws IllegalArgumentException {
        switch(type) {
            case "R":
                return Registered;
            case "P":
                return Processing;
            case "D":
                return Done;
            case "C":
                return Canceled;
            case "O":
                return Out;
            default:
                throw new IllegalArgumentException(String.format("No such event type: %s", type));
        }
    }

    public String getType() {
        return type;
    }

}
