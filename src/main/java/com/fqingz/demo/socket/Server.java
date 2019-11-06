package com.fqingz.demo.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Fang Qing
 * @date 2019/11/5 13:14
 */
public class Server extends Thread {

    Socket socket = null;

    public Server(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        String charset = "UTF-8";
        try (BufferedReader br = new BufferedReader (new InputStreamReader (socket.getInputStream ( ),charset))) {
            String info;
            while ((info = br.readLine ( )) != null) {
                System.out.println ("msg:" + info + Thread.currentThread ().getName ());
            }

            socket.shutdownInput ();
        } catch (IOException e) {
            e.printStackTrace ( );
        }
    }

    public static void main(String[] args) {
        try(ServerSocket serverSocket = new ServerSocket (999)){
            Socket socket = null;
            int count = 0;
            //noinspection InfiniteLoopStatement
            while (true){
                //BIO的阻塞方式
                socket = serverSocket.accept ();
                Server server = new Server (socket);
                server.start ();
                count++;
                System.out.println ("客户端数量" + count );
            }
        } catch (IOException e) {
            e.printStackTrace ( );
        }
    }
}
