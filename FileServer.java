import java.io.*;
import java.net.*;
import java.util.*;

class FileServer 
{	

	public static void main(String args[])
	{
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket();
			serverSocket.bind(new InetSocketAddress("localhost", 5777));
			
			while(true)
			{	
				//connection to client
				InetAddress ip = InetAddress.getLocalHost();
				System.out.println("Server started... Local IP address is " +ip.getHostAddress() + ";" + "Port"+ serverSocket.getLocalPort() + "..." + "wating for a client");
				Socket socket = serverSocket.accept();
				InetSocketAddress isa = (InetSocketAddress) socket.getRemoteSocketAddress();//ip address of client
				int serverPort = serverSocket.getLocalPort();
				System.out.println("Incoming Client: IP: " + isa.getHostName() + "Port: " + serverPort);

				InputStream is = socket.getInputStream();//input
				OutputStream os = socket.getOutputStream();//output

				//receiving file name from client - input
				byte[] bytes = new byte[200];
				int readByteCount = is.read(bytes);
				String fileName = new String(bytes,0,readByteCount, "UTF-8");
				System.out.println("File requested: "+ fileName + "..."+" Searching in the root ... ...");

				//search a file
				String path = System.getProperty("user.dir") + "\\" + "ServerFiles" + "\\" + fileName;//absolute path
				File file = new File(path);

				if(!file.exists())
				{	//if file name does NOT match, it sends a warning message to the client
					String msg = "false";
					System.out.println("File Does not exist");
					byte[] byteArr = msg.getBytes("UTF-8");
					os.write(byteArr);
					os.flush();

				}else 
				{	
					//sending true 
					String msg = "true";
					System.out.print("File Found!\n");
					byte [] msgbyte = msg.getBytes("UTF-8");
					os.write(msgbyte);
					os.flush();

					//sending files
					System.out.println("Starting Transfer... ...");
					FileInputStream fis = new FileInputStream(path);
					byte [] fileBytes = new byte[(int)file.length()];
					//byte [] fileBytes = new byte[500];
					//fis.read(fileBytes,0,fileBytes.length);
					int n;

					while((n = fis.read(fileBytes,0,fileBytes.length))>0)
					{
						os.write(fileBytes,0,n);
						//os.flush();
					}
					//os.write(fileBytes,0,fileBytes.length);
					fis.close();
					System.out.println("File transferred successfully!");

				}
				//receive connection message from client
				byte [] clientConnMsgByte = new byte[300];
				int connMsg = is.read(clientConnMsgByte);
				String clientConnMsg = new String(clientConnMsgByte,0,connMsg, "UTF-8");
				if(clientConnMsg.equals("clientClosed"))
				{	
					System.out.println("Connection terminated by client");
				}
				os.close(); 
			}  
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		if(!serverSocket.isClosed())
		{
			try {
				serverSocket.close();
			}catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		
	}
}