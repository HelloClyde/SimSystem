package fileSystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import RandomFileCpp.RandomAccessFileCpp;

public class FileSystem {
	File VirtualDisk;
	public FCB CurrFile;
	FCB Root;
	
	/**
	 * 
	 * @param FileName
	 * @param srcFileName
	 */
	public void MKCPY(String FileName,String srcFileName){
		try {
			RandomAccessFileCpp srcFileRF = new RandomAccessFileCpp(srcFileName,"rw");
			long FileLength = srcFileRF.length();
			srcFileRF.close();
			this.MakeFile(FileName, (int) FileLength);
			this.CopyFile(FileName, srcFileName);
			srcFileRF.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 显示树形结构
	 */
	public void ShowTree(){
		ShowTree(CurrFile);
	}
	
	public void ShowFile(String FileName){
		try{
			RandomAccessFileCpp VirtualDiskRF = new RandomAccessFileCpp(VirtualDisk.getAbsolutePath(),"rw");
			int FCBListBlock = CurrFile.FirstBlock;
			/*
			 * 判断该文件是否存在
			 */
			for (int FCBIndex = 0;FCBIndex < 32;FCBIndex ++){
				//定位FCB
				VirtualDiskRF.seek(32 + FCBListBlock * 1024 + FCBIndex * 32);
				byte[] TempByteArray = new byte[32];
				VirtualDiskRF.read(TempByteArray, 0, 32);
				FCB TempFCB = new FCB(TempByteArray);
				if (TempFCB.Type == 1){
					int FStart = 0;
					int pos;
					for (pos = 0;pos < TempFCB.Size;pos ++){
						String HexStr;
						HexStr = Integer.toHexString(this.GetUnsignedByte(FileName, pos)).toUpperCase();
						if (HexStr.length() == 1){
							HexStr = "0" + HexStr;
						}
						System.out.print(HexStr + " ");
						FStart ++;
						if (FStart == 16){
							System.out.print("\t");
							for (int pi = 0;pi < FStart;pi ++){
								char CharStr;
								CharStr = (char)this.GetUnsignedByte(FileName, pos - FStart + pi + 1);
								if (CharStr == 13 || CharStr == 10){
									CharStr = 0;
								}
								System.out.print(CharStr + " ");
							}
							System.out.println();
							FStart = 0;
						}
					}
					for (int i = 0;i < 16 - FStart;i ++){
						System.out.print("   ");
					}
					System.out.print("\t");
					for (int pi = 0;pi < FStart;pi ++){
						char CharStr;
						CharStr = (char)this.GetUnsignedByte(FileName, pos - FStart + pi);
						if (CharStr == 13 || CharStr == 10){
							CharStr = 0;
						}
						System.out.print(CharStr + " ");
					}
					System.out.println();
					return;
				}
			}
			System.out.println("文件不存在。");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 将物理磁盘文件内容复制到虚拟磁盘文件
	 * @param FileName
	 * @param srcFileName
	 * @return
	 */
	public boolean CopyFile(String FileName,String srcFileName){
		try {
			RandomAccessFileCpp srcFileRF = new RandomAccessFileCpp(srcFileName,"rw");
			for (long pos = 0;pos < srcFileRF.length();pos ++){
				srcFileRF.seek(pos);
				this.PutUnsignedByte(FileName, (int) pos, srcFileRF.readUnsignedByte());
			}
			srcFileRF.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 写入文件内容
	 * @param FileName
	 * @param FilePointer
	 * @param ByteDate 0~255
	 * @return
	 */
	public boolean PutUnsignedByte(String FileName,int FilePointer,int ByteDate){
		try {
			RandomAccessFileCpp VirtualDiskRF = new RandomAccessFileCpp(VirtualDisk.getAbsolutePath(),"rw");
			int FCBListBlock = CurrFile.FirstBlock;
			/*
			 * 判断该文件是否存在
			 */
			for (int FCBIndex = 0;FCBIndex < 32;FCBIndex ++){
				//定位FCB
				VirtualDiskRF.seek(32 + FCBListBlock * 1024 + FCBIndex * 32);
				byte[] TempByteArray = new byte[32];
				VirtualDiskRF.read(TempByteArray, 0, 32);
				FCB TempFCB = new FCB(TempByteArray);
				if (TempFCB.Type == 1){
					if (TempFCB.Name.equals(FileName)){
						if (FilePointer >= TempFCB.Size){
							System.out.println("文件指针越界。");
							return false;
						}
						else{
							int BlockIndex;
							int InBlockIndex;
							BlockIndex = FilePointer / 1024;
							InBlockIndex = FilePointer % 1024;
							int TempBlockAdd;
							TempBlockAdd = TempFCB.FirstBlock;
							for (int i = 0;i < BlockIndex;i ++){
								VirtualDiskRF.seek(TempBlockAdd * 2);
								TempBlockAdd = VirtualDiskRF.readUnsignedShort();
							}
							VirtualDiskRF.seek(32 + TempBlockAdd * 1024 + InBlockIndex);
							if (ByteDate >= 0 && ByteDate <= 255){
								VirtualDiskRF.writeByte(ByteDate);
								VirtualDiskRF.close();
								return true;
							}
							else{
								System.out.println("数据不在正确范围内。");
								return false;
							}
						}
					}
				}
			}
			System.out.println("文件不存在。");
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	/**
	 * 获取目标文件内容
	 * @param FileName
	 * @param FilePointer
	 * @return 0~255 -1表示出错
	 */
	public int GetUnsignedByte(String FileName,int FilePointer){
		try {
			RandomAccessFileCpp VirtualDiskRF = new RandomAccessFileCpp(VirtualDisk.getAbsolutePath(),"rw");
			int FCBListBlock = CurrFile.FirstBlock;
			/*
			 * 判断该文件是否存在
			 */
			for (int FCBIndex = 0;FCBIndex < 32;FCBIndex ++){
				//定位FCB
				VirtualDiskRF.seek(32 + FCBListBlock * 1024 + FCBIndex * 32);
				byte[] TempByteArray = new byte[32];
				VirtualDiskRF.read(TempByteArray, 0, 32);
				FCB TempFCB = new FCB(TempByteArray);
				if (TempFCB.Type == 1){
					if (TempFCB.Name.equals(FileName)){
						if (FilePointer >= TempFCB.Size){
							System.out.println("文件指针越界。");
							return -1;
						}
						else{
							int BlockIndex;
							int InBlockIndex;
							BlockIndex = FilePointer / 1024;
							InBlockIndex = FilePointer % 1024;
							int TempBlockAdd;
							TempBlockAdd = TempFCB.FirstBlock;
							for (int i = 0;i < BlockIndex;i ++){
								VirtualDiskRF.seek(TempBlockAdd * 2);
								TempBlockAdd = VirtualDiskRF.readUnsignedShort();
							}
							VirtualDiskRF.seek(32 + TempBlockAdd * 1024 + InBlockIndex);
							return VirtualDiskRF.readUnsignedByte();
						}
					}
				}
			}
			System.out.println("文件不存在。");
			return -1;
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	/**
	 * 判断这个FCB是不是父目录的最后一个
	 * @param File
	 * @return
	 */
	private boolean IsLastFCB(FCB Dir){
		try {
			RandomAccessFileCpp VirtualDiskRF = new RandomAccessFileCpp(VirtualDisk.getAbsolutePath(),"rw");
			/*
			 * 找到父目录
			 */
			VirtualDiskRF.seek(32 + Dir.FirstBlock * 1024);
			byte[] TempByteArray = new byte[32];
			VirtualDiskRF.read(TempByteArray, 0, 32);
			FCB ParentFCB = new FCB(TempByteArray);
			/*
			 * 找到Dir在父目录的位置
			 */
			int DirIndex = -1;
			for (int FCBIndex = 0;FCBIndex < 32;FCBIndex ++){
				//定位FCB
				VirtualDiskRF.seek(32 + ParentFCB.FirstBlock * 1024 + FCBIndex * 32);
				VirtualDiskRF.read(TempByteArray, 0, 32);
				FCB TempFCB = new FCB(TempByteArray);
				if (TempFCB.Type == 2){
					if (TempFCB.Name.equals(Dir.Name)){
						DirIndex = FCBIndex;
						break;
					}
				}
			}
			if (DirIndex == 31){
				return true;
			}
			/*
			 * 判断在dir之后是否还有目录
			 */
			for (int FCBIndex = DirIndex + 1;FCBIndex < 32;FCBIndex ++){
				//定位FCB
				VirtualDiskRF.seek(32 + ParentFCB.FirstBlock * 1024 + FCBIndex * 32);
				VirtualDiskRF.read(TempByteArray, 0, 32);
				FCB TempFCB = new FCB(TempByteArray);
				if (TempFCB.Type == 2){
					return false;
				}
			}
			return true;
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
			return false;
		}
	}
	
	private FCB GetParentFCB(FCB Dir){
		try {
			RandomAccessFileCpp VirtualDiskRF;
			VirtualDiskRF = new RandomAccessFileCpp(VirtualDisk.getAbsolutePath(),"rw");
			VirtualDiskRF.seek(32 + Dir.FirstBlock * 1024);
			byte[] TempByteArray = new byte[32];
			VirtualDiskRF.read(TempByteArray, 0, 32);
			FCB ParentFCB = new FCB(TempByteArray);
			return ParentFCB;
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 树形显示的递归函数
	 * @param Dir
	 */
	private void ShowTree(FCB Dir){
		try {
			RandomAccessFileCpp VirtualDiskRF = new RandomAccessFileCpp(VirtualDisk.getAbsolutePath(),"rw");
			int FCBListBlock = Dir.FirstBlock;
			
			
			int FCBStartIndex;
			if (Dir.FirstBlock == Root.FirstBlock){
				FCBStartIndex = 0;
			}
			else{
				FCBStartIndex = 1;
			}
			for (int FCBIndex = FCBStartIndex;FCBIndex < 32;FCBIndex ++){
				//定位FCB
				VirtualDiskRF.seek(32 + FCBListBlock * 1024 + FCBIndex * 32);
				byte[] TempByteArray = new byte[32];
				VirtualDiskRF.read(TempByteArray, 0, 32);
				FCB TempFCB = new FCB(TempByteArray);
				if (TempFCB.Type == 2){
					/*
					 * 树形显示
					 */
					ArrayList<String> StrArray = new ArrayList<String>();
					StrArray.add(TempFCB.Name);
					if (this.IsLastFCB(TempFCB)){
						StrArray.add("└─");
					}
					else{
						StrArray.add("├─");
					}
					FCB TempFCB2 = this.GetParentFCB(TempFCB);
					while (TempFCB2.FirstBlock != CurrFile.FirstBlock){
						//StrArray.add(TempFCB2.Name);
						if (this.IsLastFCB(TempFCB2)){
							StrArray.add("  ");
						}
						else{
							StrArray.add("│ ");
						}
						TempFCB2 = this.GetParentFCB(TempFCB2);
					}
					for (int i = StrArray.size() - 1;i >= 0; i --){
						System.out.print(StrArray.get(i));
					}
					System.out.println();
					ShowTree(TempFCB);
				}
			}
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
	
	/**
	 * 删除文件
	 * @param FileName
	 * @return
	 */
	public boolean DelFile(String FileName){
		try {
			RandomAccessFileCpp VirtualDiskRF = new RandomAccessFileCpp(VirtualDisk.getAbsolutePath(),"rw");
			int FCBListBlock = CurrFile.FirstBlock;
			/*
			 * 搜索是否具有相同文件名的FCB
			 */
			for (int FCBIndex = 0;FCBIndex < 32;FCBIndex ++){
				//定位FCB
				VirtualDiskRF.seek(32 + FCBListBlock * 1024 + FCBIndex * 32);
				byte[] TempByteArray = new byte[32];
				VirtualDiskRF.read(TempByteArray, 0, 32);
				FCB TempFCB = new FCB(TempByteArray);
				if (TempFCB.Type == 1){
					if (TempFCB.Name.equals(FileName)){
						/*
						 * 删除这个文件FCB
						 */
						TempFCB.Type = 0;
						VirtualDiskRF.seek(32 + FCBListBlock * 1024 + FCBIndex * 32);
						VirtualDiskRF.write(TempFCB.ToByteArray());
						/*
						 * 将这个文件的所占的FAT表清空
						 */
						int TempBlockIndex = TempFCB.FirstBlock;
						while (TempBlockIndex != 0xffff){
							int NextBlockIndex;
							VirtualDiskRF.seek(TempBlockIndex * 2);
							NextBlockIndex = VirtualDiskRF.readUnsignedShort();
							VirtualDiskRF.seek(TempBlockIndex * 2);
							VirtualDiskRF.writeShort(0x0000);
							TempBlockIndex = NextBlockIndex;
						}
						return true;
					}
				}
			}
			System.out.println("文件不存在。");
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 创建文件
	 * @param FileName
	 * @param FileLength
	 * @return
	 */
	public boolean MakeFile(String FileName,int FileLength){
		try {
			RandomAccessFileCpp VirtualDiskRF = new RandomAccessFileCpp(VirtualDisk.getAbsolutePath(),"rw");
			int FCBListBlock = CurrFile.FirstBlock;
			/*
			 * 搜索是否具有相同文件名的FCB
			 */
			for (int FCBIndex = 0;FCBIndex < 32;FCBIndex ++){
				//定位FCB
				VirtualDiskRF.seek(32 + FCBListBlock * 1024 + FCBIndex * 32);
				byte[] TempByteArray = new byte[32];
				VirtualDiskRF.read(TempByteArray, 0, 32);
				FCB TempFCB = new FCB(TempByteArray);
				if (TempFCB.Type != 0){
					if (TempFCB.Name.equals(FileName)){
						System.out.println("该文件已经存在。");
						return false;
					}
				}
			}
			/*
			 * 新建FCB
			 */
			if (FileName.length() > 8){
				System.out.println("文件名长度超过8字节。");
				return false;
			}
			FCB desFCB = new FCB();
			desFCB.Size = FileLength;
			desFCB.SetTime();
			desFCB.Name = FileName;
			desFCB.Type = 1;
			
			/*
			 * 计算所需空间
			 */
			int BlockNum = (int) Math.ceil((double)FileLength / 1024);
			//System.out.println("BlockNum" + BlockNum);
			int[] desIntArray = new int[BlockNum];
			desIntArray = this.GetEmptyBlockIndex(BlockNum);
			if (desIntArray == null){
				System.out.println("磁盘空间不足。");
				VirtualDiskRF.close();
				return false;
			}
			desFCB.FirstBlock = desIntArray[0];
			
			/*
			 * FCBList是否还有地方存新建的文件
			 */
			int EmptyFCBIndex = -1;
			for (int FCBIndex = 0;FCBIndex < 32;FCBIndex ++){
				//定位FCB
				VirtualDiskRF.seek(32 + FCBListBlock * 1024 + FCBIndex * 32);
				byte[] TempByteArray = new byte[32];
				VirtualDiskRF.read(TempByteArray, 0, 32);
				FCB TempFCB = new FCB(TempByteArray);
				if (TempFCB.Type == 0){
					EmptyFCBIndex = FCBIndex;
					break;
				}
			}
			if (EmptyFCBIndex == -1){
				System.out.println("该目录下文件数量已经到达上限。");
				return false;
			}
			/*
			 * 插入新建的FCB至文件 
			 */
			byte[] TempByte;
			TempByte = desFCB.ToByteArray();
			VirtualDiskRF.seek(32 + FCBListBlock * 1024 + EmptyFCBIndex * 32);
			VirtualDiskRF.write(TempByte);
			
			/*
			 * 修改FAT，并且清空新建文件所占的块
			 */
			for (int i = 0;i < BlockNum - 1;i ++){
				VirtualDiskRF.seek(32 + desIntArray[i] * 1024);
				for (int j = 0;j < 1024;j ++){
					VirtualDiskRF.writeByte(0);
				}
				VirtualDiskRF.seek(desIntArray[i] * 2);
				VirtualDiskRF.writeShort(desIntArray[i + 1]);
			}
			VirtualDiskRF.seek(32 + desIntArray[BlockNum - 1] * 1024);
			for (int j = 0;j < 1024;j ++){
				VirtualDiskRF.writeByte(0);
			}
			VirtualDiskRF.seek(desIntArray[BlockNum - 1] * 2);
			VirtualDiskRF.writeShort(0xffff);
			
			VirtualDiskRF.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean ChangeDir(String DirName){
		try {
			RandomAccessFileCpp VirtualDiskRF = new RandomAccessFileCpp(VirtualDisk.getAbsolutePath(),"rw");
			int FCBListBlock = CurrFile.FirstBlock;
			if (DirName.equals(".")){
				CurrFile = Root;
				VirtualDiskRF.close();
				return true;
			}
			else if (DirName.equals("..")){
				if (CurrFile.FirstBlock == Root.FirstBlock){
					System.out.println("已经到达根目录。");
					VirtualDiskRF.close();
					return false;
				}
				else{
					VirtualDiskRF.seek(32 + FCBListBlock * 1024);
					byte[] TempByteArray = new byte[32];
					VirtualDiskRF.read(TempByteArray, 0, 32);
					FCB TempFCB = new FCB(TempByteArray);
					CurrFile = TempFCB;
					VirtualDiskRF.close();
					return true;
				}
				
			}
			else{
				/*
				 * 搜索是否具有相同文件名的FCB
				 */
				for (int FCBIndex = 0;FCBIndex < 32;FCBIndex ++){
					//定位FCB
					VirtualDiskRF.seek(32 + FCBListBlock * 1024 + FCBIndex * 32);
					byte[] TempByteArray = new byte[32];
					VirtualDiskRF.read(TempByteArray, 0, 32);
					FCB TempFCB = new FCB(TempByteArray);
					if (TempFCB.Type == 2){
						if (TempFCB.Name.equals(DirName)){
							CurrFile = TempFCB;
							VirtualDiskRF.close();
							return true;
						}
					}
				}
				System.out.println("文件不存在。");
				VirtualDiskRF.close();
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 显示当前目录文件
	 */
	public void ShowDir(){
		try {
			RandomAccessFileCpp VirtualDiskRF = new RandomAccessFileCpp(VirtualDisk.getAbsolutePath(),"rw");
			int FCBListBlock = CurrFile.FirstBlock;
			System.out.println(String.valueOf(Root.DateTime) + (Root.Type == 1 ? "\t\t" : "\t<DIR>\t") + Root.Size + "\t.");
			for (int FCBIndex = 0;FCBIndex < 32;FCBIndex ++){
				//定位FCB
				VirtualDiskRF.seek(32 + FCBListBlock * 1024 + FCBIndex * 32);
				byte[] TempByteArray = new byte[32];
				VirtualDiskRF.read(TempByteArray, 0, 32);
				FCB TempFCB = new FCB(TempByteArray);
				if (TempFCB.Type != 0){
					/*
					 * 根目录分开处理
					 */
					if (CurrFile.FirstBlock == Root.FirstBlock){
						System.out.println(String.valueOf(TempFCB.DateTime) + (TempFCB.Type == 1 ? "\t\t" : "\t<DIR>\t") + TempFCB.Size + "\t" + TempFCB.Name);
					}
					else{
						if (FCBIndex == 0){
							System.out.println(String.valueOf(TempFCB.DateTime) + (TempFCB.Type == 1 ? "\t\t" : "\t<DIR>\t") + TempFCB.Size + "\t..");
						}
						else{
							System.out.println(String.valueOf(TempFCB.DateTime) + (TempFCB.Type == 1 ? "\t\t" : "\t<DIR>\t") + TempFCB.Size + "\t" + TempFCB.Name);
						}
					}
				}
			}
			VirtualDiskRF.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 删除目录
	 * @param DirName
	 * @return
	 */
	public boolean DelDir(String DirName){
		try {
			if (DirName.equals("..") | DirName.equals(".")){
				System.out.println("这个文件不能被删除。");
				return false;
			}
			RandomAccessFileCpp VirtualDiskRF = new RandomAccessFileCpp(VirtualDisk.getAbsolutePath(),"rw");
			int FCBListBlock = CurrFile.FirstBlock;
			/*
			 * 搜索是否具有相同文件名的FCB
			 */
			for (int FCBIndex = 0;FCBIndex < 32;FCBIndex ++){
				//定位FCB
				VirtualDiskRF.seek(32 + FCBListBlock * 1024 + FCBIndex * 32);
				byte[] TempByteArray = new byte[32];
				VirtualDiskRF.read(TempByteArray, 0, 32);
				FCB TempFCB = new FCB(TempByteArray);
				if (TempFCB.Type == 2){
					if (TempFCB.Name.equals(DirName)){
						/*
						 * 判断是否为空
						 */
						boolean IsEmpty = true;
						for (int FCBIndex2 = 1;FCBIndex2 < 32;FCBIndex2 ++){
							//定位FCB
							VirtualDiskRF.seek(32 + TempFCB.FirstBlock * 1024 + FCBIndex2 * 32);
							byte[] TempByteArray2 = new byte[32];
							VirtualDiskRF.read(TempByteArray2, 0, 32);
							FCB TempFCB2 = new FCB(TempByteArray2);
							if (TempFCB2.Type != 0){
								IsEmpty = false;
								break;
							}
						}
						if (IsEmpty){
							/*
							 * 修改FCBList
							 */
							TempFCB.Type = 0;
							VirtualDiskRF.seek(32 + FCBListBlock * 1024 + FCBIndex * 32);
							VirtualDiskRF.write(TempFCB.ToByteArray());
							/*
							 * 修改FAT
							 */
							VirtualDiskRF.seek(TempFCB.FirstBlock * 2);
							VirtualDiskRF.writeShort(0x0000);
							return true;
						}
						else{
							System.out.println("该文件夹不为空。");
							VirtualDiskRF.close();
							return false;
						}
					}
				}
			}
			System.out.println("该文件不存在。");
			VirtualDiskRF.close();
			return false;
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 在当前目录添加子目录
	 * @param DirName 子目录名称
	 * @return
	 */
	public boolean MakeDir(String DirName){
		try {
			RandomAccessFileCpp VirtualDiskRF = new RandomAccessFileCpp(VirtualDisk.getAbsolutePath(),"rw");
			int FCBListBlock = CurrFile.FirstBlock;
			/*
			 * 搜索是否具有相同文件名的FCB
			 */
			for (int FCBIndex = 0;FCBIndex < 32;FCBIndex ++){
				//定位FCB
				VirtualDiskRF.seek(32 + FCBListBlock * 1024 + FCBIndex * 32);
				byte[] TempByteArray = new byte[32];
				VirtualDiskRF.read(TempByteArray, 0, 32);
				FCB TempFCB = new FCB(TempByteArray);
				if (TempFCB.Type != 0){
					if (TempFCB.Name.equals(DirName)){
						System.out.println("该文件已经存在。");
						return false;
					}
				}
			}
			/*
			 * 新建FCB
			 */
			if (DirName.length() > 8){
				System.out.println("文件名长度超过8字节。");
				return false;
			}
			FCB desFCB = new FCB();
			desFCB.SetTime();
			desFCB.Name = DirName;
			desFCB.Type = 2;
			int[] TempIntArray = this.GetEmptyBlockIndex(1);
			if (TempIntArray == null){
				System.out.println("磁盘空间已满。");
				return false;
			}
			desFCB.FirstBlock = TempIntArray[0];
			/*
			 * FCBList是否还有地方存新建的文件
			 */
			int EmptyFCBIndex = -1;
			for (int FCBIndex = 0;FCBIndex < 32;FCBIndex ++){
				//定位FCB
				VirtualDiskRF.seek(32 + FCBListBlock * 1024 + FCBIndex * 32);
				byte[] TempByteArray = new byte[32];
				VirtualDiskRF.read(TempByteArray, 0, 32);
				FCB TempFCB = new FCB(TempByteArray);
				if (TempFCB.Type == 0){
					EmptyFCBIndex = FCBIndex;
					break;
				}
			}
			if (EmptyFCBIndex == -1){
				System.out.println("该目录下文件数量已经到达上限。");
				return false;
			}
			/*
			 * 插入新建的FCB至文件 
			 */
			byte[] TempByte;
			TempByte = desFCB.ToByteArray();
			VirtualDiskRF.seek(32 + FCBListBlock * 1024 + EmptyFCBIndex * 32);
			VirtualDiskRF.write(TempByte);
			
			/*
			 * 修改FAT
			 */
			VirtualDiskRF.seek(desFCB.FirstBlock * 2);
			VirtualDiskRF.writeShort(0xffff);
			/*
			 * 清空FCBList
			 */
			for (int FCBIndex = 0;FCBIndex < 32;FCBIndex ++){
				VirtualDiskRF.seek(32 + desFCB.FirstBlock * 1024 + FCBIndex * 32);
				FCB TempFCB = new FCB();
				TempFCB.Type = 0;
				byte[] TempByteArray = TempFCB.ToByteArray();
				VirtualDiskRF.write(TempByteArray);
			}
			/*
			 * 在FCBList中添加上级FCB
			 */
			VirtualDiskRF.seek(32 + desFCB.FirstBlock * 1024);
			byte[] TempByteArray = CurrFile.ToByteArray();
			VirtualDiskRF.write(TempByteArray);
			VirtualDiskRF.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * 获得磁盘中的空白块index
	 * @param BlockNum
	 * @return null表示获取失败
	 */
	public int[] GetEmptyBlockIndex(int BlockNum){
		try {
			int[] desIntArray = new int[BlockNum];
			RandomAccessFileCpp VirtualDiskRF = new RandomAccessFileCpp(VirtualDisk.getAbsolutePath(),"rw");
			int j = 0;
			for (int i = 0;i < 16;i ++){
				if (VirtualDiskRF.readShort() == 0x0000){
					desIntArray[j] = i;
					j ++;
					if (j == BlockNum){
						break;
					}
				}
			}
			VirtualDiskRF.close();
			if (j == BlockNum){
				return desIntArray;
			}
			else{
				return null;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public FileSystem(String FilePath){
		VirtualDisk = new File(FilePath);
		if (!VirtualDisk.canRead()){
			System.out.println("文件系统初始化错误！指定的虚拟磁盘文件" + FilePath + "不存在或者不可读。");
			this.Format();
		}
		/*
		 * 初始化rootFCB
		 */
		Root = new FCB();
		Root.Name = new String(".");
		Root.SetTime();
		Root.FirstBlock = 0;
		Root.Type = 2;
		/*
		 * CurrFile设置为root
		 */
		CurrFile = Root;
	}
	
	public boolean Format(){
		try {
			RandomAccessFileCpp VirtualDiskRF = new RandomAccessFileCpp(VirtualDisk.getAbsolutePath(),"rw");
			/**
			 * 写入FAT表，共16*2字节。
			 */
			for (int i = 0;i < 16;i ++){
				//写入short 2字节
				VirtualDiskRF.writeShort(0);
			}
			/**
			 * 设置FAT第0块内容
			 */
			long TempFileP = VirtualDiskRF.getFilePointer();
			VirtualDiskRF.seek(0);
			VirtualDiskRF.writeShort(0xffff);
			VirtualDiskRF.seek(TempFileP);
			/**
			 * 写入文件块，16*1024字节
			 */
			for (int i = 0;i < 16;i ++){
				//写入512个short
				for (int j = 0;j < 512;j ++){
					VirtualDiskRF.writeShort(0);
				}
			}
			VirtualDiskRF.close();
			return true;
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			System.out.println(VirtualDisk.getAbsolutePath()+ "文件没有找到或者不可读写。");
			return false;
		}
	}
}
