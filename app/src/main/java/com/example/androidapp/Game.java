package com.example.androidapp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.Random;

public class Game implements Runnable {

    private InputStream is;
    private OutputStream os;
    private DataOutputStream dos;
    private DataInputStream dis;
    private String begin;
    private Random r;
    private final int A_BEGINS = 1;   // Team A ist immer das Server Team
    private final int B_BEGINS = 2;
    private final int RANDOM_BEGIN = 3;
    private boolean TEAM_A = ConnectActivity.isServer;
    private int state;

    public Game(String begin, InputStream is, OutputStream os) {
        this.begin = begin;
        this.r = new Random();
        this.is = is;
        this.os = os;
        this.dis = new DataInputStream(this.is);
        this.dos = new DataOutputStream(this.os);
        this.state = 0;

    }

    private void negotiate() throws IOException {
        int sendBeginner = 0;
        switch (begin) {
            case ("a"):
                sendBeginner = A_BEGINS;
                break;
            case ("b"):
                sendBeginner = B_BEGINS;
                break;
            case ("r"):
                sendBeginner = RANDOM_BEGIN;
                break;
        }
        dos.writeInt(sendBeginner);
        int readBeginner = dis.readInt();
        if (readBeginner == sendBeginner) {
            state = readBeginner;
        }
        if (state == RANDOM_BEGIN || readBeginner != sendBeginner) {
            sendRandomInt();
        }
        switch ((state)) {
            case (A_BEGINS):
                // Team A beginnt
                break;
            case (B_BEGINS):
                // Team B beginnt
                break;
        }
    }

    private void sendRandomInt() throws IOException {
        int myRandomNumber = r.nextInt();
        dos.writeInt(myRandomNumber);
        int otherRandomNumber = dis.readInt();
        if (otherRandomNumber > myRandomNumber) {
            if (TEAM_A) {
                state = B_BEGINS;
            } else {
                state = A_BEGINS;
            }
        } else {
            if (TEAM_A) {
                state = A_BEGINS;
            } else {
                state = B_BEGINS;
            }
        }
    }

    @Override
    public void run() {
        try {
            this.negotiate();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}