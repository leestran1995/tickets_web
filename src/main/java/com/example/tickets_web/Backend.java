package com.example.tickets_web;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * This class is a placeholder for the backend manager of the ticketing system. It will handle the following
 * functionalities:
 *
 * Creating tickets
 * Storing tickets
 * Retrieving stored tickets
 * Verifying tickets
 * Marking tickets as used
 */
@RestController
public class Backend {

    /**
     * Take a single ticket and verify whether it is valid
     * <p>
     *     If the ticket exists and has not been used then the ticket is valid. If the ticket
     *     does not exist, it is invalid. If the ticket has already been used (a parameter of the
     *     ticket object itself), it is invalid.
     * </p>
     * @param eventName Name of the event the ticket is being used for
     * @param username Name of the user that generated the tickets for the event
     * @param ticket The data of the ticket itself
     * @return True if valid, False if invalid
     */
    @RequestMapping("/verify_tickets/{username}/{event}/{ticket}")
    public static boolean verifyTicket(@PathVariable("event") String eventName,
                                        @PathVariable("username") String username,
                                        @PathVariable("ticket") byte[] ticket) throws Exceptions.EventNotFoundException{
        Event currentEvent = readEventFromFile(username, eventName);
        if(currentEvent != null) {
            return currentEvent.verifyTicket(ticket);
        }
        else {
            throw new Exceptions.EventNotFoundException("Error reading event information from file");
        }
    }

    /**
     * Verify the refunding of a single ticket.
     * @param username  Name of the user that generated the tickets for the event
     * @param eventname Name of the event the ticket is being used for
     * @param ticket The data of the ticket itself
     * @return True if valid, False if invalid
     */
    @RequestMapping("refund_ticket/{username}/{event}/{ticket}")
    public static boolean refundTicket(@PathVariable("username") String username,
                                       @PathVariable("event") String eventname,
                                       @PathVariable("ticket") byte[] ticket) {
        Event currentEvent = readEventFromFile(username, eventname);
        return currentEvent.refundTicket(ticket);
    }


    /**
     * Given a password, create a single ticket hash using the password as a key.
     * @param password The key to create the ticket hashes, never saved.
     * @return a byte[] array that represents the single ticket
     */
    public static byte[] createHashFromPassword(String password) {
        try {
            byte[] data1 = password.getBytes("UTF-8");

            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] digest = messageDigest.digest(data1);

            return digest;
        } catch (IOException e) {
            System.out.println("Unknown encoding");
        } catch (NoSuchAlgorithmException n) {
            System.out.println("Unknown algorithm");
        }
        return null;
    }

    /**
     * create an array, each entry of which represents a ticket.
     * @param password The key to create the ticket hashes, never saved.
     * @param numTickets The number of tickets to be created
     * @return the array of tickets
     */
    public static Ticket[] createTicketHashes(String password, int numTickets, int startNum) {
        Ticket[] tickets = new Ticket[numTickets];
        for (int i = 0; i < numTickets; i++) {
            String iteratedPassword = password + i;
            byte[] hash = createHashFromPassword(iteratedPassword);

            tickets[i] = new Ticket(hash);
        }
        return tickets;
    }


    // Generate a QR Code Image from 'text' and store to 'filePath'
    private static void generateQRCodeImage(String text, int width, int height, String filePath)
            throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

        Path path = FileSystems.getDefault().getPath(filePath);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
    }

    /**
     * Write an Event object to a file to use later.
     * @param outputPath Path to save the event to
     * @param eventToWrite The Event object we're writing
     * @return True if the file was written successfully, false otherwise.
     */
    private static void saveEventToFile(String outputPath, Event eventToWrite) {
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(outputPath));
            outputStream.writeObject(eventToWrite);
            outputStream.close();
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.toString());
        }
    }

    /**
     * Create and save tickets
     * <p>
     *     Given a password, event name, number, and a username, create a set of tickets for that
     *     event and save it to the database.
     * </p>
     * @param password Only used once and never stored, used for hashing the tickets.
     * @param eventName The name of the event the tickets are for.
     * @param numTickets The number of tickets to be created.
     * @param username Name of the user that is making the tickets, used for naming the database.
     * @return a string indicating whether the tickets were created and saved properly
     */
    @RequestMapping("/create_tickets/{password}/{num_tickets}/{username}")
    public static Event createAndSaveTickets(@PathVariable("password") String password,
                                               @PathVariable("eventName") String eventName,
                                             @PathVariable("num_tickets") int numTickets,
                                             @PathVariable("username") String username){

        String directory_path = "Ticket_Database/" + username;
        boolean make_dirs = (new File(directory_path).mkdirs());

        String outputPath = directory_path + "/" + eventName;
        Ticket[] tickets = createTicketHashes(password, numTickets, 0);
        Event newEvent = new Event(eventName, numTickets, tickets);
        return newEvent;

    }


    /**
     * Get the stored event object from a file.
     * @param username Name of the user who is directing the server to read the file
     * @param eventName Name of the event that is having its tickets get read
     * @return The event object
     */
    private static Event readEventFromFile(String username, String eventName) {
        try {
            String filePath = "Ticket_Database/" + username + "/" + eventName;
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(filePath));
            return (Event) inputStream.readObject();
        } catch (IOException e) {
            System.out.println("Error reading from file: " + e.toString());
            e.printStackTrace();
        } catch (ClassNotFoundException c) {
            System.out.println("Error: " + c.toString());
            c.printStackTrace();
        }
        return null;    // I think maybe this is dangerous
    }

    @RequestMapping("/test")
    public static String test() {
        return "Test successful";
    }

    public static void main(String[] args) {
        createAndSaveTickets("Hello", "concert", 5, "First Ave");
        Event currentEvent = readEventFromFile("First Ave", "concert");
        System.out.println("Tickets sold: " + currentEvent.getTicketsSold());
        try {
            Ticket tic = currentEvent.getNextUnsoldTicket();
        } catch (Exceptions.NoTicketsAvailableException e) {
            e.printStackTrace();
        }
        System.out.println("Tickets sold: " + currentEvent.getTicketsSold());
    }
}