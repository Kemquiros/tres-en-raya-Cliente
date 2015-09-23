/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package triquisocketscliente;

import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;

/**
 *
 * @author esteban.catanoe
 */
public class ClienteHilo implements Runnable {

    private Socket socketCliente;
    private DataOutputStream out;
    private DataInputStream in;
    private int puerto ;
    private String ip ;
    private String mensaje;
    private ClienteVista ventanaCliente;
    private JButton[][] arregloBotones;
    private ActionListener[][] arregloEventos;
    private Image X;
    private Image O;
    private char[] letras;
    private int[] primos;

    private boolean turno;

    public ClienteHilo(ClienteVista frame, char[]  _letras, int[] _primos) {
        try {
            this.ventanaCliente = frame;
            this.letras=_letras;
            System.out.println("Llega al hilo cliente");
            try {
                 this.primos= _primos;
                 System.out.println("Copia el vector de primos");
            } catch (Exception e) {
                System.out.println("Errorzzzz: "+e.getMessage());
            }
           
            //Cargamos las imagenes de la X y O
            X = ImageIO.read(getClass().getResource("/Resources/Xpic.png"));
            O = ImageIO.read(getClass().getResource("/Resources/Opic.png"));
            //Creamos el socket con el host y el puerto, declaramos los streams de comunicacion
            ip=frame.clienteMenu.servidor;
            puerto=frame.clienteMenu.puerto;
            System.out.println("Comienza la creacion de socket");
            socketCliente = new Socket(ip, puerto);
            System.out.println("Termina la creacion de socket");
            in = new DataInputStream(socketCliente.getInputStream());
            out = new DataOutputStream(socketCliente.getOutputStream());
            System.out.println("Captura los datos in-out");
            //Tomamos una matriz con los 9 botones del juego
            arregloBotones = this.ventanaCliente.getBotones();
            arregloEventos = new ActionListener[3][3];
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    arregloEventos[i][j] = arregloBotones[i][j].getActionListeners()[0];
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(ventanaCliente, e.getMessage(), "Errortttt", 2);
            System.out.println("Se sale en la excepción del CLienteHilo " +e.toString());
            System.exit(0);
            //e.printStackTrace();
            return;
        }
        frame.setVisible(true);
    }

