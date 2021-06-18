package com.company;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import javax.net.ssl.SSLException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class Server {

    public static void main(String[] args) {

        int port = 8888;
        String Ks = "pre master secret key";

        try {
            ObjectMapper objectMapper = new ObjectMapper();

            ServerSocket listener = new ServerSocket(port);
            Socket clientSocket = listener.accept();

            DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
            DataInputStream in = new DataInputStream(clientSocket.getInputStream());

            String message = in.readUTF();//read string message sent by client

            System.out.println("Client Hello: " + message);
            // choose of the cipher suites from the client

            HelloMessage serverHello = new HelloMessage();


            out.writeUTF(objectMapper.writeValueAsString(serverHello)); //writing to Client

            Certificate cert = new Certificate(); //SSL Certificate //verifying the certificate chain? how
            cert.setSignature("CA");

            out.writeUTF(objectMapper.writeValueAsString(cert));
            System.out.println("Server sent certificate");

            //Server Key Exchange - only if the server certificate is not enough

            out.writeUTF("server_hello_done");
            System.out.println("Server sent server_hello_done");

            String change_cipher_spec = in.readUTF();//read server_hello_done

            String Kpms = in.readUTF();

            SecretKeyFactory skf = SecretKeyFactory.getInstance(serverHello.getCipherSuite());
            SecretKey skey = skf.generateSecret(new DESKeySpec(Kpms.getBytes()));

            out.writeUTF("change_cipher_spec");
            System.out.println("Server sent change cipher spec message");


            KeyPairGenerator pkeyGen = KeyPairGenerator.getInstance("RSA");
            pkeyGen.initialize(1024);
            KeyPair pair = pkeyGen.generateKeyPair();
            PrivateKey priKey = pair.getPrivate();
            PublicKey pubKey = pair.getPublic();


            //send public key to client

            int pubKeylen = pubKey.getEncoded().length;
            out.writeInt(pubKeylen);
            out.write(pubKey.getEncoded(), 0, pubKeylen);
            System.out.println("Server sends public key");


            //reading public key

            int clientPubKeylen = in.readInt();
            byte[] clientPubKeyByte = new byte[clientPubKeylen];
            in.read(clientPubKeyByte, 0, clientPubKeylen);

            X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(clientPubKeyByte);
            KeyFactory keyFact = KeyFactory.getInstance("RSA");
            PublicKey clientPubKey = keyFact.generatePublic(pubKeySpec);

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            Cipher cipherDES = Cipher.getInstance("DES/ECB/PKCS5Padding");

            cipher.init(Cipher.WRAP_MODE, priKey);
            byte[] wrappedPriKey =cipher.wrap(skey);
            cipher.init(Cipher.ENCRYPT_MODE, clientPubKey);
            byte[] serverSecretKeyByte =cipher.doFinal(wrappedPriKey);

            out.writeInt(serverSecretKeyByte.length);
            out.write(serverSecretKeyByte);
            System.out.println("Server sends encrypted generated secret key");

            //Reading Client final message

            cipherDES.init(Cipher.DECRYPT_MODE, skey);
            int clientFinalMessagelen = in.readInt();
            byte[] clientFinalMessageByte = new byte[clientFinalMessagelen];
            in.read(clientFinalMessageByte,0,clientFinalMessagelen);
            byte[] clientFinalMessage = cipherDES.doFinal(clientFinalMessageByte);

            if (!(new String(clientFinalMessage)).equals("Finished Message"))
            {
                throw new InvalidObjectException("Client failed to send Final Message: Client sent ->" + new String(clientFinalMessageByte));
            }

            System.out.println("Server received final message from client");
            System.out.println("SSL handshake is done");

            String s = "",s1="";
            while(true)
            {
                System.out.println("Read Messages from Client");
                s1 = in.readUTF();
                s = s + s1;
                System.out.println("s1: " + s1);
                System.out.println("s: " + s);
                if (s.endsWith("~^"))
                {
                    String compressedMessage = s.replace("$#$"," ").replace("~^"," ");
                    System.out.println("Client sent message: " + compressedMessage);
                    s="";
                }
            }
        }
        catch (IOException e) {
            System.err.println("Error: connection failed");
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
    }
}
