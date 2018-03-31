package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 1).
 *
 * @author Olivier Liechti
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {
    private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());
    // Instance variables
    Socket socket;
    private DataOutputStream outputStream;
    private BufferedReader bufferedReader;

    @Override
    public void connect(String server, int port) throws IOException {
        // Iniatialize instance objects
        socket = new Socket(server, port);
        outputStream = new DataOutputStream(socket.getOutputStream());
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public void disconnect() throws IOException {
        socket.close();
    }

    @Override
    public boolean isConnected() {
        /*
        If we are not connected, there is a high probability that the socket object is null, that's why we test
        first if socket is not null (to avoid NullPointerException, then, if the socket is not null, we ask it if
        it is connected or not
        */
        return (socket != null && socket.isConnected());
    }

    @Override
    public void loadStudent(String fullname) throws IOException {
        // Call the LOAD function
        outputStream.writeBytes("LOAD\n");
        outputStream.writeBytes(fullname + "\nENDOFDATA\n");
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {
        /*
        Instead of rewrinting the function core, we could have simply called the loadStudent() method for each
        studen. But in case we want to load huge number of students, it's faster to involve only one time the LOAD
        function and put all students in it, instead of calling many many times the LOAD function
        */
        // Call the LOAD function
        outputStream.writeBytes("LOAD\n");
        // Write name of each students from the list, on a new line each
        for (Student s : students) {
            outputStream.writeBytes(s.getFullname() + "\n");
        }
        // Write ENDOFDATA and add new line character to submit the load request
        outputStream.writeBytes("ENDOFDATA\n");
    }

    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {
        String fromServer;
        JsonObjectMapper mapper = new JsonObjectMapper();
        Student s = new Student();
        // Call the RANDOM function
        outputStream.writeBytes("RANDOM\n");
        // Loop through all lines returned by the server and treat only lines that contain "error" or "fullname"
        while ((fromServer = bufferedReader.readLine()) != null) {
            // Treat only lines that contain "error" or "fullname"
            if (fromServer.contains("\"error\"") || fromServer.contains("\"fullname\"")) {
                // Store JSON output in RandomCommandResponse object
                RandomCommandResponse response = new JsonObjectMapper().parseJson(fromServer, RandomCommandResponse.class);
                // If the student store is empty, throw an exception
                if (response.getError().length() != 0) { throw new EmptyStoreException(); }
                // If the store is not empty, we continue and get his / her name
                s = new Student(response.getFullname());
                break;
            }
        }
        // Return Student object
        return s;
    }

    @Override
    public int getNumberOfStudents() throws IOException {
        String fromServer;
        int nbStudents = 0;
        // Call the INFO function
        outputStream.writeBytes("INFO\n");
        // Loop through all lines received from server
        while ((fromServer = bufferedReader.readLine()) != null) {
            // When line "protocolVersion" is received, we can get the number of students
            if (fromServer.contains("\"protocolVersion\"")) {
                InfoCommandResponse infoCommandResponse = new JsonObjectMapper().parseJson(fromServer, InfoCommandResponse.class);
                nbStudents = infoCommandResponse.getNumberOfStudents();
                break;
            }
        }
        return nbStudents;
    }

    @Override
    public String getProtocolVersion() throws IOException {
        // This function is more or less the same than getNumberOfStudents() except that it returns a String instead of an int
        String fromServer;
        outputStream.writeBytes("INFO\n");
        String protocolVersion = new String();
        while ((fromServer = bufferedReader.readLine()) != null) {
            if (fromServer.contains("\"protocolVersion\"")) {
                InfoCommandResponse infoCommandResponse = new JsonObjectMapper().parseJson(fromServer, InfoCommandResponse.class);
                protocolVersion = infoCommandResponse.getProtocolVersion();
                break;
            }
        }
        return protocolVersion;
    }


}
