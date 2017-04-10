package Server;

/**
 * Created by michael-setnyk on 24/03/17.
 */
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.io.*;
import java.net.*;


/**
 * This Class is instantiated when a new client
 * connects the server
 */
public class ChatServerThread extends Thread {
    protected Socket clientSocket = null;
    protected PrintWriter out = null;
    protected BufferedReader in = null;
    private boolean inUse;
    private static String SERVER_LOCATION;
    private  ObservableList<ChatServerThread> activeclients = FXCollections.observableArrayList();
    private int clientID;

    /**
     * constructor initalizes the client socket
     * and the in and out streams
     * @param clientSocket the socket that the Server classes passes in
     * @param SERVER_LOCATION where the server files are
     * @param activeclients all ChatServerThreads connected the server
     */


    public ChatServerThread(Socket clientSocket, String SERVER_LOCATION,ObservableList<ChatServerThread> activeclients) {
        super();
        this.activeclients=activeclients;
        this.clientSocket = clientSocket;
        this.SERVER_LOCATION = SERVER_LOCATION;
        inUse =false;
        try {
            out = new PrintWriter(this.clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
        } catch (IOException e) {
            System.err.println("IOEXception while opening a read/write connection");
        }

        /**
         * when there is a change in active clients
         * out puts the active clients to each logged in client
         */
        activeclients.addListener(new ListChangeListener<ChatServerThread>() {
            @Override
            public void onChanged(Change<? extends ChatServerThread> c) {
                activeClient();
            }
        });
    }

    /**
     * keeps looping as long as the connection remains open
     */
    public void run() {
        // initialize interaction
        boolean endOfSession = false;
        while (!endOfSession) {
            endOfSession = listen();
        }
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * returns true or false if the sockets is closed or open
     * @return boolean
     */
    public boolean socketClosed() {
        if (clientSocket.isClosed())
            return true;
        return false;
    }

    /**
     * returns true or false depending on given command
     * returns true if CMD is READ_EMPLOYEES OR UPLOAD
     * @return boolean
     */
    private boolean listen() {
        String cmd;
        try {
            cmd = in.readLine();
            while (cmd==null) {
                if (clientSocket.isClosed())
                    in = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
                cmd = in.readLine();
            }
                inUse = true;
                return processCommand(cmd);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * required to make sure that cmds do not happen out of order and inputs get lost
     * @param cmd cmd being checked
     * @return
     */
    private boolean processCommand(String cmd) {
        try {

            if (cmd.equalsIgnoreCase("READ_EMPLOYEES")) {
                inUse=true;
                returnEmployeeList();
                inUse = false;
                return true;
            } else if (cmd.equalsIgnoreCase("MESSAGE")) {
                inUse=true;
                uploadMessage(in.readLine());
                inUse = false;
                return false;
            } else if (cmd.equalsIgnoreCase("ADD_ID")) {
                inUse=true;
                addID(in.readLine());
                inUse = false;
                return false;
            } else if (cmd.equalsIgnoreCase("REFRESH")) {
                inUse=true;
                activeClient();
                inUse=false;
                return false;
            } else if (cmd.equalsIgnoreCase("ADD_EMPLOYEE")) {
                inUse=true;
                addEmployee();
                inUse=false;
                return true;
            }
            else if (cmd.equalsIgnoreCase("UPLOAD")) {
                inUse=true;
                out.println("UPLOAD");
                String[] inputs = in.readLine().split(",");
                String fName =inputs[0];
                String id = inputs[1];
                UPLOAD(6,fName,id);
                inUse=false;
                return true;
            } else if (cmd.equalsIgnoreCase("CHANGE_NAME")) {
                inUse=true;
                out.println("CHANGE_NAME");
                String[] inputs = in.readLine().split(",");
                String name = inputs[0];
                String id = inputs[1];
                changeEmployeeFile(2,name,id);
                inUse=false;
                return false;
            } else if (cmd.equalsIgnoreCase("CHANGE_NUMBER")) {
                inUse=true;
                out.println("CHANGE_NUMBER");
                String[] inputs = in.readLine().split(",");
                String number = inputs[0];
                String id = inputs[1];
                changeEmployeeFile(4,number,id);
                inUse=false;
                return false;
            } else if (cmd.equalsIgnoreCase("CHANGE_ROLE")) {
                inUse=true;
                out.println("CHANGE_ROLE");
                String[] inputs = in.readLine().split(",");
                String role = inputs[0];
                String id = inputs[1];
                changeEmployeeFile(0,role,id);
                inUse=false;
                return false;
            } else if (cmd.equalsIgnoreCase("CHANGE_EMAIL")) {
                inUse=true;
                out.println("CHANGE_EMAIL");
                String[] inputs = in.readLine().split(",");
                String email = inputs[0];
                String id = inputs[1];
                changeEmployeeFile(5,email,id);
                inUse=false;
                return false;
            }
            else {
                return false;
            }
        }catch (IOException e){e.printStackTrace();}
        return false;
    }

    /**
     * add and employee to the employee file for sign up
     */
    private void addEmployee(){

        try {
            FileWriter fw = new FileWriter("resources/ServerFiles/EmployeeList.csv", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);
            String newEmployee=in.readLine()+",";
            for (int i =0;i<=4;i++) {
            newEmployee+= in.readLine()+",";
            }
            String id[] =newEmployee.split(",");
            newEmployee+="resources/LocalImages/basicProfilePic.jpg,TaskList/"+id[1]+".csv,";
            newEmployee+= in.readLine();
            pw.println(newEmployee);
            pw.close();
            fw.close();
            bw.close();

            //create new Task File
            //String taskListPath="resources/ServerFiles/TaskList/"+id[1]+".csv";
            String taskListPath ="resources/TaskList/"+id[1]+".csv";
            fw = new FileWriter(taskListPath);
            bw = new BufferedWriter(fw);
            pw = new PrintWriter(bw);
            String header ="number,status,task";
            pw.println(header);
            pw.close();
            fw.close();
            bw.close();
            }
        catch (IOException e) {
            System.out.println("<add valid employee list>");
        }


    }

    /**
     * uploads file image to server client image location
     * @param i the token being checked
     * @param fileName the field (name, email, image, role)
     * @param id the id of the employee
     */
    private void UPLOAD(int i,String fileName,String id)
    {
        File newfile = new File("resources/ServerFiles/ServerClientImages"+"/"+fileName);
        File serverImagePath = new File("resources/ServerFiles/ServerClientImages");

        serverImagePath.mkdirs();
        //create a new file
        try {
            DataInputStream dIn = new DataInputStream(clientSocket.getInputStream());
            int length = dIn.readInt();                    // read length of incoming message
            if (length > 0) {
                byte[] message = new byte[length];
                dIn.readFully(message, 0, message.length); // read the message
                FileOutputStream stream = new FileOutputStream(newfile);
                try {
                    stream.write(message);
                } finally {
                    stream.close();
                }
            }
            changeEmployeeFile(i,"resources/LocalImages/"+fileName, id);
        }catch (IOException e)
    {e.printStackTrace();}
        }

    /**
     * uses the id to check determine which line to alter
     * change the employee file
     * @param i the token in the employee file
     * @param field the field (name, email, image, role)
     * @param id the id of the employee
     */
        private void changeEmployeeFile(int i,String field, String id){
        try{
            String originalPath="resources/ServerFiles/EmployeeList.csv";
            String tmpPath="resources/ServerFiles/EmployeeListTMP.csv";
            FileReader fileReader = new FileReader(originalPath);
            BufferedReader input = new BufferedReader(fileReader);
            String line;

        //creates a temporary file
        FileWriter fw = new FileWriter(tmpPath, false);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter pw = new PrintWriter(bw);

        while ((line = input.readLine()) != null)
        {
            String[] tokens = line.split(",");
            if(tokens[1].equals(id))
            {
                line="";
                tokens[i] = field;
                for (String t: tokens)
                   line+=t+",";
            }
            pw.println(line);

        }
            input.close();
            pw.close();
            fw.close();
            bw.close();
            //temp file created
            //copy tmp file into original one and delete tmp file
             fileReader = new FileReader(tmpPath);
             input = new BufferedReader(fileReader);

             //write to original file
            fw = new FileWriter(originalPath, false);
            bw = new BufferedWriter(fw);
            pw = new PrintWriter(bw);
            while ((line = input.readLine()) != null)
            {
              pw.println(line);
            }
            input.close();
            pw.close();
            fw.close();
            bw.close();

            File tmpFile = new File(tmpPath);
            tmpFile.delete();

        }catch (IOException e)
    {e.printStackTrace();}

        }

    /**
     * adds id to the the chat serverThread to match client id
     * @param id
     */
    private void addID(String id){
           clientID = Integer.parseInt(id);
    }

    /**
     * prints active clients to each of the logged in clients
     */
    private void activeClient(){
                String ids ="";
        for (ChatServerThread i:activeclients)
        {ids +=i.clientID+",";
        }

        for (ChatServerThread s: activeclients){
            s.out.println("CHANGE");
            s.out.println(ids);
            s.out.flush();
        }
    }

    /**
     * uploads message to each logged in client
     * @param message message being sent
     */
    private void uploadMessage(String message){
            for (ChatServerThread s: activeclients){
                s.out.println("MESSAGE");
                s.out.println(message);
                s.out.flush();

            }
    }

    /**
     * sends all the employees in the server location
     * over a sockets to fill each client's global server list
     * @return
     */
    private boolean returnEmployeeList() {

        try {
            out = new PrintWriter(this.clientSocket.getOutputStream(), true);
            FileReader fileReader = new FileReader("resources/ServerFiles/EmployeeList.csv");
            BufferedReader input = new BufferedReader(fileReader);
            out.println("READ_EMPLOYEES");

            String line = input.readLine();
            //reads the employee list
            while ((line = input.readLine()) != null) {
                String[] tokens = line.split(",");
                for (int a = 0; a <= 8; a++) {
                    out.println(tokens[a]);
                    out.flush();
                }
            }

        } catch (IOException e) {
            System.out.println("<add valid employee list>");
        }
        return true;
    }

    /**
     * ideally it would fill the clients local task list
     * @param id id that has to be filled
     */
    private void readTaskList(String id) {
        String status;
        try {
            String taskLocation = "resources/ServerFiles/TaskList/" + id + ".csv";
            FileReader fileReader = new FileReader(taskLocation);
            BufferedReader input = new BufferedReader(fileReader);
            String line = input.readLine();//remove header
            while ((line = input.readLine()) != null) {
                String[] tokens = line.split(",");

                status = tokens[1];
                if (status.equalsIgnoreCase("complete")) {
                    out.println(tokens[0]);
                    out.flush();
                    out.println("1");
                    out.flush();
                    out.println(tokens[1]);
                    out.flush();

                } else {
                    out.println(tokens[0]);
                    out.flush();
                    out.println("0");
                    out.flush();
                    out.println(tokens[1]);
                    out.flush();
                }
                out.close();
            }

        } catch (IOException e) {
            System.out.println("<add valid taskList>");
        }
    }
}