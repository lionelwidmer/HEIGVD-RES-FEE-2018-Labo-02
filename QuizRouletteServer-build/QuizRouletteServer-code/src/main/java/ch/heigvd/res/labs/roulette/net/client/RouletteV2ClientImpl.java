package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.ListCommandResponse;

import java.io.IOException;
import java.util.List;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

    @Override
    public void clearDataStore() throws IOException {
        outputStream.writeBytes("CLEAR\n");
        // Throw the response away as the client do not need it
        bufferedReader.readLine();
    }

    @Override
    public List<Student> listStudents() throws IOException {
        List<Student> students = null;
        String fromServer;
        // Call the 'LIST' functionnality
        outputStream.writeBytes("LIST\n");
        // Read the buffered reader until finding what we search for
        while ((fromServer = bufferedReader.readLine()) != null) {
            if (fromServer.contains("\"students\"")) {
                ListCommandResponse list = new JsonObjectMapper().parseJson(fromServer, ListCommandResponse.class);
                students = list.getStudents();
                break;
            }
        }
        return students;
    }

}
