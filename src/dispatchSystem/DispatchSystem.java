package dispatchSystem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class DispatchSystem {
	
	static public void main(String[] args){
		DispatchSystem MainDispatchSystem = new DispatchSystem(0);
		MainDispatchSystem.AddProcess("A", 0, 3);
		MainDispatchSystem.AddProcess("B", 0, 2);
		MainDispatchSystem.AddProcess("C", 3, 2);
		MainDispatchSystem.AddProcess("D", 4, 4);
		MainDispatchSystem.CalRunResult(-1);
	}
	/*
	 * 调度类型：0,先来先服务，1，短作业优先，2，时间片调度
	 */
	int DispatchType;
	//初始进程队列
	PriorityQueue<Process> ProcessQueue;
	//就绪队列
	Queue<Process> RunningProcessQueue;
	//作业已经完成队列
	ArrayList<Process> FinishedProcessQueue = new ArrayList<Process>();
	
	//根据进程到达时间由小到大排序
	Comparator<Process> ProcessArrTimeComper =  new Comparator<Process>(){  
        public int compare(Process arg0, Process arg1) {  
        	Process Process1 = (Process)arg0;
    		Process Process2 = (Process)arg1;
    		if (Process1.ArrivalTime > Process2.ArrivalTime){
    			return 1;
    		}
    		else if (Process1.ArrivalTime < Process2.ArrivalTime){
    			return -1;
    		}
    		else{
    			return 0;
    		}
        }
	};
	
	//根据进程服务时间由小到大排序
	Comparator<Process> ProcessBurTimeComper =  new Comparator<Process>(){  
        public int compare(Process arg0, Process arg1) {  
        	Process Process1 = (Process)arg0;
    		Process Process2 = (Process)arg1;
    		if (Process1.BurstTime > Process2.BurstTime){
    			return 1;
    		}
    		else if (Process1.BurstTime < Process2.BurstTime){
    			return -1;
    		}
    		else{
    			return 0;
    		}
        }
	};
	
	public DispatchSystem(int SrcDispatchType){
		this.DispatchType = SrcDispatchType;
		this.ProcessQueue = new PriorityQueue<Process>(ProcessArrTimeComper);
		if (this.DispatchType == 0){
			this.RunningProcessQueue = new PriorityQueue<Process>(ProcessArrTimeComper);
		}
		else if (this.DispatchType == 1){
			this.RunningProcessQueue = new PriorityQueue<Process>(ProcessBurTimeComper);
		}
		else if (this.DispatchType == 2){
			this.RunningProcessQueue = new LinkedList<Process>();
		}
	}
	
	public void AddProcess(String Name,int ArrTime,int BurTime){
		Process TempProcess = new Process(Name, ArrTime, BurTime);
		this.ProcessQueue.add(TempProcess);
	}
	
	/**
	 * 
	 * @param TimeN -1代表时间片不轮转
	 */
	public void CalRunResult(int TimeN){
		int TimeNow;
		int TimeOverN;
		Process RunProcess = null;
		TimeNow = 0;
		TimeOverN = 0;
		while (!(this.ProcessQueue.isEmpty() && this.RunningProcessQueue.isEmpty())){
			//把到达时间的进程加入就绪队列
			Process ReadProcess = this.ProcessQueue.peek();
			while (ReadProcess != null && ReadProcess.ArrivalTime <= TimeNow){
				this.RunningProcessQueue.add(this.ProcessQueue.poll());
				ReadProcess = this.ProcessQueue.peek();
			}
			//运行就绪进程最上一个，运行时间加1
			if (RunProcess == null){
				RunProcess = this.RunningProcessQueue.peek();
				//初始化时间片
				TimeOverN = 0;
			}
			else{
				//判断时间片是否到达
				if (TimeN == -1){
					//不会进行时间片调度
				}
				else{
					if (TimeOverN == TimeN){
						//把原来那个调到最后
						this.RunningProcessQueue.remove(RunProcess);
						this.RunningProcessQueue.add(RunProcess);
						RunProcess = this.RunningProcessQueue.peek();
						TimeOverN = 0;
					}
				}
			}
			System.out.println("Name:" + RunProcess.ProcessName + " Time:" + TimeNow);
			RunProcess.RunnedTime ++;
			TimeNow ++;
			TimeOverN ++;
			
			//把运行完成的转到完成队列
			if (RunProcess.RunnedTime >= RunProcess.BurstTime){
				RunProcess.FinishedTime = TimeNow;
				this.RunningProcessQueue.remove(RunProcess);
				this.FinishedProcessQueue.add(RunProcess);
				RunProcess = null;
			}
		}
		System.out.println("进程名\t\t\t到达时间\t\t\t服务时间\t\t\t完成时间\t\t\t周转时间\t\t\t带权周转时间");
		double ExistTimeTotal = 0;
		double SuperExistTimeTotal = 0;
		for (Process TempProcess:this.FinishedProcessQueue){
			double ExistTime = TempProcess.FinishedTime - TempProcess.ArrivalTime;
			double SuperExistTime = ExistTime / TempProcess.BurstTime;
			System.out.println(TempProcess.ProcessName + "\t\t\t" + TempProcess.ArrivalTime + "\t\t\t" + TempProcess.BurstTime + "\t\t\t" + TempProcess.FinishedTime
					+ "\t\t\t" + ExistTime + "\t\t\t" + SuperExistTime);
			ExistTimeTotal += ExistTime;
			SuperExistTimeTotal += SuperExistTime;
		}
		System.out.println("平均周转时间:" + ExistTimeTotal / this.FinishedProcessQueue.size() + "\t\t\t" + "平均带权周转时间:" + SuperExistTimeTotal / this.FinishedProcessQueue.size());
	}
}
