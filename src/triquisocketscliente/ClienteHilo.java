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
    private int puerto = 3020;
    private String ip = "localhost";
    private String mensaje;
    private ClienteVista ventanaCliente;
    private JButton[][] arregloBotones;
    private ActionListener[][] arregloEventos;
    private Image X;
    private Image O;

    private boolean turno;

    public ClienteHilo(ClienteVista frame) {
        try {
            this.ventanaCliente = frame;
            //Cargamos las imagenes de la X y O
            X = ImageIO.read(getClass().getResource("/jugador/Xpic.png"));
            O = ImageIO.read(getClass().getResource("/jugador/Opic.png"));
            //Creamos el socket con el host y el puerto, declaramos los streams de comunicacion
            socketCliente = new Socket(ip, puerto);
            in = new DataInputStream(socketCliente.getInputStream());
            out = new DataOutputStream(socketCliente.getOutputStream());
            //Tomamos una matriz con los 9 botones del juego
            arregloBotones = this.ventanaCliente.getBotones();
            arregloEventos = new ActionListener[3][3];
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    arregloEventos[i][j] = arregloBotones[i][j].getActionListeners()[0];
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            mensaje = in.readUTF();
            String[] splitMensaje = mensaje.split(";");  
            String XO = splitMensaje[0].split(" ")[1];
            ventanaCliente.cambioTexto("Juegas con: " + XO);
            turno = Boolean.valueOf(splitMensaje[1]);

           while (true) {
                //Recibimos el mensaje
                mensaje = in.readUTF();
                /*
                 El mensaje esta compuesto por una cadena separada por ; cada separacion representa un dato
                 mensaje[0] : representa X o O 
                 mensaje[1] : representa fila del tablero
                 mensaje[2] : representa columna del tablero
                 mensaje[3] : representa estado del juego [Perdiste, Ganaste, Empate]
                 */

                if (mensaje.equals("Reiniciar")) {
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

                    String[] mensajes = mensaje.split(";");
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
                        new ClienteVista().setVisible(true);
                        ventanaCliente.dispose();
                    } else if ("EMPATE".equals(mensajes[3])) {
                        JOptionPane.showMessageDialog(ventanaCliente, "¡¡Han quedado empatados!!");
                        new ClienteVista().setVisible(true);
                        ventanaCliente.dispose();
                    } else if (!"NINGUNO".equals(mensajes[3]) && !mensajes[3].equals(mensajes[0])) {
                        JOptionPane.showMessageDialog(ventanaCliente, "¡¡Que mal, Has perdido!!");
                        new ClienteVista().setVisible(true);
                        ventanaCliente.dispose();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
                String datos = "";
                datos += f + ";";
                datos += c + ";";
                out.writeUTF(datos);
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

}
