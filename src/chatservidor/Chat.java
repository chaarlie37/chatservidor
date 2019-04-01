/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatservidor;

import java.awt.AWTException;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 *
 * @author carlo
 */
public class Chat {
    
    public Chat(int port, VentanaChat ventana) throws UnknownHostException, IOException, AWTException, ClassNotFoundException{
        this.ventana = ventana;
        EstablecerConexion(port);
    }
    
    public Chat(){}
    private ServerSocket serverSocket;
    private Socket socket;
    private DataOutputStream streamToClient;
    private ObjectOutputStream objectStreamToClient;
    private ObjectInputStream objectInputStream;
    private InputStreamReader streamFromClient;
    private BufferedReader bufferedReader;
    private Thread hilo;
    private VentanaChat ventana;
    public Notification notificacion = new Notification();
    private String ip_cliente;
    
    public void EstablecerConexion(int port) throws UnknownHostException, IOException, AWTException, ClassNotFoundException{       
        /*
        socket = new Socket(ip, port);
        streamToServer = new DataOutputStream(socket.getOutputStream());
        streamFromServer = new InputStreamReader(socket.getInputStream());
        bufferedReader = new BufferedReader(streamFromServer);
        ventana.ActualizarConversacion("Estableciendo conexión con " + ip +"...");
        EnviarMensaje("confirm");
        String mensajeRecibido = bufferedReader.readLine();
        if(mensajeRecibido.equals("confirm")){
            notificacion.displayTray("Conexión establecida", socket.getInetAddress().toString());
            ventana.conectado = true;
            ventana.ActualizarConversacion("Conectado correctamente.");
        }
        */
        serverSocket = new ServerSocket(port);
        socket = serverSocket.accept();
        String confirmacion;
        do{
            //streamFromClient = new InputStreamReader(socket.getInputStream());
            //bufferedReader = new BufferedReader(streamFromClient);
            //confirmacion = bufferedReader.readLine();
            //streamToClient = new DataOutputStream(socket.getOutputStream());
            //streamToClient.writeBytes(confirmacion + '\n');
            objectStreamToClient = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            confirmacion = DecodificarLista((ArrayList<Integer>) objectInputStream.readObject());
            System.out.println(confirmacion);
            EnviarMensaje(confirmacion);
            
        }while(!confirmacion.equals("confirm"));
        StringBuilder ip = new StringBuilder(socket.getInetAddress().toString());
        ip.deleteCharAt(0);
        ip_cliente = ip.toString();
    }
    
    public void EnviarMensaje(String msg) throws IOException{
        //streamToClient.writeBytes(msg + '\n');

        ArrayList<Integer> list = CodificarString(msg);
        objectStreamToClient.writeObject(list);
    }
    
    public void Escuchar(){
        try {
            String mensajeRecibido;
            //mensajeRecibido = bufferedReader.readLine();
            mensajeRecibido = DecodificarLista((ArrayList < Integer >)objectInputStream.readObject());
            if ((!mensajeRecibido.equals(""))) {
                ventana.EscribirMensajeCliente(mensajeRecibido);
                if(ventana.minimizado){
                    //ventana.Notificar();
                    notificacion.displayTray("Mensaje nuevo", mensajeRecibido);
                    System.out.println("Notificacion");
                }
            }
        } catch (IOException e) {
        } catch(AWTException e){
        } catch(ClassNotFoundException e){}

    }
    
    public void CerrarConexion() throws IOException{
        socket.close();
    }
    
    public ArrayList<Integer> CodificarString(String cadena){
        ArrayList<Integer> lista = new ArrayList<>();
        for(int i = 0; i<cadena.length(); i++){
            int ascii = cadena.codePointAt(i);
            lista.add(CodificarInt(ascii));
            System.out.println(CodificarInt(ascii));
        }
        return lista;
    }
    
    public int CodificarInt(int entero){
        
        int n1 = entero * 84 * 24;
        int n2  = (n1 + 58) * 54;
        int n3 = (n2 * 34) / 44;
        
        return (int) n2;
    }
    
    public int DecodificarInt(int entero){
        
        int n1 = (entero * 44) / 34;
        int n2 = (entero / 54) - 58;
        int n3 = n2 / (84*24);

        return n3;
    }
    
    public String DecodificarLista(ArrayList<Integer> lista){
        StringBuilder resultado = new StringBuilder();
        for(Integer ent : lista){
            int decodificado = DecodificarInt(ent);
            resultado.appendCodePoint(decodificado);
        }
        return resultado.toString();
    }
    
    public static void main(String[] args) {
        Chat c = new Chat();
        ArrayList<Integer> lista = c.CodificarString("hola");
        System.out.println("");
        System.out.println(c.DecodificarLista(lista));
    }
    
    public String getIpCliente(){
        return ip_cliente;
    }
    
}
