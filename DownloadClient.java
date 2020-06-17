import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.Scanner;
import java.util.regex.Pattern;

public class DownloadClient {

	private Scanner intScanner = new Scanner(System.in);
	static final String INVALID_EXP = "[:\\\\/%*?:|\"<>]";
	/*
	*
	*
	*
	*/
	public boolean validateFile(String fileStr)
	{	
		if(!Pattern.compile(INVALID_EXP).matcher(fileStr).find() && fileStr.contains("."))
		{
			return true;
		}
		return false;
	}
	/*
	*
	*
	*
	*
	*/
	public int menu()
	{	
		System.out.println("Welcome to File Downloader!\nPlease choose options below.");
		System.out.println("|------------------|");
		System.out.print("| 1. Download File |\n");
		System.out.print("|------------------|\n");
		System.out.print("| 2. Exit 	   |\n");
		System.out.println("|------------------|");	
		
		int returnValue = 0;
		int userChoice = intScanner.nextInt();
		
		switch(userChoice)
		{	
			case 1:
				returnValue = 1;
				break;
			case 2:
				System.exit(0);
			default:
				System.out.println("Please choose option 1 or option 2. Do not enter anyother numbers");
				menu();//recursion until user chooses correctly.
		}
		return returnValue;
	}
	/*
	*
	*
	*
	*
	*
	*/
	public static void main(String[] args) {
		Socket socket = null;//socket

		Scanner scn = new Scanner(System.in);

		String userWish = ""; //variable to use in while loop contains user answer.
		String pathToDownloadFile = System.getProperty("user.dir") + "\\" + "Server Downloaded Files" ;//absolute path for download file

		DownloadClient dlc = new DownloadClient();
		int choice = dlc.menu();

		//if user enters 1 to download file
		if(choice == 1)
		{
			try {
				InetAddress ipAddr = InetAddress.getByName("localhost");//ip address
				int portNum = 6777;//port number
				do{

				System.out.println("Please enter the file name you wish to download.");
				String fileName = scn.nextLine();//file name input by user
				Boolean isFileNameValid = dlc.validateFile(fileName);//invoke method to assign boolean value

				//file validation check
				if(!isFileNameValid)
				{
					System.out.println("!!Your file name is not valid. Check if you accidently entered illegal symbols. You may also missed an extension.");
					System.out.println("Exiting a program... ...");
					System.exit(0);
				
				}

				//socket linked to TCP port
				System.out.println("Connecting to server ... ...");
				socket = new Socket(ipAddr,5277 ,ipAddr, portNum);
				//socket = new Socket();
				//socket.connect(new InetSocketAddress("localhost", 6777));
				System.out.println("Connection established successfully!");
				
				OutputStream os = socket.getOutputStream();//export stream
				InputStream is = socket.getInputStream();//import stream

				//send filename to server to check if file exists
				byte[] byteArr = fileName.getBytes("UTF-8");
				os.write(byteArr);
				os.flush();
				System.out.println("requesting the file ... ...");

				//input - receive information if a file exists
				byte[] bytes = new byte[200];
				int readByteCount = is.read(bytes);
				String msg = new String(bytes,0,readByteCount, "UTF-8");
				
				if(msg.equals("false"))
				{
					System.out.println("!!The file you requested does not exist");

				}else 
				{	
					//receive files
					System.out.println("File found on server. Starting the download... ...");
					FileOutputStream fo = new FileOutputStream(pathToDownloadFile + "\\" + fileName);
					byte [] filebytes = new byte[2000];
					//InputStream ism = socket.getInputStream();
					//ism.read(filebytes, 0, filebytes.length);
					int original = filebytes.length;
					int n;
					System.out.println("sssssssssssssssssssssss");
					while((n = is.read(filebytes, 0, filebytes.length))>0)
					{
						fo.write(filebytes,0,n);
						System.out.println(n+ "bytes downloaded!");
						if((n- original)<0)
						{
							break;
						}
					}

					//FileOutputStream fo = new FileOutputStream(pathToDownloadFile + "\\" + fileName);
					//fo.write(filebytes,0,filebytes.length);
					fo.close();
					System.out.println("File downloaded successfully!");
					System.out.println("You can check the file now");
					
				}
				System.out.println("Do you wish to continue? Enter 'Y' if you wish to. Otherwise, server will close");
				userWish = scn.nextLine();//user close or continue

				//if client wants to continue, it sends message to server to run continuously
				if(userWish.equals("Y") ||userWish.equals("y") || userWish.equals("Yes") || userWish.equals("yes"))
				{
					String connectionMsg = "clientStillOpen";
					byte[] connMsgByte = connectionMsg.getBytes("UTF-8");
					os.write(connMsgByte);
					os.flush();
				}

				}while(userWish.equals("Y") ||userWish.equals("y") || userWish.equals("Yes") || userWish.equals("yes"));

				scn.close();

			}catch(Exception e)
			{
				e.printStackTrace();
			}
			
			/*
			//if client wants to continue, it sends message to server to run continuously
			if(userWish.equals("Y") ||userWish.equals("y") || userWish.equals("Yes") || userWish.equals("yes"))
			{	
				try{	
					OutputStream outst = socket.getOutputStream();
					String connectionMsg = "clientStillOpen";
					byte[] connMsgByte = connectionMsg.getBytes("UTF-8");
					outst.write(connMsgByte);
					outst.flush();
				}catch(IOException e)
				{
					e.printStackTrace();
				}
			}else
			{	
				try{
				//close connection
				OutputStream outst = socket.getOutputStream();
				String connectionMsg = "clientClosed";
				byte[] connMsgByte = connectionMsg.getBytes("UTF-8");
				outst.write(connMsgByte);
				outst.flush();
				outst.close();
				}catch(IOException e)
				{
					e.printStackTrace();
				}
			}*/

			//if user does not want to continnue, send message to the server to notify that the client is closed and socket closed 
			if(!socket.isClosed())
			{
				try {
					//close connection
					OutputStream outst = socket.getOutputStream();
					String connectionMsg = "clientClosed";
					byte[] connMsgByte = connectionMsg.getBytes("UTF-8");
					outst.write(connMsgByte);
					outst.flush();
					outst.close();
					socket.close();
				}catch(IOException e)
				{
					e.printStackTrace();
				}
			}
		}//if
	}//main method 
}// class 
