package ch.heigvd.res.labs.roulette.net.protocol;

/**
 * This class is used to serialize/deserialize the response sent by the server
 * when processing the "BYE" command defined in the protocol specification. The
 * JsonObjectMapper utility class can use this class.
 *
 * @author Lionel Widmer
 */
public class ByeCommandResponse {

    private String status;

    private int numberOfCommands;

    public ByeCommandResponse (int numberOfCommands) {
        this.numberOfCommands = numberOfCommands;
        this.status = "success";
    }

    public String getStatus() {
        return this.status;
    }

    public int getNumberOfCommands() {
        return this.numberOfCommands;
    }
}