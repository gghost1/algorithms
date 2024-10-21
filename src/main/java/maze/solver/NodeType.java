package maze.solver;

public enum NodeType {
    START,
    DANGER,
    AGENT,
    Sentinel,
    KEY,
    END;

    public static NodeType of(String value) {
        switch (value) {
            case "P":
                return NodeType.DANGER;
            case "A":
                return NodeType.AGENT;
            case "S":
                return NodeType.Sentinel;
            case "B":
                return NodeType.KEY;
            case "K":
                return NodeType.END;
            default:
                return null;
        }
    }
}
