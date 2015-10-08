package org.tourgune.mdp.amazon.http;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.torugune.mdp.log.LogPriority;
import org.torugune.mdp.log.Logger;
import org.tourgune.mdp.amazon.exception.HttpParseException;

/**
 * @todo
 * 	- Asegurarse de que llaman a finalize()
 * 	- Métodos privados para generar logs de varias líneas con logger.print(ln)()
 */
public class HttpClient {
	
	private String host;
	private int port;
	
	private HttpURLConnection http;
	private String content;
	
	private Logger logger;
	private String lastErrorLog;
	
	public HttpClient(String host, int port) {
		this.host = host;
		this.port = port;
		this.content = null;
		this.http = null;
		this.logger = null;
		this.lastErrorLog = null;
	}
	public HttpClient(String host, int port, Logger logger) {
		this(host, port);
		this.logger = logger;
	}
	
	public void finalize(){
		if(http instanceof HttpURLConnection)
			http.disconnect();
	}

	public void sendRequest(String path) throws HttpParseException{
		System.out.println("Conectando a " + this.host + ":" + this.port + "...");
		try {
			_sendHeaders(path);
			this.content = _getReply();
		} catch (IOException e) {
			this.content = null;
			_logError(e.getMessage());
		}
	}
	
	public String getResponseBody() throws IOException{
		if(this.content == null)
			_throwIOExceptionFromLogs();
		return this.content;
	}
	
	/*	M�todos privados	*/
	
	private void _throwIOExceptionFromLogs() throws IOException{
		String msg;
		
		if(this.lastErrorLog != null)
			msg = "HttpClient - " + this.lastErrorLog;
		else
			msg = "HttpClient - Unexpected error";
		
		throw new IOException(msg);
	}
	
	private void _sendHeaders(String path) throws IOException {
		URL url = new URL("http://" + host + ":" + port + path);
		
		http = (HttpURLConnection) url.openConnection();
		
		http.setRequestProperty("Accept", "application/json, */*;q=0.8");
		http.setRequestProperty("User-Agent", "Mozilla/5.0 Firefox/3.2");
		
		_log("Request sent (" + path + ").");
	}
	
	private String _getReply() throws IOException, HttpParseException {
		String content = null;
		InputStream in = http.getInputStream();
		int len = http.getContentLength();
		
		try{
			if(len > 0){
				// Contenido normal (no chunked).
				_log("Retrieving server reply.");
				byte[] contentBytes = new byte[len];
				for(int i = 0; i < len; i++){
					contentBytes[i] = (byte) in.read();
					if(contentBytes[i] == -1)
						break;
				}
				//in.read(contentBytes);
				content = new String(contentBytes);
			}else if(len == -1 && http.getHeaderField("Transfer-Encoding").equals("chunked")){
				_log("Retrieving server reply. Data is chunked.");
				// No podemos anticipar el tama�o de todo el contenido
				// Tratamos los caracteres en un buffer din�mico con un tama�o inicial de 1000 caracteres
				StringBuffer buf = new StringBuffer(1000);
				InputStreamReader r = new InputStreamReader(in);
				int nextChar = -1;
				do{
					nextChar = r.read();
					if(nextChar != -1)
						buf.append((char) nextChar);
				}while(nextChar != -1);
				content = buf.toString();
			}else{
				content = null;
				_logError("Unknown error: could not read server reply.");
			}
		}catch(NullPointerException e){
			content = null;
			if(len == -1 && http.getHeaderField("Transfer-Encoding") == null)
				_logError("Could not read server reply. The content was not chunked, but the content's length is unknown.");
			else
				_logError("Unknown error.");
		}
		
		in.close();
		return content;
	}
	
	private void _log(String description){
		if(this.logger != null)
			this.logger.log(description, "HttpClient");
	}
	private void _logError(String description){
		if(this.logger != null)
			this.logger.log(description, "HttpClient", LogPriority.ERROR);
		else
			this.lastErrorLog = new String(description);
	}
}
