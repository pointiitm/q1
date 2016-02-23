package com.quopn.wallet.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class MultipartRequest {
	class PostData {
		boolean isBinary;
		String mime;
		String filename;
		String key;
		byte[] data;
		
		public byte[] getMultipartData() {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			byte[] bytesToWrite;
			
			String leadup = DASHES + BOUNDARY + CRLF + DECL + "\"" + key + "\"";
			if (isBinary) {
				leadup += "; filename=\"" + filename + "\"" + CRLF;
				leadup += "Content-Type: " + mime + CRLF;
				leadup += "Content-Transfer-Encoding: binary";
			}
			leadup += CRLF + CRLF;
			bytesToWrite = leadup.getBytes();

			stream.write(bytesToWrite, 0, bytesToWrite.length);
			stream.write(data, 0, data.length);
			stream.write(CRLF.getBytes(), 0, CRLF.length());
			
			return stream.toByteArray();
		}
	}
	
	private static final String CRLF = "\r\n";
	private static final String DASHES = "--";
	private static final String BOUNDARY = "XYZ";
	private static final String DECL = "Content-disposition: form-data; name=";
	
	private String url;
	private ArrayList<PostData> postData;
	private byte[] returnData;
	
	public MultipartRequest() {
		postData = new ArrayList<PostData>();
		returnData = new byte[4096];
	}
	
	public void setUrl(String url) { this.url = url; }
	
	public void addFieldValue(String field, String value) {
		PostData data = new PostData();
		data.isBinary = false;
		data.key = field;
		if (value == null || value.isEmpty()) {
			value = "no data";
		}
		data.data = value.getBytes();
		postData.add(data);
	}
	
	public void addFile(String field, String filePath, String mime) {
		try {
			File file = new File(filePath);
			FileInputStream fileStream = new FileInputStream(file);

			int size = fileStream.available();
			byte[] fileBytes = new byte[size];
			
			fileStream.read(fileBytes);
			PostData data = new PostData();
			data.isBinary = true;
			data.mime = mime;
			data.filename = file.getName();
			data.key = field;
			data.data = fileBytes;
			postData.add(data);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
	}
	
	public int request() {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		
		for (PostData data:postData) {
			byte[] multipartData = data.getMultipartData();
			stream.write(multipartData, 0, multipartData.length);
		}
		
		String finalBoundary = DASHES + BOUNDARY + DASHES;
		stream.write(finalBoundary.getBytes(), 0, finalBoundary.length());
		
		byte[] dataToWrite = stream.toByteArray();
		int contentLength = dataToWrite.length;
		
//		String log = new String(dataToWrite);
//		System.out.println(log);
		
		URL uri = null;
		HttpURLConnection connection = null;
		int response = 0;
		
		try {
			uri = new URL(url);
			connection = (HttpURLConnection) uri.openConnection();
			connection.setDoOutput(true);
			
			connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
			connection.setRequestProperty("Content-Length", "" + contentLength);
			
			OutputStream oStream = connection.getOutputStream();
			oStream.write(dataToWrite);
			oStream.close();
			
			response = connection.getResponseCode();
			//if (response != 200) { return response; }
			
			InputStream iStream = connection.getInputStream();
			int bytesRead = 10;
			ByteArrayOutputStream bStream = new ByteArrayOutputStream();
			while ((bytesRead = iStream.read(returnData)) > 0) {
				bStream.write(returnData, 0, bytesRead);
			}
			
			returnData = bStream.toByteArray();
			
			iStream.close();
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return -1;
		} catch (IOException e) {
			e.printStackTrace();
			if (connection != null) { connection = null; }
			return -1;
		}
		
		return response;
	}
	
	public byte[] getReturnData() { return returnData; }
}
