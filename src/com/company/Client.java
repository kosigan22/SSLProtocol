package com.company;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.Scanner;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;


public class Client {


    public static void main(String[] args) {

        int port = 8888;
        String Kpms = "pre master secret key";
        String MacCode = "Mac Code";

        try {
            ObjectMapper objectMapper = new ObjectMapper();

            Socket socket = new Socket("localhost", port);

            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());

            HelloMessage clientHello = new HelloMessage();
            out.writeUTF(clientHello.toString()); //writing to Server

            String message = in.readUTF();//read string message sent by client

            System.out.println("Server Hello: " + message);

            HelloMessage serverHello = objectMapper.readValue(message, HelloMessage.class);

            System.out.println("Server Hello: " + serverHello.toString());
            System.out.println("Server Hello: " + serverHello.getVersion());



            String sslCertString = in.readUTF();//read cert by server

            Certificate sslCert = objectMapper.readValue(sslCertString,Certificate.class);

            System.out.println("Server signature: " + sslCert.getSignature());

            if(!isCertificateVerified(sslCert))
            {
                throw new InvalidObjectException("Certificate is not valid");
            }


            String server_hello_done = in.readUTF();//read server_hello_done
            //check if statement

            out.writeUTF(Kpms);
            System.out.println("Client sent Pre master secret Key");

            out.writeUTF("change_cipher_spec");
            System.out.println("Client sent change cipher spec message");

            String change_cipher_spec = in.readUTF();

            KeyFactory keyFact = KeyFactory.getInstance("RSA");
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA"); //why RSA
            keyPairGen.initialize(2048);
            KeyPair pair = keyPairGen.generateKeyPair();
            PrivateKey priKey = pair.getPrivate();
            PublicKey pubKey = pair.getPublic();


            //get server public key
            int serverPubKeylen = in.readInt();
            byte[] serverPubKeyByte = new byte[serverPubKeylen];
            in.read(serverPubKeyByte, 0, serverPubKeylen);

            X509EncodedKeySpec ServerPubKeySpec = new X509EncodedKeySpec(serverPubKeyByte);
            PublicKey serverPubKey = keyFact.generatePublic(ServerPubKeySpec);

            //send client public key
            byte[] pubKeyBytes = pubKey.getEncoded();
            out.writeInt(pubKeyBytes.length);
            out.write(pubKeyBytes, 0, pubKeyBytes.length);


            int serverSecretKeylen = in.readInt();
            byte[] serverSecretKeyByte = new byte[serverSecretKeylen];
            in.read(serverSecretKeyByte,0,serverSecretKeylen);

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, priKey);
            byte[] serverSecretKeyDecrypted =cipher.doFinal(serverSecretKeyByte);
            cipher.init(Cipher.UNWRAP_MODE, serverPubKey);
            Key serverSercetKeyUnwrapped = cipher.unwrap(serverSecretKeyDecrypted,"DES", Cipher.SECRET_KEY);
            SecretKey serverSercretKey = (SecretKey) serverSercetKeyUnwrapped;

            //Sending Finish
            Cipher cipherDES = Cipher.getInstance("DES/ECB/PKCS5Padding");
            String finalmsg = "Finished Message";
            cipherDES.init(Cipher.ENCRYPT_MODE, serverSercretKey);
            byte[] finalMessageByte = cipherDES.doFinal(finalmsg.getBytes());

            out.writeInt(finalMessageByte.length);
            out.write(finalMessageByte);

            System.out.println("Client sent final message");

            String s;
            while(true)
            {
                System.out.println("Send a message to server: ");
                Scanner input = new Scanner(System.in); // Reads plain text from user input
                s = input.nextLine();
                String[] decompressedMessage = decompress(s);
                System.out.println("decompressedMessage: " + decompressedMessage);

                for (String s1 : decompressedMessage)
                {
                    System.out.println("s1:" + s1);
                    out.writeUTF(s1);
                }

            }
        }
        catch (IOException e) {
            System.err.println("Error: connection failed");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch(InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }

    private static boolean isCertificateVerified(Certificate certificate)
    {
        Date currentDate = new Date();
        return (certificate.getSignature().equals("CA") && (certificate.getExpirationDate().compareTo(currentDate) > 0));
    }

    private static String[] decompress(String input)
    {
        String data = input.replace(" ","$#$") + "~^";
        // spaces -> "$#$"
        // appended at the end -> "~^"

        System.out.println("decompressedMessage: " + data);

        return data.split("(?<=\\G...)");
    }

}
