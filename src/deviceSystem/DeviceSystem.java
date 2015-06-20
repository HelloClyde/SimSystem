package deviceSystem;

import java.util.ArrayList;

import 操作系统实验.VirtualSystem;
import ProcessSystem.*;
import ProcessSystem.Process;

public class DeviceSystem {
	/*
	 * DCT列表，设备
	 */
	ArrayList <DCT> DCTArray = new ArrayList<DCT>();
	/*
	 * COCT列表，控制器
	 */
	ArrayList <COCT> COCTArray = new ArrayList<COCT>();
	/*
	 * CHCT列表，通道
	 */
	ArrayList<CHCT> CHCTArray = new ArrayList<CHCT>();
	
	/**
	 * 为就绪队列的进程分配设备
	 * @param DeviceName
	 * @return
	 */
	public boolean ApplyDevice(String DeviceName){
		/*
		 * 在DCT列表中搜索设备
		 */
		for (DCT TempD:DCTArray){
			if (TempD.name.equals(DeviceName)){
				Process TempProcess;
				TempProcess = VirtualSystem.MainProcessManager.ExecutionQueue.peek();
				if (TempProcess != null){
					if (TempD.process == null){
						TempD.process = TempProcess;
						COCT TempCO;
						TempCO = (COCT) TempD.parent;
						if (TempCO.process == null){
							TempCO.process = TempProcess;
							CHCT TempCH;
							TempCH = (CHCT) TempCO.parent;
							if (TempCH.process == null){
								TempCH.process = TempProcess;
								VirtualSystem.MainProcessManager.BlockingProcess();
								System.out.println("设备分配成功。");
								return true;
							}
							else{
								TempCH.waitinglist.add(TempProcess);
								VirtualSystem.MainProcessManager.BlockingProcess();
								return false;
							}
						}
						else{
							TempCO.waitinglist.add(TempProcess);
							VirtualSystem.MainProcessManager.BlockingProcess();
							return false;
						}
					}
					else{
						TempD.waitinglist.add(TempProcess);
						VirtualSystem.MainProcessManager.BlockingProcess();
						return false;
					}
				}
				else{
					System.out.println("未在执行队列找到进程。");
					return false;
				}
			}
		}
		System.out.println("没有找到设备。");
		return false;
	}
	
	public boolean RecoverDevice(String ProcessName,String DeviceName){
		/*
		 * 搜索进程
		 */
		Process DesProcess;
		DesProcess = null;
		for (Process TempProcess:VirtualSystem.MainProcessManager.BlockingQueue){
			if (TempProcess.GetName().equals(ProcessName)){
				DesProcess = TempProcess;
				break;
			}
		}
		if (DesProcess == null){
			System.out.println("未在阻塞队列找到该进程。");
			return false;
		}
		/*
		 * 搜索设备
		 */
		for (DCT TempDCT:DCTArray){
			if (TempDCT.name.equals(DeviceName)){
				if (TempDCT.process == DesProcess){
					if (TempDCT.waitinglist.isEmpty()){
						TempDCT.process = null;
					}
					else{
						TempDCT.process = TempDCT.waitinglist.poll();
					}
					COCT TempCO;
					TempCO = (COCT) TempDCT.parent;
					if (TempCO.process == DesProcess){
						if (TempCO.waitinglist.isEmpty()){
							TempCO.process = null;
						}
						else{
							TempCO.process = TempCO.waitinglist.poll();
						}
						CHCT TempCH;
						TempCH = (CHCT) TempCO.parent;
						if (TempCH.process == DesProcess){
							if (TempCH.waitinglist.isEmpty()){
								TempCH.process = null;
							}
							else{
								TempCH.process = TempCH.waitinglist.poll();
							}
							System.out.println("设备回收成功。");
							VirtualSystem.MainProcessManager.WakeProcess(DesProcess);
							return true;
						}
						else{
							System.out.println("设备回收成功。");
							VirtualSystem.MainProcessManager.WakeProcess(DesProcess);
							return true;
						}
					}
					else{
						System.out.println("设备回收成功。");
						VirtualSystem.MainProcessManager.WakeProcess(DesProcess);
						return true;
					}
				}
				else{
					System.out.println("设备未处理进程。");
					return false;
				}
			}
		}
		System.out.println("未找到该设备。");
		return false;
	}
	
	/**
	 * 显示通道、控制器、设备结构
	 */
	public void ShowDevieSystem(){
		for (CHCT TempCHCT:CHCTArray){
			System.out.println("通道：" + TempCHCT.name);
			for (COCT TempCOCT:COCTArray){
				if (TempCOCT.parent == TempCHCT){
					System.out.println("\t控制器：" + TempCOCT.name);
					for (DCT TempDCT:DCTArray){
						if (TempDCT.parent == TempCOCT){
							System.out.println("\t\t设备：" + TempDCT.name);
						}
					}
				}
			}
		}
	}
	
