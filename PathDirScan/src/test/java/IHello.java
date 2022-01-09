import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IHello extends Remote {
    public String sayHelloToSomeBody() throws RemoteException;
}