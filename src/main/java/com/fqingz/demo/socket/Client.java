package com.fqingz.demo.socket;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

/**
 * @author Fang Qing
 * @date 2019/11/5 9:53
 */
public class Client extends Thread {

    public static class ClientThread implements Runnable {
        private String host;
        private int port;
        private String msg;

        ClientThread(String host, int port, String msg) {
            this.host = host;
            this.port = port;
            this.msg = msg;
        }

        @Override
        public void run() {
            connect (host, port, msg);
        }
    }

    public static void main(String[] args) {
        String host = "127.0.0.1";
        int port = 999;
        String msg = "测试test演示了两台正在进行双向通信的主机";
        int n = 2;
        for (int i = 0; i < n; i++) {
            Thread.currentThread ().setName (String.valueOf (i));
            new ClientThread (host, port, msg).run ();
        }
    }

    private static void connect(String host, int port, String msg) {
        try (Socket socket = new Socket (host, port);
             PrintWriter pw = new PrintWriter (socket.getOutputStream ( ))) {
            pw.write (new Date ( ) + msg);
            pw.flush ( );
            socket.shutdownOutput ( );
        } catch (IOException e) {
            e.printStackTrace ( );
        }
    }
}