    @Override
    public void run() {
        try {
            System.out.println("**Ingresa al run de ClienteHilo");
            mensaje = in.readUTF();
            System.out.println("Mensaje sin Desenncriptar: "+mensaje);
            String[] splitMensaje = mensaje.split(";");
            int clave=Integer.parseInt(splitMensaje[2]);
            System.out.println("Clave1: "+clave);
            String temporal=desencriptacion(clave, mensaje);
            System.out.println("Mensaje Desencriptado: "+temporal);
            String XO = splitMensaje[0].split(" ")[1];
            XO=desencriptacion(clave, XO);
            ventanaCliente.cambioTexto("Juegas con: " + XO);
            splitMensaje[1]=desencriptacion(clave,splitMensaje[1]);
            turno = Boolean.valueOf(splitMensaje[1]);

           while (true) {
                //Recibimos el mensaje
                mensaje = in.readUTF();
                String[] mensajes = mensaje.split(";");
                int clave1=Integer.parseInt(mensajes[mensajes.length-1]);
                System.out.println("Clave1: "+clave1);
                /*
                 El mensaje esta compuesto por una cadena separada por ; cada separacion representa un dato
                 mensaje[0] : representa X o O 
                 mensaje[1] : representa fila del tablero
                 mensaje[2] : representa columna del tablero
                 mensaje[3] : representa estado del juego [Perdiste, Ganaste, Empate]
                 mensaje[4] : clave
                 */

                if (mensajes.length==3) {
                    arregloBotones[0][0].setIcon(null);
                    arregloBotones[0][1].setIcon(null);
                    arregloBotones[0][2].setIcon(null);
                    arregloBotones[1][0].setIcon(null);
                    arregloBotones[1][1].setIcon(null);
                    arregloBotones[1][2].setIcon(null);
                    arregloBotones[2][0].setIcon(null);
                    arregloBotones[2][1].setIcon(null);
                    arregloBotones[2][2].setIcon(null);
                    
                    
                    arregloBotones[0][0].addActionListener(arregloEventos[0][0]);
                    arregloBotones[0][1].addActionListener(arregloEventos[0][1]);
                    arregloBotones[0][2].addActionListener(arregloEventos[0][2]);
                    arregloBotones[1][0].addActionListener(arregloEventos[1][0]);
                    arregloBotones[1][1].addActionListener(arregloEventos[1][1]);
                    arregloBotones[1][2].addActionListener(arregloEventos[1][2]);
                    arregloBotones[2][0].addActionListener(arregloEventos[2][0]);
                    arregloBotones[2][1].addActionListener(arregloEventos[2][1]);
                    arregloBotones[2][2].addActionListener(arregloEventos[2][2]);
                    
                    turno = !turno;
                } else {

                    //Desencriptación
                    mensajes[0] = desencriptacion(Integer.parseInt(mensajes[4]), mensajes[0]);
                    mensajes[1] = desencriptacion(Integer.parseInt(mensajes[4]), mensajes[1]);
                    mensajes[2] = desencriptacion(Integer.parseInt(mensajes[4]), mensajes[2]);
                    mensajes[3] = desencriptacion(Integer.parseInt(mensajes[4]), mensajes[3]);
                    //--
                    int jugador = Integer.parseInt(mensajes[0]);
                    int fila = Integer.parseInt(mensajes[1]);
                    int columna = Integer.parseInt(mensajes[2]);

                    if (jugador == 1) {
                        arregloBotones[fila][columna].setIcon(new ImageIcon(X));
                    } else {
                        arregloBotones[fila][columna].setIcon(new ImageIcon(O));
                    }
                    
                    arregloBotones[fila][columna].removeActionListener(arregloBotones[fila][columna].getActionListeners()[0]);
                    turno = !turno;
    
                    if (XO.equals(mensajes[3])) {
                        JOptionPane.showMessageDialog(ventanaCliente, "¡¡Muy bien, Has ganado!!");
                        //new ClienteVista().setVisible(true);
                        //ventanaCliente.dispose();
                        
                    } else if ("EMPATE".equals(mensajes[3])) {
                        JOptionPane.showMessageDialog(ventanaCliente, "¡¡Han quedado empatados!!");
                        //new ClienteVista().setVisible(true);
                        //ventanaCliente.dispose();
                    } else if (!"NINGUNO".equals(mensajes[3]) && !mensajes[3].equals(mensajes[0])) {
                        JOptionPane.showMessageDialog(ventanaCliente, "¡¡Que mal, Has perdido!!");
                        //new ClienteVista().setVisible(true);
                        //ventanaCliente.dispose();
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(ventanaCliente, e.getMessage()+" "+e.getLocalizedMessage()+" "+e.toString(), "ErrorHHHHH", 2);
            System.out.println(e.getMessage());
            System.out.println(e.getLocalizedMessage());
        }
    }

    //Funcion sirve para enviar la jugada al servidor
    public void enviarTurno(int f, int c) {
        /*
         Comprobamos que sea nuestro turno para jugar, si no es devolmemos un mensaje
         Si es el turno entonces mandamos un mensaje al servidor con los datos de la jugada que hicimos
         */
        try {
            if (turno) {
                int claveX=generarClave();
                String datos = "";
                datos += f + ";";
                datos += c + ";";
                System.out.println(">>Sin encriptar: "+datos);
                encriptar(claveX, datos);
                out.writeUTF(datos+claveX);
                System.out.println(">>Encriptados: "+datos);
            } else {
                JOptionPane.showMessageDialog(ventanaCliente, "Espera tu turno");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reiniciar() {
        try {
            out.writeUTF("Reiniciar");
        } catch (IOException ex) {
            Logger.getLogger(ClienteHilo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //---------------Encriptación
    //Encripta un mensaje con una clave
    //Encripta un mensaje con una clave
String encriptar(int clave, String mensaje){
        System.out.println("Ingresa a encriptar ServidorHilo mensaje:"+mensaje);
        mensaje=mensaje.toLowerCase();
        char[] m = mensaje.toCharArray();
        //try {
            
        
        
        //System.out.println("Ingresa a Descomposición prima con clave:"+clave);
        int god = descomposicionPrima(clave);
        System.out.println("Clave: "+clave+"  DescomPrima: "+god);
        for(int i=0;i<m.length;i++){
            //El separador  lo deja intacto
            if(m[i]!=';'){
               System.out.println("-Indice original: "+i + " Caracter:"+m[i]);
               int indice=buscarIndice(m[i]);
               if(indice==-1){
                   System.out.println("Error: no se encuentra el indice:"+indice);
                   
               }
                System.out.print("  Indice busqueda: "+indice+" Caracter Busqueda: "+letras[indice]);
                //Porque hay 36 caracteres
               
                int indice1=((indice+god)%letras.length);
                //System.out.println(" Nuevo indice: "+indice1+" Nuevo Caracter:"+letras[indice1]+" god:"+god+" i+g:"+(indice+god)+" leng:"+letras.length+" mod:" +((indice+god)%letras.length));
                m[i]= letras[indice1]; 
            }
            
        //}
        System.out.println("Sale de encriptar mensaje:"+Arrays.toString(m));
        
        //} catch (Exception e) {
        //    System.out.println("Herror:"+e.getMessage());
        }
        return new String(m);
    }
    
    int buscarIndice(char c){        
        for(int i=0;i<letras.length;i++){
            //Encuentra el indice
            if(letras[i]==c){
                return i;
            }
        }
        return -1;
    }
    
    //Descomposicion en factores primos
    int descomposicionPrima(int clave){
        int sum=0;
        int indice=0;
        while(clave>1){
            //Si es divisible entre el primo
            //sumelo
            if(clave%primos[indice]==0){
                sum+=primos[indice];
                clave=clave/primos[indice];
            }
            else{//De lo contrario pase al siguiente primo
                indice++;
            }            
        }
        return sum;
    }
    String desencriptacion(int clave, String mensaje){
      
        System.out.println("Ingresa a encriptar ServidorHilo mensaje:"+mensaje);
        mensaje=mensaje.toLowerCase();
        char[] m = mensaje.toCharArray();
        //try {
            
        
        
        //System.out.println("Ingresa a Descomposición prima con clave:"+clave);
        int god = descomposicionPrima(clave);
        System.out.println("Clave: "+clave+"  DescomPrima: "+god);
        for(int i=0;i<m.length;i++){
            //El separador  lo deja intacto
            if(m[i]!=';'){
               System.out.println("-Indice original: "+i + " Caracter:"+m[i]);
               int indice=buscarIndice(m[i]);
               if(indice==-1){
                   System.out.println("Error: no se encuentra el indice:"+indice);
                   
               }
                System.out.print("  Indice busqueda: "+indice+" Caracter Busqueda: "+letras[indice]);
                //Porque hay 36 caracteres
                try {
                     int indice1=(Math.abs(indice-god)%letras.length);
                System.out.println(" Nuevo indice: "+indice1+" Nuevo Caracter:"+letras[indice1]+" god:"+god+" i+g:"+(indice+god)+" leng:"+letras.length+" mod:" +((indice+god)%letras.length));
                m[i]= letras[indice1];
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "nada que hacer");
                }
                
            }
            
        //}
        System.out.println("Sale de encriptar mensaje:"+Arrays.toString(m));
        
        //} catch (Exception e) {
        //    System.out.println("Herror:"+e.getMessage());
        }
        return new String(m);
    }
    //Genera numero aleatorio no primo
    int generarClave(){
        int num=-1;
        while(num==-1){
            num=calcularNumeroAleatorioNoPrimo();
        }
        return num;
    }
    
    int calcularNumeroAleatorioNoPrimo(){
        int range = (1000 - 100) + 1;     
        int num=(int)(Math.random() * range) + 100;
        if(!esPrimo(num)){
            return num;
        }
        return -1;
    }
    
    public static boolean esPrimo(int numero){
      int contador = 2;
      boolean primo=true;
      while ((primo) && (contador!=numero)){
        if (numero % contador == 0)
          primo = false;
        contador++;
      }
      return primo;
    }
    //--------------------------
    
    

}
