package 操作系统实验;

import java.util.Scanner;

import sourceSystem.SourceSystem;
import dispatchSystem.DispatchSystem;

public class Main{
	
	static public void main(String[] arg){
		Scanner MyScanner = new Scanner(System.in);
		while (true){
			System.out.print("$");
			String LineString;
			LineString = MyScanner.nextLine();
			String[] args;
			args = LineString.split(" +");
			if (args[0].equals("help")){
				System.out.println("AddCh CHName --------------------- 添加通道：通道名称");
				System.out.println("AddCo CHName COName -------------- 添加控制器：通道名称 控制器名称");
				System.out.println("AddD COName DName ---------------- 添加设备：控制器名称 设备名称");
				System.out.println("DelCh CHName --------------------- 删除通道：通道名称");
				System.out.println("DelCo COName --------------------- 删除控制器：控制器名称");
				System.out.println("DelD DName ----------------------- 删除设备：设备名称");
				System.out.println("ShowDs --------------------------- 显示所有设备结构");
				System.out.println("ApplyD DName---------------------- 进程申请设备：设备名");
				System.out.println("RecoverD PName DName-------------- 进程归还设备：进程名 设备名");
				System.out.println("CreateP PName -------------------- 创建进程：进程名");
				System.out.println("ShowP ---------------------------- 显示进程队列");
				System.out.println("TimeOverP ------------------------ 时间片到");
				System.out.println("ExitP ---------------------------- 退出进程");
				System.out.println("MD ------------------------------- 创建子目录：目录名称");
				System.out.println("CD ------------------------------- 切换目录：目录名称");
				System.out.println("RD ------------------------------- 删除空目录：目录名称");
				System.out.println("MK ------------------------------- 创建空文件：文件名 文件大小");
				System.out.println("DEL ------------------------------ 删除文件： 文件名");
				System.out.println("DIR ------------------------------ 列出当前目录所有文件项");
				System.out.println("FORMAT --------------------------- 格式化磁盘");
				System.out.println("TREE ----------------------------- 显示树形目录");
				System.out.println("CPY ------------------------------ 复制物理磁盘文件至虚拟磁盘：虚拟磁盘文件名 物理磁盘文件路径");
				System.out.println("MKCPY ---------------------------- 新建文件，并且复制物理磁盘文件内容： 虚拟磁盘文件名 物理磁盘文件路径");
				System.out.println("SHOWF ---------------------------- 显示文件内容： 文件名");
				System.out.println("DisType -------------------------- 进程调度方式： 类型名，FCFS,SJF,RR");
				System.out.println("DisAddP -------------------------- 添加进程： 进程名 到达时间 服务时间");
				System.out.println("ShowDis -------------------------- 显示调度结果：【轮转时间】");
				System.out.println("SetSource ------------------------ 设置资源：资源数据");
				System.out.println("SourceAddP ----------------------- 添加资源进程：进程名 所需资源 已有资源");
				System.out.println("GetSafeSer ----------------------- 获取安全序列");
				System.out.println("CanGetSource --------------------- 询问是否能获得资源:进程名 资源数据");
			}
			else if (IsOrder(args,"AddCh",1)){
				VirtualSystem.MainDeviceSystem.AddCH(args[1]);
			}
			else if (IsOrder(args,"AddCo",2)){
				VirtualSystem.MainDeviceSystem.AddControler(args[1], args[2]);
			}
			else if (IsOrder(args,"AddD",2)){
				VirtualSystem.MainDeviceSystem.AddDevice(args[1], args[2]);
			}
			else if (IsOrder(args,"DelCh",1)){
				VirtualSystem.MainDeviceSystem.DeleteCH(args[1]);
			}
			else if (IsOrder(args,"DelCo",1)){
				VirtualSystem.MainDeviceSystem.DeleteControler(args[1]);
			}
			else if (IsOrder(args,"DelD",1)){
				VirtualSystem.MainDeviceSystem.DeleteDevice(args[1]);
			}
			else if (IsOrder(args,"ShowDs",0)){
				VirtualSystem.MainDeviceSystem.ShowDevieSystem();
			}
			else if (IsOrder(args,"ApplyD",1)){
				VirtualSystem.MainDeviceSystem.ApplyDevice(args[1]);
			}
			else if (IsOrder(args,"RecoverD",2)){
				VirtualSystem.MainDeviceSystem.RecoverDevice(args[1], args[2]);
			}
			else if (IsOrder(args,"CreateP",1)){
				VirtualSystem.MainProcessManager.CreateProcess(args[1]);
			}
			else if (IsOrder(args,"ShowP",0)){
				VirtualSystem.MainProcessManager.ShowProcessQueue();
			}
			else if (IsOrder(args,"TimeOverP",0)){
				VirtualSystem.MainProcessManager.TimeOver();
			}
			else if (IsOrder(args,"ExitP",0)){
				VirtualSystem.MainProcessManager.ExitProcess();
			}
			else if (IsOrder(args,"MD",1)){
				VirtualSystem.MainFileSystem.MakeDir(args[1]);
			}
			else if (IsOrder(args,"CD",1)){
				VirtualSystem.MainFileSystem.ChangeDir(args[1]);
			}
			else if (IsOrder(args,"RD",1)){
				VirtualSystem.MainFileSystem.DelDir(args[1]);
			}
			else if (IsOrder(args,"MK",2)){
				VirtualSystem.MainFileSystem.MakeFile(args[1], Integer.valueOf(args[2]));
			}
			else if (IsOrder(args,"DEL",1)){
				VirtualSystem.MainFileSystem.DelFile(args[1]);
			}
			else if (IsOrder(args,"DIR",0)){
				VirtualSystem.MainFileSystem.ShowDir();
			}
			else if (IsOrder(args,"FORMAT",0)){
				VirtualSystem.MainFileSystem.Format();
			}
			else if (IsOrder(args,"TREE",0)){
				VirtualSystem.MainFileSystem.ShowTree();
			}
			else if (IsOrder(args,"CPY",2)){
				VirtualSystem.MainFileSystem.CopyFile(args[1], args[2]);
			}
			else if (IsOrder(args,"SHOWF",1)){
				VirtualSystem.MainFileSystem.ShowFile(args[1]);
			}
			else if (IsOrder(args,"MKCPY",2)){
				VirtualSystem.MainFileSystem.MKCPY(args[1], args[2]);
			}
			else if (IsOrder(args,"DisType",1)){
				if (args[1].equals("FCFS")){
					VirtualSystem.MainDispatchSystem = new DispatchSystem(0);
				}
				else if (args[1].equals("SJF")){
					VirtualSystem.MainDispatchSystem = new DispatchSystem(1);
				}
				else if (args[1].equals("RR")){
					VirtualSystem.MainDispatchSystem = new DispatchSystem(2);
				}
				else{
					System.out.println("未知类型。");
				}
			}
			else if (IsOrder(args,"DisAddP",3)){
				if (VirtualSystem.MainDispatchSystem == null){
					System.out.println("调度系统未初始化。");
				}
				else{
					VirtualSystem.MainDispatchSystem.AddProcess(args[1], Integer.valueOf(args[2]), Integer.valueOf(args[3]));
				}
			}
			else if (IsOrder(args,"ShowDis",1)){
				VirtualSystem.MainDispatchSystem.CalRunResult(Integer.valueOf(args[1]));
			}
			else if (IsOrder(args,"ShowDis",0)){
				VirtualSystem.MainDispatchSystem.CalRunResult(-1);
			}
			else if (args[0].toLowerCase().equals("SetSource".toLowerCase())){
				int[] TempIntArray = new int[args.length - 1];
				for (int i = 1;i < args.length;i ++){
					TempIntArray[i - 1] = Integer.valueOf(args[i]);
				}
				VirtualSystem.MainSourceSystem = new SourceSystem(TempIntArray);
			}
			else if (args[0].toLowerCase().equals("SourceAddP".toLowerCase())){
				if (VirtualSystem.MainSourceSystem == null){
					System.out.println("资源未初始化。");
				}
				else{
					int SourceLength = VirtualSystem.MainSourceSystem.GetSourceNum();
					int TempIndex = 2;
					int[] Total = new int[SourceLength];
					for (int i = 0;i < SourceLength;i ++,TempIndex ++){
						Total[i] = Integer.valueOf(args[TempIndex]);
					}
					int[] Exist = new int[SourceLength];
					for (int i = 0;i < SourceLength;i ++,TempIndex ++){
						Exist[i] = Integer.valueOf(args[TempIndex]);
					}
					VirtualSystem.MainSourceSystem.AddProcess(args[1],Total,Exist);
				}
			}
			else if (IsOrder(args,"GetSafeSer",0)){
				if (VirtualSystem.MainSourceSystem == null){
					System.out.println("资源未初始化。");
				}
				else{
					VirtualSystem.MainSourceSystem.ShowSafeSeries();
				}
			}
			else if (args[0].toLowerCase().equals("CanGetSource".toLowerCase())){
				if (VirtualSystem.MainSourceSystem == null){
					System.out.println("资源未初始化。");
				}
				else{
					int TempIndex = 2;
					int SourceLength = VirtualSystem.MainSourceSystem.GetSourceNum();
					int[] Total = new int[SourceLength];
					for (int i = 0;i < SourceLength;i ++,TempIndex ++){
						Total[i] = Integer.valueOf(args[TempIndex]);
					}
					System.out.println(VirtualSystem.MainSourceSystem.IsQuest(args[1], Total));
				}
			}
			else{
				System.out.println("未知指令！");
			}
			
		}
	}
	
	static public boolean IsOrder(String[] args,String Order,int argc){
		if (args.length == argc + 1 && args[0].toLowerCase().equals(Order.toLowerCase())) return true;
		else return false;
	}
}
