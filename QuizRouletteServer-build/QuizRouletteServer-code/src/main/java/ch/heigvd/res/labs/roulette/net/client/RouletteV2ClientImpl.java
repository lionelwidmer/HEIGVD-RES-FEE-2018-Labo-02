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
        //bufferedReader.readLine();
    }

    @Override
    public List<Student> listStudents() throws IOException {
        List<Student> students = null;
        String fromServer;
        ListCommandResponse list = new ListCommandResponse();
        outputStream.writeBytes("LIST\n");
        while ((fromServer = bufferedReader.readLine()) != null) {
            if (fromServer.contains("\"students\"")) {
                list = new JsonObjectMapper().parseJson(bufferedReader.readLine(), ListCommandResponse.class);
                students = list.getStudents();
                break;
            }
            System.out.println("################################## Received " + fromServer);

        }

        return students;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
