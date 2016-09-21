package jmr.application;

import iu.ImageInternalFrame;
import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.JFrame;
import jmr.descriptor.mpeg7.MPEG7DominantColors;

/**
 *
 * @author Jesús Chamorro Martínez (jesus@decsai.ugr.es)
 */
public class JMRImageInternalFrame extends ImageInternalFrame {

    private MPEG7DominantColors dcd = null;
    ColorSetPanel panelDCD = null;
    
    /**
     * Creates new form JMRImageInternalFrame
     * @param parent
     * @param img
     */
    public JMRImageInternalFrame(JFrame parent, BufferedImage img) {
        super(parent,img);
        initComponents();
    }

    public void setDominantColorDescriptor(MPEG7DominantColors dcd){
        this.dcd = dcd;
        addColorPanel();
    }
    
    public MPEG7DominantColors getDominantColorDescriptor(){
        return dcd;
    }
    
    private void addColorPanel() {
        if (dcd != null) {
            panelDCD = new ColorSetPanel();
            ArrayList<MPEG7DominantColors.MPEG7SingleDominatColor> list = dcd.getDominantColors();
            for (MPEG7DominantColors.MPEG7SingleDominatColor c : list) {
                panelDCD.addColor(c.getColor());
            }
            this.add(panelDCD, BorderLayout.EAST);
            this.validate();
            this.repaint();
        }
    }
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
