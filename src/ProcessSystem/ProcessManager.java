package ProcessSystem;

import java.util.LinkedList;
import java.util.Queue;


public class ProcessManager {
	//进程id生成为系统时间Ms+序列。
	long SerialNumber;
	//
	public Queue<Process> ReadQueue;
	public Queue<Process> ExecutionQueue;
	public Queue<Process> BlockingQueue;
	
	
	public ProcessManager(){
		SerialNumber = 0;
		ReadQueue = new LinkedList<Process>();
		ExecutionQueue = new LinkedList<Process>();
		BlockingQueue = new LinkedList<Process>();
	}
	
	public void CreateProcess(String srcName){
		Process TempProcess = new Process(srcName,new String(String.valueOf(System.currentTimeMillis()) + this.GetSerialNumber()));
		AddProcessToReadQueue(TempProcess);
		System.out.println("新建名称为" + srcName + "的进程。");
	}
	
	public void AddProcessToReadQueue(Process srcProcess){
		ReadQueue.offer(srcProcess);
		if (ExecutionQueue.size() == 0){
			ExecutionQueue.offer(srcProcess);
			ReadQueue.remove(srcProcess);
		}
	}
	
	public void ExecutionProcess(Process srcProcess){
		if (ExecutionQueue.size() != 0){
			System.out.println("执行队列已满，只能有一个执行进程。");
		}
		else{
			ExecutionQueue.offer(srcProcess);
			ReadQueue.remove(srcProcess);
		}
	}
	
	public void ExecutionProcess(){
		if (ExecutionQueue.size() != 0){
			System.out.println("执行队列已满，只能有一个执行进程。");
		}
		else{
			Process TempProcess;
			TempProcess = ReadQueue.poll();
			if (TempProcess == null){
				System.out.println("就绪队列为空，不能执行进程。");
			}
			else{
				ExecutionQueue.offer(TempProcess);
			}
		}
	}
	
	public void BlockingProcess(){
		if (ExecutionQueue.size() == 0){
			System.out.println("执行队列为空。");
		}
		else{
			Process TempProcess;
			TempProcess = ExecutionQueue.poll();
			BlockingQueue.offer(TempProcess);
			System.out.println("名称为" + TempProcess.name + "的因等待IO被阻塞。");
			ExecutionProcess();
		}
	}
	
	public void WakeProcess(Process srcProcess){
		ReadQueue.offer(srcProcess);
		BlockingQueue.remove(srcProcess);
		ExecutionProcess();
	}
	
	public void TimeOver(){
		if (ExecutionQueue.size() == 0){
			System.out.println("执行队列为空。");
		}
		else{
			Process TempProcess;
			TempProcess = ExecutionQueue.poll();
			ReadQueue.offer(TempProcess);
			ExecutionProcess();
			System.out.println("名称为" + TempProcess.name + "的进程时间片到。");
		}
	}
	
	public void ExitProcess(){
		if (ExecutionQueue.size() == 0){
			System.out.println("执行队列为空。");
		}
		else{
			Process TempProcess;
			TempProcess = ExecutionQueue.poll();
			TempProcess.Destory();
			System.out.println("名称为" + TempProcess.name + "的进程已经退出。");
			ExecutionProcess();
		}
	}
	
	private long GetSerialNumber(){
		long returnN;
		returnN = SerialNumber;
		SerialNumber ++;
		if (SerialNumber >= 10){
			SerialNumber = 0;
		}
		return returnN;
	}
	
	public void ShowProcessQueue(){
		System.out.println("就绪队列：");
		for (Process TempProcess:ReadQueue){
			System.out.println(TempProcess.GetName());
		}
		System.out.println("执行队列：");
		for (Process TempProcess:ExecutionQueue){
			System.out.println(TempProcess.GetName());
		}
		System.out.println("阻塞队列：");
		for (Process TempProcess:BlockingQueue){
			System.out.println(TempProcess.GetName());
		}
	}
}
