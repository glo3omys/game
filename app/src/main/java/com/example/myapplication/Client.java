package com.example.myapplication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client
{
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;



    public Client()
    {
        try
        {
            // create new socket and connect to the server
            this.socket = new Socket( "211.234.199.59" , 23456 );
        }
        catch( IOException e )
        {
            System.out.println( "failed to create socket" );
            e.printStackTrace();
        }

        System.out.println( "connected" );

        try
        {
            this.dataInputStream = new DataInputStream( new BufferedInputStream( this.socket.getInputStream() ) );
            this.dataOutputStream = new DataOutputStream( new BufferedOutputStream( this.socket.getOutputStream() ) );
        }
        catch ( IOException e )
        {
            System.out.println( "failed to create streams" );
            e.printStackTrace();
        }

        while ( true )
        {
            try
            {
                int test = this.dataInputStream.readInt();
                System.out.println( "int received: "+test );

                if ( test == 42 ) break;
            }
            catch ( IOException e )
            {
                System.out.println( "failed to read data" );
                e.printStackTrace();
                break;
            }
        }
    }
}