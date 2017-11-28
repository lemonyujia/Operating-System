package utd.persistentDataStore.datastoreClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import utd.persistentDataStore.utils.StreamUtil;

public class DatastoreClientImpl implements DatastoreClient
{
	private static Logger logger = Logger.getLogger(DatastoreClientImpl.class);

	private InetAddress address;
	private int port;

	public DatastoreClientImpl(InetAddress address, int port)
	{
		this.address = address;
		this.port = port;
	}

	/* (non-Javadoc)
	 * @see utd.persistentDataStore.datastoreClient.DatastoreClient#write(java.lang.String, byte[])
	 */
	@Override
    public void write(String name, byte data[]) throws ClientException, ConnectionException
	{
		try {
			logger.debug("Opening Socket");
			Socket socket = new Socket();
			SocketAddress saddr = new InetSocketAddress(address, port);
			socket.connect(saddr);
			InputStream inputStream = socket.getInputStream();
			OutputStream outputStream = socket.getOutputStream();
			
			logger.debug("Executing Write Operation");
			StreamUtil.writeLine("write", outputStream);
			StreamUtil.writeLine(name, outputStream);
			StreamUtil.writeLine(data.length+"", outputStream);
			StreamUtil.writeData(data, outputStream);
			
			logger.debug("Reading Response");
			String response = StreamUtil.readLine(inputStream);
			logger.debug(response);

		} catch (IOException e) {
			throw new ClientException(e.getMessage(), e);
		}	
	}

	/* (non-Javadoc)
	 * @see utd.persistentDataStore.datastoreClient.DatastoreClient#read(java.lang.String)
	 */
	@Override
    public byte[] read(String name) throws ClientException, ConnectionException
	{
		byte[] data = null;
		try {
			logger.debug("Opening Socket");
			Socket socket = new Socket();
			SocketAddress saddr = new InetSocketAddress(address, port);
			socket.connect(saddr);
			InputStream inputStream = socket.getInputStream();
			OutputStream outputStream = socket.getOutputStream();
			
			logger.debug("Executing Read Operation");
			// StreamUtil.writeLine can detect the end of String and add newline command for it if needed
			StreamUtil.writeLine("read", outputStream);
			StreamUtil.writeLine(name, outputStream);
			
			logger.debug("Reading Response");
			String response = StreamUtil.readLine(inputStream);
			if(response.equalsIgnoreCase("ok"))
			{
			logger.debug(response);
			String dataLength = StreamUtil.readLine(inputStream);
			logger.debug("Read Length " + dataLength);
			data = StreamUtil.readData(Integer.parseInt(dataLength), inputStream);
			String output = "";
			for(int i = 0; i < data.length; i++) output += data[i] + " ";
			logger.debug(output);
			} else {
				throw new ClientException(response);
			}
		} catch (IOException e) {
			throw new ClientException(e.getMessage(), e);
		}
		return data;
	}

	/* (non-Javadoc)
	 * @see utd.persistentDataStore.datastoreClient.DatastoreClient#delete(java.lang.String)
	 */
	@Override
    public void delete(String name) throws ClientException, ConnectionException
	{
		try {
			logger.debug("Opening Socket");
			Socket socket = new Socket();
			SocketAddress saddr = new InetSocketAddress(address, port);
			socket.connect(saddr);
			InputStream inputStream = socket.getInputStream();
			OutputStream outputStream = socket.getOutputStream();
			
			logger.debug("Executing Delete Operation");
			// StreamUtil.writeLine can detect the end of String and add newline command for it if needed
			StreamUtil.writeLine("delete", outputStream);
			StreamUtil.writeLine(name, outputStream);
			
			logger.debug("Reading Response");
			String response = StreamUtil.readLine(inputStream);
			if(response.equalsIgnoreCase("ok")) {
			logger.debug(response);
			} else {
				throw new ClientException(response);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new ClientException(e.getMessage(), e);
		}
	}

	/* (non-Javadoc)
	 * @see utd.persistentDataStore.datastoreClient.DatastoreClient#directory()
	 */
	@Override
    public List<String> directory() throws ClientException, ConnectionException
	{
		LinkedList<String> fileList = null;
		try {
			logger.debug("Opening Socket");
			Socket socket = new Socket();
			SocketAddress saddr = new InetSocketAddress(address, port);
			socket.connect(saddr);
			InputStream inputStream = socket.getInputStream();
			OutputStream outputStream = socket.getOutputStream();
			
			logger.debug("Executing Directory Operation");
			// StreamUtil.writeLine can detect the end of String and add newline command for it if needed
			StreamUtil.writeLine("directory", outputStream);
			
			logger.debug("Reading Respose");
			String response = StreamUtil.readLine(inputStream);
			logger.debug(response);
			String numberOfFileNames = StreamUtil.readLine(inputStream);
			logger.debug(numberOfFileNames);
			for(int i = 0; i < Integer.parseInt(numberOfFileNames); i++) {
				fileList = new LinkedList<>();
				fileList.add(StreamUtil.readLine(inputStream));
				logger.debug(fileList);
			} 
		} catch (IOException e) {
			throw new ClientException(e.getMessage(), e);
		}
		return fileList;
	}
}