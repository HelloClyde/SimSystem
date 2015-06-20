package 操作系统实验;

import sourceSystem.SourceSystem;
import ProcessSystem.ProcessManager;
import deviceSystem.DeviceSystem;
import dispatchSystem.DispatchSystem;
import fileSystem.FileSystem;

public class VirtualSystem {
	static public DeviceSystem MainDeviceSystem = new DeviceSystem();
	static public ProcessManager MainProcessManager = new ProcessManager();
	static public FileSystem MainFileSystem = new FileSystem("VirtualDisk.bin");
	static public DispatchSystem MainDispatchSystem = null;
	static public SourceSystem MainSourceSystem = null;
}
