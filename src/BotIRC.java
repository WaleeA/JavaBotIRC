import java.net.*;
import java.text.*;
import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.math.*;
import java.time.*;



/*
 * Bot runs given a specified ip, port, nick, user, and channel.
 * I have implemented IRC commands such as JOIN, NICK, USER, PRIVMSG
 */
public class BotIRC {
    //declare all variables needed for bot
    private BufferedWriter bWrite;
    private Socket socket;
    private InetAddress server   = InetAddress.getLocalHost();;
    private final int port;
    private String botOput  = "Hello";
    private String userIput;
    private String nick = "nickname";
    private String user = "JavaIRCBot 1 * :JavaIRCBot";
    private String channel  = "#chat";


    /**
     * Initialise a new client. To run the client, call run().
     * This function connects the bot to he port of the network
     */

    public BotIRC(int port) throws UnknownHostException {
        this.port = port;
    }

    /**SendMsg function is used to send messages from bot to user.
    *Str holds the string to be sent and BufferedWriter bw allows the movement.*/
    public void sendMsg(BufferedWriter bw, String str) {
        try {
            bw.write(str + "\r\n");
            bw.flush();
        }
        catch (Exception e) {
            System.out.println("Exception: "+e);
        }
    }

    /**
     *recieveMsg collects the message sent to the user receiveMsg collects the message sent to user
     * and reads user input from IRC Server
     */

    public void recieveMsg(BufferedReader br){
        try {
            userIput = br.readLine();
            System.out.println(userIput);
        }
        catch (Exception e) {
            System.out.println("Exception: "+e);
        }
    }


    /**
     * This function returns time which we use for !time command
     * StrDate is what is returned to the file for when the command is called and needed to be printed
     */

    public String getTime(){
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        String strDate = dateFormat.format(date);
        return strDate;
    }


    //Main running function for the bot
    public void run() throws IOException, InterruptedException{

        System.out.println("BotClient connecting to " + server + ": " + port);

        // Initialize socket and Botcommand objects
        Socket socket = new Socket(server,port);


        // Use the input and output stream directly
        InputStream inputS = socket.getInputStream();
        OutputStream outputS = socket.getOutputStream();


        // Open OutputStreamWriter to write to a channel
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputS);

        System.out.println("Opened BufferedWriter.");
        BufferedWriter bWrite = new BufferedWriter(outputStreamWriter);


        //Open InputStreamReader to take commands from the user's inputs
        InputStreamReader inputStreamReader = new InputStreamReader(inputS);

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);



        // Bot connects to specified channel
        sendMsg(bWrite,"NICK "+nick);
        sendMsg(bWrite,"USER "+ user);
        sendMsg(bWrite,"JOIN "+channel);
        sendMsg(bWrite,"PRIVMSG "+channel+" :"+ botOput);
        bWrite.flush();


        while (true) {  // The client loops until interrupted

            // If there is not a line's worth of data...
            while (inputS.available() < 16) {
                Thread.sleep(1000);  // wait 1 second
            }

            // An array of byte to hold the packet of data being used
            byte packet[] = new byte[512];

            // Read it in
            inputS.read(packet, 0, packet.length);


            //Packet is converted into a string and outputted for the user
            String str1 = new String(packet);
            System.out.println(str1);


            //List of Commands the Bot can perform while running.

            // !hello command. User inputs !hello and the bot greets user back
            if (str1.contains("PRIVMSG "+ channel+ " :!hello")) {
                sendMsg(bWrite,"PRIVMSG "+channel+" :"+ "#Hello Human");
                bWrite.flush();
            }

            //!time command. User inputs !time and bot responds with current time
            if (str1.contains("PRIVMSG "+ channel+ " :!time")) {
                sendMsg(bWrite,"PRIVMSG "+channel+" :"+ "The time is " + getTime());
                bWrite.flush();
            }

            // !restart command. user inputs !restart and bot restarts itself
            if (str1.contains("PRIVMSG "+ channel+ " :!restart")) {
                sendMsg(bWrite,"RESTART" );
                bWrite.flush();
            }
        }
    }

    // Main function for Java file
    public static void main(String args[]) throws IOException, InterruptedException {
        //Error messages thrown if port is not usable or invalid
        String usage = "Usage: java BotIRC [<port-number>] ";
        if (args.length > 1) {
            throw new Error(usage);
        }

        
        int port = 0x1A0B;

        try {
            if (args.length > 0) {
                port = Integer.parseInt(args[0]);
            }
        } catch (NumberFormatException e) {
            throw new Error(usage + "\n" + "<port-number> must be an integer");
        }
        //If valid then Port number is used for the bot and is run
        BotIRC client = new BotIRC(port);
        client.run();

    }
}