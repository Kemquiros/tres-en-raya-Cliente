/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package triquisocketscliente;

import java.awt.Graphics;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 *
 * @author jedisson.tapias
 */

public class Fondo extends javax.swing.JPanel {
    int x, y;
    ImageIcon Img;

    public Fondo(JPanel jPanel1) {
        this.x = jPanel1.getWidth();
        this.y = jPanel1.getHeight();
        this.setSize(x, y);
    }

    @Override
    public void paint(Graphics g) {
        try{
            Img = new ImageIcon(getClass().getResource("/Resources/fantasy.jpg"));
            g.drawImage(Img.getImage(), 0, 0, x, y, null);
        }catch(Exception e){
            System.out.println("Error:"+e.getMessage());
        }
       
    }    

}