package ch.heigvd.res.labs.roulette.net.protocol;

/**
 * This class is used to serialize/deserialize the response sent by the server
 * when processing the "LOAD" command defined in the protocol specification. The
 * JsonObjectMapper utility class can use this class.
 *
 * @author Lionel Widmer
 */
public class LoadCommandResponse {

    private String status;

    private int numberOfStudents;

    public LoadCommandResponse (int before, int after) {
        this.numberOfStudents = (after - before);
        if (this.numberOfStudents > 0) {
            this.status = "success";
        } else {
            this.status = "failure";
        }
    }

    public String getStatus() {
        return this.status;
    }

    public int getNumberOfStudents() {
        return this.numberOfStudents;
    }
}