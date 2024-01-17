package com.example;

import java.io.*;
import java.net.*;
import java.nio.file.Files;

public class App {
    public static void main(String[] args) {
        try {
            System.out.println("Server started and waiting for connections...");
            ServerSocket server = new ServerSocket(3000);
            System.out.println(System.getProperty("user.dir"));
            while (true) {
                Socket client = server.accept();

                BufferedReader input = new java.io.BufferedReader(new java.io.InputStreamReader(client.getInputStream()));
                DataOutputStream output = new DataOutputStream(client.getOutputStream());

                String messaggio;
                messaggio = input.readLine();
                String[] messaggi = messaggio.split(" ");
                String path = messaggi[1].substring(1);

                while (!messaggio.isEmpty()) {
                    messaggio = input.readLine();
                    System.out.println(messaggio);
                }

                if (path.equals("") || path.charAt(path.length() - 1) == '/') {
                    path += "index.html";
                }

                File file = new File("docs/" + path);
                System.out.println(path);

                if (path.equals("test")) {
                    output.writeBytes("HTTP/1.1 301 MOVED PERMANENTLY\n");
                    output.writeBytes("Location: https://google.com\n");
                    output.writeBytes("\n");
                }

                if (file.exists()) {
                    System.out.println("the file exists");
                    sendBinaryFile(client, file);
                } else {
                    messaggio = "file not found";
                    System.out.println(messaggio);
                    output.writeBytes("HTTP/1.1 404 NOT FOUND\n");
                    output.writeBytes("Content-length: " + messaggio.length() + "\n");
                    output.writeBytes("Content-type: text/plain\n");
                    output.writeBytes("\n");
                    output.writeBytes(messaggio + "\n");
                }

                input.close();
                output.close();
                client.close();
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void sendBinaryFile(Socket socket, File file) throws IOException {
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        out.writeBytes("HTTP/1.1 200 OK\n");
        out.writeBytes("Content-Length: " + file.length() + "\n");
        out.writeBytes(getContentType(file) + "\n");
        out.writeBytes("\n");

        InputStream input = new FileInputStream(file);
        byte[] buf = new byte[8192];
        int n;
        while ((n = input.read(buf)) != -1) {
            out.write(buf, 0, n);
        }
        input.close();
    }

    private static String getContentType(File file) {
        String filename = file.getName();
        String[] temp = filename.split("\\.");
        String extension = temp[temp.length - 1];
        switch (extension) {
            case "html":
                return "Content-Type: text/html";
            case "png":
                return "Content-Type: image/png";
            case "jpeg":
            case "jpg":
                return "Content-Type: image/jpeg";
            case "css":
                return "Content-Type: text/css";
            default:
                return "Content-Type: text/plain";
        }
    }
}
