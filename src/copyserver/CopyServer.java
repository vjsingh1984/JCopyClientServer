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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author vsingh
 */
public class CopyServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        System.out.println("!1!");
        if (null == args || args.length < 1) {
            System.out.println("Usage: java CopyServer port");
            System.out.println(args.length);
            System.exit(1);
        }
        CopyServer service = new CopyServer();
        service.run(Integer.parseInt(args[0]));

    }

    public void run(int port) throws IOException {

        // TODO code application logic here
        BufferedReader bufferedReader = null;
        BufferedOutputStream bufferedOutputStream = null;
        Socket clientSocket = null;

        try {
            ServerSocket listener = new ServerSocket(port);
            while (listener.isBound()) {
                clientSocket = listener.accept();
                clientSocket.setReceiveBufferSize(8192);
                clientSocket.setSendBufferSize(8192);

                bufferedOutputStream = new BufferedOutputStream(clientSocket.getOutputStream());
                bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
                String readInput = bufferedReader.readLine();
                System.out.println("Received:" + readInput);
                if (null != readInput) {

                    File fp = new File(readInput);
                    if (fp.isFile() && fp.exists() && fp.canRead()) {
                        File[] listFiles = fp.listFiles();
                        for (File lfp : listFiles) {
                            copyFile(bufferedOutputStream, lfp);
                        }
                    } else {
                        copyFile(bufferedOutputStream, fp);

                    }
                    if (null != bufferedOutputStream) {
                        bufferedOutputStream.close();
                    }
                    if (null != bufferedReader) {
                        bufferedReader.close();
                    }
                    if (!clientSocket.isClosed()) {
                        clientSocket.close();
                    }
                }
            }
        } finally {
            if (null != bufferedOutputStream) {
                bufferedOutputStream.close();
            }
            if (null != bufferedReader) {
                bufferedReader.close();
            }
            clientSocket.close();

        }
    }

    private static void copyFile(BufferedOutputStream bufferedOutputStream, File fp) {
        int length = 8192;
        byte[] b = new byte[length];
        try {
            if (!fp.canRead()) {
                System.out.println("Cannot Read:" + fp.getAbsolutePath());
            }
            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(fp));
            for (int read = bufferedInputStream.read(b); read >= 0; read = bufferedInputStream.read(b)) {
                bufferedOutputStream.write(b, 0, read);
            }
            bufferedOutputStream.flush();
        } catch (IOException ex) {
            Logger.getLogger(CopyServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
