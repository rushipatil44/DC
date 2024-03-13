package EXPT_2.RMI;

import java.rmi.RemoteException;

public class CalculatorImpl extends java.rmi.server.UnicastRemoteObject implements Calculator {
    // Implementations must have an explicit constructor
    // in order to declare the RemoteException exception
    public CalculatorImpl() throws RemoteException {
        super();
    }

    public long add(long a, long b) throws RemoteException {
        return a + b;
    }

    public long sub(long a, long b) throws RemoteException {
        return a - b;
    }

    public long mul(long a, long b) throws RemoteException {
        return a * b;
    }

    public long div(long a, long b) throws RemoteException {
        return a / b;
    }
}
