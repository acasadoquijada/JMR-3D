package jmr.application;

import java.awt.image.BufferedImage;
import jmr.result.ResultList;

/**
 *
 * @author Jesús Chamorro Martínez (jesus@decsai.ugr.es)
 */
public class ImageListInternalFrame extends javax.swing.JInternalFrame {

    public ImageListInternalFrame() {
        initComponents();     
    }
    
     public ImageListInternalFrame(ResultList list){
        this();
        if(list!=null)
            imageListPanel.add(list);
    }
    
    public void add(BufferedImage image){
        imageListPanel.add(image);
    }
    
    public void add(BufferedImage image, String label){
        imageListPanel.add(image, label);
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        imageListPanel = new jmr.iu.ImageListPanel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Result");
        getContentPane().add(imageListPanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private jmr.iu.ImageListPanel imageListPanel;
    // End of variables declaration//GEN-END:variables
}
