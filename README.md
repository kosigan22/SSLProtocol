# SSLProtocol

This project's purpose is to create a system at the application layer that will use this SSL protocol with the Java security framework. 
The main parts of the Java security framework that we would be using are the API & Tools for Secure Code and File Exchanges, Signing Code 
and Granting it Permissions, Exchanging Files, and Generation and Verifying Signatures. The SSL protocol will ensure that the traffic 
between end users remains secure, allowing us to perform secure tasks. All in all, the cryptographic protocols that we will apply in the 
project will allow for protection against man-in-the-middle like attacks, ensuring that there is no third party capable of having “a look” 
into the communication channel(s).

Design and Implementation:

**Server**:
The server.java file was implemented using socket programming where it accepts an incoming connection from the client. The server is set up 
to accept clients on port 8888. After a successful connection with the client, input and output streams are initialized to be able to send 
and receive data from server to client, and vice versa. First, the server receives a message from the client called client_hello. The server 
then sends out a message to the client with the different parameters such as version, session id, cipher suite, initial random numbers and 
the compression method. A SSL certificate is then created by the server and sent to the client and then let the client know that the server 
is done with a server_hello_done message. A change cipher spec protocol is conducted where the server sends a message to notify the client 
that cipher keys will be utilized. The server then encrypts a generated secret key with the public key. The 
client will then send a final message and be decrypted by the server to conclude the handshake protocol. 

**Client**: 
The client.java file is used to connect the client to the server. The first step of establishing a connection is by creating a socket, the 
two parameters used to create the socket object is “localhost” since client and server are on the same system as well as selecting a port 
number, we used “8888”.  Next we define data input and output streams that would be responsible for sending and receiving data between the 
client and server. Following this step we begin by establishing the client_hello step, which is sending a message to the server which establishes 
the security capabilities which include, protocol version, session ID, cipher suite, initial random number and compression method. After 
receiving messages back from the server the client can move on to the next phase which is sending a certificate. After this step, the client 
continues by sending the change cipher suite as this is the last component required from the client to finish the handshake. Upon completing 
the handshake, the client will proceed to establishing the chat loop between the client and server. 

**Record Layer**:
The record layer is used to provide security with the data being sent between the client and the server. The server will receive the fragments 
and reconstruct the client’s original message. We decided to use identifiers to represent whitespaces and the end of a client’s message. 
In our implementation we represent whitespaces with “$#$”, and to signify the end of a client’s message we used “~^”. The fragment size that 
we decided to use was 3, thus every 3 characters in the altered message will be sent to the server. The server will receive these fragments
and will construct the message until the message ends with  “~^”. The server will then remove all identifiers representing the whitespaces 
and then print out the message.

**Chat Loop**: 
Once the handshake is completed, the chat loop is able to begin. The chat loop is contained in a while loop structure that is able to 
iterate infinitely until the program is instructed to stop. Entering the message “exit” will allow the client to leave the program. The 
client and server implement the record layer, in fragmenting, compressing and decompressing the messages that are sent. 