	/**
	 * 删除通道，和通道下的控制器，控制器下的设备
	 * @param CHName
	 * @return
	 */
	public boolean DeleteCH(String CHName){
		/*
		 * 搜索通道，通道不存在则失败
		 */
		for (CHCT TempCHCT:CHCTArray){
			if (TempCHCT.name.equals(CHName)){
				/*
				 * 删除parent为此通道的控制器
				 */
				int COCTI,COCTL;
				COCTL = COCTArray.size();
				for (COCTI = 0;COCTI < COCTL;COCTI ++){
					if (COCTArray.get(COCTI).parent == TempCHCT){
						/*
						 * 删除parent为此控制器的所有设备
						 */
						int DCTI,DCTL;
						DCTL = DCTArray.size();
						for (DCTI = 0;DCTI < DCTL;DCTI ++){
							if (DCTArray.get(DCTI).parent == COCTArray.get(COCTI)){
								DCTArray.remove(DCTI);
								DCTI --;
								DCTL --;
							}
						}
						COCTArray.remove(COCTI);
						COCTI --;
						COCTL --;
					}
				}
				CHCTArray.remove(TempCHCT);
				return true;
			}
		}
		System.out.println("通道" + CHName + "已经不存在,删除失败。");
		return false;
	}
	
	/**
	 * 删除控制器与此控制器下的设备
	 * @param ControlerName
	 * @return
	 */
	public boolean DeleteControler(String ControlerName){
		/*
		 * 搜索控制器列表,如果控制器不存在则删除失败
		 */
		for (COCT TempCOCT:COCTArray){
			if (TempCOCT.name.equals(ControlerName)){
				/*
				 * 删除parent为此控制器的所有设备
				 */
				int DCTI,DCTL;
				DCTL = DCTArray.size();
				for (DCTI = 0;DCTI < DCTL;DCTI ++){
					if (DCTArray.get(DCTI).parent == TempCOCT){
						DCTArray.remove(DCTI);
						DCTI --;
						DCTL --;
					}
				}
				COCTArray.remove(TempCOCT);
				return true;
			}
		}
		System.out.println("控制器" + ControlerName + "已经不存在,删除失败。");
		return false;
	}
	
	/**
	 * 删除设备
	 * @param DeviceName
	 * @return
	 */
	public boolean DeleteDevice(String DeviceName){
		/*
		 * 搜索设备列表,如果设备不存在则删除失败
		 */
		for (DCT TempDCT:DCTArray){
			if (TempDCT.name.equals(DeviceName)){
				DCTArray.remove(TempDCT);
				return true;
			}
		}
		System.out.println("设备" + DeviceName + "已经不存在,删除失败。");
		return false;
	}
	
	/**
	 * 添加新通道
	 * @param CHName通道名称
	 * @return
	 */
	public boolean AddCH(String CHName){
		
		/*
		 * 搜索通道列表,如果通道已经存在则添加失败
		 */
		for (int i = 0;i < CHCTArray.size();i ++){
			if (CHCTArray.get(i).name.equals(CHName)){
				System.out.println("通道" + CHName + "已经存在,添加失败。");
				return false;
			}
		}
		/*
		 * 新建CH
		 */
		CHCT srcCHCT = new CHCT();
		srcCHCT.name = CHName;
		srcCHCT.parent = null;
		/*
		 * 添加入DCT列表
		 */
		CHCTArray.add(srcCHCT);
		return true;
	}
	
	/**
	 * 把控制器添加进通道
	 * @param CHName添加位置通道名称
	 * @param ControlerName想添加的控制器名称
	 * @return
	 */
	public boolean AddControler(String CHName,String ControlerName){
		/*
		 * 搜索通道列表
		 */
		CHCT desCHCT = null;
		for (int i = 0;i < CHCTArray.size();i ++){
			if (CHCTArray.get(i).name.equals(CHName)){
				desCHCT = CHCTArray.get(i);
				break;
			}
		}
		if (desCHCT == null){
			System.out.println("未找到通道" + CHName + ",添加失败。");
			return false;
		}
		else{
			/*
			 * 搜索控制器列表,如果控制器已经存在则添加失败
			 */
			for (int i = 0;i < COCTArray.size();i ++){
				if (COCTArray.get(i).name.equals(ControlerName)){
					System.out.println("控制器" + ControlerName + "已经存在,添加失败。");
					return false;
				}
			}
			/*
			 * 新建COCT
			 */
			COCT srcCOCT = new COCT();
			srcCOCT.name = ControlerName;
			srcCOCT.parent = desCHCT;
			/*
			 * 添加入DCT列表
			 */
			COCTArray.add(srcCOCT);
		}
		return true;
	}
	
	/**
	 * 向控制器添加设备
	 * @param ControlerName控制器名称
	 * @param DeviceName设备名称
	 * @return 添加结果
	 */
	public boolean AddDevice(String ControlerName,String DeviceName){
		/*
		 * 搜索控制器列表
		 */
		COCT desCOCT = null;
		for (int i = 0;i < COCTArray.size();i ++){
			if (COCTArray.get(i).name.equals(ControlerName)){
				desCOCT = COCTArray.get(i);
				break;
			}
		}
		if (desCOCT == null){
			System.out.println("未找到控制器" + ControlerName + ",添加失败。");
			return false;
		}
		else{
			/*
			 * 搜索设备列表,如果设备已经存在则添加失败
			 */
			for (int i = 0;i < DCTArray.size();i ++){
				if (DCTArray.get(i).name.equals(DeviceName)){
					System.out.println("设备" + DeviceName + "已经存在,添加失败。");
					return false;
				}
			}
			/*
			 * 新建DCT
			 */
			DCT srcDCT = new DCT();
			srcDCT.name = DeviceName;
			srcDCT.parent = desCOCT;
			/*
			 * 添加入DCT列表
			 */
			DCTArray.add(srcDCT);
		}
		return true;
	}
}
