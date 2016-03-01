/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package copyserver;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author vsingh
 */
public class CopyClient {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("!1!");
        if(null == args  || args.length < 2) {
            System.out.println("Usage: java  CopyClient port hostname");
            System.exit(1);
            
        }
        for(int i=0 ; i < args.length;i++) {
            System.out.println("i=" +i +"\targs["+i+"]="+args[i]);
        }
        BufferedReader stdIn = new BufferedReader(
                new InputStreamReader(System.in));
        String userInput = null;

        System.out.println("Copy File Name:");
        userInput = stdIn.readLine();
        Socket clientSocket = new Socket(args[1], Integer.parseInt(args[0]));
        clientSocket.setReceiveBufferSize(8192);
            clientSocket.setSendBufferSize(8192);

        BufferedInputStream bufferedInputStream = new BufferedInputStream(clientSocket.getInputStream());

        PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"),true);
        printWriter.println(userInput);
        //printWriter.close();
        copyFileFromServer(bufferedInputStream, new File(userInput + "X"));

        bufferedInputStream.close();
        clientSocket.close();
    }

    private static void copyFileFromServer(BufferedInputStream bufferedInputStream, File fp) {
        int length = 8192;
        byte [] b = new byte[length];
        try {
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(fp));
            for(int read = bufferedInputStream.read(b);read >=0;read=bufferedInputStream.read(b)) {
                bufferedOutputStream.write(b, 0, read);
            }
            bufferedOutputStream.flush();
        } catch (IOException ex) {
            Logger.getLogger(CopyClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
