package fileSystem;

import java.text.SimpleDateFormat;
import java.util.Date;

import RandomFileCpp.RandomAccessFileCpp;

/**
 * 忽略中文,java一个中文也是一个char
 * 共8+4+4+1+15=32字节
 * @author 胤栋
 *
 */
public class FCB {
	/**
	 * 保存到文件时是8字节
	 */
	String Name;
	/**
	 * 保存到文件时是4字节
	 */
	int Size;
	/**
	 * 保存到文件时是4字节，虽然2字节就够用了
	 */
	int FirstBlock;
	/**
	 * 保存到文件时是1字节
	 * Type为1表示文件，为2表示目录
	 * 为0表示已经删除目录
	 */
	char Type;
	/**
	 * 保存在内存中是14字节，保存到文件时是15字节,最后一位用0填充
	 * 格式为yyyymmddhhmmss
	 */
	char[] DateTime;
	
	static public void main(String[] args){
		FCB test = new FCB();
		test.SetTime();
	}
	
	/**
	 * 从文件中获得的32bit数据构造FCB
	 * @param src
	 */
	public FCB(byte[] src){
		/*
		 * debug
		 */
		/*
		for (int i = 0;i < 32;i ++){
			System.out.print(src[i] + " ");
		}
		System.out.println();
		*/
		/*
		 * 读入name
		 */
		StringBuffer TempStrBuffered = new StringBuffer("");
		for (int i = 0;i < 8 && src[i] != 0;i ++){
			TempStrBuffered.append((char)src[i]);
		}
		this.Name = new String(TempStrBuffered);
		/*
		 * 读入size
		 */
		this.Size = RandomAccessFileCpp.unsignedByteToInt(src[8]) + RandomAccessFileCpp.unsignedByteToInt(src[9]) * 256 + 
				RandomAccessFileCpp.unsignedByteToInt(src[10]) * 256 * 256 + RandomAccessFileCpp.unsignedByteToInt(src[11]) * 256 * 256 * 256;
		/*
		 * 读入FirstBlock
		 */
		this.FirstBlock = RandomAccessFileCpp.unsignedByteToInt(src[12]) + RandomAccessFileCpp.unsignedByteToInt(src[13]) * 256 + 
				RandomAccessFileCpp.unsignedByteToInt(src[14]) * 256 * 256 + RandomAccessFileCpp.unsignedByteToInt(src[15]) * 256 * 256 * 256;
		/*
		 * 读入type
		 */
		this.Type = (char) src[16];
		/*
		 * 读入DateTime
		 */
		this.DateTime = new char[14];
		for (int i = 0;i < 14;i ++){
			this.DateTime[i] = (char) src[17 + i];
		}
	}
	
	/**
	 * 将FCB转换成32字节的byte数组，便于写入文件
	 * @return
	 */
	public byte[] ToByteArray(){
		byte[] desByte = new byte[32];
		/*
		 * 转换name
		 */
		char[] TempCharArray = this.Name.toCharArray();
		for (int i = 0;i < 8 && i < TempCharArray.length;i ++){
			desByte[i] = (byte) TempCharArray[i];
		}
		/*
		 * 转换size
		 */
		desByte[8] = (byte) (this.Size % 256);
		desByte[9] = (byte) (this.Size / 256 % 256);
		desByte[10] = (byte) (this.Size / 256 / 256 % 256);
		desByte[11] = (byte) (this.Size / 256 / 256 / 256 % 256);
		/*
		 * 转换FirstBlock
		 */
		desByte[12] = (byte) (this.FirstBlock % 256);
		desByte[13] = (byte) (this.FirstBlock / 256 % 256);
		desByte[14] = (byte) (this.FirstBlock / 256 / 256 % 256);
		desByte[15] = (byte) (this.FirstBlock / 256 / 256 / 256 % 256);
		/*
		 * 转换type
		 */
		desByte[16] = (byte) this.Type;
		/*
		 * 转换dateTime
		 */
		for (int i = 0;i < 14;i ++){
			desByte[17 + i] = (byte) this.DateTime[i];
		}
		desByte[31] = 0;
		return desByte;
	}
	
	public FCB() {
		this.Name = "";
		this.Size = 0;
		this.FirstBlock = 0;
		this.Type = 0;
		this.SetTime();
	}

	public void SetTime(){
		Date CurrDate = new Date();
		SimpleDateFormat DateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		DateTime = DateFormat.format(CurrDate).toCharArray();
		
		/*
		 * debug
		 */
		/*
		for (int i = 0;i < DateTime.length;i ++){
			System.out.println(DateTime[i]);
		}
		*/
	}
}
