package RandomFileCpp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.swing.JOptionPane;

public class RandomAccessFileCpp extends RandomAccessFile{

	public RandomAccessFileCpp(File file, String mode)
			throws FileNotFoundException {
		super(file, mode);
		// TODO 自动生成的构造函数存根
	}
	
	public RandomAccessFileCpp(String string, String mode)
			throws FileNotFoundException {
		super(string, mode);
		// TODO 自动生成的构造函数存根
	}
	
	public void writeAntiInt(long nn) throws IOException{
		int n = (int) nn;
		this.writeByte(n % 256);
		this.writeByte(n / 256 % 256);
		this.writeByte(n / 256 / 256 % 256);
		this.writeByte(n  / 256 / 256 / 256 % 256);
	}
	
	public long readAnti2Bit() throws IOException{
		byte[] b = new byte[2];
		this.read(b);
		return unsignedByteToInt(b[0]) + unsignedByteToInt(b[1]) * 256;
	}
	
	public long readAntiInt() throws IOException{
		byte[] b = new byte[4];
		this.read(b);
		//JOptionPane.showMessageDialog(null,"byte:" + b[0] + "," + b[1] + "," + b[2] + "," + b[3]);
		return unsignedByteToInt(b[0]) + unsignedByteToInt(b[1]) * 256 + unsignedByteToInt(b[2]) * 256 * 256 + unsignedByteToInt(b[3]) * 256 * 256 * 256;
	}
	
	static public int unsignedByteToInt(byte b) {  
	    return (int) b & 0xFF;
	}
	
	/*
	public int readUnsignedByte(){
		
	}
	*/

}
